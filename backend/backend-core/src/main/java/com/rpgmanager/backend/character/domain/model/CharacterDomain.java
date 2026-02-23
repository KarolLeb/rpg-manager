package com.rpgmanager.backend.character.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.OffsetDateTime;

/** Domain model representing a Character. */
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

  public Long getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getCharacterClass() {
    return characterClass;
  }

  public Integer getLevel() {
    return level;
  }

  public String getStats() {
    return stats;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public String getOwnerUsername() {
    return ownerUsername;
  }

  public Long getControllerId() {
    return controllerId;
  }

  public String getCampaignName() {
    return campaignName;
  }

  public Long getCampaignId() {
    return campaignId;
  }

  public CharacterType getCharacterType() {
    return characterType;
  }

  /** Type of the character. */
  public enum CharacterType {
    PERMANENT,
    TEMPORARY
  }
}
