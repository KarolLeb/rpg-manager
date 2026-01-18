package com.rpgmanager.backend.style;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RaceStyleRepository extends JpaRepository<RaceStyle, Long> {
    Optional<RaceStyle> findByRaceName(String raceName);
}
