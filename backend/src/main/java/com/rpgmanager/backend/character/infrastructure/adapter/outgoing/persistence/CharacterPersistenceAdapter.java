package com.rpgmanager.backend.character.infrastructure.adapter.outgoing.persistence;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.JpaCampaignRepository;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.character.domain.repository.CharacterRepository;
import com.rpgmanager.backend.character.infrastructure.mapper.CharacterPersistenceMapper;
import com.rpgmanager.backend.user.User;
import com.rpgmanager.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CharacterPersistenceAdapter implements CharacterRepository {

    private final JpaCharacterRepository jpaCharacterRepository;
    private final JpaCampaignRepository jpaCampaignRepository;
    private final UserRepository userRepository;

    @Override
    public List<CharacterDomain> findAll() {
        return jpaCharacterRepository.findAll().stream()
                .map(CharacterPersistenceMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CharacterDomain> findByUuid(UUID uuid) {
        return jpaCharacterRepository.findByUuid(uuid)
                .map(CharacterPersistenceMapper::toDomain);
    }

    @Override
    public CharacterDomain save(CharacterDomain domain) {
        CharacterEntity entity = jpaCharacterRepository.findByUuid(domain.getUuid())
                .orElse(null);

        CampaignEntity campaign = null;
        if (domain.getCampaignId() != null) {
            campaign = jpaCampaignRepository.findById(domain.getCampaignId())
                    .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + domain.getCampaignId()));
        }

        if (entity != null) {
            // Update
            CharacterPersistenceMapper.updateEntity(entity, domain, campaign);
        } else {
            // Create New
            User owner = null;
            if (domain.getOwnerUsername() != null) {
                owner = userRepository.findByUsername(domain.getOwnerUsername())
                        .orElseThrow(() -> new RuntimeException("User not found: " + domain.getOwnerUsername()));
            }
            entity = CharacterPersistenceMapper.toEntity(domain, owner, campaign);
        }

        CharacterEntity savedEntity = jpaCharacterRepository.save(entity);
        return CharacterPersistenceMapper.toDomain(savedEntity);
    }
}