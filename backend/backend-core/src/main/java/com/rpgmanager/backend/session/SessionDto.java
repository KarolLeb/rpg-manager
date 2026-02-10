package com.rpgmanager.backend.session;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Data Transfer Object for Session information. */
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionDto {
  private Long id;
  private Long campaignId;
  private String campaignName;
  private String name;
  private String description;
  private OffsetDateTime sessionDate;
  private Session.SessionStatus status;
}
