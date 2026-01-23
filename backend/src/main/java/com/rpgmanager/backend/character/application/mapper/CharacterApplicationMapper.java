package com.rpgmanager.backend.character.application.mapper;

import com.rpgmanager.backend.character.application.dto.CharacterResponse;
import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CharacterApplicationMapper {

  @Mapping(
      target = "characterType",
      expression =
          "java(domain.getCharacterType() != null ? domain.getCharacterType().name() : null)")
  @Mapping(source = "ownerUsername", target = "ownerName")
  CharacterResponse toResponse(CharacterDomain domain);
}
