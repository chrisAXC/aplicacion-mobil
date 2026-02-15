package com.example.tiendaandroid.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tiendaandroid.R;
import com.example.tiendaandroid.adapters.ProductoAdapter;
import com.example.tiendaandroid.models.Producto;
import com.example.tiendaandroid.models.Usuario;
import com.example.tiendaandroid.network.ApiClient;
import com.example.tiendaandroid.network.ApiService;
import com.example.tiendaandroid.utils.SharedPrefManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements ProductoAdapter.OnProductoClickListener {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvEmpty;
    private ProductoAdapter adapter;
    private List<Producto> productoList = new ArrayList<>();
    private String categoria;
    private SharedPrefManager sharedPrefManager;
    private Usuario usuario;

    public HomeFragment() {}

    public static HomeFragment newInstance(String categoria) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString("categoria", categoria);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        usuario = sharedPrefManager.getUser();

        initViews(view);
        setupRecyclerView();

        if (getArguments() != null) {
            categoria = getArguments().getString("categoria");
        }

        cargarProductos();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressBar);
        tvEmpty = view.findViewById(R.id.tvEmpty);
    }

    private void setupRecyclerView() {
        adapter = new ProductoAdapter(productoList, getContext(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }

    private void cargarProductos() {
        mostrarCargando(true);

        // ✅ CORREGIDO: Pasamos getContext()
        ApiService apiService = ApiClient.getService(getContext());
        Call<List<Producto>> call = apiService.getProductos();

        call.enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                mostrarCargando(false);

                if (response.isSuccessful() && response.body() != null) {
                    productoList.clear();
                    productoList.addAll(response.body());
                    adapter.actualizarLista(productoList);

                    if (productoList.isEmpty()) {
                        mostrarMensajeVacio(true, "No hay productos disponibles");
                    } else {
                        mostrarMensajeVacio(false, "");
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Error desconocido";
                        mostrarMensajeVacio(true, "Error " + response.code());
                        Toast.makeText(getContext(),
                                "Error " + response.code() + ": " + errorBody,
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        mostrarMensajeVacio(true, "Error al cargar productos");
                        Toast.makeText(getContext(), "Error al cargar productos", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                mostrarCargando(false);
                mostrarMensajeVacio(true, "Error de conexión");
                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void mostrarCargando(boolean mostrar) {
        if (mostrar) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            tvEmpty.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void mostrarMensajeVacio(boolean mostrar, String mensaje) {
        if (mostrar) {
            tvEmpty.setText(mensaje);
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProductoClick(Producto producto) {
        Toast.makeText(getContext(), "Producto: " + producto.getNombre(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAgregarCarrito(Producto producto) {
        if (producto.getStock() <= 0) {
            Toast.makeText(getContext(), "Producto sin stock", Toast.LENGTH_SHORT).show();
            return;
        }

        if (usuario == null) {
            Toast.makeText(getContext(), "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> itemData = new HashMap<>();
        itemData.put("id_usuario", usuario.getId_usuario());
        itemData.put("id_producto", producto.getId_producto());
        itemData.put("cantidad", 1);

        // ✅ CORREGIDO: Pasamos getContext()
        ApiService apiService = ApiClient.getService(getContext());
        Call<Map<String, Object>> call = apiService.agregarAlCarrito(itemData);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(),
                            "✓ " + producto.getNombre() + " agregado al carrito",
                            Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Error desconocido";
                        Toast.makeText(getContext(),
                                "Error: " + errorBody,
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error al agregar al carrito", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }
}