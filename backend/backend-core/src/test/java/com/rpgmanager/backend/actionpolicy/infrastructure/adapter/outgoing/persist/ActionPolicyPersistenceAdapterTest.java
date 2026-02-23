package com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.rpgmanager.backend.actionpolicy.domain.ActionPolicy;
import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import com.rpgmanager.backend.actionpolicy.infrastructure.mapper.ActionPolicyPersistenceMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ActionPolicyPersistenceAdapterTest {

  @Mock private JpaActionPolicyRepository jpaRepository;
  @Mock private ActionPolicyPersistenceMapper mapper;

  @InjectMocks private ActionPolicyPersistenceAdapter adapter;

  @Test
  void shouldFindByActionTypeAndContextTypeAndContextId() {
    ActionPolicyEntity entity = ActionPolicyEntity.builder().build();
    ActionPolicy domain = ActionPolicy.builder().build();

    when(jpaRepository.findByActionTypeAndContextTypeAndContextId(any(), any(), any()))
        .thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    Optional<ActionPolicy> result =
        adapter.findByActionTypeAndContextTypeAndContextId(
            ActionType.LEVEL_UP, ContextType.CAMPAIGN, 100L);

    assertThat(result).isPresent().contains(domain);
  }

  @Test
  void shouldReturnEmptyWhenNotFound() {
    when(jpaRepository.findByActionTypeAndContextTypeAndContextId(any(), any(), any()))
        .thenReturn(Optional.empty());

    Optional<ActionPolicy> result =
        adapter.findByActionTypeAndContextTypeAndContextId(
            ActionType.LEVEL_UP, ContextType.CAMPAIGN, 100L);

    assertThat(result).isEmpty();
  }
}
