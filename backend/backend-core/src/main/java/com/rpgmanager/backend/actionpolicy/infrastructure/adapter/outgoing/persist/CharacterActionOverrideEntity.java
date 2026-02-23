package com.rpgmanager.backend.actionpolicy.infrastructure.adapter.outgoing.persist;

import com.rpgmanager.backend.actionpolicy.domain.ActionType;
import com.rpgmanager.backend.actionpolicy.domain.ContextType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "character_action_overrides", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "character_id", "action_type", "context_type", "context_id" })
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterActionOverrideEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "character_id", nullable = false)
    private Long characterId;

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
