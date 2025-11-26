package com.github.hugosilvaf2.mychest.commands;

import org.bukkit.entity.Player;

import com.github.hugosilvaf2.mychest.Main;
import com.github.hugosilvaf2.mychest.message.MessageHandler;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;

@CommandAlias("%chestreload")
@Description("Reload the plugin configuration")
public class ChestReloadCommand extends BaseCommand {

    @Default
    public void onReload(Player player) {
        if (!player.hasPermission("mychest.admin")) {
            MessageHandler.NOT_PERMISSION.send(player);
            return;
        }

        try {
            Main.getInstance().reloadConfiguration();
            MessageHandler.CONFIG_RELOADED.send(player);
        } catch (Exception e) {
            MessageHandler.CONFIG_RELOAD_FAILED.send(player);
            e.printStackTrace();
        }
    }
}
