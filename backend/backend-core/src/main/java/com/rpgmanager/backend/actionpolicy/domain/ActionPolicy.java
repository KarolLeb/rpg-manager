package com.rpgmanager.backend.actionpolicy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionPolicy {
  private Long id;
  private ActionType actionType;
  private ContextType contextType;
  private Long contextId;
  private boolean isAllowed;
}
