package org.example;

import org.example.dao.ClienteDAO;
import org.example.dao.FacturaDAO;
import org.example.dao.FacturaProductoDAO;
import org.example.dao.ProductoDAO;
import org.example.dto.ClientesFacturadosDTO;
import org.example.dto.ProductoRecaudacionDTO;
import org.example.entities.Producto;
import org.example.factory.AbstractFactory;
import org.example.utils.HelperMySQL;

import java.util.List;

public class Main {
    static void main() {

        HelperMySQL mySQLdb = new HelperMySQL();

        try {
            mySQLdb.setUpDatabase();

            AbstractFactory mySQLFactory = AbstractFactory.getDAOFactory(1, mySQLdb.getConnection());

            System.out.println("Base de datos lista");

            // Punto 3: producto que mas recaudo
            ProductoDAO productoDAO = mySQLFactory.getProductoDAO();
            ProductoRecaudacionDTO productoMasRecaudado = productoDAO.getProductoMasRecaudado();
            System.out.println("Producto que mas recaudo: " + productoMasRecaudado);

            // Punto 4: clientes ordenados por facturacion
            ClienteDAO clienteDAO = mySQLFactory.getClienteDAO();
            List<ClientesFacturadosDTO> clientesOrdenados = clienteDAO.getClientesOrdenadosPorFacturacion();
            System.out.println("Clientes ordenados por facturacion:");
            for (ClientesFacturadosDTO cliente : clientesOrdenados) {
                System.out.println(cliente);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            mySQLdb.closeConnection();

        }
    }
}
