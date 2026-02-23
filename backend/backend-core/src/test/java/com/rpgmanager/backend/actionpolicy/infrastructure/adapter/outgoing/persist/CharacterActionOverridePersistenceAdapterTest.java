package com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.CharacterActionOverride;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import com.rpgmanager.backend.actionpolicy.infrastructure.mapper.CharacterActionOverridePersistenceMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CharacterActionOverridePersistenceAdapterTest {

  @Mock private JpaCharacterActionOverrideRepository jpaRepository;
  @Mock private CharacterActionOverridePersistenceMapper mapper;

  @InjectMocks private CharacterActionOverridePersistenceAdapter adapter;

  @Test
  void shouldFindByCharacterIdAndActionTypeAndContextTypeAndContextId() {
    CharacterActionOverrideEntity entity = CharacterActionOverrideEntity.builder().build();
    CharacterActionOverride domain = CharacterActionOverride.builder().build();

    when(jpaRepository.findByCharacterIdAndActionTypeAndContextTypeAndContextId(any(), any(), any(), any()))
        .thenReturn(Optional.of(entity));
    when(mapper.toDomain(entity)).thenReturn(domain);

    Optional<CharacterActionOverride> result =
        adapter.findByCharacterIdAndActionTypeAndContextTypeAndContextId(
            1L, ActionType.DISTRIBUTE_POINTS, ContextType.SESSION, 200L);

    assertThat(result).isPresent().contains(domain);
  }

  @Test
  void shouldReturnEmptyWhenNotFound() {
    when(jpaRepository.findByCharacterIdAndActionTypeAndContextTypeAndContextId(any(), any(), any(), any()))
        .thenReturn(Optional.empty());

    Optional<CharacterActionOverride> result =
        adapter.findByCharacterIdAndActionTypeAndContextTypeAndContextId(
            1L, ActionType.DISTRIBUTE_POINTS, ContextType.SESSION, 200L);

    assertThat(result).isEmpty();
  }
}
