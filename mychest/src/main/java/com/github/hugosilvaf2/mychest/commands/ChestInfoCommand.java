package com.github.hugosilvaf2.mychest.commands;

import java.util.Optional;

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

@CommandAlias("%chestinfo")
@Description("Show detailed information about a chest")
public class ChestInfoCommand extends BaseCommand {

    @Dependency
    private ChestController chestController;

    @Dependency
    private UserController userController;

    @Default
    public void onChestInfo(Player player, String name) {
        if (!player.hasPermission("mychest.use")) {
            MessageHandler.NOT_PERMISSION.send(player);
            return;
        }

        Optional<User> optionalUser = userController.getUserService().getUserByID(player.getUniqueId().toString());
        if (!optionalUser.isPresent()) {
            MessageHandler.YOU_DONT_HAVE_ANY_CHEST.send(player);
            return;
        }

        User user = optionalUser.get();
        boolean found = false;

        for (Integer chestId : user.getChestsID()) {
            Optional<Chest> optionalChest = chestController.getChestservice().getChestByID(String.valueOf(chestId));
            if (optionalChest.isPresent() && optionalChest.get().getName().equalsIgnoreCase(name)) {
                Chest chest = optionalChest.get();
                
                // Count non-empty slots
                long usedSlots = chest.getItems().values().stream()
                    .filter(item -> item != null && item.getType() != org.bukkit.Material.AIR)
                    .count();
                
                MessageHandler.CHEST_INFO.send(player, new Replaces()
                    .add("id", String.valueOf(chest.getID()))
                    .add("name", chest.getName())
                    .add("title", chest.getTitle())
                    .add("size", chest.getChestSize().name())
                    .add("slots", String.valueOf(chest.getChestSize().getSize()))
                    .add("used", String.valueOf(usedSlots)));
                found = true;
                break;
            }
        }

        if (!found) {
            MessageHandler.NOT_FOUND_CHEST.send(player, new Replaces().add("name", name));
        }
    }
}
