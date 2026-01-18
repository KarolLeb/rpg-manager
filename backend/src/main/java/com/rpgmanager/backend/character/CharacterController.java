package com.rpgmanager.backend.character;

import com.rpgmanager.backend.character.dto.CharacterResponse;
import com.rpgmanager.backend.character.mapper.CharacterMapper;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {
    
    private final CharacterRepository characterRepository;

    @GetMapping
    public List<CharacterResponse> getAllCharacters() {
        return characterRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "id"))
                .stream()
                .map(CharacterMapper::toResponse)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public CharacterResponse updateCharacter(@PathVariable Long id, @RequestBody Character characterDetails) {
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Character not found with id: " + id));
        
        character.setName(characterDetails.getName());
        character.setCharacterClass(characterDetails.getCharacterClass());
        character.setLevel(characterDetails.getLevel());
        character.setStats(characterDetails.getStats());
        
        Character savedCharacter = characterRepository.save(character);
        return CharacterMapper.toResponse(savedCharacter);
    }
}
