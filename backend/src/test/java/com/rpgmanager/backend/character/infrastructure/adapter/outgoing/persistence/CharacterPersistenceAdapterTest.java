package com.rpgmanager.backend.character.infrastructure.adapter.outgoing.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.JpaCampaignRepository;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.user.infrastructure.adapter.outgoing.persist.JpaUserRepository;
import com.rpgmanager.backend.user.infrastructure.adapter.outgoing.persist.UserEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CharacterPersistenceAdapterTest {

  @Mock private JpaCharacterRepository jpaCharacterRepository;
  @Mock private JpaCampaignRepository jpaCampaignRepository;
  @Mock private JpaUserRepository userRepository;

  @InjectMocks private CharacterPersistenceAdapter adapter;

  @Test
  void findAll_shouldReturnList() {
    CharacterEntity entity = Instancio.create(CharacterEntity.class);
    when(jpaCharacterRepository.findAll()).thenReturn(List.of(entity));

    List<CharacterDomain> result = adapter.findAll();

    assertThat(result).hasSize(1);
  }

  @Test
  void findByUuid_shouldReturnDomain() {
    UUID uuid = UUID.randomUUID();
    CharacterEntity entity = Instancio.create(CharacterEntity.class);
    when(jpaCharacterRepository.findByUuid(uuid)).thenReturn(Optional.of(entity));

    Optional<CharacterDomain> result = adapter.findByUuid(uuid);

    assertThat(result).isPresent();
  }

  @Test
  void save_shouldCreateNewCharacterAndReturnDomain() {
    UUID uuid = UUID.randomUUID();
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    domain.setUuid(uuid);
    domain.setOwnerUsername("owner");
    domain.setCampaignId(1L);

    UserEntity owner = Instancio.create(UserEntity.class);
    CampaignEntity campaign = Instancio.create(CampaignEntity.class);
    CharacterEntity entity = Instancio.create(CharacterEntity.class);

    when(jpaCharacterRepository.findByUuid(uuid)).thenReturn(Optional.empty());
    when(jpaCampaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
    when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
    when(jpaCharacterRepository.save(any())).thenReturn(entity);

    CharacterDomain result = adapter.save(domain);

    assertThat(result).isNotNull();
    verify(jpaCharacterRepository).save(any());
  }

  @Test
  void save_shouldUpdateExistingCharacter() {
    UUID uuid = UUID.randomUUID();
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    domain.setUuid(uuid);
    domain.setCampaignId(null);

    CharacterEntity existingEntity = Instancio.create(CharacterEntity.class);

    when(jpaCharacterRepository.findByUuid(uuid)).thenReturn(Optional.of(existingEntity));
    when(jpaCharacterRepository.save(any())).thenReturn(existingEntity);

    CharacterDomain result = adapter.save(domain);

    assertThat(result).isNotNull();
    verify(jpaCharacterRepository).save(any());
  }
}
