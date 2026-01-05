package com.rpgmanager.backend.character;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import java.util.List;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {
    
    private final CharacterRepository characterRepository;

    @GetMapping
    public List<Character> getAllCharacters() {
        return characterRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "id"));
    }

    @PutMapping("/{id}")
    public Character updateCharacter(@PathVariable Long id, @RequestBody Character characterDetails) {
        Character character = characterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Character not found with id: " + id));
        
        character.setName(characterDetails.getName());
        character.setCharacterClass(characterDetails.getCharacterClass());
        character.setLevel(characterDetails.getLevel());
        character.setStats(characterDetails.getStats());
        
        return characterRepository.save(character);
    }
}
