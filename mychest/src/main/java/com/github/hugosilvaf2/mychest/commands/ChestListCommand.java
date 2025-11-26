package com.github.hugosilvaf2.mychest.commands;

import java.util.List;

import org.bukkit.entity.Player;

import com.github.hugosilvaf2.mychest.controller.ChestController;
import com.github.hugosilvaf2.mychest.entity.chest.Chest;
import com.github.hugosilvaf2.mychest.message.MessageHandler;
import com.github.hugosilvaf2.mychest.message.Replaces;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;

@CommandAlias("%chestlist")
@Description("List all chests in the database (admin only)")
public class ChestListCommand extends BaseCommand {

    @Dependency
    private ChestController chestController;

    @Default
    public void onChestList(Player player, @co.aikar.commands.annotation.Optional Integer page) {
        if (!player.hasPermission("mychest.admin")) {
            MessageHandler.NOT_PERMISSION.send(player);
            return;
        }

        List<Chest> allChests = chestController.getChestservice().getAllChests();
        
        if (allChests.isEmpty()) {
            MessageHandler.NO_CHESTS_IN_DATABASE.send(player);
            return;
        }

        int pageSize = 10;
        int currentPage = (page == null || page < 1) ? 1 : page;
        int totalPages = (int) Math.ceil((double) allChests.size() / pageSize);
        
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        int startIndex = (currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, allChests.size());

        MessageHandler.CHEST_LIST_HEADER.send(player, new Replaces()
            .add("page", String.valueOf(currentPage))
            .add("total", String.valueOf(totalPages))
            .add("count", String.valueOf(allChests.size())));

        for (int i = startIndex; i < endIndex; i++) {
            Chest chest = allChests.get(i);
            MessageHandler.CHEST_LIST_ITEM.send(player, new Replaces()
                .add("id", String.valueOf(chest.getID()))
                .add("name", chest.getName())
                .add("title", chest.getTitle())
                .add("size", chest.getChestSize().name()));
        }

        if (currentPage < totalPages) {
            MessageHandler.CHEST_LIST_FOOTER.send(player, new Replaces()
                .add("nextpage", String.valueOf(currentPage + 1)));
        }
    }
}
