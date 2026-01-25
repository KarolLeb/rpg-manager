package com.rpgmanager.backend.character.application.mapper;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/** Mapper for converting between Character domain objects and response DTOs. */
@Mapper(componentModel = "spring")
public interface CharacterApplicationMapper {

  /**
   * Converts a CharacterDomain object to a CharacterResponse DTO.
   *
   * @param domain the domain object
   * @return the response DTO
   */
  @Mapping(
      target = "characterType",
      expression =
          "java(domain.getCharacterType() != null ? domain.getCharacterType().name() : null)")
  @Mapping(source = "ownerUsername", target = "ownerName")
  CharacterResponse toResponse(CharacterDomain domain);
}
