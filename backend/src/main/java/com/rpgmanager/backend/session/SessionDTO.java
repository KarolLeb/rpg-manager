package com.rpgmanager.backend.session;

import com.rpgmanager.backend.session.Session.SessionStatus;
import lombok.Builder;
import lombok.Data;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
public class SessionDTO {
    private Long id;
    private UUID uuid;
    private Long campaignId;
    private String campaignName;
    private String name;
    private String description;
    private OffsetDateTime sessionDate;
    private SessionStatus status;
}
