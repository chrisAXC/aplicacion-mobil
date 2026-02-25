package com.example.tiendaandroid.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tiendaandroid.R;
import com.example.tiendaandroid.adapters.CarritoAdapter;
import com.example.tiendaandroid.models.CarritoItem;
import com.example.tiendaandroid.models.Usuario;
import com.example.tiendaandroid.network.ApiClient;
import com.example.tiendaandroid.network.ApiService;
import com.example.tiendaandroid.utils.SharedPrefManager;
import com.google.android.material.appbar.MaterialToolbar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends Fragment implements CarritoAdapter.OnCarritoListener {

    private RecyclerView rvCarrito;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty, layoutCheckout;
    private TextView tvTotal;
    private Button btnIrATienda, btnComprar;
    private MaterialToolbar toolbar;
    private CarritoAdapter adapter;
    private List<CarritoItem> itemList = new ArrayList<>();
    private SharedPrefManager sharedPrefManager;
    private Usuario usuario;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        sharedPrefManager = SharedPrefManager.getInstance(getContext());
        usuario = sharedPrefManager.getUser();

        initViews(view);
        setupRecyclerView();
        cargarCarrito();

        return view;
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbarCart);
        rvCarrito = view.findViewById(R.id.rvCarrito);
        progressBar = view.findViewById(R.id.progressBarCart);
        layoutEmpty = view.findViewById(R.id.layoutEmptyCart);
        layoutCheckout = view.findViewById(R.id.layoutCheckout);
        tvTotal = view.findViewById(R.id.tvTotalCarrito);
        btnIrATienda = view.findViewById(R.id.btnIrATienda);
        btnComprar = view.findViewById(R.id.btnComprar);

        btnIrATienda.setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.container, new HomeFragment())
                        .commit();
            }
        });

        btnComprar.setOnClickListener(v -> realizarCompra());
    }

    private void setupRecyclerView() {
        adapter = new CarritoAdapter(itemList, getContext(), this);
        rvCarrito.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCarrito.setAdapter(adapter);
    }

    private void cargarCarrito() {
        if (usuario == null) {
            mostrarVacio(true);
            return;
        }

        mostrarCargando(true);

        // ✅ CORREGIDO: Pasamos getContext()
        ApiService apiService = ApiClient.getService(getContext());
        Call<List<CarritoItem>> call = apiService.getCarrito(usuario.getId_usuario());

        call.enqueue(new Callback<List<CarritoItem>>() {
            @Override
            public void onResponse(Call<List<CarritoItem>> call, Response<List<CarritoItem>> response) {
                mostrarCargando(false);

                if (response.isSuccessful()) {
                    List<CarritoItem> items = response.body();
                    if (items != null) {
                        itemList.clear();
                        itemList.addAll(items);
                        adapter.actualizarLista(itemList);

                        if (itemList.isEmpty()) {
                            mostrarVacio(true);
                            mostrarCheckout(false);
                        } else {
                            mostrarVacio(false);
                            mostrarCheckout(true);
                            calcularTotal();
                        }
                    }
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Error desconocido";
                        Toast.makeText(getContext(),
                                "Error " + response.code() + ": " + errorBody,
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(),
                                "Error " + response.code() + " al cargar carrito",
                                Toast.LENGTH_SHORT).show();
                    }
                    mostrarVacio(true);
                }
            }

            @Override
            public void onFailure(Call<List<CarritoItem>> call, Throwable t) {
                mostrarCargando(false);
                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
                mostrarVacio(true);
            }
        });
    }

    private void calcularTotal() {
        double total = 0;
        for (CarritoItem item : itemList) {
            total += item.getSubtotal();
        }
        tvTotal.setText("$" + String.format(Locale.getDefault(), "%.2f", total));
    }

    private void mostrarCargando(boolean mostrar) {
        progressBar.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        rvCarrito.setVisibility(mostrar ? View.GONE : View.VISIBLE);
    }

    private void mostrarVacio(boolean mostrar) {
        layoutEmpty.setVisibility(mostrar ? View.VISIBLE : View.GONE);
        if (mostrar) {
            rvCarrito.setVisibility(View.GONE);
            layoutCheckout.setVisibility(View.GONE);
        }
    }

    private void mostrarCheckout(boolean mostrar) {
        layoutCheckout.setVisibility(mostrar ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onCantidadCambiada(CarritoItem item, int nuevaCantidad) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("id_usuario", usuario.getId_usuario());
        datos.put("id_producto", item.getId_producto());
        datos.put("cantidad", nuevaCantidad);

        // ✅ CORREGIDO: Pasamos getContext()
        ApiService apiService = ApiClient.getService(getContext());
        Call<Map<String, Object>> call = apiService.actualizarCantidad(datos);

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    item.setCantidad(nuevaCantidad);
                    adapter.notifyDataSetChanged();
                    calcularTotal();
                    Toast.makeText(getContext(), "Cantidad actualizada", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Error desconocido";
                        Toast.makeText(getContext(),
                                "Error: " + errorBody,
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error al actualizar cantidad", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onEliminarItem(CarritoItem item) {
        // ✅ CORREGIDO: Pasamos getContext()
        ApiService apiService = ApiClient.getService(getContext());
        Call<Map<String, Object>> call = apiService.eliminarDelCarrito(
                usuario.getId_usuario(),
                item.getId_producto()
        );

        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    itemList.remove(item);
                    adapter.actualizarLista(itemList);

                    if (itemList.isEmpty()) {
                        mostrarVacio(true);
                        mostrarCheckout(false);
                    } else {
                        calcularTotal();
                    }

                    Toast.makeText(getContext(), "Producto eliminado", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Error desconocido";
                        Toast.makeText(getContext(),
                                "Error: " + errorBody,
                                Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(getContext(), "Error al eliminar", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(getContext(),
                        "Error de conexión: " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemClick(CarritoItem item) {
        Toast.makeText(getContext(), "Ver detalle: " + item.getNombre_producto(),
                Toast.LENGTH_SHORT).show();
    }

    private void realizarCompra() {
        Toast.makeText(getContext(), "Procesando compra...", Toast.LENGTH_SHORT).show();
        // Implementaremos la compra después
    }
}