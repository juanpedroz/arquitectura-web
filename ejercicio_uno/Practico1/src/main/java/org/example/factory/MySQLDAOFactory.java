package org.example.factory;

import org.example.dao.ClienteDAO;
import org.example.dao.ProductoDAO;
import org.example.dao.FacturaDAO;
import org.example.dao.FacturaProductoDAO;

import java.sql.Connection;

public class MySQLDAOFactory extends AbstractFactory {
    private static MySQLDAOFactory instance = null;
    public static Connection conn;

    private MySQLDAOFactory(Connection connection) {
        conn = connection;
    }

    public static synchronized MySQLDAOFactory getInstance(Connection connection) {
        if (instance == null) {
            instance = new MySQLDAOFactory(connection);
        }
        return instance;
    }

    @Override
    public ClienteDAO getClienteDAO() {
        return new ClienteDAO(conn);
    }

    @Override
    public FacturaDAO getFacturaDAO() {
        return null;
    }

    @Override
    public ProductoDAO getProductoDAO() {
        return null;
    }

    @Override
    public FacturaProductoDAO getFacturaProductoDAO() {
        return new FacturaProductoDAO(conn);
    }
}
