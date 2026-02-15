package com.example.tiendaandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.tiendaandroid.models.Usuario;
import com.google.gson.Gson;

public class SharedPrefManager {
    private static SharedPrefManager instance;
    private static Context ctx;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    private SharedPrefManager(Context context) {
        ctx = context;
        sharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPrefManager(context);
        }
        return instance;
    }

    public void saveUser(Usuario usuario, String token) {
        String userJson = gson.toJson(usuario);
        editor.putString(Constants.USER_KEY, userJson);
        editor.putString(Constants.TOKEN_KEY, token);
        editor.putBoolean(Constants.LOGIN_KEY, true);
        editor.apply();
    }

    public Usuario getUser() {
        String userJson = sharedPreferences.getString(Constants.USER_KEY, null);
        if (userJson == null) return null;
        return gson.fromJson(userJson, Usuario.class);
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(Constants.LOGIN_KEY, false);
    }

    public String getToken() {
        return sharedPreferences.getString(Constants.TOKEN_KEY, null);
    }

    public void logout() {
        editor.clear();
        editor.apply();
    }
}