package com.github.yannicklampers.mychest.feature.service;

import com.github.yannicklampers.mychest.dao.ShareDao;

import java.util.List;
import java.util.UUID;

/**
 * Service for managing chest sharing between players.
 */
public class ShareService {

    /** Permission level for view-only access. */
    public static final String PERMISSION_VIEW = "VIEW";
    
    /** Permission level for full edit access. */
    public static final String PERMISSION_EDIT = "EDIT";

    private final ShareDao shareDao;

    /**
     * Creates a new ShareService.
     *
     * @param shareDao the share DAO implementation
     */
    public ShareService(ShareDao shareDao) {
        this.shareDao = shareDao;
    }

    /**
     * Share a chest with another player with view permission.
     *
     * @param chestId  the ID of the chest
     * @param ownerId  the UUID of the chest owner
     * @param targetId the UUID of the player to share with
     */
    public void shareChest(int chestId, UUID ownerId, UUID targetId) {
        shareChest(chestId, ownerId, targetId, PERMISSION_VIEW);
    }

    /**
     * Share a chest with another player with specified permission.
     *
     * @param chestId     the ID of the chest
     * @param ownerId     the UUID of the chest owner
     * @param targetId    the UUID of the player to share with
     * @param permissions the permission level (VIEW or EDIT)
     */
    public void shareChest(int chestId, UUID ownerId, UUID targetId, String permissions) {
        if (ownerId.equals(targetId)) {
            throw new IllegalArgumentException("Cannot share a chest with yourself");
        }
        shareDao.shareChest(chestId, ownerId, targetId, permissions);
    }

    /**
     * Revoke chest sharing from a player.
     *
     * @param chestId  the ID of the chest
     * @param targetId the UUID of the player to revoke access from
     */
    public void revokeShare(int chestId, UUID targetId) {
        shareDao.revokeShare(chestId, targetId);
    }

    /**
     * Get all players a chest is shared with.
     *
     * @param chestId the ID of the chest
     * @return a list of UUIDs of players the chest is shared with
     */
    public List<UUID> getSharedPlayers(int chestId) {
        return shareDao.getSharedPlayers(chestId);
    }

    /**
     * Get all chests shared with a specific player.
     *
     * @param playerId the UUID of the player
     * @return a list of chest IDs shared with this player
     */
    public List<Integer> getSharedChests(UUID playerId) {
        return shareDao.getSharedChests(playerId);
    }

    /**
     * Check if a player has access to a chest.
     *
     * @param chestId  the ID of the chest
     * @param playerId the UUID of the player
     * @return true if the player has access
     */
    public boolean hasAccess(int chestId, UUID playerId) {
        return shareDao.hasAccess(chestId, playerId);
    }

    /**
     * Check if a player can edit a chest.
     *
     * @param chestId  the ID of the chest
     * @param playerId the UUID of the player
     * @return true if the player can edit
     */
    public boolean canEdit(int chestId, UUID playerId) {
        String perms = shareDao.getPermissions(chestId, playerId);
        return PERMISSION_EDIT.equals(perms);
    }

    /**
     * Get the permission level for a player on a chest.
     *
     * @param chestId  the ID of the chest
     * @param playerId the UUID of the player
     * @return the permission string, or null if no access
     */
    public String getPermissions(int chestId, UUID playerId) {
        return shareDao.getPermissions(chestId, playerId);
    }
}
