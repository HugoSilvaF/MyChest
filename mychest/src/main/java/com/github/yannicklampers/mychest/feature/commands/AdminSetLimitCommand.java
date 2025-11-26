package com.github.yannicklampers.mychest.feature.commands;

import com.github.yannicklampers.mychest.feature.service.AuditLoggerService;
import com.github.yannicklampers.mychest.feature.service.ChestLimitService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * Example admin command for setting player chest slot limits.
 * Usage: /setlimit <player> <slots>
 * 
 * <p>This command requires the permission "mychest.admin.setlimit".</p>
 */
public class AdminSetLimitCommand implements CommandExecutor {

    private static final String PERMISSION = "mychest.admin.setlimit";

    private final ChestLimitService limitService;
    private final AuditLoggerService auditService;

    /**
     * Creates a new AdminSetLimitCommand.
     *
     * @param limitService the limit service
     * @param auditService the audit service for logging
     */
    public AdminSetLimitCommand(ChestLimitService limitService, AuditLoggerService auditService) {
        this.limitService = limitService;
        this.auditService = auditService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check permission
        if (!sender.hasPermission(PERMISSION)) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        // Validate arguments
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /" + label + " <player> <slots>");
            sender.sendMessage(ChatColor.GRAY + "Use 'reset' as slots to reset to default.");
            return true;
        }

        String playerName = args[0];
        String slotsArg = args[1];

        // Find the target player
        Player targetPlayer = Bukkit.getPlayer(playerName);
        UUID targetId;

        if (targetPlayer != null) {
            targetId = targetPlayer.getUniqueId();
        } else {
            // Get offline player UUID (generates a random UUID if player never joined)
            @SuppressWarnings("deprecation")
            targetId = Bukkit.getOfflinePlayer(playerName).getUniqueId();
        }

        // Get admin UUID (console uses null UUID)
        UUID adminId = (sender instanceof Player) 
            ? ((Player) sender).getUniqueId() 
            : new UUID(0, 0);

        // Handle reset
        if (slotsArg.equalsIgnoreCase("reset")) {
            limitService.resetSlotLimit(targetId);
            auditService.logLimitReset(adminId, targetId);
            
            sender.sendMessage(ChatColor.GREEN + "Reset slot limit for " + playerName + " to default (" 
                + limitService.getDefaultSlots() + " slots).");
            
            if (targetPlayer != null && targetPlayer.isOnline()) {
                targetPlayer.sendMessage(ChatColor.YELLOW + "Your chest slot limit has been reset to default.");
            }
            return true;
        }

        // Parse and validate slot count
        int slots;
        try {
            slots = Integer.parseInt(slotsArg);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid slot count: " + slotsArg);
            return true;
        }

        if (slots <= 0) {
            sender.sendMessage(ChatColor.RED + "Slot count must be positive.");
            return true;
        }

        if (slots > 54) {
            sender.sendMessage(ChatColor.YELLOW + "Warning: Setting slots above 54 may cause UI issues.");
        }

        // Set the limit
        limitService.setSlotLimit(targetId, slots);
        auditService.logLimitSet(adminId, targetId, slots);

        sender.sendMessage(ChatColor.GREEN + "Set slot limit for " + playerName + " to " + slots + " slots.");
        
        if (targetPlayer != null && targetPlayer.isOnline()) {
            targetPlayer.sendMessage(ChatColor.YELLOW + "Your chest slot limit has been set to " + slots + " slots.");
        }

        return true;
    }
}
