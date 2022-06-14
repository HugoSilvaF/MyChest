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
                chestController.updateItems(
                        chestController.getChestservice().getChestByID(String.valueOf(optionalChest.get())).get(),
                        event.getInventory());
                Optional<Session> optionalSession = Main.getSessionService()
                        .getSessionByID(optionalChest.get());
                if (optionalSession.isPresent() && optionalSession.get().getViewers().size() == 1) {
                    Main.getSessionService().removeSessionByID(optionalChest.get());
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
            session.updateInventoryOfViewersExcept(player);
            if(Main.getDefaultConfig().getBoolean("save_chest_every_click")) {
                Main.getChestController().updateItems(session.getChest(), event.getInventory());
            }
        }
    }

}
