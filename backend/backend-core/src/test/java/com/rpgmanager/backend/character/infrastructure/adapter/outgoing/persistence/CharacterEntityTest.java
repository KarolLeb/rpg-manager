package com.rpgmanager.backend.character.infrastructure.adapter.outgoing.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CharacterEntityTest {

    @Test
    void testGetId() {
        CharacterEntity entity = new CharacterEntity();
        entity.setId(42L);
        assertThat(entity.getId()).isEqualTo(42L);
    }

    @Test
    void testBasicGettersAndSetters() {
        CharacterEntity entity = new CharacterEntity();
        entity.setUserId(1L);
        entity.setControllerId(2L);

        assertThat(entity.getUserId()).isEqualTo(1L);
        assertThat(entity.getControllerId()).isEqualTo(2L);
    }
}
