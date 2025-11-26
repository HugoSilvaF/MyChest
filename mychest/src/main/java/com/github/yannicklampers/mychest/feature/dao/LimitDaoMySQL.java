package com.github.yannicklampers.mychest.feature.dao;

import com.github.yannicklampers.mychest.dao.LimitDao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MySQL implementation of the LimitDao interface.
 */
public class LimitDaoMySQL implements LimitDao {

    private static final String SELECT_LIMIT = 
        "SELECT slots FROM chest_limits WHERE player_id = ?";
    private static final String INSERT_LIMIT = 
        "INSERT INTO chest_limits (player_id, slots) VALUES (?, ?) " +
        "ON DUPLICATE KEY UPDATE slots = VALUES(slots)";
    private static final String DELETE_LIMIT = 
        "DELETE FROM chest_limits WHERE player_id = ?";

    private final DataSource dataSource;
    private final Logger logger;

    /**
     * Creates a new LimitDaoMySQL instance.
     *
     * @param dataSource the data source to use
     * @param logger     the logger instance
     */
    public LimitDaoMySQL(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
    }

    @Override
    public Optional<Integer> getSlotLimit(UUID playerId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_LIMIT)) {
            
            stmt.setString(1, playerId.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getInt("slots"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get slot limit for player " + playerId, e);
        }
        return Optional.empty();
    }

    @Override
    public void setSlotLimit(UUID playerId, int slots) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_LIMIT)) {
            
            stmt.setString(1, playerId.toString());
            stmt.setInt(2, slots);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to set slot limit for player " + playerId, e);
        }
    }

    @Override
    public void deleteSlotLimit(UUID playerId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_LIMIT)) {
            
            stmt.setString(1, playerId.toString());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to delete slot limit for player " + playerId, e);
        }
    }
}
