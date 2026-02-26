package com.rpgmanager.backend.style;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Entity representing a hierarchical Style in the database. */
@Entity
@Table(
    name = "styles",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"level", "reference_id"})})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Style {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Column(name = "level", nullable = false)
  private StyleLevel level;

  @Column(name = "reference_id", nullable = false)
  private String referenceId; // e.g., 'global', campaignId, raceName, className, characterId

  @Column(name = "css_content", columnDefinition = "text")
  private String cssContent;
}
