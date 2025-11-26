package com.github.yannicklampers.mychest.dao;

import java.util.List;
import java.util.UUID;

/**
 * DAO interface for audit logging of chest operations.
 */
public interface AuditDao {

    /**
     * Represents an audit log entry.
     */
    class AuditEntry {
        private final long id;
        private final UUID playerId;
        private final String action;
        private final String details;
        private final long timestamp;

        public AuditEntry(long id, UUID playerId, String action, String details, long timestamp) {
            this.id = id;
            this.playerId = playerId;
            this.action = action;
            this.details = details;
            this.timestamp = timestamp;
        }

        public long getId() {
            return id;
        }

        public UUID getPlayerId() {
            return playerId;
        }

        public String getAction() {
            return action;
        }

        public String getDetails() {
            return details;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Log an audit event.
     *
     * @param playerId the UUID of the player performing the action
     * @param action   the action type (e.g., "LIMIT_SET", "SHARE", "REVOKE")
     * @param details  additional details about the action
     */
    void logAction(UUID playerId, String action, String details);

    /**
     * Get audit logs for a specific player.
     *
     * @param playerId the UUID of the player
     * @param limit    maximum number of entries to return
     * @return a list of audit entries
     */
    List<AuditEntry> getLogsForPlayer(UUID playerId, int limit);

    /**
     * Get recent audit logs.
     *
     * @param limit maximum number of entries to return
     * @return a list of recent audit entries
     */
    List<AuditEntry> getRecentLogs(int limit);

    /**
     * Delete audit logs older than a certain number of days.
     *
     * @param days the retention period in days
     * @return the number of entries deleted
     */
    int deleteOldLogs(int days);
}
