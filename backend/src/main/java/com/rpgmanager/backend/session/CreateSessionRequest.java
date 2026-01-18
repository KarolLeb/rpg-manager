package com.rpgmanager.backend.session;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSessionRequest {
    private Long campaignId;
    private String name;
    private String description;
    private OffsetDateTime sessionDate;
}
