package com.github.yannicklampers.mychest.feature.dao;

import com.github.yannicklampers.mychest.dao.ShareDao;

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
 * MySQL implementation of the ShareDao interface.
 */
public class ShareDaoMySQL implements ShareDao {

    private static final String INSERT_SHARE = 
        "INSERT INTO chest_shares (chest_id, owner_id, target_id, permissions) VALUES (?, ?, ?, ?) " +
        "ON DUPLICATE KEY UPDATE permissions = VALUES(permissions)";
    private static final String DELETE_SHARE = 
        "DELETE FROM chest_shares WHERE chest_id = ? AND target_id = ?";
    private static final String SELECT_SHARED_PLAYERS = 
        "SELECT target_id FROM chest_shares WHERE chest_id = ?";
    private static final String SELECT_SHARED_CHESTS = 
        "SELECT chest_id FROM chest_shares WHERE target_id = ?";
    private static final String SELECT_HAS_ACCESS = 
        "SELECT 1 FROM chest_shares WHERE chest_id = ? AND target_id = ?";
    private static final String SELECT_PERMISSIONS = 
        "SELECT permissions FROM chest_shares WHERE chest_id = ? AND target_id = ?";

    private final DataSource dataSource;
    private final Logger logger;

    /**
     * Creates a new ShareDaoMySQL instance.
     *
     * @param dataSource the data source to use
     * @param logger     the logger instance
     */
    public ShareDaoMySQL(DataSource dataSource, Logger logger) {
        this.dataSource = dataSource;
        this.logger = logger;
    }

    @Override
    public void shareChest(int chestId, UUID ownerId, UUID targetId, String permissions) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(INSERT_SHARE)) {
            
            stmt.setInt(1, chestId);
            stmt.setString(2, ownerId.toString());
            stmt.setString(3, targetId.toString());
            stmt.setString(4, permissions);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to share chest " + chestId + " with player " + targetId, e);
        }
    }

    @Override
    public void revokeShare(int chestId, UUID targetId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(DELETE_SHARE)) {
            
            stmt.setInt(1, chestId);
            stmt.setString(2, targetId.toString());
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to revoke share for chest " + chestId + " from player " + targetId, e);
        }
    }

    @Override
    public List<UUID> getSharedPlayers(int chestId) {
        List<UUID> players = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_SHARED_PLAYERS)) {
            
            stmt.setInt(1, chestId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    players.add(UUID.fromString(rs.getString("target_id")));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get shared players for chest " + chestId, e);
        }
        
        return players;
    }

    @Override
    public List<Integer> getSharedChests(UUID playerId) {
        List<Integer> chests = new ArrayList<>();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_SHARED_CHESTS)) {
            
            stmt.setString(1, playerId.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    chests.add(rs.getInt("chest_id"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get shared chests for player " + playerId, e);
        }
        
        return chests;
    }

    @Override
    public boolean hasAccess(int chestId, UUID playerId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_HAS_ACCESS)) {
            
            stmt.setInt(1, chestId);
            stmt.setString(2, playerId.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to check access for chest " + chestId + " player " + playerId, e);
        }
        
        return false;
    }

    @Override
    public String getPermissions(int chestId, UUID playerId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SELECT_PERMISSIONS)) {
            
            stmt.setInt(1, chestId);
            stmt.setString(2, playerId.toString());
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("permissions");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get permissions for chest " + chestId + " player " + playerId, e);
        }
        
        return null;
    }
}
