package com.github.hugosilvaf2.mychest.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import net.milkbowl.vault.permission.Permission;

import java.util.Optional;
import com.github.hugosilvaf2.mychest.Main;
import com.github.hugosilvaf2.mychest.controller.ChestController;
import com.github.hugosilvaf2.mychest.controller.UserController;
import com.github.hugosilvaf2.mychest.entity.User;
import com.github.hugosilvaf2.mychest.entity.chest.Chest;
import com.github.hugosilvaf2.mychest.message.MessageHandler;
import com.github.hugosilvaf2.mychest.message.Replaces;
import org.bukkit.entity.Player;

@CommandAlias("%chest")
public class ChestCommand extends BaseCommand {

  @Dependency
  private ChestController chestController;

  @Dependency
  private UserController userController;

  @Dependency
  private Permission perms;

  private String permission = "mychest.";

  @Default
  public void openChest(Player player, String chestname) {
    User user = getUser(player);
    if (!perms.playerHas(player, "mychest.use")) {
      MessageHandler.NOT_PERMISSION.send(player);
      return;
    }

    // permissão, checar a quantidade de bau
    // checar o tamanho do bau
    if (!user.getChestsID().stream().filter(c -> {
      // found chest
      Optional<Chest> optionalChest = chestController.getChestservice().getChestByID(String.valueOf(c));
      if (optionalChest.isPresent() && optionalChest.get().getName().equalsIgnoreCase(chestname)) {
        MessageHandler.OPENING_CHEST.send(player, new Replaces().add("name", optionalChest.get().getName()).add("id",
            String.valueOf(optionalChest.get().getID())));
        chestController.openChestToPlayer(player, String.valueOf(c));
        return true;
      }
      return false;
    }).findFirst().isPresent()) {
      // not found chest
      Main.getGroups().stream().filter(a -> {
        //está criando multiplos bau por causo dos grupos
        // corrigir
        // tirar essa parte daqui
        if (perms.has(player, permission + a.getGroupName())) {
          if (user.getChestsID().size() < a.getChestsLimit()) {
            Chest chest = new Chest(0, chestname, chestname, a.getChestSize());

            save(chest, user);
            chestController.openChestToPlayer(player, String.valueOf(chest.getID()));
            
            MessageHandler.OPENING_CHEST.send(player, new Replaces().add("name", chest.getName()).add("id",
                String.valueOf(chest.getID())));
            return true;
          }
        }
		return false;
      }).findAny();

    }
  }

  private void save(Chest chest, User user) {
    chestController.getChestservice().saveChest(chest);
    user.addChestID(chest.getID());
    userController.getUserService().saveUser(user);
  }

  private User getUser(Player player) {
    if (userController.getUserService().getUserByID(player.getUniqueId().toString()).isPresent()) {
      return userController.getUserService().getUserByID(player.getUniqueId().toString()).get();
    } else {
      return new User(player.getName(), player.getUniqueId().toString());
    }
  }
}
