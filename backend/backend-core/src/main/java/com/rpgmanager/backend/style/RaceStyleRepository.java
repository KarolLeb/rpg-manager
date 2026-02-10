package com.rpgmanager.backend.style;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** JPA Repository for RaceStyle entities. */
public interface RaceStyleRepository extends JpaRepository<RaceStyle, Long> {
  Optional<RaceStyle> findByRaceName(String raceName);
}
