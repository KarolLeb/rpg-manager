package com.rpgmanager.backend.campaign;

import com.rpgmanager.backend.user.User;
import com.rpgmanager.backend.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<CampaignDTO> getAllCampaigns() {
        return campaignRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CampaignDTO getCampaignById(Long id) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
        return toDTO(campaign);
    }

    @Transactional
    public CampaignDTO createCampaign(CreateCampaignRequest request) {
        User gameMaster = userRepository.findById(request.getGameMasterId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getGameMasterId()));

        Campaign campaign = Campaign.builder()
                .name(request.getName())
                .description(request.getDescription())
                .status(Campaign.CampaignStatus.ACTIVE)
                .gameMaster(gameMaster)
                .build();

        Campaign savedCampaign = campaignRepository.save(campaign);
        return toDTO(savedCampaign);
    }
    
    @Transactional
    public CampaignDTO updateCampaign(Long id, CreateCampaignRequest request) {
        Campaign campaign = campaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Campaign not found with id: " + id));
        
        campaign.setName(request.getName());
        campaign.setDescription(request.getDescription());
        
        if (request.getGameMasterId() != null && !request.getGameMasterId().equals(campaign.getGameMaster().getId())) {
             User newGameMaster = userRepository.findById(request.getGameMasterId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + request.getGameMasterId()));
             campaign.setGameMaster(newGameMaster);
        }

        return toDTO(campaignRepository.save(campaign));
    }

    @Transactional
    public void deleteCampaign(Long id) {
        if (!campaignRepository.existsById(id)) {
            throw new RuntimeException("Campaign not found with id: " + id);
        }
        campaignRepository.deleteById(id);
    }

    private CampaignDTO toDTO(Campaign campaign) {
        return CampaignDTO.builder()
                .id(campaign.getId())
                .uuid(campaign.getUuid())
                .name(campaign.getName())
                .description(campaign.getDescription())
                .creationDate(campaign.getCreationDate())
                .status(campaign.getStatus())
                .gameMasterId(campaign.getGameMaster().getId())
                .gameMasterName(campaign.getGameMaster().getUsername())
                .build();
    }
}
