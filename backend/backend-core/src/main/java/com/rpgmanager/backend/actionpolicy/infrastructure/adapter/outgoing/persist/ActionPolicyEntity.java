package com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist;

import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "action_policies", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "action_type", "context_type", "context_id" })
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionPolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", nullable = false)
    private ActionType actionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "context_type", nullable = false)
    private ContextType contextType;

    @Column(name = "context_id", nullable = true)
    private Long contextId;

    @Column(name = "is_allowed", nullable = false)
    private boolean isAllowed;
}
