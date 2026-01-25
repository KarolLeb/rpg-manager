package com.rpgmanager.backend.character.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDomain {
  private Long id;
  private String name;
  private String characterClass;
  private Integer level;
  private String stats;
  private Long ownerId;
  private String ownerUsername;
  private Long controllerId;
  private String campaignName;
  private Long campaignId; // Needed for joining campaigns
  private CharacterType characterType;

  public enum CharacterType {
    PERMANENT,
    TEMPORARY
  }
}
