package com.rpgmanager.backend.style;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.rpgmanager.backend.character.domain.model.CharacterDomain;
import com.rpgmanager.backend.character.domain.repository.CharacterRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StyleServiceUnitTest {

  @Mock private StyleRepository styleRepository;
  @Mock private CharacterRepository characterRepository;

  private StyleService styleService;

  @BeforeEach
  void setUp() {
    styleService = new StyleService(styleRepository, characterRepository);
  }

  @Test
  void getAggregatedCss_shouldConcatenateAllLevels() {
    Long charId = 1L;
    Long campaignId = 10L;
    CharacterDomain character =
        CharacterDomain.builder()
            .id(charId)
            .name("Legolas")
            .race("Elf")
            .campaignId(campaignId)
            .build();

    when(characterRepository.findById(charId)).thenReturn(Optional.of(character));

    // Default style
    when(styleRepository.findByLevelAndReferenceId(StyleLevel.DEFAULT, "global"))
        .thenReturn(Optional.of(Style.builder().cssContent("body { margin: 0; }").build()));

    // Campaign style
    when(styleRepository.findByLevelAndReferenceId(StyleLevel.CAMPAIGN, campaignId.toString()))
        .thenReturn(Optional.of(Style.builder().cssContent(".campaign { color: green; }").build()));

    // Campaign+Race style
    when(styleRepository.findByLevelAndReferenceId(StyleLevel.CAMPAIGN, campaignId + ":Elf"))
        .thenReturn(
            Optional.of(Style.builder().cssContent(".elf { font-style: italic; }").build()));

    // Character style
    when(styleRepository.findByLevelAndReferenceId(StyleLevel.CHARACTER, charId.toString()))
        .thenReturn(
            Optional.of(Style.builder().cssContent(".char { border: 1px solid; }").build()));

    String result = styleService.getAggregatedCss(charId);

    assertThat(result)
        .contains("body { margin: 0; }")
        .contains(".campaign { color: green; }")
        .contains(".elf { font-style: italic; }")
        .contains(".char { border: 1px solid; }");
  }

  @Test
  void getAggregatedCss_shouldReturnFallback_whenNoStylesFound() {
    Long charId = 1L;
    CharacterDomain character = CharacterDomain.builder().id(charId).name("Nameless").build();

    when(characterRepository.findById(charId)).thenReturn(Optional.of(character));
    when(styleRepository.findByLevelAndReferenceId(any(), any())).thenReturn(Optional.empty());

    String result = styleService.getAggregatedCss(charId);

    assertThat(result).contains("Default minimal style fallback");
  }

  @Test
  void saveStyle_shouldCreateNew_whenNotFound() {
    when(styleRepository.findByLevelAndReferenceId(StyleLevel.DEFAULT, "new"))
        .thenReturn(Optional.empty());
    when(styleRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

    Style saved = styleService.saveStyle(StyleLevel.DEFAULT, "new", "content");

    assertThat(saved.getCssContent()).isEqualTo("content");
    verify(styleRepository).save(any());
  }
}
