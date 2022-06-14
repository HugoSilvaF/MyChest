package com.github.hugosilvaf2.mychest.dao;

import java.util.List;
import java.util.Optional;



public interface Dao<T> {
    
    Optional<T> find(String id);
    
    List<T> findAll();
    
    void save(T t);
    
    void update(T t, String[] params);
    
    void delete(T t);
    
}
