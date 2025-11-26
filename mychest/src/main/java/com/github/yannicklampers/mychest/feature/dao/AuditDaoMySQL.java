package com.github.yannicklampers.mychest.feature.dao;

import com.github.yannicklampers.mychest.dao.AuditDao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MySQL implementation of the AuditDao interface.
 */
public class AuditDaoMySQL implements AuditDao {

    private static final String INSERT_LOG = 
        "INSERT INTO chest_audit (player_id, action, details, created_at) VALUES (?, ?, ?, NOW())";
    private static final String SELECT_PLAYER_LOGS = 
        "SELECT id, player_id, action, details, UNIX_TIMESTAMP(created_at) as timestamp " +
        "FROM chest_audit WHERE player_id = ? ORDER BY created_at DESC LIMIT ?";
    private static final String SELECT_RECENT_LOGS = 
        "SELECT id, player_id, action, details, UNIX_TIMESTAMP(created_at) as timestamp " +
        "FROM chest_audit ORDER BY created_at DESC LIMIT ?";
    private static final String DELETE_OLD_LOGS = 
        "DELETE FROM chest_audit WHERE created_at < DATE_SUB(NOW(), INTERVAL ? DAY)";

    private final DataSource dataSource;
    private final Logger logger;

    /**
     * Creates a new AuditDaoMySQL instance.
     *
     * @param dataSource the data source to use
     * @param logger     the logger instance
     */
    public AuditDaoMySQL(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
    }

    @Override
    public void logAction(UUID playerId, String action, String details) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_LOG)) {
            
            stmt.setString(1, playerId.toString());
            stmt.setString(2, action);
            stmt.setString(3, details);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to log audit action for player " + playerId, e);
        }
    }

    @Override
    public List<AuditEntry> getLogsForPlayer(UUID playerId, int limit) {
        List<AuditEntry> entries = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_PLAYER_LOGS)) {
            
            stmt.setString(1, playerId.toString());
            stmt.setInt(2, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(createAuditEntry(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get audit logs for player " + playerId, e);
        }
        
        return entries;
    }

    @Override
    public List<AuditEntry> getRecentLogs(int limit) {
        List<AuditEntry> entries = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_RECENT_LOGS)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    entries.add(createAuditEntry(rs));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get recent audit logs", e);
        }
        
        return entries;
    }

    @Override
    public int deleteOldLogs(int days) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_OLD_LOGS)) {
            
            stmt.setInt(1, days);
            return stmt.executeUpdate();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to delete old audit logs", e);
        }
        
        return 0;
    }

    /**
     * Create an AuditEntry from a ResultSet row.
     *
     * @param rs the result set positioned at the current row
     * @return the audit entry
     * @throws SQLException if reading fails
     */
    private AuditEntry createAuditEntry(ResultSet rs) throws SQLException {
        return new AuditEntry(
            rs.getLong("id"),
            UUID.fromString(rs.getString("player_id")),
            rs.getString("action"),
            rs.getString("details"),
            rs.getLong("timestamp") * 1000 // Convert to milliseconds
        );
    }
}
