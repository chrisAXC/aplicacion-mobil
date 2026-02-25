package com.example.tiendaandroid.models;

public class Usuario {
    private int id_usuario;
    private String nombre;
    private String email;
    private String password;  // ← AGREGAR ESTE CAMPO
    private String telefono;
    private String direccion;
    private String token;

    // Constructor vacío
    public Usuario() {}

    // Constructor con parámetros
    public Usuario(int id_usuario, String nombre, String email, String password,
                   String telefono, String direccion, String token) {
        this.id_usuario = id_usuario;
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.telefono = telefono;
        this.direccion = direccion;
        this.token = token;
    }

    // Getters y Setters
    public int getId_usuario() { return id_usuario; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }  // ← AGREGAR
    public void setPassword(String password) { this.password = password; }  // ← AGREGAR

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}