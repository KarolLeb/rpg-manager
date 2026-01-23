package com.rpgmanager.backend.character.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.application.mapper.CharacterApplicationMapper;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.character.domain.repository.CharacterRepository;
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
class CharacterApplicationServiceTest {

  @Mock private CharacterRepository characterRepository;
  @Mock private CharacterApplicationMapper characterApplicationMapper;

  @InjectMocks private CharacterApplicationService service;

  @Test
  void getAllCharacters_shouldReturnList() {
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    CharacterResponse response = Instancio.create(CharacterResponse.class);

    when(characterRepository.findAll()).thenReturn(List.of(domain));
    when(characterApplicationMapper.toResponse(domain)).thenReturn(response);

    List<CharacterResponse> result = service.getAllCharacters();

    assertThat(result).hasSize(1);
  }

  @Test
  void getCharacter_shouldReturnResponse() {
    UUID uuid = UUID.randomUUID();
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    CharacterResponse response = Instancio.create(CharacterResponse.class);

    when(characterRepository.findByUuid(uuid)).thenReturn(Optional.of(domain));
    when(characterApplicationMapper.toResponse(domain)).thenReturn(response);

    CharacterResponse result = service.getCharacter(uuid);

    assertThat(result).isNotNull();
  }

  @Test
  void getCharacter_shouldThrowException_whenNotFound() {
    UUID uuid = UUID.randomUUID();
    when(characterRepository.findByUuid(uuid)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getCharacter(uuid))
        .isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void updateCharacter_shouldSaveAndReturnResponse() {
    UUID uuid = UUID.randomUUID();
    CharacterDomain existing = Instancio.create(CharacterDomain.class);
    CharacterDomain details = Instancio.create(CharacterDomain.class);
    CharacterResponse response = Instancio.create(CharacterResponse.class);

    when(characterRepository.findByUuid(uuid)).thenReturn(Optional.of(existing));
    when(characterRepository.save(any())).thenReturn(existing);
    when(characterApplicationMapper.toResponse(existing)).thenReturn(response);

    CharacterResponse result = service.updateCharacter(uuid, details);

    assertThat(result).isNotNull();
    verify(characterRepository).save(any());
  }

  @Test
  void joinCampaign_shouldUpdateCampaignId() {
    UUID uuid = UUID.randomUUID();
    Long campaignId = 10L;
    CharacterDomain character = Instancio.create(CharacterDomain.class);
    CharacterResponse response = Instancio.create(CharacterResponse.class);

    when(characterRepository.findByUuid(uuid)).thenReturn(Optional.of(character));
    when(characterRepository.save(character)).thenReturn(character);
    when(characterApplicationMapper.toResponse(character)).thenReturn(response);

    CharacterResponse result = service.joinCampaign(uuid, campaignId);

    assertThat(result).isNotNull();
    assertThat(character.getCampaignId()).isEqualTo(campaignId);
  }
}
