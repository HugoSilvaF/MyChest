package com.github.hugosilvaf2.mychest.entity;

import java.util.LinkedList;

public class User {

    private String name;
    private String uuid;
    private LinkedList<Integer> chests;

    public User(String name, String uuid) {
        this(name, uuid, new LinkedList<>());
    }

    public User(String name, String uuid, LinkedList<Integer> chests) {
        this.name = name;
        this.uuid = uuid;
        this.chests = chests;
    }

    public String getName() {
        return this.name;
    }

    public String getUUID() {
        return this.uuid;
    }

    public LinkedList<Integer> getChestsID() {
        return this.chests;
    }

    public User setChestsID(LinkedList<Integer> chests) {
        this.chests = chests;
        return this;
    }


    public User addChestID(Integer chestID) {
        this.chests.add(chestID);
        return this;
    }
    
    public User removeChestID(Integer chestID) {
        this.chests.remove(chestID);
        return this;
    }
    
}
