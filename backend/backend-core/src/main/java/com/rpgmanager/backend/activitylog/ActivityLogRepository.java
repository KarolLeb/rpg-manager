package com.rpgmanager.backend.activitylog;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for activity log entries with vector similarity search support.
 */
@Repository
public interface ActivityLogRepository extends JpaRepository<ActivityLogEntry, Long> {

    List<ActivityLogEntry> findBySessionIdOrderByCreatedAtDesc(Long sessionId);

    List<ActivityLogEntry> findByCampaignIdOrderByCreatedAtDesc(Long campaignId);

    List<ActivityLogEntry> findByActionTypeOrderByCreatedAtDesc(ActivityLogEntry.ActionType actionType);

    /**
     * Performs a vector similarity search using pgvector's cosine distance
     * operator.
     *
     * @param embedding the query vector as a string (e.g., "[0.1,0.2,...]")
     * @param limit     maximum number of results
     * @return entries ordered by similarity (most similar first)
     */
    @Query(value = "SELECT *, 1 - (embedding <=> CAST(:embedding AS vector)) AS similarity_score "
            + "FROM activity_log "
            + "WHERE embedding IS NOT NULL "
            + "ORDER BY embedding <=> CAST(:embedding AS vector) "
            + "LIMIT :limit", nativeQuery = true)
    List<Object[]> findSimilar(@Param("embedding") String embedding, @Param("limit") int limit);
}
