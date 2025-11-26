package com.github.hugosilvaf2.mychest.commands;

import org.bukkit.entity.Player;

import com.github.hugosilvaf2.mychest.message.MessageHandler;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;

@CommandAlias("%chesthelp")
@Description("Show all available MyChest commands")
public class ChestHelpCommand extends BaseCommand {

    @Default
    public void onHelp(Player player) {
        MessageHandler.HELP_HEADER.send(player);
        MessageHandler.HELP_CHEST.send(player);
        MessageHandler.HELP_CHESTS.send(player);
        MessageHandler.HELP_CHESTNAME.send(player);
        MessageHandler.HELP_CHESTTITLE.send(player);
        MessageHandler.HELP_CHESTINFO.send(player);
        MessageHandler.HELP_CHESTDELETE.send(player);
        
        if (player.hasPermission("mychest.admin")) {
            MessageHandler.HELP_ADMIN_HEADER.send(player);
            MessageHandler.HELP_CHESTADMIN.send(player);
            MessageHandler.HELP_CHESTLIST.send(player);
            MessageHandler.HELP_CHESTRELOAD.send(player);
        }
        
        MessageHandler.HELP_FOOTER.send(player);
    }
}
