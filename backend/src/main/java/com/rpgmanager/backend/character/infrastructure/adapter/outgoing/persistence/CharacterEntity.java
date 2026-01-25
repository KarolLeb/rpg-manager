package com.rpgmanager.backend.character.infrastructure.adapter.outgoing.persistence;

import com.rpgmanager.backend.campaign.infrastructure.adapter.outgoing.persist.CampaignEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "characters")
@Data
public class CharacterEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

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
  private CampaignEntity campaign;

  @Column(name = "user_id")
  private Long userId;

  @Column(name = "controller_id")
  private Long controllerId;

  @Enumerated(EnumType.STRING)
  @Column(name = "character_type", length = 20)
  private CharacterType characterType = CharacterType.PERMANENT;

  public enum CharacterType {
    PERMANENT,
    TEMPORARY
  }
}
