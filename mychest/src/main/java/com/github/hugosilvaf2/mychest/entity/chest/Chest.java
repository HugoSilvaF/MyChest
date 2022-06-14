package com.github.hugosilvaf2.mychest.entity.chest;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Chest{

    private int id;
    private String name;
    private String title;
    private ChestSize chestSize;
    private ConcurrentHashMap<Integer, ItemStack> items;

    public Chest(int id, String name, String title, ChestSize chestSize) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.chestSize = chestSize;
        this.items = new ConcurrentHashMap<Integer,ItemStack>();
        this.setItemsDefault();
    }

    public int getID() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getTitle() {
        return this.title;
    }

    public ChestSize getChestSize() {
        return this.chestSize;
    }

    public ConcurrentHashMap<Integer, ItemStack> getItems() {
        return this.items;
    }

    
    public Chest setID(int id) {
        this.id = id;
        return this;
    }

    public Chest setName(String name) {
        this.name = name;
        return this;
    }

    public Chest setTitle(String title) {
        this.title = title;
        return this;
    }

    public Chest setItems(ConcurrentHashMap<Integer, ItemStack> items) {
        items.forEach((a, b) -> {
            if(a >= 0 && a < chestSize.getSize()) {
                items.replace(a, b);
            }
        });
        return this;
    }

    private void setItemsDefault() {
        for(int i = 0; i < chestSize.getSize(); i++) {
            this.items.put(i, new ItemStack(Material.AIR));
        }
    }

}
