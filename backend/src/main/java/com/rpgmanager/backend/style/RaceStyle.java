package com.rpgmanager.backend.style;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Table(name = "race_styles")
@Data
public class RaceStyle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID uuid;

    @Column(name = "race_name", nullable = false, unique = true)
    private String raceName;

    @Column(name = "css_content", columnDefinition = "text")
    private String cssContent;

    @PrePersist
    protected void onCreate() {
        if (uuid == null) {
            uuid = UUID.randomUUID();
        }
    }
}
