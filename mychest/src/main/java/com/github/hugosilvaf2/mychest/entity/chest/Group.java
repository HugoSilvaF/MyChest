package com.github.hugosilvaf2.mychest.entity.chest;

public class Group {

    private int chestsLimit;
    private String groupName;
    private ChestSize chestSize;

    public Group(String groupName, int chestsLimits, String chestSize) {
        this.groupName = groupName;
        this.chestsLimit = chestsLimits;
        this.chestSize = ChestSize.valueOf(chestSize);
    }

    public String getGroupName() {
        return this.groupName;
    }

    public int getChestsLimit() {
        return this.chestsLimit;
    }

    public ChestSize getChestSize() {
        return this.chestSize;
    }

}
