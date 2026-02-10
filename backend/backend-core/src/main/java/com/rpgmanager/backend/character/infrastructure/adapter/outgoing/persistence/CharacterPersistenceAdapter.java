package com.rpgmanager.backend.character.infrastructure.adapter.outgoing.persistence;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.JpaCampaignRepository;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.character.domain.repository.CharacterRepository;
import com.rpgmanager.backend.character.infrastructure.mapper.CharacterPersistenceMapper;
import com.rpgmanager.backend.user.domain.repository.UserRepositoryPort;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Adapter implementation for Character persistence. */
@Component
@RequiredArgsConstructor
public class CharacterPersistenceAdapter implements CharacterRepository {

  private final JpaCharacterRepository jpaCharacterRepository;
  private final JpaCampaignRepository jpaCampaignRepository;
  private final UserRepositoryPort userRepository;

  /**
   * Retrieves all characters.
   *
   * @return a list of all characters
   */
  @Override
  public List<CharacterDomain> findAll() {
    return jpaCharacterRepository.findAll().stream()
        .map(CharacterPersistenceMapper::toDomain)
        .map(this::enrichWithUserData)
        .toList();
  }

  /**
   * Finds a character by ID.
   *
   * @param id the character ID
   * @return an optional containing the character if found
   */
  @Override
  public Optional<CharacterDomain> findById(Long id) {
    return jpaCharacterRepository
        .findById(id)
        .map(CharacterPersistenceMapper::toDomain)
        .map(this::enrichWithUserData);
  }

  /**
   * Saves a character.
   *
   * @param domain the character domain object to save
   * @return the saved character domain object
   */
  @Override
  public CharacterDomain save(CharacterDomain domain) {
    CharacterEntity entity = null;
    if (domain.getId() != null) {
      entity = jpaCharacterRepository.findById(domain.getId()).orElse(null);
    }

    CampaignEntity campaign = null;
    if (domain.getCampaignId() != null) {
      campaign =
          jpaCampaignRepository
              .findById(domain.getCampaignId())
              .orElseThrow(
                  () ->
                      new RuntimeException(
                          "Campaign not found with id: " + domain.getCampaignId()));
    }

    if (entity != null) {
      // Update
      CharacterPersistenceMapper.updateEntity(entity, domain, campaign);
    } else {
      // Create New
      if (domain.getOwnerId() == null && domain.getOwnerUsername() != null) {
        userRepository
            .findByUsername(domain.getOwnerUsername())
            .ifPresent(user -> domain.setOwnerId(user.getId()));
      }
      entity = CharacterPersistenceMapper.toEntity(domain, campaign);
    }

    CharacterEntity savedEntity = jpaCharacterRepository.save(entity);
    return enrichWithUserData(CharacterPersistenceMapper.toDomain(savedEntity));
  }

  private CharacterDomain enrichWithUserData(CharacterDomain character) {
    if (character != null && character.getOwnerId() != null) {
      userRepository
          .findById(character.getOwnerId())
          .ifPresent(user -> character.setOwnerUsername(user.getUsername()));
    }
    return character;
  }
}
