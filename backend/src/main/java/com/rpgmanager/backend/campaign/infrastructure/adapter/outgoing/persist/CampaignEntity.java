package com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entity representing a Campaign in the database. */
@Entity
@Table(name = "campaigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "creation_date", nullable = false)
  private OffsetDateTime creationDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private CampaignStatus status;

  @Column(name = "game_master_id", nullable = false)
  private Long gameMasterId;

  /** Sets the creation date to the current timestamp before persisting if it's null. */
  @PrePersist
  protected void onCreate() {
    if (creationDate == null) {
      creationDate = OffsetDateTime.now();
    }
  }

  /** Status of the campaign. */
  public enum CampaignStatus {
    ACTIVE,
    FINISHED,
    ARCHIVED
  }
}
