package com.github.yannicklampers.mychest.feature.service;

import com.github.yannicklampers.mychest.dao.AuditDao;
import com.github.yannicklampers.mychest.dao.AuditDao.AuditEntry;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 * Service for logging and retrieving audit records.
 */
public class AuditLoggerService {

    // Common audit action types
    public static final String ACTION_LIMIT_SET = "LIMIT_SET";
    public static final String ACTION_LIMIT_RESET = "LIMIT_RESET";
    public static final String ACTION_SHARE = "SHARE";
    public static final String ACTION_REVOKE = "REVOKE";
    public static final String ACTION_CHEST_CREATE = "CHEST_CREATE";
    public static final String ACTION_CHEST_DELETE = "CHEST_DELETE";
    public static final String ACTION_CHEST_OPEN = "CHEST_OPEN";

    private final AuditDao auditDao;
    private final Logger logger;
    private final int retentionDays;

    /**
     * Creates a new AuditLoggerService.
     *
     * @param auditDao      the audit DAO implementation
     * @param logger        the plugin logger
     * @param retentionDays the number of days to retain logs
     */
    public AuditLoggerService(AuditDao auditDao, Logger logger, int retentionDays) {
        this.auditDao = auditDao;
        this.logger = logger;
        this.retentionDays = retentionDays;
    }

    /**
     * Creates a new AuditLoggerService with retention from config.
     *
     * @param auditDao the audit DAO implementation
     * @param logger   the plugin logger
     * @param config   the plugin configuration
     */
    public AuditLoggerService(AuditDao auditDao, Logger logger, FileConfiguration config) {
        this(auditDao, logger, config.getInt("settings.audit_retention_days", 30));
    }

    /**
     * Log an audit action.
     *
     * @param playerId the UUID of the player performing the action
     * @param action   the action type
     * @param details  additional details
     */
    public void log(UUID playerId, String action, String details) {
        // Sanitize details to prevent log injection
        String sanitizedDetails = sanitizeLogInput(details);
        auditDao.logAction(playerId, action, sanitizedDetails);
        logger.info("[Audit] Player " + playerId + " performed " + action + ": " + sanitizedDetails);
    }
    
    /**
     * Sanitize input to prevent log injection attacks.
     * Removes newlines and carriage returns, and limits length.
     *
     * @param input the input string
     * @return sanitized string
     */
    private String sanitizeLogInput(String input) {
        if (input == null) {
            return "";
        }
        // Remove newlines and carriage returns to prevent log forging
        String sanitized = input.replace("\n", " ").replace("\r", " ");
        // Limit length to prevent excessively long log entries
        if (sanitized.length() > 500) {
            sanitized = sanitized.substring(0, 500) + "...";
        }
        return sanitized;
    }

    /**
     * Log a limit set action.
     *
     * @param adminId   the admin who set the limit
     * @param targetId  the player whose limit was set
     * @param newSlots  the new slot limit
     */
    public void logLimitSet(UUID adminId, UUID targetId, int newSlots) {
        String details = String.format("Set limit for %s to %d slots", targetId.toString(), newSlots);
        log(adminId, ACTION_LIMIT_SET, details);
    }

    /**
     * Log a limit reset action.
     *
     * @param adminId  the admin who reset the limit
     * @param targetId the player whose limit was reset
     */
    public void logLimitReset(UUID adminId, UUID targetId) {
        String details = String.format("Reset limit for %s to default", targetId.toString());
        log(adminId, ACTION_LIMIT_RESET, details);
    }

    /**
     * Log a chest share action.
     *
     * @param ownerId     the owner of the chest
     * @param chestId     the chest ID
     * @param targetId    the player the chest was shared with
     * @param permissions the permission level
     */
    public void logShare(UUID ownerId, int chestId, UUID targetId, String permissions) {
        String details = String.format("Shared chest %d with %s (permission: %s)", 
            chestId, targetId.toString(), permissions);
        log(ownerId, ACTION_SHARE, details);
    }

    /**
     * Log a chest revoke action.
     *
     * @param ownerId  the owner of the chest
     * @param chestId  the chest ID
     * @param targetId the player whose access was revoked
     */
    public void logRevoke(UUID ownerId, int chestId, UUID targetId) {
        String details = String.format("Revoked access to chest %d from %s", chestId, targetId.toString());
        log(ownerId, ACTION_REVOKE, details);
    }

    /**
     * Get audit logs for a specific player.
     *
     * @param playerId the UUID of the player
     * @param limit    maximum number of entries
     * @return list of audit entries
     */
    public List<AuditEntry> getLogsForPlayer(UUID playerId, int limit) {
        return auditDao.getLogsForPlayer(playerId, limit);
    }

    /**
     * Get recent audit logs.
     *
     * @param limit maximum number of entries
     * @return list of audit entries
     */
    public List<AuditEntry> getRecentLogs(int limit) {
        return auditDao.getRecentLogs(limit);
    }

    /**
     * Clean up old audit logs based on retention policy.
     *
     * @return the number of entries deleted
     */
    public int cleanupOldLogs() {
        int deleted = auditDao.deleteOldLogs(retentionDays);
        if (deleted > 0) {
            logger.info("[Audit] Cleaned up " + deleted + " old audit entries (older than " + retentionDays + " days)");
        }
        return deleted;
    }

    /**
     * Get the configured retention period.
     *
     * @return the number of days logs are retained
     */
    public int getRetentionDays() {
        return retentionDays;
    }
}
