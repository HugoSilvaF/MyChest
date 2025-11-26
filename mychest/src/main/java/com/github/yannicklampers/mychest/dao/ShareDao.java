package com.github.yannicklampers.mychest.dao;

import java.util.List;
import java.util.UUID;

/**
 * DAO interface for managing chest sharing between players.
 */
public interface ShareDao {

    /**
     * Share a chest with another player.
     *
     * @param chestId     the ID of the chest to share
     * @param ownerId     the UUID of the chest owner
     * @param targetId    the UUID of the player to share with
     * @param permissions the permission level (e.g., "VIEW", "EDIT")
     */
    void shareChest(int chestId, UUID ownerId, UUID targetId, String permissions);

    /**
     * Revoke chest sharing from a player.
     *
     * @param chestId  the ID of the chest
     * @param targetId the UUID of the player to revoke access from
     */
    void revokeShare(int chestId, UUID targetId);

    /**
     * Get all players a chest is shared with.
     *
     * @param chestId the ID of the chest
     * @return a list of UUIDs of players the chest is shared with
     */
    List<UUID> getSharedPlayers(int chestId);

    /**
     * Get all chests shared with a specific player.
     *
     * @param playerId the UUID of the player
     * @return a list of chest IDs shared with this player
     */
    List<Integer> getSharedChests(UUID playerId);

    /**
     * Check if a player has access to a chest.
     *
     * @param chestId  the ID of the chest
     * @param playerId the UUID of the player
     * @return true if the player has access, false otherwise
     */
    boolean hasAccess(int chestId, UUID playerId);

    /**
     * Get the permission level for a player on a chest.
     *
     * @param chestId  the ID of the chest
     * @param playerId the UUID of the player
     * @return the permission string, or null if no access
     */
    String getPermissions(int chestId, UUID playerId);
}
