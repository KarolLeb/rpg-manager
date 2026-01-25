package com.rpgmanager.backend.session;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entity representing a Game Session in the database. */
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

  /** Sets the session date to current timestamp if null before persisting. */
  @PrePersist
  protected void onCreate() {
    if (sessionDate == null) {
      sessionDate = OffsetDateTime.now();
    }
  }

  /** Status of the session. */
  public enum SessionStatus {
    ACTIVE,
    FINISHED,
    CANCELLED
  }
}
