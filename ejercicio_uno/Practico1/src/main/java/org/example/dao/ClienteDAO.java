package org.example.dao;

import org.example.dto.ClientesFacturadosDTO;
import org.example.entities.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAO {
    private Connection connection;

    public ClienteDAO(Connection connection){
        this.connection = connection;
    }

    public List<Cliente> getAll(){
        String query = "SELECT * FROM Cliente";
        ArrayList<Cliente> clientes = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                clientes.add(new Cliente(
                        rs.getInt("idCliente"),
                        rs.getString("nombre"),
                        rs.getString("email")));
            }
            return clientes;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean insert (Cliente cliente){
        String query = "INSERT INTO Cliente (idCliente, nombre, email) VALUES (?,?,?)";
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1, cliente.getIdCliente());
            ps.setString(2, cliente.getNombre());
            ps.setString(3, cliente.getEmail());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public boolean delete(int idCliente){
        String query = "DELETE FROM Cliente WHERE idCliente = ?";
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1, idCliente);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null){
                    ps.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Cliente find(int idCliente){
        String query = "SELECT * FROM Cliente WHERE idCliente = ?";
        Cliente cliente = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1, idCliente); //seteo el cliente que voy a buscar
            rs = ps.executeQuery(); //ejecuto la consulta
            if (rs.next()) {
                return new Cliente(
                        rs.getInt("idCliente"),
                        rs.getString("nombre"),
                        rs.getString("email"));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null){
                    ps.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean update(Cliente cliente){
        String query = "UPDATE Cliente SET nombre = ?, email = ? WHERE idCliente = ?";
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(query);
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getEmail());
            ps.setInt(3, cliente.getIdCliente());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (ps != null){
                try {
                    ps.close();
                } catch (SQLException e) {}
            }
        }

    }

    // Calcula el total facturado por cliente (cantidad * valor de cada producto, sumado por factura)
    // y devuelve la lista ordenada de mayor a menor facturación.
    
    public List<ClientesFacturadosDTO> getClientesOrdenadosPorFacturacion(){
        String query =  "SELECT c.idCliente, c.nombre, SUM(fp.cantidad * p.valor) AS totalFacturado " +
                        "FROM Cliente c " +
                        "JOIN Factura f ON f.idCliente = c.idCliente " +
                        "JOIN Factura_Producto fp ON fp.idFactura = f.idFactura " +
                        "JOIN Producto p ON p.idProducto = fp.idProducto " +
                        "GROUP BY c.idCliente, c.nombre " +
                        "ORDER BY totalFacturado DESC";
        ArrayList<ClientesFacturadosDTO> data = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            while (rs.next()) {
                data.add(new ClientesFacturadosDTO(
                        rs.getInt("idCliente"),
                        rs.getString("nombre"),
                        rs.getDouble("totalFacturado")));
            }
            return data;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (ps != null) {
                    ps.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
