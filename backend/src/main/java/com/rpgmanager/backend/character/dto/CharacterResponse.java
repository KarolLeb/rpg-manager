package com.rpgmanager.backend.character.dto;

import java.util.UUID;

public record CharacterResponse(
    UUID uuid,
    String name,
    String characterClass,
    Integer level,
    String stats,
    String ownerName,
    String campaignName,
    String characterType
) {}
