package com.rpgmanager.backend.style;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/** JPA Repository for hierarchical Style entities. */
public interface StyleRepository extends JpaRepository<Style, Long> {

  Optional<Style> findByLevelAndReferenceId(StyleLevel level, String referenceId);

  @Query("SELECT s FROM Style s WHERE (s.level = :level AND s.referenceId = :referenceId)")
  Optional<Style> findSpecificStyle(
      @Param("level") StyleLevel level, @Param("referenceId") String referenceId);
}
