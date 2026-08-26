package org.example.utils;

import lombok.Getter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.example.entities.Cliente;
import org.example.entities.Factura;
import org.example.entities.FacturaProducto;
import org.example.entities.Producto;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *  Handles the connection to the MySQL database.
 * */


@Getter
public class HelperMySQL {
    /** Active connection used to execute basic DB operations. */
    private Connection connection = null;

     /**
      * Loads the JDBC driver and initializes the connection with the database.
      * */

     public HelperMySQL(){
         try {
             String driver = "com.mysql.cj.jdbc.Driver";
             Class.forName(driver).getDeclaredConstructor().newInstance();

         } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                  | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
             e.printStackTrace();
             System.exit(1);
         }

         try {
             String DB_USER = "root";
             String DB_URL = "jdbc:mysql://localhost:3306/db_mysql_arq_web";
             String DB_PASSWORD = "";

             this.connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             this.connection.setAutoCommit(false);

         } catch (Exception e) {
             e.printStackTrace();
         }

     }

    /**
     *  Closes the current database connection, if exists one.
     */

    public void closeConnection() {
        if (this.connection != null){
            try {
                connection.close();
                this.connection = null;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Call the methods that initialize the tables and populates the data.
     *
     * @throws Exception if an error occurs while initializing the database or populating the data
     * */

    public void setUpDatabase() throws Exception {
        this.dropTables();
        this.createTables();
        this.populateDatabase();
    }

    /**
     * Execute the sentences for the creation of the different tables (Product, Client, Facture, FactureProduct).
     * Each table have a sql query. The current connection prepares, execute and commit the query.
     *
     * @throws SQLException if a database access error occurs while creating the tables
     * */

    private void createTables() throws SQLException {
        String createClient =   "CREATE TABLE IF NOT EXISTS Cliente("          +
                                "idCliente    INT                NOT NULL, "   +
                                "nombre       VARCHAR(500)       NOT NULL, "   +
                                "email        VARCHAR(150)       NOT NULL, "   +
                                "CONSTRAINT pk_cliente PRIMARY KEY (idCliente));";
        this.connection.prepareStatement(createClient).execute();
        this.connection.commit();

        String createFactura =     "CREATE TABLE IF NOT EXISTS Factura("                     +
                                    "idFactura    INT                NOT NULL, "             +
                                    "idCliente    INT                NOT NULL, "             +
                                    "CONSTRAINT pk_Factura PRIMARY KEY (idFactura),"         +
                                    "CONSTRAINT fk_Factura_Cliente FOREIGN KEY (idCliente) " +
                                    "references Cliente (idCliente));";
        this.connection.prepareStatement(createFactura).execute();
        this.connection.commit();

        String createProduct =  "CREATE TABLE IF NOT EXISTS Producto("                    +
                                "idProducto    INT                NOT NULL, "             +
                                "nombre        VARCHAR(45)        NOT NULL, "             +
                                "valor         FLOAT              NOT NULL, "             +
                                "CONSTRAINT pk_Producto PRIMARY KEY (idProducto));";
        this.connection.prepareStatement(createProduct).execute();
        this.connection.commit();

        String createFacturaProducto =  "CREATE TABLE IF NOT EXISTS Factura_Producto("                        +
                                        "idFactura    INT                NOT NULL, "                          +
                                        "idProducto   INT                NOT NULL, "                          +
                                        "cantidad     INT                NOT NULL, "                          +
                                        "CONSTRAINT pk_Factura_Producto PRIMARY KEY (idFactura, idProducto)," +
                                        "CONSTRAINT fk_Factura_Producto_Factura FOREIGN KEY (idFactura)"      +
                                        "references Factura (idFactura), "                                    +
                                        "CONSTRAINT fk_Factura_Producto_Producto FOREIGN KEY (idProducto) "   +
                                        "references Producto (idProducto));";
        this.connection.prepareStatement(createFacturaProducto).execute();
        this.connection.commit();
    }

    /**
     * Delete the tables of the database.
     * Delete each table with a DROP query. The current connection prepare, exceute and commit the query.
     *
     * @throws SQLException if a database access error occurs while dropping the tables
     * */

    private void dropTables() throws SQLException {
        String dropFacturaProducto = "DROP TABLE IF EXISTS Factura_Producto";
        this.connection.prepareStatement(dropFacturaProducto).execute();
        this.connection.commit();

        String dropFactura = "DROP TABLE IF EXISTS Factura";
        this.connection.prepareStatement(dropFactura).execute();
        this.connection.commit();

        String dropClient = "DROP TABLE IF EXISTS Cliente";
        this.connection.prepareStatement(dropClient).execute();
        this.connection.commit();

        String dropProduct = "DROP TABLE IF EXISTS Producto";
        this.connection.prepareStatement(dropProduct).execute();
        this.connection.commit();
    }

    /***
     * Read archives.csv and return the data.
     *
     * @return Iterable with the data
     */
    private Iterable<CSVRecord> getData(String archive) throws IOException{
        String path = "src/main/resources/" + archive;
        Reader in = new FileReader(path);
        String[] header = {};
        CSVParser csvParser = CSVFormat.EXCEL.withHeader(header).parse(in);

        return csvParser.getRecords();
    }

    /**
     * Populates the data allowed in the tables.
     * Read the .csv archives and insert the data in each tabla with sql queries.
     * The current connection prepares, execute and commit the queries.
     *
     @throws Exception if an error occurs while reading the CSV files or inserting the data into the database
     *
     */

    private void populateDatabase() throws Exception {
        try {

            // Clientes.csv
            for(CSVRecord row : this.getData("clientes.csv")){
                if(row.size() == 3){ //Client only have 3 attributes
                    String idCLienteStr = row.get(0);
                    String nombre = row.get(1);
                    String email = row.get(2);

                    if(!idCLienteStr.isEmpty() && !nombre.isEmpty() && !email.isEmpty()){
                        int idCliente = Integer.parseInt(idCLienteStr);

                        Cliente newCliente = new Cliente(idCliente, nombre, email);
                        this.insertClient(newCliente);

                    }else{
                        System.err.println("Campos incompletos. No se podra agregar el registro " + row);
                    }

                }else{
                     System.err.println("Fila inválida, se esperaban 3 campos: " + row);
                }
            }

            //Facturas.csv
            for(CSVRecord row : this.getData("facturas.csv")){
                if(row.size() == 2){
                    String idFacturaStr = row.get(0);
                    String idClienteStr = row.get(1);

                    if(!idFacturaStr.isEmpty() && !idClienteStr.isEmpty()){
                        int idFactura = Integer.parseInt(idFacturaStr);
                        int idCliente = Integer.parseInt(idClienteStr);

                        Factura newFactura = new Factura(idFactura, idCliente);
                        this.insertFactura(newFactura);

                    }else{
                        System.err.println("Campos incompletos. No se podra agregar el registro " + row);
                    }

                }else{
                    System.err.println("Fila inválida, se esperaban 2 campos: " + row);
                }

            }

            //Productos.csv
            for(CSVRecord row : this.getData("productos.csv")){
                if(row.size() == 3){
                    String idProductoStr = row.get(0);
                    String nombre = row.get(1);
                    String valorStr = row.get(2);

                    if(!idProductoStr.isEmpty() && !nombre.isEmpty() && !valorStr.isEmpty()){;
                        int idProducto = Integer.parseInt(idProductoStr);
                        float valor = Float.parseFloat(valorStr);

                        Producto newProducto = new Producto(idProducto, nombre, valor);
                        this.insertProducto(newProducto);

                    }else{
                        System.err.println("Campos incompletos. No se podra agregar el registro " + row);
                    }

                }else{
                    System.err.println("Fila inválida, se esperaban 3 campos: " + row);
                }

            }

            //FacturasProductos.csv
            for(CSVRecord row : this.getData("facturas-productos.csv")){
                if(row.size() == 3){
                    String idFacturaStr = row.get(0);
                    String idProductoStr = row.get(1);
                    String cantidadStr = row.get(2);

                    if(!idFacturaStr.isEmpty() && !idProductoStr.isEmpty() && !cantidadStr.isEmpty()){
                        int idFactura = Integer.parseInt(idFacturaStr);
                        int idProducto = Integer.parseInt(idProductoStr);
                        int cantidad = Integer.parseInt(cantidadStr);

                        FacturaProducto newFacturaProducto = new FacturaProducto(idFactura, idProducto, cantidad);
                        this.insertFacturaProducto(newFacturaProducto);

                    }else{
                        System.err.println("Campos incompletos. No se podra agregar el registro " + row);
                    }

                }else{
                   System.err.println("Fila inválida, se esperaban 3 campos: " + row);
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Insert a new Product in the corresponding table
     *
     * @param newProducto object with the data to insert
     * @throws Exception if an error occurs while trying to insert
     * */

    private void insertProducto(Producto newProducto) throws Exception{
        String insert = "INSERT INTO Producto (idProducto, nombre, valor) VALUES (?,?,?) ";
        PreparedStatement pst = null;

        try {
            pst = this.connection.prepareStatement(insert);
            pst.setInt(1, newProducto.getIdProducto());
            pst.setString(2,newProducto.getNombre());
            pst.setFloat(3, newProducto.getValor());

            if(pst.executeUpdate() == 0) {
                throw new Exception("No se pudo insertar en producto");
            }

        }catch (SQLException e){
            e.printStackTrace();

        }finally{
            this.closePst(pst);
        }
    }

    /**
     * Insert a new factureProduct in the corresponding table
     *
     * @param newFacturaProducto object with the data to insert
     * @throws Exception if an error occurs while trying to insert
     * */

    private void insertFacturaProducto(FacturaProducto newFacturaProducto) throws Exception {
        String insert = "INSERT INTO Factura_Producto (idFactura, idProducto, cantidad) VALUES (?,?,?) ";
        PreparedStatement pst = null;

        try {
            pst = this.connection.prepareStatement(insert);
            pst.setInt(1, newFacturaProducto.getIdFactura());
            pst.setInt(2,newFacturaProducto.getIdProducto());
            pst.setInt(3, newFacturaProducto.getCantidad());

            if(pst.executeUpdate() == 0) {
                throw new Exception("No se pudo insertar en factura-producto");
            }

        }catch (SQLException e){
            e.printStackTrace();

        }finally{
            this.closePst(pst);
        }
    }

    /**
     * Insert a new facture in the corresponding table
     *
     * @param newFactura object with the data to insert
     * @throws Exception if an error occurs while trying to insert
     * */

    private void insertFactura(Factura newFactura) throws Exception {
        String insert = "INSERT INTO Factura (idFactura, idCliente) VALUES (?,?) ";
        PreparedStatement pst = null;

        try {
            pst = this.connection.prepareStatement(insert);
            pst.setInt(1, newFactura.getIdFactura());
            pst.setInt(2, newFactura.getIdCliente());

            if(pst.executeUpdate() == 0) {
                throw new Exception("No se pudo insertar en factura");
            }

        }catch (SQLException e){
            e.printStackTrace();

        }finally{
            this.closePst(pst);
        }
    }

    /**
     * Insert a new client in the corresponding table
     *
     * @param newCliente object with the data to insert
     * @throws Exception if an error occurs while trying to insert
     * */

    private void insertClient(Cliente newCliente) throws Exception {
        String insert = "INSERT INTO Cliente (idCliente, nombre, email) VALUES (?,?,?)";
        PreparedStatement pst = null;

        try {
            pst = this.connection.prepareStatement(insert);
            pst.setInt(1, newCliente.getIdCliente());
            pst.setString(2, newCliente.getNombre());
            pst.setString(3, newCliente.getEmail());

            if(pst.executeUpdate() == 0){
                throw new Exception("No se pudo insertar en cliente");
            }

        }catch (SQLException e){
            e.printStackTrace();

        }finally{
            this.closePst(pst);
        }
    }

    /**
     * Closes the given PreparedStatement and commits the pending changes,
     * if a connection is currently open.
     *
     * @param pst PreparedStatement to close
     */

    private void closePst(PreparedStatement pst){
        if (pst != null) {
            try {
                pst.close();
                if (this.connection != null) {
                    this.connection.commit();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
