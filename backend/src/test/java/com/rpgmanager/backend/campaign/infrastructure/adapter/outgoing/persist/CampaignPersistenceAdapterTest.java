package com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import com.rpgmanager.backend.user.infrastructure.adapter.outgoing.persist.JpaUserRepository;
import com.rpgmanager.backend.user.infrastructure.adapter.outgoing.persist.UserEntity;
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
  @Mock private JpaUserRepository userRepository;

  @InjectMocks private CampaignPersistenceAdapter adapter;

  @Test
  void findAll_shouldReturnList() {
    CampaignEntity entity = Instancio.create(CampaignEntity.class);
    when(jpaCampaignRepository.findAll()).thenReturn(List.of(entity));

    List<CampaignDomain> result = adapter.findAll();

    assertThat(result).hasSize(1);
    verify(jpaCampaignRepository).findAll();
  }

  @Test
  void findById_shouldReturnDomain() {
    CampaignEntity entity = Instancio.create(CampaignEntity.class);
    when(jpaCampaignRepository.findById(1L)).thenReturn(Optional.of(entity));

    Optional<CampaignDomain> result = adapter.findById(1L);

    assertThat(result).isPresent();
    verify(jpaCampaignRepository).findById(1L);
  }

  @Test
  void save_shouldSaveAndReturnDomain() {
    CampaignDomain domain = Instancio.create(CampaignDomain.class);
    domain.setGameMasterId(1L);
    UserEntity gm = Instancio.create(UserEntity.class);
    CampaignEntity entity = Instancio.create(CampaignEntity.class);

    when(userRepository.findById(1L)).thenReturn(Optional.of(gm));
    when(jpaCampaignRepository.save(any())).thenReturn(entity);

    CampaignDomain result = adapter.save(domain);

    assertThat(result).isNotNull();
    verify(jpaCampaignRepository).save(any());
  }

  @Test
  void save_shouldThrowException_whenGMNotFound() {
    CampaignDomain domain = Instancio.create(CampaignDomain.class);
    domain.setGameMasterId(1L);

    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> adapter.save(domain))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Game Master not found");
  }

  @Test
  void deleteById_shouldCallRepository() {
    adapter.deleteById(1L);
    verify(jpaCampaignRepository).deleteById(1L);
  }

  @Test
  void existsById_shouldReturnBoolean() {
    when(jpaCampaignRepository.existsById(1L)).thenReturn(true);
    assertThat(adapter.existsById(1L)).isTrue();
  }
}
