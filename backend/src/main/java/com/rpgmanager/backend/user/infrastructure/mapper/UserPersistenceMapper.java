package com.rpgmanager.backend.user.infrastructure.mapper;

import com.rpgmanager.backend.user.domain.model.UserDomain;
import com.rpgmanager.backend.user.infrastructure.adapter.outgoing.persist.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {
  UserDomain toDomain(UserEntity entity);

  UserEntity toEntity(UserDomain domain);
}
