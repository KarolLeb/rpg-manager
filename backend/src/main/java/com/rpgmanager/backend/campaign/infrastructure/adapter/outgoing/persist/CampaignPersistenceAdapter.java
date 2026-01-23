package com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist;

import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import com.rpgmanager.backend.campaign.domain.repository.CampaignRepository;
import com.rpgmanager.backend.campaign.infrastructure.mapper.CampaignPersistenceMapper;
import com.rpgmanager.backend.user.infrastructure.adapter.outgoing.persist.JpaUserRepository;
import com.rpgmanager.backend.user.infrastructure.adapter.outgoing.persist.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CampaignPersistenceAdapter implements CampaignRepository {

    private final JpaCampaignRepository jpaCampaignRepository;
    private final JpaUserRepository userRepository;

    @Override
    public List<CampaignDomain> findAll() {
        return jpaCampaignRepository.findAll().stream()
                .map(CampaignPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<CampaignDomain> findById(Long id) {
        return jpaCampaignRepository.findById(id)
                .map(CampaignPersistenceMapper::toDomain);
    }

    @Override
    public Optional<CampaignDomain> findByUuid(UUID uuid) {
        return jpaCampaignRepository.findByUuid(uuid)
                .map(CampaignPersistenceMapper::toDomain);
    }

    @Override
    public CampaignDomain save(CampaignDomain campaign) {
        UserEntity gameMaster = null;
        if (campaign.getGameMasterId() != null) {
            gameMaster = userRepository.findById(campaign.getGameMasterId())
                    .orElseThrow(() -> new RuntimeException("Game Master not found with id: " + campaign.getGameMasterId()));
        }
        
        CampaignEntity entity = CampaignPersistenceMapper.toEntity(campaign, gameMaster);
        CampaignEntity savedEntity = jpaCampaignRepository.save(entity);
        return CampaignPersistenceMapper.toDomain(savedEntity);
    }

    @Override
    public void deleteById(Long id) {
        jpaCampaignRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return jpaCampaignRepository.existsById(id);
    }
}
