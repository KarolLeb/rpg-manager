package com.rpgmanager.backend.actionpolicy.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.rpgmanager.backend.actionpolicy.application.port.out.ActionPolicyRepositoryPort;
import com.rpgmanager.backend.actionpolicy.application.port.out.CharacterActionOverrideRepositoryPort;
import com.rpgmanager.backend.actionpolicy.domain.ActionPolicy;
import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.CharacterActionOverride;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import java.util.Optional;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.Provide;
import net.jqwik.api.lifecycle.BeforeProperty;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * Property-based tests for {@link ActionPermissionService}.
 *
 * <p>
 * These tests verify the 4-level priority resolution invariants of
 * {@code canPerformAction}
 * across all possible combinations of {@link ActionType}, context IDs, and
 * allowed/denied states.
 * They complement the example-based scenarios in
 * {@link ActionPermissionServiceTest}.
 */
class ActionPermissionServicePropertyTest {

    @Mock
    private ActionPolicyRepositoryPort actionPolicyRepositoryPort;

    @Mock
    private CharacterActionOverrideRepositoryPort characterActionOverrideRepositoryPort;

    private ActionPermissionService underTest;

    @BeforeProperty
    void initMocks() {
        MockitoAnnotations.openMocks(this);
        underTest = new ActionPermissionService(actionPolicyRepositoryPort, characterActionOverrideRepositoryPort);
    }

    // ---------------------------------------------------------------------------
    // Arbitraries
    // ---------------------------------------------------------------------------

    @Provide
    Arbitrary<ActionType> actionTypes() {
        return Arbitraries.of(ActionType.values());
    }

    @Provide
    Arbitrary<Long> positiveIds() {
        return Arbitraries.longs().between(1L, Long.MAX_VALUE);
    }

    // ---------------------------------------------------------------------------
    // Property 1: Character session override is always the highest priority
    // ---------------------------------------------------------------------------

    /**
     * When a character-session override exists, its {@code isAllowed} value MUST be
     * returned,
     * regardless of every other policy or override beneath it.
     */
    @Property
    void sessionOverrideAlwaysWins(
            @ForAll("positiveIds") Long characterId,
            @ForAll("actionTypes") ActionType actionType,
            @ForAll("positiveIds") Long campaignId,
            @ForAll("positiveIds") Long sessionId,
            @ForAll boolean overrideAllowed) {

        Mockito.reset(actionPolicyRepositoryPort, characterActionOverrideRepositoryPort);

        // Session override present — this is the highest priority
        when(characterActionOverrideRepositoryPort
                .findByCharacterIdAndActionTypeAndContextTypeAndContextId(
                        characterId, actionType, ContextType.SESSION, sessionId))
                .thenReturn(Optional.of(CharacterActionOverride.builder().isAllowed(overrideAllowed).build()));

        // All lower-priority lookups return the opposite to make the test falsifiable
        boolean opposite = !overrideAllowed;
        when(characterActionOverrideRepositoryPort
                .findByCharacterIdAndActionTypeAndContextTypeAndContextId(
                        characterId, actionType, ContextType.CAMPAIGN, campaignId))
                .thenReturn(Optional.of(CharacterActionOverride.builder().isAllowed(opposite).build()));
        when(actionPolicyRepositoryPort.findByActionTypeAndContextTypeAndContextId(
                eq(actionType), any(ContextType.class), any(Long.class)))
                .thenReturn(Optional.of(ActionPolicy.builder().isAllowed(opposite).build()));

        boolean result = underTest.canPerformAction(characterId, actionType, campaignId, sessionId);

        assertThat(result)
                .as(
                        "session override (allowed=%s) must win over all lower-priority rules for"
                                + " characterId=%d, actionType=%s",
                        overrideAllowed, characterId, actionType)
                .isEqualTo(overrideAllowed);
    }

    // ---------------------------------------------------------------------------
    // Property 2: Campaign override wins when no session override is present
    // ---------------------------------------------------------------------------

    /**
     * When no session override exists but a campaign override does, the campaign
     * override's {@code
     * isAllowed} value MUST be returned.
     */
    @Property
    void campaignOverrideWinsWhenNoSessionOverride(
            @ForAll("positiveIds") Long characterId,
            @ForAll("actionTypes") ActionType actionType,
            @ForAll("positiveIds") Long campaignId,
            @ForAll("positiveIds") Long sessionId,
            @ForAll boolean overrideAllowed) {

        Mockito.reset(actionPolicyRepositoryPort, characterActionOverrideRepositoryPort);

        // No session override
        when(characterActionOverrideRepositoryPort
                .findByCharacterIdAndActionTypeAndContextTypeAndContextId(
                        characterId, actionType, ContextType.SESSION, sessionId))
                .thenReturn(Optional.empty());

        // Campaign override present
        when(characterActionOverrideRepositoryPort
                .findByCharacterIdAndActionTypeAndContextTypeAndContextId(
                        characterId, actionType, ContextType.CAMPAIGN, campaignId))
                .thenReturn(Optional.of(CharacterActionOverride.builder().isAllowed(overrideAllowed).build()));

        // Lower-priority policies return the opposite
        boolean opposite = !overrideAllowed;
        when(actionPolicyRepositoryPort.findByActionTypeAndContextTypeAndContextId(
                eq(actionType), any(ContextType.class), any(Long.class)))
                .thenReturn(Optional.of(ActionPolicy.builder().isAllowed(opposite).build()));

        boolean result = underTest.canPerformAction(characterId, actionType, campaignId, sessionId);

        assertThat(result)
                .as(
                        "campaign override (allowed=%s) must win when session override absent for"
                                + " characterId=%d, actionType=%s",
                        overrideAllowed, characterId, actionType)
                .isEqualTo(overrideAllowed);
    }

    // ---------------------------------------------------------------------------
    // Property 3: Session policy wins over campaign policy when no overrides
    // ---------------------------------------------------------------------------

    /**
     * When no character overrides exist but a session-scoped policy does, the
     * session policy's
     * {@code isAllowed} value MUST be returned, regardless of any campaign policy.
     */
    @Property
    void sessionPolicyWinsOverCampaignPolicyWhenNoOverrides(
            @ForAll("positiveIds") Long characterId,
            @ForAll("actionTypes") ActionType actionType,
            @ForAll("positiveIds") Long campaignId,
            @ForAll("positiveIds") Long sessionId,
            @ForAll boolean policyAllowed) {

        Mockito.reset(actionPolicyRepositoryPort, characterActionOverrideRepositoryPort);

        // No overrides at all
        when(characterActionOverrideRepositoryPort
                .findByCharacterIdAndActionTypeAndContextTypeAndContextId(any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        // Session policy present
        when(actionPolicyRepositoryPort.findByActionTypeAndContextTypeAndContextId(
                actionType, ContextType.SESSION, sessionId))
                .thenReturn(Optional.of(ActionPolicy.builder().isAllowed(policyAllowed).build()));

        // Campaign policy returns the opposite
        when(actionPolicyRepositoryPort.findByActionTypeAndContextTypeAndContextId(
                actionType, ContextType.CAMPAIGN, campaignId))
                .thenReturn(Optional.of(ActionPolicy.builder().isAllowed(!policyAllowed).build()));

        boolean result = underTest.canPerformAction(characterId, actionType, campaignId, sessionId);

        assertThat(result)
                .as(
                        "session policy (allowed=%s) must win over campaign policy for"
                                + " characterId=%d, actionType=%s",
                        policyAllowed, characterId, actionType)
                .isEqualTo(policyAllowed);
    }

    // ---------------------------------------------------------------------------
    // Property 4: Campaign policy is authoritative when nothing more specific
    // exists
    // ---------------------------------------------------------------------------

    /**
     * When no overrides and no session policy exist, the campaign policy's
     * {@code isAllowed} value
     * MUST be the final answer.
     */
    @Property
    void campaignPolicyIsAuthoritativeWhenNothingMoreSpecific(
            @ForAll("positiveIds") Long characterId,
            @ForAll("actionTypes") ActionType actionType,
            @ForAll("positiveIds") Long campaignId,
            @ForAll("positiveIds") Long sessionId,
            @ForAll boolean policyAllowed) {

        Mockito.reset(actionPolicyRepositoryPort, characterActionOverrideRepositoryPort);

        when(characterActionOverrideRepositoryPort
                .findByCharacterIdAndActionTypeAndContextTypeAndContextId(any(), any(), any(), any()))
                .thenReturn(Optional.empty());

        when(actionPolicyRepositoryPort.findByActionTypeAndContextTypeAndContextId(
                actionType, ContextType.SESSION, sessionId))
                .thenReturn(Optional.empty());

        when(actionPolicyRepositoryPort.findByActionTypeAndContextTypeAndContextId(
                actionType, ContextType.CAMPAIGN, campaignId))
                .thenReturn(Optional.of(ActionPolicy.builder().isAllowed(policyAllowed).build()));

        boolean result = underTest.canPerformAction(characterId, actionType, campaignId, sessionId);

        assertThat(result)
                .as(
                        "campaign policy (allowed=%s) must apply when nothing more specific exists for"
                                + " characterId=%d, actionType=%s",
                        policyAllowed, characterId, actionType)
                .isEqualTo(policyAllowed);
    }

    // ---------------------------------------------------------------------------
    // Property 5: Default is always allow when nothing is configured
    // ---------------------------------------------------------------------------

    /**
     * When absolutely no overrides and no policies are found for any context, the
     * service MUST
     * default to {@code true} (allow).
     */
    @Property
    void defaultsToAllowWhenNoRulesConfigured(
            @ForAll("positiveIds") Long characterId,
            @ForAll("actionTypes") ActionType actionType,
            @ForAll("positiveIds") Long campaignId,
            @ForAll("positiveIds") Long sessionId) {

        Mockito.reset(actionPolicyRepositoryPort, characterActionOverrideRepositoryPort);

        when(characterActionOverrideRepositoryPort
                .findByCharacterIdAndActionTypeAndContextTypeAndContextId(any(), any(), any(), any()))
                .thenReturn(Optional.empty());
        when(actionPolicyRepositoryPort.findByActionTypeAndContextTypeAndContextId(
                any(), any(), any()))
                .thenReturn(Optional.empty());

        boolean result = underTest.canPerformAction(characterId, actionType, campaignId, sessionId);

        assertThat(result)
                .as(
                        "must default to true (allow) when no rules exist for characterId=%d, actionType=%s",
                        characterId, actionType)
                .isTrue();
    }

    // ---------------------------------------------------------------------------
    // Property 6: Null context IDs always return true (no lookups can match)
    // ---------------------------------------------------------------------------

    /**
     * When both {@code campaignId} and {@code sessionId} are {@code null}, the
     * service skips all
     * context-specific lookups and MUST return {@code true}.
     */
    @Property
    void nullContextIdsAlwaysReturnTrue(
            @ForAll("positiveIds") Long characterId, @ForAll("actionTypes") ActionType actionType) {

        Mockito.reset(actionPolicyRepositoryPort, characterActionOverrideRepositoryPort);

        // Ensure any accidental lookup would return deny — result must still be true
        when(characterActionOverrideRepositoryPort
                .findByCharacterIdAndActionTypeAndContextTypeAndContextId(any(), any(), any(), any()))
                .thenReturn(Optional.of(CharacterActionOverride.builder().isAllowed(false).build()));
        when(actionPolicyRepositoryPort.findByActionTypeAndContextTypeAndContextId(
                any(), any(), any()))
                .thenReturn(Optional.of(ActionPolicy.builder().isAllowed(false).build()));

        boolean result = underTest.canPerformAction(characterId, actionType, null, null);

        assertThat(result)
                .as(
                        "null context IDs must always return true (no context lookups) for"
                                + " characterId=%d, actionType=%s",
                        characterId, actionType)
                .isTrue();
    }
}
