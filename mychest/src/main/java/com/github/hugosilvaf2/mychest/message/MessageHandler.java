package com.github.hugosilvaf2.mychest.message;

import java.util.List;

import com.github.hugosilvaf2.mychest.Main;
import com.github.hugosilvaf2.mychest.message.Replaces.Replacer;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public enum MessageHandler {

    YOU_DONT_HAVE_ANY_CHEST("you_dont_have_any_chest"),
    USER_DONT_HAVE_ANY_CHEST("user_dont_have_any_chest"),
    USER_CHESTS("user_chests"),
    USER_CHESTS_INFO("user_chests_info"),
    YOUR_CHESTS("your_chests"),
    YOUR_CHESTS_INFO("your_chests_info"),
    OPENING_CHEST("opening_chest"),
    NOT_FOUND_CHEST("not_found_chest"),
    TITLE_CHANGED_SUCCESSFULLY("title_changed_successfully"),
    NAME_CHANGED_SUCCESSFULLY("name_changed_successfully"),
    NOT_PERMISSION("not_permission"),
    CHEST_DELETED_SUCCESSFULLY("chest_deleted_successfully"),
    CONFIG_RELOADED("config_reloaded"),
    CONFIG_RELOAD_FAILED("config_reload_failed"),
    CHEST_INFO("chest_info"),
    NO_CHESTS_IN_DATABASE("no_chests_in_database"),
    CHEST_LIST_HEADER("chest_list_header"),
    CHEST_LIST_ITEM("chest_list_item"),
    CHEST_LIST_FOOTER("chest_list_footer"),
    HELP_HEADER("help_header"),
    HELP_CHEST("help_chest"),
    HELP_CHESTS("help_chests"),
    HELP_CHESTNAME("help_chestname"),
    HELP_CHESTTITLE("help_chesttitle"),
    HELP_CHESTINFO("help_chestinfo"),
    HELP_CHESTDELETE("help_chestdelete"),
    HELP_ADMIN_HEADER("help_admin_header"),
    HELP_CHESTADMIN("help_chestadmin"),
    HELP_CHESTLIST("help_chestlist"),
    HELP_CHESTRELOAD("help_chestreload"),
    HELP_FOOTER("help_footer"),
    CHEST_LIMIT_REACHED("chest_limit_reached");

    private String path;

    private Replaces replaces;

    private MessageHandler(String path) {
        this.path = path;
        this.replaces = new Replaces().set(Replacer.COLOR.getValue(), Replacer.TO_COLOR.getValue()).set(Replacer.PLAYER.getValue(), "playername");
    }

    private String getPath() {
        return path;
    }

    private List<String> getList(FileConfiguration file) {
        return file.getStringList(getPath());
    }

    public void send(Player player) {
        send(player, Main.getMessageConfig());
    }

    public void send(Player player, Replaces replaces) {
        send(player, Main.getMessageConfig(), replaces);
    }

    public void send(Player player, FileConfiguration file) {
        send(player, file, replaces);
    }

    public void send(Player player, FileConfiguration file, Replaces replaces) {
                getList(file).forEach(consumer -> {
            player.sendMessage(replaces.set(Replacer.PLAYER.getValue(), player.getName()).getTextReplaced(consumer));
        });
    }
}
