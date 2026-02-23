package com.rpgmanager.backend.activitylog;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** Controller for activity log operations — logging and searching RPG session actions. */
@RestController
@RequestMapping("/api/activity-log")
@RequiredArgsConstructor
public class ActivityLogController {

  private final ActivityLogService activityLogService;

  /**
   * Logs a new activity.
   *
   * @param request the activity details
   * @return the created activity log entry
   */
  @PostMapping
  public ActivityLogDto logActivity(@RequestBody CreateActivityLogRequest request) {
    return activityLogService.logActivity(request);
  }

  /**
   * Performs a semantic search over activity logs.
   *
   * @param q the natural-language search query
   * @param limit maximum number of results (default 10)
   * @return activities ordered by semantic similarity
   */
  @GetMapping("/search")
  public List<ActivityLogDto> searchActivities(
      @RequestParam String q, @RequestParam(defaultValue = "10") int limit) {
    return activityLogService.searchActivities(q, limit);
  }

  /**
   * Retrieves activities for a specific session.
   *
   * @param sessionId the session ID
   * @return list of activity log entries for the session
   */
  @GetMapping("/session/{sessionId}")
  public List<ActivityLogDto> getActivitiesBySession(@PathVariable Long sessionId) {
    return activityLogService.getActivitiesBySession(sessionId);
  }

  /**
   * Retrieves activities for a specific campaign.
   *
   * @param campaignId the campaign ID
   * @return list of activity log entries for the campaign
   */
  @GetMapping("/campaign/{campaignId}")
  public List<ActivityLogDto> getActivitiesByCampaign(@PathVariable Long campaignId) {
    return activityLogService.getActivitiesByCampaign(campaignId);
  }
}
