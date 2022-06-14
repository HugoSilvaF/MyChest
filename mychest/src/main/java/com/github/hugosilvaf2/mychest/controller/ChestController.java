package com.github.hugosilvaf2.mychest.controller;

import java.util.Optional;

import com.github.hugosilvaf2.mychest.Main;
import com.github.hugosilvaf2.mychest.entity.User;
import com.github.hugosilvaf2.mychest.entity.chest.Chest;
import com.github.hugosilvaf2.mychest.service.ChestService;
import com.github.hugosilvaf2.mychest.service.SessionService;
import com.github.hugosilvaf2.mychest.session.Session;
import com.github.hugosilvaf2.mychest.utils.Utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChestController {

    private ChestService chestService;
    private SessionService sessionService;

    public ChestController(ChestService chestService) {
        this.chestService = chestService;
        this.sessionService = Main.getSessionService();
    }

    public ChestService getChestservice() {
        return chestService;
    }

    public Optional<Chest> getChestOfUserByName(User user, String name) {
        Optional<Chest> optional = Optional.empty();
        chestService.getChestByID(String.valueOf(user.getChestsID().stream().filter(a -> {
            Optional<Chest> o = chestService.getChestByID(String.valueOf(a));
            if(o.isPresent() && o.get().getName().equalsIgnoreCase(name)) {
                return true;
            }
            return false;
        }).findAny().get()));
        return optional;
    }

    public ChestController openChestToPlayer(Player player, String id) {
        Optional<Chest> optional = chestService.getChestByID(id);
        if(optional.isPresent()) {
            Session session;
            Chest chest = optional.get();
            Optional<Session> optionalS = sessionService.getSessionByID(Integer.parseInt(id));
            if(optionalS.isPresent()) {
                session = optionalS.get();
                if(!session.getViewers().contains(player)) {
                    session.addViewer(player);
                }
            player.openInventory(session.getInventory());
            } else {
                session = new Session(chest);
                if(!session.getViewers().contains(player)) {
                    session.addViewer(player);
                }
                player.openInventory(session.getInventory());
                sessionService.addSession(session);
            }
        }
        
        return this;
    }

    public ChestController updateItems(Chest chest, Inventory inventory) {
        ItemStack[] contents = inventory.getContents();
        for(int i = 0; i < contents.length; i++) {
            ItemStack item = contents[i];
            if(item == null) {
                item = new ItemStack(Material.AIR);
            }
            Utils.putValueOrReplace(i, item, chest.getItems());
        }
        chestService.updateChest(chest);
        return this;
    }
    
    
}
