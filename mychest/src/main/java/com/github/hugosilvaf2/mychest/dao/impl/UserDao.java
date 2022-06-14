package com.github.hugosilvaf2.mychest.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import com.github.hugosilvaf2.mychest.Main;
import com.github.hugosilvaf2.mychest.dao.Dao;
import com.github.hugosilvaf2.mychest.entity.User;
import com.github.hugosilvaf2.mychest.utils.Utils;

import org.bukkit.Bukkit;

public class UserDao implements Dao<User> {

    private static final String FIND = "SELECT * FROM chests_users where uuid=?";
    private static final String FIND_ALL = "SELECT * FROM chests_users ORDER BY uuid";
    private static final String DELETE = "DELETE FROM chests_users WHERE uuid=?";
    private static final String INSERT = "INSERT INTO chests_users (uuid, name, chests) VALUES (?,?,?);";
    private static final String UPDATE = "UPDATE chests_users SET chests=? WHERE uuid=?";

    @Override
    public Optional<User> find(String id) {
        User user = null;
        try {
            PreparedStatement preparedStatement =  Main.getMySQL().getConnection().prepareStatement(FIND);

            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
              
                user = new User(resultSet.getString("name"), resultSet.getString("uuid"), new LinkedList<>(Arrays.asList(Utils.parseToInteger(resultSet.getString("chests")))));
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        
        try {
            PreparedStatement preparedStatement = Main.getMySQL().getConnection().prepareStatement(FIND_ALL);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
               users.add(new User(resultSet.getString("name"), resultSet.getString("uuid"), new LinkedList<>(Arrays.asList(Utils.parseToInteger(resultSet.getString("chests"))))));
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public void save(User t) {
        try {
            PreparedStatement preparedStatement =  Main.getMySQL().getConnection().prepareStatement(INSERT);

            preparedStatement.setString(1, t.getUUID());
            preparedStatement.setString(2, t.getName());
            preparedStatement.setString(3, t.getChestsID().toString());

            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(User t, String[] params) {
    try {
        
        PreparedStatement preparedStatement =  Main.getMySQL().getConnection().prepareStatement(UPDATE);

        preparedStatement.setString(1, t.getChestsID().toString());
        preparedStatement.setString(2, t.getUUID());
        Bukkit.getLogger().info("-- update user t.getUUID(): "  + t.getUUID());
        Bukkit.getLogger().info("user.getChestsID().toString(): "  + t.getChestsID().toString());
        
        preparedStatement.executeUpdate();
        preparedStatement.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
        
    }

    @Override
    public void delete(User t) {
        try {
            PreparedStatement preparedStatement =  Main.getMySQL().getConnection().prepareStatement(DELETE);
    
            preparedStatement.setString(1, t.getUUID());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        };
        
    }
    
}
