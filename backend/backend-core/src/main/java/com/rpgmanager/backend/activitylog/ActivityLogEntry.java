package com.rpgmanager.backend.activitylog;

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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Entity representing a user action logged during an RPG session. */
@Entity
@Table(name = "activity_log")
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActivityLogEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "session_id")
  private Long sessionId;

  @Column(name = "campaign_id")
  private Long campaignId;

  @Column(name = "user_id")
  private Long userId;

  @Enumerated(EnumType.STRING)
  @Column(name = "action_type", nullable = false, length = 50)
  private ActionType actionType;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String description;

  @Column(columnDefinition = "JSONB")
  private String metadata;

  @Column(columnDefinition = "vector(384)")
  private float[] embedding;

  @Column(name = "created_at")
  private OffsetDateTime createdAt;

  /** Sets creation timestamp before persisting. */
  @PrePersist
  protected void onCreate() {
    if (createdAt == null) {
      createdAt = OffsetDateTime.now();
    }
  }

  /** Types of actions tracked in the activity log. */
  public enum ActionType {
    DICE_ROLL,
    CHARACTER_UPDATE,
    NOTE_ADDED,
    SESSION_START,
    SESSION_END,
    CAMPAIGN_ACTION
  }
}
