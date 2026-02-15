package com.example.tiendaandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tiendaandroid.MainActivity;
import com.example.tiendaandroid.R;
import com.example.tiendaandroid.models.Usuario;
import com.example.tiendaandroid.network.ApiClient;
import com.example.tiendaandroid.network.ApiService;
import com.example.tiendaandroid.utils.SharedPrefManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegister;
    private SharedPrefManager sharedPrefManager;
    private Toast currentToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPrefManager = SharedPrefManager.getInstance(this);

        if (sharedPrefManager.isLoggedIn()) {
            irAMain();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                realizarLogin();
            }
        });

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    }

    private void mostrarMensaje(String mensaje) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT);
        currentToast.show();
    }

    private void irAMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void realizarLogin() {
        String email = etEmail.getText() != null ? etEmail.getText().toString().trim() : "";
        String password = etPassword.getText() != null ? etPassword.getText().toString().trim() : "";

        if (email.isEmpty()) {
            etEmail.setError("Ingresa tu correo");
            etEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            etPassword.setError("Ingresa tu contraseña");
            etPassword.requestFocus();
            return;
        }

        btnLogin.setEnabled(false);
        btnLogin.setText("Iniciando sesión...");

        Map<String, String> credenciales = new HashMap<>();
        credenciales.put("email", email);
        credenciales.put("password", password);

        // ✅ CORREGIDO: Pasamos this como Context
        ApiService apiService = ApiClient.getService(LoginActivity.this);
        Call<Map<String, Object>> call = apiService.loginUsuario(credenciales);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Iniciar Sesión");

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        Map<String, Object> data = response.body();
                        String token = data.containsKey("token") ? (String) data.get("token") : "";

                        if (data.containsKey("usuario")) {
                            Object userObj = data.get("usuario");
                            Usuario usuario = new Usuario();

                            if (userObj instanceof Map) {
                                Map<String, Object> userMap = (Map<String, Object>) userObj;

                                if (userMap.containsKey("id_usuario")) {
                                    Object idObj = userMap.get("id_usuario");
                                    if (idObj instanceof Double) {
                                        usuario.setId_usuario(((Double) idObj).intValue());
                                    } else if (idObj instanceof Integer) {
                                        usuario.setId_usuario((Integer) idObj);
                                    } else if (idObj instanceof String) {
                                        usuario.setId_usuario(Integer.parseInt((String) idObj));
                                    }
                                }
                                if (userMap.containsKey("nombre")) {
                                    usuario.setNombre((String) userMap.get("nombre"));
                                }
                                if (userMap.containsKey("email")) {
                                    usuario.setEmail((String) userMap.get("email"));
                                }
                                if (userMap.containsKey("telefono")) {
                                    usuario.setTelefono((String) userMap.get("telefono"));
                                }
                                if (userMap.containsKey("direccion")) {
                                    usuario.setDireccion((String) userMap.get("direccion"));
                                }
                            }

                            sharedPrefManager.saveUser(usuario, token);

                            mostrarMensaje("¡Bienvenido " + usuario.getNombre() + "!");

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    irAMain();
                                }
                            }, 500);
                        }
                    } catch (Exception e) {
                        mostrarMensaje("Error al procesar la respuesta");
                        e.printStackTrace();
                    }
                } else {
                    try {
                        String errorMsg = response.errorBody() != null ?
                                response.errorBody().string() : "Email o contraseña incorrectos";
                        mostrarMensaje(errorMsg);
                    } catch (Exception e) {
                        mostrarMensaje("Email o contraseña incorrectos");
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btnLogin.setEnabled(true);
                btnLogin.setText("Iniciar Sesión");
                mostrarMensaje("Error de conexión: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }
}