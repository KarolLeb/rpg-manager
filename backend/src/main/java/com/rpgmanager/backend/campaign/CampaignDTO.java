package com.rpgmanager.backend.campaign;

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
    private Campaign.CampaignStatus status;
    private Long gameMasterId;
    private String gameMasterName;
}
