package com.rpgmanager.backend.character.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain model representing a Character. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterDomain {
  private Long id;
  private String name;
  private String race;
  private String characterClass;
  private Integer level;
  private String stats;
  private Long ownerId;
  private String ownerUsername;
  private Long controllerId;
  private String campaignName;
  private Long campaignId; // Needed for joining campaigns
  private CharacterType characterType;

  /** Type of the character. */
  public enum CharacterType {
    PERMANENT,
    TEMPORARY
  }
}
