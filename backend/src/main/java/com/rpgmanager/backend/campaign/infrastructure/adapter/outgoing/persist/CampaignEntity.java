package com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist;

import com.rpgmanager.backend.user.infrastructure.adapter.outgoing.persist.UserEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;
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

  @Column(nullable = false, unique = true)
  private UUID uuid;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "creation_date", nullable = false)
  private OffsetDateTime creationDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private CampaignStatus status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_master_id", nullable = false)
  private UserEntity gameMaster;

  @PrePersist
  protected void onCreate() {
    if (creationDate == null) {
      creationDate = OffsetDateTime.now();
    }
    if (uuid == null) {
      uuid = UUID.randomUUID();
    }
  }

  public enum CampaignStatus {
    ACTIVE,
    FINISHED,
    ARCHIVED
  }
}
