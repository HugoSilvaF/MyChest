package com.github.hugosilvaf2.mychest.service;

import java.sql.SQLException;
import java.util.Optional;

import com.github.hugosilvaf2.mychest.dao.impl.ChestDao;
import com.github.hugosilvaf2.mychest.entity.chest.Chest;

import pro.husk.mysql.MySQL;

public class ChestService {

    private MySQL mysql;
    private ChestDao chestDao;
    private static String CREATE = "CREATE TABLE IF NOT EXISTS `chests` (`id` INT NOT NULL AUTO_INCREMENT,`size` TEXT,`name` TEXT,`title` TEXT,`items` LONGTEXT,PRIMARY KEY (`id`),INDEX (id));";
    
    public ChestService(MySQL mysql) {
     this.mysql = mysql; 
     this.chestDao = new ChestDao();
     try {
        this.mysql.update(CREATE);
    } catch (SQLException e) {
        e.printStackTrace();
    }
    }

    public Optional<Chest> getChestByID(String string) {
        return chestDao.find(string);
    }

    public ChestService saveChest(Chest chest) {
        chestDao.save(chest);
        return this;
    }

    public ChestService updateChest(Chest chest) {
        chestDao.update(chest, null);
        return this;
    }
    
    public ChestService deleteChest(Chest chest) {
        chestDao.delete(chest);
        return this;
    }
}
