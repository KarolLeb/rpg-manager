package com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  @PrePersist
  protected void onCreate() {
    if (creationDate == null) {
      creationDate = OffsetDateTime.now();
    }
  }

  public enum CampaignStatus {
    ACTIVE,
    FINISHED,
    ARCHIVED
  }
}
