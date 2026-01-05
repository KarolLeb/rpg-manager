package com.rpgmanager.backend.character;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "characters")
@Data
public class Character {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "character_class")
    private String characterClass;

    @Column(nullable = false)
    private Integer level;

    @Column(columnDefinition = "TEXT")
    private String stats;
}
