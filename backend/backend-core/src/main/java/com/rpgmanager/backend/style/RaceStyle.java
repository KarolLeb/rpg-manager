package com.rpgmanager.backend.style;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/** Entity representing a Race Style in the database. */
@Entity
@Table(name = "race_styles")
@Data
public class RaceStyle {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "race_name", nullable = false, unique = true)
  private String raceName;

  @Column(name = "css_content", columnDefinition = "text")
  private String cssContent;
}
