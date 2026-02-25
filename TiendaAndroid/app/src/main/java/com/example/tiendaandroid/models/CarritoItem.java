package com.example.tiendaandroid.models;

public class CarritoItem {
    private int id_carrito;
    private int id_usuario;
    private int id_producto;
    private int cantidad;
    private String fecha_agregado;

    // Datos del producto (para mostrar)
    private String nombre_producto;
    private double precio_producto;
    private String imagen_producto;
    private int stock_producto;

    // Constructor vacío
    public CarritoItem() {}

    // Constructor con parámetros
    public CarritoItem(int id_carrito, int id_usuario, int id_producto, int cantidad,
                       String fecha_agregado, String nombre_producto, double precio_producto,
                       String imagen_producto, int stock_producto) {
        this.id_carrito = id_carrito;
        this.id_usuario = id_usuario;
        this.id_producto = id_producto;
        this.cantidad = cantidad;
        this.fecha_agregado = fecha_agregado;
        this.nombre_producto = nombre_producto;
        this.precio_producto = precio_producto;
        this.imagen_producto = imagen_producto;
        this.stock_producto = stock_producto;
    }

    // Getters y Setters
    public int getId_carrito() { return id_carrito; }
    public void setId_carrito(int id_carrito) { this.id_carrito = id_carrito; }

    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public int getId_producto() { return id_producto; }
    public void setId_producto(int id_producto) { this.id_producto = id_producto; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getFecha_agregado() { return fecha_agregado; }
    public void setFecha_agregado(String fecha_agregado) { this.fecha_agregado = fecha_agregado; }

    public String getNombre_producto() { return nombre_producto; }
    public void setNombre_producto(String nombre_producto) { this.nombre_producto = nombre_producto; }

    public double getPrecio_producto() { return precio_producto; }
    public void setPrecio_producto(double precio_producto) { this.precio_producto = precio_producto; }

    public String getImagen_producto() { return imagen_producto; }
    public void setImagen_producto(String imagen_producto) { this.imagen_producto = imagen_producto; }

    public int getStock_producto() { return stock_producto; }
    public void setStock_producto(int stock_producto) { this.stock_producto = stock_producto; }

    // Calcular subtotal
    public double getSubtotal() {
        return cantidad * precio_producto;
    }
}