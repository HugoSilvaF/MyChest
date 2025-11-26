package com.github.hugosilvaf2.mychest.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
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
import com.github.hugosilvaf2.mychest.utils.Utils;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Dependency;
import co.aikar.commands.annotation.Description;

@CommandAlias("%chestname")
@Description("Change the chest name")
public class ChestEditCommand extends BaseCommand {

    @Dependency
    private ChestController chestController;

    @Dependency
    private UserController userController;

    @Dependency
    private SessionService sessionService;

    @Default
    public void onBauName(Player player, String name, String newName) {
        setChestProperty(0, player, name, newName);
    }

    @CommandAlias("%chesttitle")
    @Description("Change the chest title")
    public void onBauTitulo(Player player, String name, String title) {
        setChestProperty(1, player, name, title);
    }

    private void setChestProperty(int property, Player player, String name, String newValue) {
        if (!player.hasPermission("mychest.use")) {
            MessageHandler.NOT_PERMISSION.send(player);
            return;
        }
        
        Optional<User> optional = userController.getUserService().getUserByID(player.getUniqueId().toString());
        if (optional.isPresent()) {
            User user = optional.get();
            if (!user.getChestsID().stream().filter(c -> {
                Optional<Chest> optionalC = chestController.getChestservice().getChestByID(String.valueOf(c));
                if (optionalC.isPresent()) {
                    Chest chest = optionalC.get();
                    if (chest.getName().equals(name)) {
                        String oldName = chest.getName();
                        String oldTitle = chest.getTitle();
                        if (property == 0) {
                            // Validate name length
                            int maxNameLength = Main.getDefaultConfig().getInt("max_chest_name_length", 32);
                            String validatedName = newValue.length() > maxNameLength ? newValue.substring(0, maxNameLength) : newValue;
                            
                            chestController.getChestservice().updateChest(chest.setName(validatedName));
                            MessageHandler.NAME_CHANGED_SUCCESSFULLY.send(player,
                                    new Replaces().add("oldname", oldName).add("newname", validatedName));
                            return true;
                        }
                        if (property == 1) {
                            // FECHAR TODOS INVENTÁRIOS, CRIAR UM COM O NOVO TITULO E ABRIR-LOS NOVAMENTE
                            // PARA OS JOGADORES
                            // Validate title length
                            int maxTitleLength = Main.getDefaultConfig().getInt("max_chest_title_length", 32);
                            String validatedTitle = newValue.length() > maxTitleLength ? newValue.substring(0, maxTitleLength) : newValue;
                            String fixedTitle = Utils.fixTitle(validatedTitle);
                            
                            chestController.getChestservice().updateChest(chest.setTitle(fixedTitle));
                            MessageHandler.TITLE_CHANGED_SUCCESSFULLY.send(player,
                                    new Replaces().add("oldtitle", oldTitle).add("newtitle", fixedTitle));
                            Optional<Session> optionalS = sessionService.getSessionByID(chest.getID());
                            if (optionalS.isPresent()) {
                                List<Player> viewers = new ArrayList<>(optionalS.get().getViewers());
                                if (optionalS.isPresent()) {
                                    optionalS.get().getViewers().forEach(b -> {
                                        b.closeInventory();
                                    });
                                    sessionService.removeSessionByID(chest.getID());
                                    viewers.forEach(b -> {
                                        Bukkit.getScheduler().runTaskLater(
                                                Bukkit.getPluginManager().getPlugin("mychest"),
                                                new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        chestController.openChestToPlayer(b,
                                                                String.valueOf(chest.getID()));
                                                    }
                                                }, 1L);
                                    });

                                }
                            }
                        }
                        return true;
                    }
                }
                return false;
            }).findFirst().isPresent()) {
                MessageHandler.NOT_FOUND_CHEST.send(player, new Replaces().add("name", name));
            }
        }
    }

}
