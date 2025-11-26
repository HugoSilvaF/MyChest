package com.github.yannicklampers.mychest.feature.commands;

import com.github.yannicklampers.mychest.feature.service.AuditLoggerService;
import com.github.yannicklampers.mychest.feature.service.ShareService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

/**
 * Example command for sharing chests with other players.
 * Usage: /sharechest <chestId> <player> [permission]
 *        /sharechest revoke <chestId> <player>
 *        /sharechest list <chestId>
 * 
 * <p>This command requires the permission "mychest.share".</p>
 */
public class UserShareCommand implements CommandExecutor {

    private static final String PERMISSION = "mychest.share";

    private final ShareService shareService;
    private final AuditLoggerService auditService;

    /**
     * Creates a new UserShareCommand.
     *
     * @param shareService the share service
     * @param auditService the audit service for logging
     */
    public UserShareCommand(ShareService shareService, AuditLoggerService auditService) {
        this.shareService = shareService;
        this.auditService = auditService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Only players can share chests
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        // Check permission
        if (!player.hasPermission(PERMISSION)) {
            player.sendMessage(ChatColor.RED + "You don't have permission to share chests.");
            return true;
        }

        if (args.length < 1) {
            sendUsage(player, label);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "revoke":
                return handleRevoke(player, args, label);
            case "list":
                return handleList(player, args, label);
            default:
                return handleShare(player, args, label);
        }
    }

    /**
     * Handle the share subcommand.
     */
    private boolean handleShare(Player player, String[] args, String label) {
        if (args.length < 2) {
            sendUsage(player, label);
            return true;
        }

        int chestId;
        try {
            chestId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid chest ID: " + args[0]);
            return true;
        }

        String targetName = args[1];
        Player targetPlayer = Bukkit.getPlayer(targetName);
        
        if (targetPlayer == null) {
            player.sendMessage(ChatColor.RED + "Player not found: " + targetName);
            return true;
        }

        if (targetPlayer.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You cannot share a chest with yourself.");
            return true;
        }

        // Determine permission level
        String permissions = ShareService.PERMISSION_VIEW;
        if (args.length >= 3) {
            String permArg = args[2].toUpperCase();
            if (permArg.equals(ShareService.PERMISSION_VIEW) || permArg.equals(ShareService.PERMISSION_EDIT)) {
                permissions = permArg;
            } else {
                player.sendMessage(ChatColor.RED + "Invalid permission. Use VIEW or EDIT.");
                return true;
            }
        }

        // Share the chest
        try {
            shareService.shareChest(chestId, player.getUniqueId(), targetPlayer.getUniqueId(), permissions);
            auditService.logShare(player.getUniqueId(), chestId, targetPlayer.getUniqueId(), permissions);

            player.sendMessage(ChatColor.GREEN + "Shared chest #" + chestId + " with " + targetName 
                + " (" + permissions + " permission).");
            targetPlayer.sendMessage(ChatColor.YELLOW + player.getName() + " shared their chest #" 
                + chestId + " with you (" + permissions + " permission).");
        } catch (IllegalArgumentException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
        }

        return true;
    }

    /**
     * Handle the revoke subcommand.
     */
    private boolean handleRevoke(Player player, String[] args, String label) {
        if (args.length < 3) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " revoke <chestId> <player>");
            return true;
        }

        int chestId;
        try {
            chestId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid chest ID: " + args[1]);
            return true;
        }

        String targetName = args[2];
        Player targetPlayer = Bukkit.getPlayer(targetName);
        UUID targetId;

        if (targetPlayer != null) {
            targetId = targetPlayer.getUniqueId();
        } else {
            // Get offline player UUID (generates a random UUID if player never joined)
            @SuppressWarnings("deprecation")
            targetId = Bukkit.getOfflinePlayer(targetName).getUniqueId();
        }

        shareService.revokeShare(chestId, targetId);
        auditService.logRevoke(player.getUniqueId(), chestId, targetId);

        player.sendMessage(ChatColor.GREEN + "Revoked access to chest #" + chestId + " from " + targetName + ".");
        
        if (targetPlayer != null && targetPlayer.isOnline()) {
            targetPlayer.sendMessage(ChatColor.YELLOW + player.getName() + " revoked your access to their chest #" + chestId + ".");
        }

        return true;
    }

    /**
     * Handle the list subcommand.
     */
    private boolean handleList(Player player, String[] args, String label) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /" + label + " list <chestId>");
            return true;
        }

        int chestId;
        try {
            chestId = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            player.sendMessage(ChatColor.RED + "Invalid chest ID: " + args[1]);
            return true;
        }

        List<UUID> sharedPlayers = shareService.getSharedPlayers(chestId);

        if (sharedPlayers.isEmpty()) {
            player.sendMessage(ChatColor.YELLOW + "Chest #" + chestId + " is not shared with anyone.");
            return true;
        }

        player.sendMessage(ChatColor.GREEN + "Chest #" + chestId + " is shared with:");
        for (UUID uuid : sharedPlayers) {
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            String perms = shareService.getPermissions(chestId, uuid);
            player.sendMessage(ChatColor.GRAY + "  - " + (name != null ? name : uuid.toString()) 
                + " (" + perms + ")");
        }

        return true;
    }

    /**
     * Send usage information to the player.
     */
    private void sendUsage(Player player, String label) {
        player.sendMessage(ChatColor.YELLOW + "Chest Sharing Commands:");
        player.sendMessage(ChatColor.GRAY + "  /" + label + " <chestId> <player> [VIEW|EDIT]" + ChatColor.WHITE + " - Share a chest");
        player.sendMessage(ChatColor.GRAY + "  /" + label + " revoke <chestId> <player>" + ChatColor.WHITE + " - Revoke access");
        player.sendMessage(ChatColor.GRAY + "  /" + label + " list <chestId>" + ChatColor.WHITE + " - List shared players");
    }
}
