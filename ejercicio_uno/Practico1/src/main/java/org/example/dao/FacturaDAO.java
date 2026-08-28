package org.example.dao;

import org.example.entities.Factura;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacturaDAO {

    private Connection connection;

    public FacturaDAO(Connection connection){
        this.connection = connection;
    }

    public List<Factura> getAll() throws SQLException {
        String query = "SELECT * FROM Factura";
        ArrayList<Factura> facturas = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(query);
            rs = ps.executeQuery();
            while(rs.next()){
                Factura factura = new Factura(rs.getInt("idFactura"), rs.getInt("idCliente"));
                facturas.add(factura);
            }

            return facturas;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (ps != null)
                ps.close();
        }
    }

    public boolean insert(Factura factura) {
        String query = "INSERT INTO Factura (idCliente, idFactura) VALUES (?, ?)";
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1, factura.getIdCliente());
            ps.setInt(2, factura.getIdFactura());
            return ps.executeUpdate() > 0; // devuelve cant de lineas afectadas
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null )
                    ps.close();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

    }

    public boolean delete(int idFactura) {
        String query = "DELETE FROM Factura WHERE idFactura = ?";
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1, idFactura);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Factura find(int idFactura){
        String query = "SELECT * FROM Factura WHERE idFactura = ?";
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1, idFactura);
            rs = ps.executeQuery();
            if(rs.next()){
                return (new Factura(rs.getInt("idFactura"), rs.getInt("idCliente")));
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean update(Factura factura) {
        String query = "UPDATE Factura SET idCliente = ?, idFactura = ? WHERE idFactura = ?";
        PreparedStatement ps = null;

        try {
            ps = connection.prepareStatement(query);
            ps.setInt(1, factura.getIdCliente());
            ps.setInt(2, factura.getIdFactura());
            ps.setInt(3, factura.getIdFactura());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (ps != null)
                    ps.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
}
