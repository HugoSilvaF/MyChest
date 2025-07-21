package com.github.hugosilvaf2.mychest.session;

import java.util.ArrayList;
import java.util.List;

import com.github.hugosilvaf2.mychest.entity.chest.Chest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class Session {

    private Chest chest;
    private Inventory inventory;
    private List<Player> viewers;

    public Session(Chest chest) {
        this.chest = chest;
        this.viewers = new ArrayList<>();
        this.inventory = Bukkit.createInventory(null, chest.getChestSize().getSize(), chest.getTitle());
        chest.getItems().forEach((k, v) -> {
            inventory.setItem(k, v);
        });
    }

    public Chest getChest() {
        return this.chest;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public Session updateInventoryOfViewers() {
        this.updateInventoryOfViewersExcept(null);
        return this;
    }

    public Session updateInventoryOfViewersExcept(Player player) {
        this.viewers.forEach(a -> {
            if(player != null) {
                if(!player.getUniqueId().toString().equals(a.getUniqueId().toString())) {
                    a.updateInventory();
                }
            } else {
                a.updateInventory();
            }
        });
        return this;
    }

    public Session addViewer(Player player) {
        this.viewers.add(player);
        return this;
    }

    public Session removeViewer(Player player) {
        this.viewers.remove(player);
        return this;
    }

    public List<Player> getViewers() {
        return viewers;
    }
    
}
