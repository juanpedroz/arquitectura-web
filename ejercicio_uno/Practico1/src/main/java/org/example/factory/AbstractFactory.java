package org.example.factory;

import org.example.dao.ClienteDAO;
import org.example.dao.ProductoDAO;
import org.example.dao.FacturaDAO;
import org.example.dao.FacturaProductoDAO;


public abstract class AbstractFactory {
    public static final int MYSQL_JDBC = 1;

    public abstract ClienteDAO getClienteDAO();
    public abstract FacturaDAO getFacturaDAO();
    public abstract ProductoDAO getProductoDAO();
    public abstract FacturaProductoDAO getFacturaProductoDAO();

    public static AbstractFactory getDAOFactory(int whichFactory) {
        switch (whichFactory) {
            case MYSQL_JDBC : {
                return MySQLDAOFactory.getInstance();
            }
            default: return null;
        }
    }
}
