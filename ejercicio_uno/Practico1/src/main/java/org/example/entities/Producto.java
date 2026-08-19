package org.example.entities;

public class Producto {
    private int idProducto;
    private String nombre;
    private double valor;

    public Producto(int idProducto, String nombre, double valor) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.valor = valor;
    }

    public int getIdProducto() {
        return idProducto;
    }
    public String getNombre() {
        return nombre;
    }
    public double getValor() {
        return valor;
    }
    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public void setValor(double valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return String.format("Producto[idProducto=%d, nombre=%s, valor=%f]", idProducto, nombre, valor);
    }
}
