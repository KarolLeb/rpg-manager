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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class CharacterApplicationServiceTest {

  @Mock private CharacterRepository characterRepository;
  @Mock private CharacterApplicationMapper characterApplicationMapper;
  @Mock private org.springframework.context.ApplicationEventPublisher eventPublisher;

  @InjectMocks private CharacterApplicationService service;

  @BeforeEach
  void setUp() {
    mockSecurityContext(1L, "GM");
  }

  @AfterEach
  void tearDown() {
    SecurityContextHolder.clearContext();
  }

  private void mockSecurityContext(Long userId, String role) {
    SecurityContext securityContext = mock(SecurityContext.class);
    Authentication authentication = mock(Authentication.class);
    com.rpgmanager.common.security.UserContext userContext =
        new com.rpgmanager.common.security.UserContext(
            "user",
            "pass",
            java.util.List.of(
                new org.springframework.security.core.authority.SimpleGrantedAuthority(
                    "ROLE_" + role)),
            userId);

    lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
    lenient().when(authentication.getPrincipal()).thenReturn(userContext);
    SecurityContextHolder.setContext(securityContext);
  }

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
    existing.setOwnerId(1L); // Matches mocked security context
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
    verify(eventPublisher)
        .publishEvent(any(com.rpgmanager.backend.activitylog.ActivityEvent.class));
  }

  @Test
  void joinCampaign_shouldUpdateCampaignId() {
    Long id = 1L;
    Long campaignId = 10L;
    CharacterDomain character = Instancio.create(CharacterDomain.class);
    character.setOwnerId(1L); // Matches mocked security context
    CharacterResponse response = Instancio.create(CharacterResponse.class);

    when(characterRepository.findById(id)).thenReturn(Optional.of(character));
    when(characterRepository.save(character)).thenReturn(character);
    when(characterApplicationMapper.toResponse(character)).thenReturn(response);

    CharacterResponse result = service.joinCampaign(id, campaignId);

    assertThat(result).isNotNull();
    assertThat(character.getCampaignId()).isEqualTo(campaignId);
    verify(eventPublisher)
        .publishEvent(any(com.rpgmanager.backend.activitylog.ActivityEvent.class));
  }

  @Test
  void updateCharacter_shouldThrowAccessDenied_whenUserUnauthorized() {
    mockSecurityContext(99L, "PLAYER"); // Different user
    Long id = 1L;
    CharacterDomain character = Instancio.create(CharacterDomain.class);
    character.setOwnerId(1L);
    character.setControllerId(2L);

    when(characterRepository.findById(id)).thenReturn(Optional.of(character));

    assertThatThrownBy(() -> service.updateCharacter(id, character))
        .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
  }

  @Test
  void joinCampaign_shouldThrowAccessDenied_whenUserUnauthorized() {
    mockSecurityContext(99L, "PLAYER");
    Long id = 1L;
    CharacterDomain character = Instancio.create(CharacterDomain.class);
    character.setOwnerId(1L);

    when(characterRepository.findById(id)).thenReturn(Optional.of(character));

    assertThatThrownBy(() -> service.joinCampaign(id, 10L))
        .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
  }

  @Test
  void updateCharacter_shouldAllowAdmin() {
    mockSecurityContext(99L, "ADMIN");
    Long id = 1L;
    CharacterDomain existing = Instancio.create(CharacterDomain.class);
    existing.setOwnerId(1L);
    CharacterDomain details = Instancio.create(CharacterDomain.class);

    when(characterRepository.findById(id)).thenReturn(Optional.of(existing));
    when(characterRepository.save(any())).thenReturn(existing);

    service.updateCharacter(id, details);

    verify(characterRepository).save(any());
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
