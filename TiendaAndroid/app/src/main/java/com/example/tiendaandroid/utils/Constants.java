package com.example.tiendaandroid.utils;

public class Constants {
    // Para dispositivo FÍSICO (misma red WiFi)
    public static final String BASE_URL_PHYSICAL = "http://192.168.1.8:3000/api/";

    // Para EMULADOR (siempre funciona)
    public static final String BASE_URL_EMULATOR = "http://10.0.2.2:3000/api/";

    // Cambia esta variable según donde ejecutes
    public static final String BASE_URL = BASE_URL_EMULATOR;  // ← Cambia a PHYSICAL cuando uses teléfono

    public static final String IMAGES_BASE_URL = BASE_URL.replace("/api/", "/uploads/");

    public static final String SHARED_PREF_NAME = "tienda_prefs";
    public static final String LOGIN_KEY = "isLoggedIn";
    public static final String USER_KEY = "user_data";
    public static final String TOKEN_KEY = "auth_token";
}