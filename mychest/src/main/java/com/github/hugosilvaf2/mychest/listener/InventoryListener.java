package com.github.hugosilvaf2.mychest.listener;

import java.util.Optional;

import com.github.hugosilvaf2.mychest.Main;
import com.github.hugosilvaf2.mychest.controller.ChestController;
import com.github.hugosilvaf2.mychest.entity.User;
import com.github.hugosilvaf2.mychest.entity.chest.Chest;
import com.github.hugosilvaf2.mychest.service.SessionService;
import com.github.hugosilvaf2.mychest.session.Session;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        ChestController chestController = Main.getChestController();
        Optional<User> optionalUser = Main.getUserController().getUserService()
                .getUserByID(((Player) event.getPlayer()).getUniqueId().toString());
        if (optionalUser.isPresent()) {
            Optional<Integer> optionalChest = optionalUser.get().getChestsID().stream().filter(c -> {
                Optional<Chest> chest = chestController.getChestservice().getChestByID(String.valueOf(c));
                if (chest.isPresent() && chest.get().getTitle().equals(event.getPlayer().getOpenInventory().getTitle())
                        && event.getInventory().getContents().length == chest.get().getChestSize().getSize()) {
                    return true;
                }
                return false;
            }).findFirst();
            if (optionalChest.isPresent()) {
                // Save the chest contents
                chestController.updateItems(
                        chestController.getChestservice().getChestByID(String.valueOf(optionalChest.get())).get(),
                        event.getInventory());
                        
                Optional<Session> optionalSession = Main.getSessionService()
                        .getSessionByID(optionalChest.get());
                if (optionalSession.isPresent()) {
                    Session session = optionalSession.get();
                    // Remove the player from the session
                    session.removeViewer((Player) event.getPlayer());
                    // If no more viewers, remove the session
                    if (session.getViewers().size() == 0) {
                        Main.getSessionService().removeSessionByID(optionalChest.get());
                    }
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        SessionService sessionService = Main.getSessionService();
        Optional<Session> optional = sessionService.getSessionByViewer(player);

        if (optional.isPresent()) {
            Session session = optional.get();
            
            // Update the session inventory with current contents
            Bukkit.getScheduler().runTaskLater(
                Bukkit.getPluginManager().getPlugin("mychest"),
                () -> {
                    // Sync the session inventory with the modified inventory
                    session.getInventory().setContents(event.getInventory().getContents());
                    // Update all other viewers in real-time
                    session.updateInventoryOfViewersExcept(player);
                },
                1L // 1 tick delay to ensure the click is processed
            );
            
            if(Main.getDefaultConfig().getBoolean("save_chest_every_click")) {
                Bukkit.getScheduler().runTaskLater(
                    Bukkit.getPluginManager().getPlugin("mychest"),
                    () -> Main.getChestController().updateItems(session.getChest(), event.getInventory()),
                    2L // Slightly delayed to ensure inventory is updated
                );
            }
        }
    }

}
