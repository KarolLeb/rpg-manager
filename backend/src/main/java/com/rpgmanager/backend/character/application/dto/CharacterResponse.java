package com.rpgmanager.backend.character.application.dto;

public record CharacterResponse(
    Long id,
    String name,
    String characterClass,
    Integer level,
    String stats,
    String ownerName,
    String campaignName,
    String characterType) {}
