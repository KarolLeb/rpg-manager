package com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
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
class CampaignPersistenceAdapterTest {

  @Mock private JpaCampaignRepository jpaCampaignRepository;
  @Mock private UserRepositoryPort userRepository;

  @InjectMocks private CampaignPersistenceAdapter adapter;

  @Test
  void findAll_shouldReturnList() {
    CampaignEntity entity = Instancio.create(CampaignEntity.class);
    UserDomain user = Instancio.create(UserDomain.class);
    when(jpaCampaignRepository.findAll()).thenReturn(List.of(entity));
    when(userRepository.findById(entity.getGameMasterId())).thenReturn(Optional.of(user));

    List<CampaignDomain> result = adapter.findAll();

    assertThat(result).hasSize(1);
    assertThat(result.get(0).getGameMasterName()).isEqualTo(user.getUsername());
    verify(jpaCampaignRepository).findAll();
  }

  @Test
  void findById_shouldReturnDomain() {
    CampaignEntity entity = Instancio.create(CampaignEntity.class);
    UserDomain user = Instancio.create(UserDomain.class);
    when(jpaCampaignRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(userRepository.findById(entity.getGameMasterId())).thenReturn(Optional.of(user));

    Optional<CampaignDomain> result = adapter.findById(1L);

    assertThat(result).isPresent();
    assertThat(result.get().getGameMasterName()).isEqualTo(user.getUsername());
    verify(jpaCampaignRepository).findById(1L);
  }

  @Test
  void save_shouldSaveAndReturnDomain() {
    CampaignDomain domain = Instancio.create(CampaignDomain.class);
    domain.setGameMasterId(1L);
    UserDomain user = Instancio.create(UserDomain.class);
    CampaignEntity entity = Instancio.create(CampaignEntity.class);
    entity.setGameMasterId(1L);

    when(jpaCampaignRepository.save(any())).thenReturn(entity);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    CampaignDomain result = adapter.save(domain);

    assertThat(result).isNotNull();
    assertThat(result.getGameMasterName()).isEqualTo(user.getUsername());
    verify(jpaCampaignRepository).save(any());
  }

  @Test
  void deleteById_shouldCallRepository() {
    adapter.deleteById(1L);
    verify(jpaCampaignRepository).deleteById(1L);
  }

  @Test
  void existsById_shouldReturnBoolean() {
    when(jpaCampaignRepository.existsById(1L)).thenReturn(true);
    when(jpaCampaignRepository.existsById(2L)).thenReturn(false);

    assertThat(adapter.existsById(1L)).isTrue();
    assertThat(adapter.existsById(2L)).isFalse();
  }

  @Test
  void enrichWithGameMasterName_shouldSkipWhenGameMasterIdIsNull() {
    CampaignEntity entity = Instancio.create(CampaignEntity.class);
    entity.setGameMasterId(null);
    when(jpaCampaignRepository.findById(1L)).thenReturn(Optional.of(entity));

    Optional<CampaignDomain> result = adapter.findById(1L);

    assertThat(result).isPresent();
    assertThat(result.get().getGameMasterName()).isNull();
    verifyNoInteractions(userRepository);
  }

  @Test
  void enrichWithGameMasterName_shouldSkipWhenUserNotFound() {
    CampaignEntity entity = Instancio.create(CampaignEntity.class);
    when(jpaCampaignRepository.findById(1L)).thenReturn(Optional.of(entity));
    when(userRepository.findById(entity.getGameMasterId())).thenReturn(Optional.empty());

    Optional<CampaignDomain> result = adapter.findById(1L);

    assertThat(result).isPresent();
    assertThat(result.get().getGameMasterName()).isNull();
    verify(userRepository).findById(entity.getGameMasterId());
  }
}
