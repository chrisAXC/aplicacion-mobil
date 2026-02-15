package com.example.tiendaandroid.models;

public class Producto {
    private int id_producto;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private int id_categoria;
    private String imagen_url;
    private int estado;  // ← CAMBIADO DE boolean A int

    // Constructor vacío
    public Producto() {}

    // Constructor con parámetros
    public Producto(int id_producto, String nombre, String descripcion, double precio,
                    int stock, int id_categoria, String imagen_url, int estado) {  // ← CAMBIADO
        this.id_producto = id_producto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.id_categoria = id_categoria;
        this.imagen_url = imagen_url;
        this.estado = estado;  // ← CAMBIADO
    }

    // Getters y Setters
    public int getId_producto() { return id_producto; }
    public void setId_producto(int id_producto) { this.id_producto = id_producto; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public int getId_categoria() { return id_categoria; }
    public void setId_categoria(int id_categoria) { this.id_categoria = id_categoria; }

    public String getImagen_url() { return imagen_url; }
    public void setImagen_url(String imagen_url) { this.imagen_url = imagen_url; }

    public int getEstado() { return estado; }  // ← CAMBIADO
    public void setEstado(int estado) { this.estado = estado; }  // ← CAMBIADO

    // Método helper para verificar si está activo
    public boolean isActivo() {
        return estado == 1;
    }
}