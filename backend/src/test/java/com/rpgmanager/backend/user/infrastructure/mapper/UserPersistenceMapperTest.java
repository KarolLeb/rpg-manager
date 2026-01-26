package com.rpgmanager.backend.user.infrastructure.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.rpgmanager.backend.user.domain.model.UserDomain;
import com.rpgmanager.backend.user.infrastructure.adapter.outgoing.persist.UserEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class UserPersistenceMapperTest {

  private final UserPersistenceMapper mapper = Mappers.getMapper(UserPersistenceMapper.class);

  @Test
  void toDomain_shouldMapAllFields() {
    UserEntity entity = Instancio.create(UserEntity.class);
    UserDomain domain = mapper.toDomain(entity);

    assertThat(domain).isNotNull();
    assertThat(domain.getId()).isEqualTo(entity.getId());
    assertThat(domain.getUsername()).isEqualTo(entity.getUsername());
    assertThat(domain.getEmail()).isEqualTo(entity.getEmail());
    assertThat(domain.getRole().name()).isEqualTo(entity.getRole().name());
  }

  @Test
  void toEntity_shouldMapAllFields() {
    UserDomain domain = Instancio.create(UserDomain.class);
    UserEntity entity = mapper.toEntity(domain);

    assertThat(entity).isNotNull();
    assertThat(entity.getId()).isEqualTo(domain.getId());
    assertThat(entity.getUsername()).isEqualTo(domain.getUsername());
    assertThat(entity.getEmail()).isEqualTo(domain.getEmail());
    assertThat(entity.getRole().name()).isEqualTo(domain.getRole().name());
  }

  @Test
  void toDomain_shouldHandleNull() {
    assertThat(mapper.toDomain(null)).isNull();
  }

  @Test
  void toEntity_shouldHandleNull() {
    assertThat(mapper.toEntity(null)).isNull();
  }
}
