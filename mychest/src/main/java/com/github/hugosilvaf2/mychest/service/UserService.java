package com.github.hugosilvaf2.mychest.service;

import java.sql.SQLException;
import java.util.Optional;

import com.github.hugosilvaf2.mychest.dao.impl.UserDao;
import com.github.hugosilvaf2.mychest.entity.User;

import pro.husk.mysql.MySQL;

public class UserService {

    private MySQL mysql;
    private UserDao userDao;
    private String CREATE = "CREATE TABLE IF NOT EXISTS `chests_users` (`uuid` TEXT NOT NULL, `name` TEXT NOT NULL, `chests` TEXT);";

    public UserService(MySQL mysql) {
        this.mysql = mysql;
        this.userDao = new UserDao();
        try {
            this.mysql.update(CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Optional<User> getUserByID(String id) {
        return userDao.find(id);
    }

    public UserService saveUser(User user) {
        userDao.save(user);
        return this;
    }

    public UserService updateUser(User user) {
        userDao.update(user, null);
        return this;
    }
    
    public UserService deleteUser(User user) {
        userDao.delete(user);
        return this;
    }
}
