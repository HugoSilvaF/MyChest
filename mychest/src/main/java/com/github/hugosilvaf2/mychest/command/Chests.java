package com.github.hugosilvaf2.mychest.command;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.github.hugosilvaf2.mychest.controller.ChestController;
import com.github.hugosilvaf2.mychest.controller.UserController;
import com.github.hugosilvaf2.mychest.entity.User;
import com.github.hugosilvaf2.mychest.entity.chest.Chest;
import com.github.hugosilvaf2.mychest.message.MessageHandler;
import com.github.hugosilvaf2.mychest.message.Replaces;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;

@CommandAlias("%chests")
@Description("List all your chests or of an especific player")
public class Chests extends BaseCommand{


  // criar os comandos de alterar o titulo
  // criar os comandos de alterar o tamanho do bau
  // criar os comandos de
    
  @Dependency
  private ChestController chestController;

  
  @Dependency
  private  UserController userController;

  @Default
  public void onChestsCommand(Player player, @co.aikar.commands.annotation.Optional String username) {
    AtomicReference<Chest> chest = new AtomicReference<>();
    Optional<User> user = userController.getUserService().getUserByID(player.getUniqueId().toString());
    if (username == null) {
      if (user.isPresent()) {
        MessageHandler.YOUR_CHESTS.send(player);
        user.get().getChestsID().forEach(c -> {
          chest.set(chestController.getChestservice().getChestByID(String.valueOf(c)).get());
          MessageHandler.YOUR_CHESTS_INFO.send(player, new Replaces().add("id", String.valueOf(chest.get().getID()))
              .add("name", chest.get().getName()).add("title", chest.get().getTitle()));
        });
      } else {
        MessageHandler.YOU_DONT_HAVE_ANY_CHEST.send(player);
      }
    } else {
      user = userController.getUserService().getUserByID(Bukkit.getOfflinePlayer(username).getUniqueId().toString());
      if (user.isPresent()) {
        MessageHandler.USER_CHESTS.send(player);
        user.get().getChestsID().forEach(c -> {
          Optional<Chest> optional = chestController.getChestservice().getChestByID(String.valueOf(c));
          if (optional.isPresent()) {
            chest.set(optional.get());
            MessageHandler.USER_CHESTS_INFO.send(player, new Replaces().add("id", String.valueOf(chest.get().getID()))
                .add("name", chest.get().getName()).add("title", chest.get().getTitle()));
          }
        });
      } else {
        MessageHandler.USER_DONT_HAVE_ANY_CHEST.send(player, new Replaces().add("target", username));
      }
    }
  }
}
