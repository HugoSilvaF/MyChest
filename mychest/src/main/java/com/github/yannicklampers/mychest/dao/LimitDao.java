package com.github.yannicklampers.mychest.dao;

import java.util.Optional;
import java.util.UUID;

/**
 * DAO interface for managing chest limits per player.
 */
public interface LimitDao {

    /**
     * Get the maximum number of chest slots allowed for a player.
     *
     * @param playerId the UUID of the player
     * @return an Optional containing the slot limit, or empty if not set
     */
    Optional<Integer> getSlotLimit(UUID playerId);

    /**
     * Set the maximum number of chest slots allowed for a player.
     *
     * @param playerId the UUID of the player
     * @param slots    the maximum number of slots
     */
    void setSlotLimit(UUID playerId, int slots);

    /**
     * Delete the slot limit for a player (revert to default).
     *
     * @param playerId the UUID of the player
     */
    void deleteSlotLimit(UUID playerId);
}
