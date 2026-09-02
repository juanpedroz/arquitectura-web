package org.example;

import org.example.dao.ClienteDAO;
import org.example.dao.FacturaDAO;
import org.example.dao.FacturaProductoDAO;
import org.example.dao.ProductoDAO;
import org.example.entities.Producto;
import org.example.factory.AbstractFactory;
import org.example.utils.HelperMySQL;

public class Main {
    static void main() {

        HelperMySQL mySQLdb = new HelperMySQL();

        try {
            mySQLdb.setUpDatabase();

            AbstractFactory mySQLFactory = AbstractFactory.getDAOFactory(1, mySQLdb.getConnection());

            System.out.println("Base de datos lista");



        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            mySQLdb.closeConnection();

        }
    }
}
