package com.github.hugosilvaf2.mychest.utils;

import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class Serializator {

  private String SECTION = "items";
  private String END_PUNCTUATION = ".";

  private YamlConfiguration configuration;

  public Serializator() {
    this.configuration = new YamlConfiguration();
  }

  public String serializeData(ConcurrentHashMap<Integer, ItemStack> items) {
    if (items != null && !items.isEmpty()) {
      items.forEach((k, v) ->{
        configuration.set(SECTION + END_PUNCTUATION + k, v);
      });
      return configuration.saveToString();
    }
    return new String();
  }

  public ConcurrentHashMap<Integer, ItemStack> deSerializeData(String data) {
    ConcurrentHashMap<Integer, ItemStack> items = new ConcurrentHashMap<>();
    try {
      if (data != null) {
        configuration.loadFromString(data);
        if (
          configuration.contains(SECTION) &&
          !configuration.getKeys(false).isEmpty()
        ) {
          configuration
            .getConfigurationSection(SECTION)
            .getKeys(false)
            .forEach(
              consumer -> {
                Integer key = Integer.parseInt(consumer);
                ItemStack value = configuration.getItemStack(SECTION + END_PUNCTUATION + consumer);
                if(items.containsKey(key)) {
                  items.replace(key, value);
                } else {
                  items.put(key, value);
                }
              }
            );
        }
      }
    } catch (InvalidConfigurationException e) {
      e.printStackTrace();
    }

    return items;
  }
}
