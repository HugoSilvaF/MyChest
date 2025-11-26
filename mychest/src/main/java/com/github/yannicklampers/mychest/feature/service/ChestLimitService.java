package com.github.yannicklampers.mychest.feature.service;

import com.github.yannicklampers.mychest.dao.LimitDao;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing player chest slot limits.
 */
public class ChestLimitService {

    private final LimitDao limitDao;
    private final int defaultSlots;

    /**
     * Creates a new ChestLimitService.
     *
     * @param limitDao     the limit DAO implementation
     * @param defaultSlots the default number of slots if none is set
     */
    public ChestLimitService(LimitDao limitDao, int defaultSlots) {
        this.limitDao = limitDao;
        this.defaultSlots = defaultSlots;
    }

    /**
     * Creates a new ChestLimitService with default from config.
     *
     * @param limitDao the limit DAO implementation
     * @param config   the plugin configuration
     */
    public ChestLimitService(LimitDao limitDao, FileConfiguration config) {
        this(limitDao, config.getInt("settings.default_slots", 27));
    }

    /**
     * Get the slot limit for a player.
     * Returns the player-specific limit if set, otherwise returns the default.
     *
     * @param playerId the UUID of the player
     * @return the slot limit
     */
    public int getSlotLimit(UUID playerId) {
        return limitDao.getSlotLimit(playerId).orElse(defaultSlots);
    }

    /**
     * Set a custom slot limit for a player.
     *
     * @param playerId the UUID of the player
     * @param slots    the number of slots
     */
    public void setSlotLimit(UUID playerId, int slots) {
        if (slots <= 0) {
            throw new IllegalArgumentException("Slot limit must be positive");
        }
        limitDao.setSlotLimit(playerId, slots);
    }

    /**
     * Reset the player's slot limit to the default.
     *
     * @param playerId the UUID of the player
     */
    public void resetSlotLimit(UUID playerId) {
        limitDao.deleteSlotLimit(playerId);
    }

    /**
     * Check if a player has a custom slot limit set.
     *
     * @param playerId the UUID of the player
     * @return true if a custom limit is set
     */
    public boolean hasCustomLimit(UUID playerId) {
        return limitDao.getSlotLimit(playerId).isPresent();
    }

    /**
     * Get the default slot limit.
     *
     * @return the default number of slots
     */
    public int getDefaultSlots() {
        return defaultSlots;
    }
}
