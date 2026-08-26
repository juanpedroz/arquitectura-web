package org.example.dao;

import java.sql.Connection;

public class ClienteDAO {
    private Connection connection;

    public ClienteDAO(Connection connection){
        this.connection = connection;

    }
    
}
