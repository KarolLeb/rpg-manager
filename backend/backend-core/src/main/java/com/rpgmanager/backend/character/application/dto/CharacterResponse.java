package com.rpgmanager.backend.character.application.dto;

/**
 * Data Transfer Object for Character response.
 *
 * @param id             the character ID
 * @param name           the character name
 * @param characterClass the character class
 * @param level          the character level
 * @param stats          the character stats (JSON)
 * @param ownerName      the name of the character's owner
 * @param campaignName   the name of the campaign the character belongs to
 * @param characterType  the type of the character
 */
public record CharacterResponse(
        Long id,
        String name,
        String characterClass,
        Integer level,
        String stats,
        String ownerName,
        Long ownerId,
        Long controllerId,
        String campaignName,
        String characterType) {
}
