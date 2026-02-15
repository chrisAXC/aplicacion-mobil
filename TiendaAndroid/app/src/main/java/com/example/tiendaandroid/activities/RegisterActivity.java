package com.example.tiendaandroid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tiendaandroid.R;
import com.example.tiendaandroid.network.ApiClient;
import com.example.tiendaandroid.network.ApiService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText etNombre, etEmail, etTelefono, etDireccion, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;
    private TextView tvLogin;
    private Toast currentToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrarUsuario();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void mostrarMensaje(String mensaje) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(RegisterActivity.this, mensaje, Toast.LENGTH_SHORT);
        currentToast.show();
    }

    private void registrarUsuario() {
        String nombre = etNombre.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String telefono = etTelefono.getText().toString().trim();
        String direccion = etDireccion.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Validaciones
        if (TextUtils.isEmpty(nombre)) {
            etNombre.setError("Nombre requerido");
            etNombre.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email requerido");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Email inválido");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Contraseña requerida");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Mínimo 6 caracteres");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Las contraseñas no coinciden");
            etConfirmPassword.requestFocus();
            return;
        }

        btnRegister.setEnabled(false);
        btnRegister.setText("Registrando...");

        Map<String, Object> userData = new HashMap<>();
        userData.put("nombre", nombre);
        userData.put("email", email);
        userData.put("password", password);

        if (!TextUtils.isEmpty(telefono)) {
            userData.put("telefono", telefono);
        }
        if (!TextUtils.isEmpty(direccion)) {
            userData.put("direccion", direccion);
        }

        // ✅ CORREGIDO: Pasamos this como Context
        ApiService apiService = ApiClient.getService(RegisterActivity.this);
        Call<Map<String, Object>> call = apiService.registrarUsuario(userData);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Registrarse");

                if (response.isSuccessful()) {
                    Map<String, Object> data = response.body();
                    if (data != null && data.containsKey("message")) {
                        mostrarMensaje((String) data.get("message"));
                    } else {
                        mostrarMensaje("Registro exitoso");
                    }
                    finish();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Error en el registro";
                        mostrarMensaje("Error: " + errorBody);
                    } catch (Exception e) {
                        mostrarMensaje("Error en el registro");
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btnRegister.setEnabled(true);
                btnRegister.setText("Registrarse");
                mostrarMensaje("Error de conexión: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }
}