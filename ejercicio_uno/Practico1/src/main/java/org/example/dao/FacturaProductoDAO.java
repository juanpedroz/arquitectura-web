package org.example.dao;

import org.example.entities.FacturaProducto;
import org.example.entities.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FacturaProductoDAO {
    Connection connection;

    public FacturaProductoDAO(Connection conn){
        this.connection = conn;
    }

    /**
     * Gets all the rows in the FacturaProducto table.
     * Prepares a SELECT query to retrieve all the rows in the table, then iterates over the
     * result set and adds each row to the returned list.
     *
     * @return List<FacturaProducto> with the recovered data or null if the list is empty.
     * */

    public List<FacturaProducto> getAll() {
        ArrayList<FacturaProducto> data = new ArrayList<>();

        String query =  "SELECT * " +
                "FROM Factura_Producto";
        PreparedStatement pst = null;
        ResultSet resultSet = null;

        try {
            pst = this.connection.prepareStatement(query);
            resultSet = pst.executeQuery();

            while (resultSet.next()){
                int idFactura = resultSet.getInt("idFactura");
                int idProducto = resultSet.getInt("idProducto");
                int cantidad = resultSet.getInt("cantidad");

                FacturaProducto nueva = new FacturaProducto(idFactura, idProducto, cantidad);
                data.add(nueva);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (pst != null) {
                    pst.close();
                }
                this.connection.commit();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return data;
    }

    /**
     * Insert a new FacturaProducto in the database.
     *
     * @param facturaProducto object with the data to insert.
     * @return true if the insert is successful, false otherwise.
     *
     */

    public boolean insert(FacturaProducto facturaProducto) {
        String query = "INSERT INTO Factura_Producto (idFactura, idProducto, cantidad) VALUES (?,?,?)";
        PreparedStatement pst = null;
        boolean isInserted = false;

        try {
            pst = this.connection.prepareStatement(query);
            pst.setInt(1, facturaProducto.getIdFactura());
            pst.setInt(2, facturaProducto.getIdProducto());
            pst.setInt(3, facturaProducto.getCantidad());

            isInserted = pst.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);

        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                this.connection.commit();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isInserted;
    }

    /**
     *  Delete a FacturaProducto from the database by its id.
     *
     *  @param id id of the product to delete.
     *  @return true if to delete is successful, false otherwise.
     *  */

    public boolean delete(int id){
        String query = "DELETE FROM Factura_Producto WHERE idProducto = ?";
        PreparedStatement pst = null;
        boolean isDeleted = false;

        try {
            pst = this.connection.prepareStatement(query);
            pst.setInt(1, id);

            isDeleted = pst.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                this.connection.commit();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isDeleted;
    }

    /**
     *  Update an existing FacturaProducto in the database.
     *
     *  @param facturaProducto object with the updated data. Must include the idFactura and idProducto of the row to update.
     *  @return true if the update is successful, false otherwise.
     * */

    public boolean update(FacturaProducto facturaProducto){
        String query = "UPDATE Factura_Producot SET cantidad = ? WHERE idFactura = ? AND idProducto = ? ";
        PreparedStatement pst = null;
        boolean isUpdated = false;

        try {
            pst = this.connection.prepareStatement(query);
            pst.setInt(1, facturaProducto.getCantidad());
            pst.setInt(2, facturaProducto.getIdFactura());
            pst.setInt(3, facturaProducto.getIdProducto());

            isUpdated = pst.executeUpdate() > 0;

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                this.connection.commit();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isUpdated;
    }


    /**
     *  Get a single FacturaProducto from the database by its id.
     *
     *  @param idFactura id of the FacturaProducto to search.
     * @param idProducto id of the FacturaProducto to search.
     *  @return the FacturaProducto with the given id, or null if it doesn't exist.
     * */

    public Producto find(int idFactura, int idProducto) {
        Producto data = null;
        String query = "SELECT * FROM Factura_Producto WHERE idFactura = ? AND idProducto = ?";
        PreparedStatement pst = null;
        ResultSet result = null;

        try {
            pst = this.connection.prepareStatement(query);
            pst.setInt(1, idFactura);
            pst.setInt(2, idProducto);
            result = pst.executeQuery();

            if (result.next()) {
                int idFactura = result.getInt("idFactura");
                String nombre = result.getString("nombre");
                float valor = result.getFloat("valor");

                data = new Producto(idProducto, nombre, valor);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);

        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
                this.connection.commit();

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return data;

    }


    
}
