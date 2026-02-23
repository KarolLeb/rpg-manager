package com.rpgmanager.backend.actionpolicy.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.rpgmanager.backend.actionpolicy.application.port.out.ActionPolicyRepositoryPort;
import com.rpgmanager.backend.actionpolicy.application.port.out.CharacterActionOverrideRepositoryPort;
import com.rpgmanager.backend.actionpolicy.domain.ActionPolicy;
import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.CharacterActionOverride;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActionPermissionServiceTest {

  @Mock private ActionPolicyRepositoryPort actionPolicyRepositoryPort;

  @Mock private CharacterActionOverrideRepositoryPort characterActionOverrideRepositoryPort;

  @InjectMocks private ActionPermissionService underTest;

  private static final Long CHARACTER_ID = 1L;
  private static final Long CAMPAIGN_ID = 100L;
  private static final Long SESSION_ID = 200L;

  @BeforeEach
  void setUp() {
    org.mockito.Mockito.reset(actionPolicyRepositoryPort, characterActionOverrideRepositoryPort);
  }

  @Test
  void shouldReturnFalseWhenBlockedGloballyInCampaignAndNoFurtherOverrides() {
    // given
    when(actionPolicyRepositoryPort.findByActionTypeAndContextTypeAndContextId(
            ActionType.LEVEL_UP, ContextType.CAMPAIGN, CAMPAIGN_ID))
        .thenReturn(Optional.of(ActionPolicy.builder().isAllowed(false).build()));

    // when
    boolean result =
        underTest.canPerformAction(CHARACTER_ID, ActionType.LEVEL_UP, CAMPAIGN_ID, null);

    // then
    assertThat(result).isFalse();
  }

  @Test
  void shouldReturnFalseWhenAllowedInCampaignButDeniedInSession() {
    // given
    // Removed CAMPAIGN mock since SESSION mock returns early and makes CAMPAIGN
    // stub unnecessary
    when(actionPolicyRepositoryPort.findByActionTypeAndContextTypeAndContextId(
            ActionType.LEVEL_UP, ContextType.SESSION, SESSION_ID))
        .thenReturn(Optional.of(ActionPolicy.builder().isAllowed(false).build()));

    // when
    boolean result =
        underTest.canPerformAction(CHARACTER_ID, ActionType.LEVEL_UP, CAMPAIGN_ID, SESSION_ID);

    // then
    assertThat(result).isFalse();
  }

  @Test
  void shouldReturnTrueWhenDeniedInCampaignButCharacterOverrideAllowsIt() {
    // given
    // Removed CAMPAIGN mock since CharacterOverride mock returns early and makes
    // CAMPAIGN stub unnecessary
    when(characterActionOverrideRepositoryPort
            .findByCharacterIdAndActionTypeAndContextTypeAndContextId(
                CHARACTER_ID, ActionType.LEVEL_UP, ContextType.CAMPAIGN, CAMPAIGN_ID))
        .thenReturn(Optional.of(CharacterActionOverride.builder().isAllowed(true).build()));

    // when
    boolean result =
        underTest.canPerformAction(CHARACTER_ID, ActionType.LEVEL_UP, CAMPAIGN_ID, null);

    // then
    assertThat(result).isTrue();
  }

  @Test
  void shouldReturnTrueWhenNoPoliciesExist() {
    // given
    // No mocks set, meaning Optional.empty() is returned

    // when
    boolean result =
        underTest.canPerformAction(CHARACTER_ID, ActionType.LEVEL_UP, CAMPAIGN_ID, null);

    // then
    assertThat(result).isTrue();
  }

  @Test
  void shouldCheckSessionOverrideFirst() {
    // given
    when(characterActionOverrideRepositoryPort
            .findByCharacterIdAndActionTypeAndContextTypeAndContextId(
                CHARACTER_ID, ActionType.LEVEL_UP, ContextType.SESSION, SESSION_ID))
        .thenReturn(Optional.of(CharacterActionOverride.builder().isAllowed(false).build()));

    // when
    boolean result =
        underTest.canPerformAction(CHARACTER_ID, ActionType.LEVEL_UP, CAMPAIGN_ID, SESSION_ID);

    // then
    assertThat(result).isFalse();
  }

  @Test
  void shouldFallBackToCampaignOverrideIfSessionOverrideMissing() {
    // given
    when(characterActionOverrideRepositoryPort
            .findByCharacterIdAndActionTypeAndContextTypeAndContextId(
                CHARACTER_ID, ActionType.LEVEL_UP, ContextType.SESSION, SESSION_ID))
        .thenReturn(Optional.empty());
    when(characterActionOverrideRepositoryPort
            .findByCharacterIdAndActionTypeAndContextTypeAndContextId(
                CHARACTER_ID, ActionType.LEVEL_UP, ContextType.CAMPAIGN, CAMPAIGN_ID))
        .thenReturn(Optional.of(CharacterActionOverride.builder().isAllowed(true).build()));

    // when
    boolean result =
        underTest.canPerformAction(CHARACTER_ID, ActionType.LEVEL_UP, CAMPAIGN_ID, SESSION_ID);

    // then
    assertThat(result).isTrue();
  }

  @Test
  void shouldCheckSessionPolicyIfNoOverridesExist() {
    // given
    when(characterActionOverrideRepositoryPort
            .findByCharacterIdAndActionTypeAndContextTypeAndContextId(any(), any(), any(), any()))
        .thenReturn(Optional.empty());
    when(actionPolicyRepositoryPort.findByActionTypeAndContextTypeAndContextId(
            ActionType.LEVEL_UP, ContextType.SESSION, SESSION_ID))
        .thenReturn(Optional.of(ActionPolicy.builder().isAllowed(false).build()));

    // when
    boolean result =
        underTest.canPerformAction(CHARACTER_ID, ActionType.LEVEL_UP, CAMPAIGN_ID, SESSION_ID);

    // then
    assertThat(result).isFalse();
  }

  @Test
  void shouldFallBackToCampaignPolicyIfNoSessionPolicyExists() {
    // given
    when(characterActionOverrideRepositoryPort
            .findByCharacterIdAndActionTypeAndContextTypeAndContextId(any(), any(), any(), any()))
        .thenReturn(Optional.empty());
    when(actionPolicyRepositoryPort.findByActionTypeAndContextTypeAndContextId(
            ActionType.LEVEL_UP, ContextType.SESSION, SESSION_ID))
        .thenReturn(Optional.empty());
    when(actionPolicyRepositoryPort.findByActionTypeAndContextTypeAndContextId(
            ActionType.LEVEL_UP, ContextType.CAMPAIGN, CAMPAIGN_ID))
        .thenReturn(Optional.of(ActionPolicy.builder().isAllowed(false).build()));

    // when
    boolean result =
        underTest.canPerformAction(CHARACTER_ID, ActionType.LEVEL_UP, CAMPAIGN_ID, SESSION_ID);

    // then
    assertThat(result).isFalse();
  }

  @Test
  void shouldHandleNullCampaignAndSessionIds() {
    // when
    boolean result = underTest.canPerformAction(CHARACTER_ID, ActionType.LEVEL_UP, null, null);

    // then
    assertThat(result).isTrue();
  }
}
