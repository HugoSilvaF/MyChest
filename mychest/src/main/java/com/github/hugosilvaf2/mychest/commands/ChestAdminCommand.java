package com.github.hugosilvaf2.mychest.commands;

import java.util.Optional;

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

@CommandAlias("%chestadmin")
@Description("Open any chest of any player")
public class ChestAdminCommand extends BaseCommand{

    
  @Dependency
  private ChestController chestController;

    @Default
    public void onAdminCommand(Player player, String id) {
      Optional<Chest> optional = chestController.getChestservice().getChestByID(id);
      if (optional.isPresent()) {
        Chest chest = chestController.getChestservice().getChestByID(id).get();
        chestController.openChestToPlayer(player, id);
        MessageHandler.OPENING_CHEST.send(player, new Replaces().add("name", chest.getName()).add("id",
            String.valueOf(chest.getID())));
      } else {
        MessageHandler.NOT_FOUND_CHEST.send(player, new Replaces().add("id",
            String.valueOf(id)));
      }
    }
}
