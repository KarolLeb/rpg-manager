package com.rpgmanager.backend.session;

import com.rpgmanager.backend.campaign.Campaign;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.OffsetDateTime;
import java.util.UUID;

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

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

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
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public enum SessionStatus {
        ACTIVE, FINISHED
    }
}