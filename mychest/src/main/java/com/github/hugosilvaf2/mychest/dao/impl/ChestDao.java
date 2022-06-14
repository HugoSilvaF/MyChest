package com.github.hugosilvaf2.mychest.dao.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.hugosilvaf2.mychest.Main;
import com.github.hugosilvaf2.mychest.dao.Dao;
import com.github.hugosilvaf2.mychest.entity.chest.Chest;
import com.github.hugosilvaf2.mychest.entity.chest.ChestSize;
import com.github.hugosilvaf2.mychest.utils.Serializator;
import com.github.hugosilvaf2.mychest.utils.Utils;

public class ChestDao implements Dao<Chest>{

    private static final String FIND = "SELECT * FROM chests where id=?";
    private static final String FIND_ALL = "SELECT * FROM chests ORDER BY id";
    private static final String DELETE = "DELETE FROM chests WHERE id=?";
    private static final String INSERT = "INSERT INTO chests (id, size, name, title, items) VALUES (DEFAULT,?,?,?,?);";
    private static final String UPDATE = "UPDATE chests SET size=?, name=?, title=?, items=? WHERE id=?";

    @Override
    public Optional<Chest> find(String id) {
        Chest chest = null;
        try {
            PreparedStatement preparedStatement =  Main.getMySQL().getConnection().prepareStatement(FIND);

            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                chest = new Chest(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("title"), ChestSize.valueOf(resultSet.getString("size")));
                Utils.updateMap(resultSet.getString("items"),  chest.getItems());
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(chest);
    }

    @Override
    public List<Chest> findAll() {
        List<Chest> chests = new ArrayList<>();
        
        try {
            PreparedStatement preparedStatement = Main.getMySQL().getConnection().prepareStatement(FIND_ALL);

            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                Chest chest = new Chest(resultSet.getInt("id"), resultSet.getString("name"), resultSet.getString("title"), ChestSize.valueOf(resultSet.getString("size")));
                Utils.updateMap(resultSet.getString("items"), chest.getItems());
                chests.add(chest);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return chests;
    }

    @Override
    public void save(Chest t) {
        try {
            PreparedStatement preparedStatement =  Main.getMySQL().getConnection().prepareStatement(INSERT, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, t.getChestSize().toString());
            preparedStatement.setString(2, t.getName());
            preparedStatement.setString(3, t.getTitle());
            preparedStatement.setString(4, new Serializator().serializeData(t.getItems()));

            preparedStatement.executeUpdate();
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            resultSet.next();
            t.setID(resultSet.getInt(1));
 
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(Chest t, String[] params) {
    try {
        PreparedStatement preparedStatement =  Main.getMySQL().getConnection().prepareStatement(UPDATE);

        preparedStatement.setString(1, t.getChestSize().toString());
        preparedStatement.setString(2, t.getName());
        preparedStatement.setString(3, t.getTitle());
        //erri esta aqui
        preparedStatement.setString(4, new Serializator().serializeData(t.getItems()));
        preparedStatement.setInt(5, t.getID());

        preparedStatement.executeUpdate();
        preparedStatement.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }
        
    }

    @Override
    public void delete(Chest t) {
        try {
            PreparedStatement preparedStatement =  Main.getMySQL().getConnection().prepareStatement(DELETE);
    
            preparedStatement.setInt(1, t.getID());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
    }
    
}
