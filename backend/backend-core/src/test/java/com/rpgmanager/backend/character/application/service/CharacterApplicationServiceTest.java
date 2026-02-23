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
  @Mock private org.springframework.context.ApplicationEventPublisher eventPublisher;

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
    Long id = 1L;
    CharacterDomain domain = Instancio.create(CharacterDomain.class);
    CharacterResponse response = Instancio.create(CharacterResponse.class);

    when(characterRepository.findById(id)).thenReturn(Optional.of(domain));
    when(characterApplicationMapper.toResponse(domain)).thenReturn(response);

    CharacterResponse result = service.getCharacter(id);

    assertThat(result).isNotNull();
  }

  @Test
  void getCharacter_shouldThrowException_whenNotFound() {
    Long id = 1L;
    when(characterRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getCharacter(id)).isInstanceOf(IllegalArgumentException.class);
  }

  @Test
  void updateCharacter_shouldSaveAndReturnResponse() {
    Long id = 1L;
    CharacterDomain existing = Instancio.create(CharacterDomain.class);
    CharacterDomain details = Instancio.create(CharacterDomain.class);
    CharacterResponse response = Instancio.create(CharacterResponse.class);

    when(characterRepository.findById(id)).thenReturn(Optional.of(existing));
    when(characterRepository.save(any())).thenReturn(existing);
    when(characterApplicationMapper.toResponse(existing)).thenReturn(response);

    CharacterResponse result = service.updateCharacter(id, details);

    assertThat(result).isNotNull();
    verify(characterRepository)
        .save(
            argThat(
                c -> {
                  assertThat(c.getName()).isEqualTo(details.getName());
                  assertThat(c.getCharacterClass()).isEqualTo(details.getCharacterClass());
                  assertThat(c.getLevel()).isEqualTo(details.getLevel());
                  assertThat(c.getStats()).isEqualTo(details.getStats());
                  return true;
                }));
  }

  @Test
  void joinCampaign_shouldUpdateCampaignId() {
    Long id = 1L;
    Long campaignId = 10L;
    CharacterDomain character = Instancio.create(CharacterDomain.class);
    CharacterResponse response = Instancio.create(CharacterResponse.class);

    when(characterRepository.findById(id)).thenReturn(Optional.of(character));
    when(characterRepository.save(character)).thenReturn(character);
    when(characterApplicationMapper.toResponse(character)).thenReturn(response);

    CharacterResponse result = service.joinCampaign(id, campaignId);

    assertThat(result).isNotNull();
    assertThat(character.getCampaignId()).isEqualTo(campaignId);
  }

  @Test
  void updateCharacter_shouldThrowException_whenNotFound() {
    Long id = 1L;
    CharacterDomain details = Instancio.create(CharacterDomain.class);
    when(characterRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.updateCharacter(id, details))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Character not found with id: " + id);
  }

  @Test
  void joinCampaign_shouldThrowException_whenNotFound() {
    Long id = 1L;
    Long campaignId = 10L;
    when(characterRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.joinCampaign(id, campaignId))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Character not found with id: " + id);
  }
}
