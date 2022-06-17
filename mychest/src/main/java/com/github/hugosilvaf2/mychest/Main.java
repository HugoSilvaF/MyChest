package com.github.hugosilvaf2.mychest;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.github.hugosilvaf2.mychest.commands.ChestAdminCommand;
import com.github.hugosilvaf2.mychest.commands.ChestEditCommand;
import com.github.hugosilvaf2.mychest.commands.ChestsCommand;
import com.github.hugosilvaf2.mychest.controller.ChestController;
import com.github.hugosilvaf2.mychest.controller.UserController;
import com.github.hugosilvaf2.mychest.entity.chest.Group;
import com.github.hugosilvaf2.mychest.listener.InventoryListener;
import com.github.hugosilvaf2.mychest.service.ChestService;
import com.github.hugosilvaf2.mychest.service.SessionService;
import com.github.hugosilvaf2.mychest.service.UserService;
import com.github.hugosilvaf2.mychest.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import co.aikar.commands.BukkitCommandManager;
import net.milkbowl.vault.permission.Permission;
import pro.husk.mysql.MySQL;

public class Main extends JavaPlugin {

   public static void main(String[] args) {

   }

   private static MySQL mysql;

   private BukkitCommandManager commandManager;

   private static SessionService sessionService;
   private static UserController userController;
   private static ChestController chestController;

   private static List<Group> groups;

   private static File messageConfigFile;
   private static FileConfiguration messageConfig;

   private static FileConfiguration defaultConfig;

   private static Permission perms = null;

   private String B = "/";
   private String DP = ":";
   private String URL, HOST, PORT, DATABASE, USERNAME, PASSWORD;

   @Override
   public void onLoad() {
      disableMySQL();
   }

   @Override
   public void onEnable() {
      // registras as denependias dos comandos chestcontroller e usercontroller
      saveDefaultConfig();
      this.getLogger().info("Starting mysql...");
      this.initMySQL();
      this.getLogger().info("Mysql started successfully");

      this.getLogger().info("Starting controllers...");
      this.startControllers();
      this.getLogger().info("Controllers started successfully");

      this.getLogger().info("Registering listeners...");
      Bukkit.getPluginManager().registerEvents(new InventoryListener(), this);
      this.getLogger().info("Listeners registered successfully");

      Main.defaultConfig = getConfig();

      this.getLogger().info("Loading groups...");
      this.loadGroups();
      this.getLogger().info("Grous loaded succesfully!");

      this.commandManager = new BukkitCommandManager(this);

      this.getLogger().info("Registering replacements...");
      registerReplacementes();
      this.getLogger().info("Replacements registered successfully");

      setupPermissions();

      this.getLogger().info("Registering dependencies...");
      registerDependencies();
      this.getLogger().info("Dependencies registered successfully");

      getLogger().info("Registering Commands in ACFCommand...");
      this.commandManager.registerCommand(new ChestsCommand());
      this.commandManager.registerCommand(new ChestAdminCommand());
      this.commandManager.registerCommand(new ChestEditCommand());
      this.commandManager.registerCommand(new ChestsCommand());
      getLogger().info("Command registered succesfully");

      initMessageConfig();

   }

   @Override
   public void onDisable() {
      disableMySQL();
   }

   private void disableMySQL() {
      try {
         if (mysql != null) {
            mysql.closeConnection();
            mysql = null;
         }
      } catch (SQLException var2) {
         var2.printStackTrace();
      }

      // remove sessions and close all chest inventories that was open
      getSessionService().gSessions().forEach(c -> {
         c.getViewers().forEach(d -> {
         if(d != null) {
            if(d.isOnline()) {
               d.closeInventory();
            }
            getSessionService().removeSessionByID(c.getChest().getID());
         }
         });
      });
   }

   public static FileConfiguration getDefaultConfig() {
      return defaultConfig;
   }

   private void initMySQL() {
      loadMysqlConnectionCredentials();
      if (mysql == null) {
         mysql = new MySQL(URL, USERNAME, PASSWORD);
      }
   }

   private void registerDependencies() {
      commandManager.registerDependency(Permission.class, perms);
      commandManager.registerDependency(UserController.class, userController);
      commandManager.registerDependency(ChestController.class, chestController);
      commandManager.registerDependency(SessionService.class, sessionService);
   }

   private void registerReplacementes() {
      commandManager.getCommandReplacements().addReplacement("chest", Utils.parseAlias(getDefaultConfig().getString("commands_alias.chest")));
      commandManager.getCommandReplacements().addReplacement("chests", Utils.parseAlias(getDefaultConfig().getString("commands_alias.chests")));
      commandManager.getCommandReplacements().addReplacement("chestname", Utils.parseAlias(getDefaultConfig().getString("commands_alias.chestname")));
      commandManager.getCommandReplacements().addReplacement("chestadmin", Utils.parseAlias(getDefaultConfig().getString("commands_alias.chestadmin")));
      commandManager.getCommandReplacements().addReplacement("chesttitle", Utils.parseAlias(getDefaultConfig().getString("commands_alias.chesttitle")));
   }

   private void loadMysqlConnectionCredentials() {
      this.URL = "jdbc:mysql://";
      this.HOST = this.getConfig().getString("mysql.host");
      this.PORT = this.getConfig().getString("mysql.port");
      this.DATABASE = this.getConfig().getString("mysql.database");
      this.USERNAME = this.getConfig().getString("mysql.username");
      this.PASSWORD = this.getConfig().getString("mysql.password");
      this.URL = URL + HOST + DP + PORT + B + DATABASE;
   }

   public static FileConfiguration getMessageConfig() {
      return messageConfig;
   }

   public static List<Group> getGroups() {
      return groups;
   }

   private void loadGroups() {
      Main.groups = new ArrayList<>();
      getDefaultConfig().getConfigurationSection("permissions.groups").getKeys(false).forEach(a -> {
         groups.add(new Group(a, getDefaultConfig().getInt("permissions.groups." + a + ".chests"),
               getDefaultConfig().getString("permissions.groups." + a + ".size")));
      });
      ;
   }

   private void initMessageConfig() {

      messageConfigFile = new File(getDataFolder(), "messages.yml");
      if (!messageConfigFile.exists()) {
         getLogger().info("Creating a messages.yml config...");
         messageConfigFile.getParentFile().mkdirs();
         saveResource("messages.yml", false);

         getLogger().info("Messages.yml created succesfully!");
      }

      messageConfig = new YamlConfiguration();
      try {
         getLogger().info("Loading Messages.yml...");
         messageConfig.load(messageConfigFile);
         getLogger().info("Messages.yml loaded succesfully");
      } catch (IOException | InvalidConfigurationException e) {
         e.printStackTrace();
      }
   }

   private boolean setupPermissions() {
      RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
      perms = rsp.getProvider();
      return perms != null;
   }

   private void startControllers() {
      sessionService = new SessionService();
      userController = new UserController(new UserService(mysql));
      chestController = new ChestController(new ChestService(mysql));
   }

   public static MySQL getMySQL() {
      return mysql;
   }

   public static Permission getPermissions() {
      return perms;
   }

   public static SessionService getSessionService() {
      return sessionService;
   }

   public static UserController getUserController() {
      return userController;
   }

   public static ChestController getChestController() {
      return chestController;
   }

}
