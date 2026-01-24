package com.rpgmanager.backend.session;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Session {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "campaign_id")
  private CampaignEntity campaign;

  @Column(nullable = false)
  private String name;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "session_date", nullable = false)
  private OffsetDateTime sessionDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private SessionStatus status;

  @PrePersist
  protected void onCreate() {
    if (sessionDate == null) {
      sessionDate = OffsetDateTime.now();
    }
  }

  public enum SessionStatus {
    ACTIVE,
    FINISHED,
    CANCELLED
  }
}
