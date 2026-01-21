package com.rpgmanager.backend.campaign.application.dto;

import com.rpgmanager.backend.campaign.domain.model.CampaignDomain;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDTO {
    private Long id;
    private UUID uuid;
    private String name;
    private String description;
    private OffsetDateTime creationDate;
    private CampaignDomain.CampaignStatus status;
    private Long gameMasterId;
    private String gameMasterName;
}