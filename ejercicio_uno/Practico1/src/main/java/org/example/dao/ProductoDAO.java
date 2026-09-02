package org.example.dao;

import org.example.entities.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Handle the consults to the Product table in the database
 * */

public class ProductoDAO {
    Connection connection;

    public ProductoDAO(Connection conn){
        this.connection = conn;
    }

    /**
     * Gets all the rows in the Producto table.
     * Prepares a SELECT query to retrieve all the rows in the table, then iterates over the
     * result set and adds each row to the returned list.
     *
     * @return List<Producto> with the recovered data or null if the list is empty.
     * */

    public List<Producto> getAll() {
        ArrayList<Producto> data = new ArrayList<>();

        String query =  "SELECT * " +
                        "FROM Producto";
        PreparedStatement pst = null;
        ResultSet resultSet = null;

        try {
            pst = this.connection.prepareStatement(query);
            resultSet = pst.executeQuery();

            while (resultSet.next()){
                int idProducto = resultSet.getInt("idProducto");
                String nombre = resultSet.getString("nombre");
                float valor =  resultSet.getFloat("valor");

                Producto producto = new Producto(idProducto, nombre, valor);
                data.add(producto);

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
     * Insert a new Producto in the database.
     *
     * @param producto object with the data to insert.
     * @return true if the insert is successful, false otherwise.
     * */

    public boolean insert(Producto producto) {
        String query = "INSERT INTO Producto (idProducto, nombre, valor) VALUES (?,?,?)";
        PreparedStatement pst = null;
        boolean isInserted = false;

        try {
            pst = this.connection.prepareStatement(query);
            pst.setInt(1,producto.getIdProducto());
            pst.setString(2,producto.getNombre());
            pst.setFloat(3, producto.getValor());

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
     *  Delete a Product from the database by its id.
     *
     *  @param id id of the product to delete.
     *  @return true if to delete is successful, false otherwise.
     *  */

    public boolean delete(int id){
        String query = "DELETE FROM Producto WHERE idProducto = ?";
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
     *  Update an existing Product in the database.
     *
     *  @param producto object with the updated data. Must include the idProducto of the row to update.
     *  @return true if the update is successful, false otherwise.
     * */

    public boolean update(Producto producto){
        String query = "UPDATE Producto SET nombre = ?, valor = ? WHERE idProducto = ? ";
        PreparedStatement pst = null;
        boolean isUpdated = false;

        try {
            pst = this.connection.prepareStatement(query);
            pst.setString(1, producto.getNombre());
            pst.setFloat(2, producto.getValor());
            pst.setInt(3, producto.getIdProducto());

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
     *  Get a single Product from the database by its id.
     *
     *  @param id id of the product to search.
     *  @return the Producto with the given id, or null if it doesn't exist.
     * */

    public Producto find(int id) {
        Producto data = null;
        String query = "SELECT * FROM Producto WHERE idProducto = ?";
        PreparedStatement pst = null;
        ResultSet result = null;

        try {
            pst = this.connection.prepareStatement(query);
            pst.setInt(1, id);
            result = pst.executeQuery();

            if (result.next()) {
                int idProducto = result.getInt("idProducto");
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
