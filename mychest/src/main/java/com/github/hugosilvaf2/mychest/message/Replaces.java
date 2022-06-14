package com.github.hugosilvaf2.mychest.message;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class Replaces {

    public enum Replacer{


        CHAR_VAR("$"),
        PLAYER("player"),
        COLOR("&"),
        TO_COLOR("§");

        private String value;
        Replacer(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private HashMap<String, String> map;

    public Replaces() {
        this.map = new HashMap<>();
    }

    public Replaces set(String key, String value) {
        if (map.containsKey(key)) {
            this.map.replace(key, value);
        }
        return this;
    }

    public Replaces add(String key, String value) {
        map.put(key, value);
        return this;
    }

    public Replaces addAll(Replaces replaces) {
        this.map.putAll(replaces.map);
        return this;
    }

    public String getTextReplaced(String text) {
        AtomicReference<String> replace = new AtomicReference<>();
        replace.set(text);
        map.forEach((k, v) -> {
            replace.set(replace.get().replace(Replacer.CHAR_VAR.value + k, v));
        });
        return replace.get().replaceAll(Replacer.COLOR.value, Replacer.TO_COLOR.value);
    }
}
