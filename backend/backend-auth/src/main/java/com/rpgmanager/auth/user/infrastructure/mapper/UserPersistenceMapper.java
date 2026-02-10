package com.rpgmanager.auth.user.infrastructure.mapper;

import com.rpgmanager.auth.user.domain.model.UserDomain;
import com.rpgmanager.auth.user.infrastructure.adapter.outgoing.persist.UserEntity;
import org.mapstruct.Mapper;

/** Mapper for converting between User domain objects and persistence entities. */
@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {

  /**
   * Converts a UserEntity to a UserDomain object.
   *
   * @param entity the entity to convert
   * @return the domain object
   */
  UserDomain toDomain(UserEntity entity);

  /**
   * Converts a UserDomain object to a UserEntity.
   *
   * @param domain the domain object to convert
   * @return the entity
   */
  UserEntity toEntity(UserDomain domain);
}
