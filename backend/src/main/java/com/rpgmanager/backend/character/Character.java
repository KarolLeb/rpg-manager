package com.rpgmanager.backend.character;

import com.rpgmanager.backend.campaign.Campaign;
import com.rpgmanager.backend.user.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.util.UUID;

@Entity
@Table(name = "characters")
@Data
public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(nullable = false)
    private String name;

    @Column(name = "character_class")
    private String characterClass;

    @Column(nullable = false)
    private Integer level;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String stats;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "controller_id")
    private User controller;

    @Enumerated(EnumType.STRING)
    @Column(name = "character_type", length = 20)
    private CharacterType characterType = CharacterType.PERMANENT; // Defines if the character is the player's main one or just temporary for a session

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }

    public enum CharacterType {
        PERMANENT, TEMPORARY
    }
}
