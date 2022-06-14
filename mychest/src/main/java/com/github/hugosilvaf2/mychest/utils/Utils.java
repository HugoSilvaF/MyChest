package com.github.hugosilvaf2.mychest.utils;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class Utils {

    public static String fixTitle(String title) {
        return (title.length() >= 32 ? title.substring(0, 32) : title).replaceAll("&", "§");
    }

    public static void putValueOrReplace(Integer k, ItemStack v, ConcurrentHashMap<Integer, ItemStack> map) {
        if (map.containsKey(k)) {
            map.replace(k, v);
        } else {
            map.put(k, v);
        }
    }

    public static void updateMap(ItemStack[] os, ConcurrentHashMap<Integer, ItemStack> map) {
        for (int i = 0; i < os.length; i++) {
            putValueOrReplace(i, os[i], map);
        }
    }

    public static void updateMap(ConcurrentHashMap<Integer, ItemStack> map1,
            ConcurrentHashMap<Integer, ItemStack> map2) {
        map1.forEach((k, v) -> {
            if (v == null) {
                v = new ItemStack(Material.AIR);
            }
            putValueOrReplace(k, v, map2);
        });

    }

    public static void updateMap(String data, ConcurrentHashMap<Integer, ItemStack> map) {
        updateMap(new Serializator().deSerializeData(data), map);
    }

    public static String parseAlias(String value) {
        StringBuilder aliases = new StringBuilder();
        Stream.of(Utils.parseToStringArray(value)).forEach(c -> {
            if (!aliases.toString().endsWith("|") && !aliases.isEmpty()) {
                aliases.append("|");
            }
            aliases.append(c);
        });
        return aliases.toString();
    }

    public static String[] parseToStringArray(String a) {
        return a.replace("[", "").replace("]", "").split(", ");
    }

    public static Integer[] parseToInteger(String a) {
        int[] parsed = parserToArrayInt(a);
        Integer[] i = new Integer[parsed.length];
        for (int j = 0; j < parsed.length; j++) {
            i[j] = Integer.parseInt("" + parsed[j]);
        }
        return i;

    }

    public static int[] parserToArrayInt(String a) {
        int[] p = new int[] {};
        Pattern pattern = Pattern.compile("\\[|\\]");
        if (Optional.ofNullable(a).isPresent() && !a.isEmpty() && pattern.matcher(a).find()) {
            String b = pattern.matcher(a).replaceAll("");
            if (!b.isEmpty()) {
                if (b.contains(", ")) {
                    String[] c = b.split(", ");
                    p = new int[c.length];
                    for (int i = 0; i < c.length; i++) {
                        p[i] = Integer.parseInt(c[i]);
                    }
                } else {
                    p = new int[] { Integer.parseInt(b) };
                }
            }

        }
        return p;
    }

}
