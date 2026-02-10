package com.rpgmanager.backend.character.infrastructure.adapter.outgoing.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.JpaCampaignRepository;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.user.domain.model.UserDomain;
import com.rpgmanager.backend.user.domain.repository.UserRepositoryPort;
import java.util.List;
import java.util.Optional;
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
  @Mock private UserRepositoryPort userRepository;

  @InjectMocks private CharacterPersistenceAdapter adapter;

  @Test
  void findAll_shouldReturnList() {
    CharacterEntity entity = Instancio.create(CharacterEntity.class);
    UserDomain user = Instancio.create(UserDomain.class);
    when(jpaCharacterRepository.findAll()).thenReturn(List.of(entity));
    when(userRepository.findById(entity.getUserId())).thenReturn(Optional.of(user));

    List<CharacterDomain> result = adapter.findAll();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getOwnerUsername()).isEqualTo(user.getUsername());
  }

  @Test
  void findByUuid_shouldReturnDomain() {
    Long id = 1L;
    CharacterEntity entity = Instancio.create(CharacterEntity.class);
    UserDomain user = Instancio.create(UserDomain.class);
    when(jpaCharacterRepository.findById(id)).thenReturn(Optional.of(entity));
    when(userRepository.findById(entity.getUserId())).thenReturn(Optional.of(user));

    Optional<CharacterDomain> result = adapter.findById(id);

    assertThat(result).isPresent();
    assertThat(result.get().getOwnerUsername()).isEqualTo(user.getUsername());
  }

  @Test
  void save_shouldCreateNewCharacterAndReturnDomain() {
    Long id = 1L;
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    domain.setId(id);
    domain.setOwnerId(null);
    domain.setOwnerUsername("owner");
    domain.setCampaignId(1L);

    UserDomain owner = Instancio.create(UserDomain.class);
    owner.setId(10L);
    owner.setUsername("owner");

    CampaignEntity campaign = Instancio.create(CampaignEntity.class);
    CharacterEntity entity = Instancio.create(CharacterEntity.class);
    entity.setUserId(10L);

    when(jpaCharacterRepository.findById(id)).thenReturn(Optional.empty());
    when(jpaCampaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
    when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
    when(jpaCharacterRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(userRepository.findById(10L)).thenReturn(Optional.of(owner));

    CharacterDomain result = adapter.save(domain);

    assertThat(result).isNotNull();
    assertThat(result.getOwnerUsername()).isEqualTo("owner");
    assertThat(result.getOwnerId()).isEqualTo(10L);
    verify(jpaCharacterRepository)
        .save(
            argThat(
                entityArg -> {
                  assertThat(entityArg.getUserId()).isEqualTo(10L);
                  assertThat(entityArg.getCampaign()).isEqualTo(campaign);
                  return true;
                }));
  }

  @Test
  void save_shouldCreateNewCharacter_whenIdIsNull() {
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    domain.setId(null);
    domain.setOwnerId(null);
    domain.setOwnerUsername("owner");
    domain.setCampaignId(1L);

    UserDomain owner = Instancio.create(UserDomain.class);
    owner.setId(10L);
    owner.setUsername("owner");

    CampaignEntity campaign = Instancio.create(CampaignEntity.class);

    when(jpaCampaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
    when(userRepository.findByUsername("owner")).thenReturn(Optional.of(owner));
    when(jpaCharacterRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(userRepository.findById(10L)).thenReturn(Optional.of(owner));

    CharacterDomain result = adapter.save(domain);

    assertThat(result).isNotNull();
    assertThat(result.getOwnerId()).isEqualTo(10L);
    verify(jpaCharacterRepository, never()).findById(any());
    verify(jpaCharacterRepository).save(any());
  }

  @Test
  void save_shouldCreateNewCharacter_whenIdNotFound_andOwnerIdProvided() {
    Long id = 999L;
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    domain.setId(id);
    domain.setOwnerId(10L); // Provided
    domain.setCampaignId(1L);

    CampaignEntity campaign = Instancio.create(CampaignEntity.class);
    UserDomain owner = Instancio.create(UserDomain.class);
    owner.setId(10L);
    owner.setUsername("owner");

    when(jpaCharacterRepository.findById(id)).thenReturn(Optional.empty());
    when(jpaCampaignRepository.findById(1L)).thenReturn(Optional.of(campaign));
    // Should NOT look up user by username because ownerId is present
    when(jpaCharacterRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(userRepository.findById(10L)).thenReturn(Optional.of(owner));

    CharacterDomain result = adapter.save(domain);

    assertThat(result).isNotNull();
    assertThat(result.getOwnerId()).isEqualTo(10L);
    verify(userRepository, never()).findByUsername(any());
    verify(jpaCharacterRepository).save(any());
  }

  @Test
  void save_shouldUpdateExistingCharacter() {
    Long id = 1L;
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    domain.setId(id);
    domain.setCampaignId(2L);
    domain.setOwnerId(10L);
    domain.setControllerId(20L);
    domain.setCharacterType(CharacterDomain.CharacterType.TEMPORARY);

    CharacterEntity existingEntity = new CharacterEntity();
    existingEntity.setId(id);
    existingEntity.setUserId(10L);

    CampaignEntity campaign = new CampaignEntity();
    campaign.setId(2L);

    UserDomain owner = Instancio.create(UserDomain.class);
    owner.setUsername("owner");

    when(jpaCharacterRepository.findById(id)).thenReturn(Optional.of(existingEntity));
    when(jpaCampaignRepository.findById(2L)).thenReturn(Optional.of(campaign));
    when(jpaCharacterRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
    when(userRepository.findById(10L)).thenReturn(Optional.of(owner));

    CharacterDomain result = adapter.save(domain);

    assertThat(result).isNotNull();
    assertThat(result.getOwnerUsername()).isEqualTo("owner");
    verify(jpaCharacterRepository)
        .save(
            argThat(
                entityArg -> {
                  assertThat(entityArg.getControllerId()).isEqualTo(20L);
                  assertThat(entityArg.getCharacterType())
                      .isEqualTo(CharacterEntity.CharacterType.TEMPORARY);
                  assertThat(entityArg.getCampaign()).isEqualTo(campaign);
                  return true;
                }));
  }

  @Test
  void save_shouldThrowException_whenCampaignNotFound() {
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    domain.setCampaignId(999L);
    when(jpaCharacterRepository.findById(any())).thenReturn(Optional.empty());
    when(jpaCampaignRepository.findById(999L)).thenReturn(Optional.empty());

    RuntimeException exception =
        org.junit.jupiter.api.Assertions.assertThrows(
            RuntimeException.class, () -> adapter.save(domain));

    assertThat(exception.getMessage()).isEqualTo("Campaign not found with id: 999");
  }

  @Test
  void save_shouldSaveCharacter_whenCampaignIdIsNull() {
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    domain.setCampaignId(null);
    domain.setId(null); // Create new

    // Ensure owner logic doesn't interfere
    domain.setOwnerId(null);
    domain.setOwnerUsername(null);

    when(jpaCharacterRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

    CharacterDomain result = adapter.save(domain);

    assertThat(result).isNotNull();
    verify(jpaCampaignRepository, never()).findById(any());
    verify(jpaCharacterRepository).save(any());
  }
}
