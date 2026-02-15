package com.example.tiendaandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.example.tiendaandroid.activities.LoginActivity;
import com.example.tiendaandroid.fragments.CartFragment;
import com.example.tiendaandroid.fragments.HomeFragment;
import com.example.tiendaandroid.fragments.ProfileFragment;
import com.example.tiendaandroid.models.Usuario;
import com.example.tiendaandroid.utils.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private MaterialToolbar toolbar;
    private SharedPrefManager sharedPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar SharedPrefManager
        sharedPrefManager = SharedPrefManager.getInstance(this);

        // Verificar si hay sesión
        if (!sharedPrefManager.isLoggedIn()) {
            irALogin();
            return;
        }

        // Inicializar vistas
        initViews();

        // Configurar toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        // Configurar navigation drawer
        navigationView.setNavigationItemSelectedListener(this);

        // Cargar datos del usuario en el header
        cargarDatosUsuario();

        // Mostrar fragment por defecto (Home)
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new HomeFragment())
                    .commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }

        // 🔥 CORRECCIÓN: Usar OnBackPressedDispatcher en lugar de onBackPressed
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    // Si no hay más fragments en el back stack, salir
                    if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                        getSupportFragmentManager().popBackStack();
                    } else {
                        finish();
                    }
                }
            }
        });
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
    }

    private void cargarDatosUsuario() {
        Usuario usuario = sharedPrefManager.getUser();
        if (usuario != null) {
            View headerView = navigationView.getHeaderView(0);
            TextView tvNombre = headerView.findViewById(R.id.navHeaderName);
            TextView tvEmail = headerView.findViewById(R.id.navHeaderEmail);

            tvNombre.setText(usuario.getNombre());
            tvEmail.setText(usuario.getEmail());
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        String title = "Tienda de Cómputo";

        int itemId = item.getItemId();
        if (itemId == R.id.nav_home) {
            fragment = new HomeFragment();
            title = "Inicio";
        } else if (itemId == R.id.nav_cart) {
            fragment = new CartFragment();
            title = "Carrito";
        } else if (itemId == R.id.nav_profile) {
            fragment = new ProfileFragment();
            title = "Mi Perfil";
        } else if (itemId == R.id.nav_laptops) {
            fragment = HomeFragment.newInstance("Laptops");
            title = "Laptops";
        } else if (itemId == R.id.nav_desktops) {
            fragment = HomeFragment.newInstance("Desktops");
            title = "Desktops";
        } else if (itemId == R.id.nav_perifericos) {
            fragment = HomeFragment.newInstance("Periféricos");
            title = "Periféricos";
        } else if (itemId == R.id.nav_componentes) {
            fragment = HomeFragment.newInstance("Componentes");
            title = "Componentes";
        } else if (itemId == R.id.nav_logout) {
            cerrarSesion();
        }

        if (fragment != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
            toolbar.setTitle(title);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void cerrarSesion() {
        sharedPrefManager.logout();
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        irALogin();
    }

    private void irALogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // ❌ ELIMINADO: el método onBackPressed ya no se usa
    // Ahora se maneja con OnBackPressedDispatcher en onCreate
}