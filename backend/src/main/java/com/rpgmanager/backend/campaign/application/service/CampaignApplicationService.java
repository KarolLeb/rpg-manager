package com.rpgmanager.backend.campaign.application.service;

import com.rpgmanager.backend.campaign.application.dto.CampaignDTO;
import com.rpgmanager.backend.campaign.application.dto.CreateCampaignRequest;
import com.rpgmanager.backend.campaign.application.mapper.CampaignApplicationMapper;
import com.rpgmanager.backend.campaign.application.port.in.CreateCampaignUseCase;
import com.rpgmanager.backend.campaign.application.port.in.DeleteCampaignUseCase;
import com.rpgmanager.backend.campaign.application.port.in.GetCampaignUseCase;
import com.rpgmanager.backend.campaign.application.port.in.UpdateCampaignUseCase;
import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import com.rpgmanager.backend.campaign.domain.repository.CampaignRepository;
import com.rpgmanager.backend.user.domain.model.UserDomain;
import com.rpgmanager.backend.user.domain.repository.UserRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignApplicationService implements CreateCampaignUseCase, GetCampaignUseCase, UpdateCampaignUseCase, DeleteCampaignUseCase {

    private final CampaignRepository campaignRepository;
    private final UserRepositoryPort userRepository;
    private final CampaignApplicationMapper campaignApplicationMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CampaignDTO> getAllCampaigns() {
        return campaignRepository.findAll().stream()
                .map(campaignApplicationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CampaignDTO getCampaignById(Long id) {
        CampaignDomain campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
        return campaignApplicationMapper.toDTO(campaign);
    }

    @Override
    @Transactional
    @CacheEvict(value = "campaigns", allEntries = true)
    public CampaignDTO createCampaign(CreateCampaignRequest request) {
        UserDomain gameMaster = userRepository.findById(request.getGameMasterId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getGameMasterId()));

        CampaignDomain campaign = CampaignDomain.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(CampaignDomain.CampaignStatus.ACTIVE)
                .creationDate(OffsetDateTime.now())
                .uuid(UUID.randomUUID())
                .gameMasterId(gameMaster.getId())
                .gameMasterName(gameMaster.getUsername())
                .build();

        CampaignDomain savedCampaign = campaignRepository.save(campaign);
        return campaignApplicationMapper.toDTO(savedCampaign);
    }

    @Override
    @Transactional
    @CacheEvict(value = "campaigns", allEntries = true)
    public CampaignDTO updateCampaign(Long id, CreateCampaignRequest request) {
        CampaignDomain campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));

        campaign.setName(request.getName());
        campaign.setDescription(request.getDescription());

        if (request.getGameMasterId() != null && !request.getGameMasterId().equals(campaign.getGameMasterId())) {
            UserDomain newGameMaster = userRepository.findById(request.getGameMasterId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getGameMasterId()));
            campaign.setGameMasterId(newGameMaster.getId());
            campaign.setGameMasterName(newGameMaster.getUsername());
        }

        return campaignApplicationMapper.toDTO(campaignRepository.save(campaign));
    }

    @Override
    @Transactional
    @CacheEvict(value = "campaigns", allEntries = true)
    public void deleteCampaign(Long id) {
        if (!campaignRepository.existsById(id)) {
            throw new RuntimeException("Campaign not found with id: " + id);
        }
        campaignRepository.deleteById(id);
    }
}
