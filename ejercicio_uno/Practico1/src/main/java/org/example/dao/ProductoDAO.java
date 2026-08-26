package org.example.dao;

import org.example.entities.Producto;

import java.sql.Connection;
import java.util.List;

public class ProductoDAO {
    Connection connection;

    public ProductoDAO(Connection conn){
        this.connection = conn;
    }

    public List<Producto> getAllProducts(){
        return null;
    }


    
}
