package com.github.hugosilvaf2.mychest.commands;

import java.util.Optional;

import org.bukkit.entity.Player;

import com.github.hugosilvaf2.mychest.Main;
import com.github.hugosilvaf2.mychest.controller.ChestController;
import com.github.hugosilvaf2.mychest.controller.UserController;
import com.github.hugosilvaf2.mychest.entity.User;
import com.github.hugosilvaf2.mychest.entity.chest.Chest;
import com.github.hugosilvaf2.mychest.message.MessageHandler;
import com.github.hugosilvaf2.mychest.message.Replaces;
import com.github.hugosilvaf2.mychest.service.SessionService;
import com.github.hugosilvaf2.mychest.session.Session;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;

@CommandAlias("%chestdelete")
@Description("Delete one of your chests")
public class ChestDeleteCommand extends BaseCommand {

    @Dependency
    private ChestController chestController;

    @Dependency
    private UserController userController;

    @Dependency
    private SessionService sessionService;

    @Default
    public void onDeleteChest(Player player, String name) {
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
                
                // Close inventory for all viewers if the chest is open
                Optional<Session> optionalSession = sessionService.getSessionByID(chest.getID());
                if (optionalSession.isPresent()) {
                    Session session = optionalSession.get();
                    session.getViewers().forEach(viewer -> {
                        if (viewer != null && viewer.isOnline()) {
                            viewer.closeInventory();
                        }
                    });
                    sessionService.removeSessionByID(chest.getID());
                }

                // Remove chest from user and delete from database
                user.removeChestID(chest.getID());
                userController.getUserService().updateUser(user);
                chestController.getChestservice().deleteChest(chest);

                MessageHandler.CHEST_DELETED_SUCCESSFULLY.send(player, 
                    new Replaces().add("name", chest.getName()).add("id", String.valueOf(chest.getID())));
                found = true;
                break;
            }
        }

        if (!found) {
            MessageHandler.NOT_FOUND_CHEST.send(player, new Replaces().add("name", name));
        }
    }
}
