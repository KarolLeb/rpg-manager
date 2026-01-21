package com.rpgmanager.backend.character.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDomain {
    private UUID uuid;
    private String name;
    private String characterClass;
    private Integer level;
    private String stats;
    private String ownerUsername;
    private String campaignName;
    private Long campaignId; // Needed for joining campaigns
    private CharacterType characterType;

    public enum CharacterType {
        PERMANENT, TEMPORARY
    }
}
