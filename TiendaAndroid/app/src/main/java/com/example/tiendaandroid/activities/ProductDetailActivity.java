package com.example.tiendaandroid.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.tiendaandroid.R;
import com.example.tiendaandroid.models.Producto;
import com.example.tiendaandroid.utils.Constants;
import com.google.android.material.button.MaterialButton;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView ivProducto;
    private TextView tvNombre, tvDescripcion, tvPrecio, tvStock;
    private MaterialButton btnAgregarCarrito;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        initViews();
        cargarDatos();
    }

    private void initViews() {
        ivProducto = findViewById(R.id.ivProductoDetail);
        tvNombre = findViewById(R.id.tvProductoNombreDetail);
        tvDescripcion = findViewById(R.id.tvProductoDescripcionDetail);
        tvPrecio = findViewById(R.id.tvProductoPrecioDetail);
        tvStock = findViewById(R.id.tvProductoStockDetail);
        btnAgregarCarrito = findViewById(R.id.btnAgregarCarritoDetail);
    }

    private void cargarDatos() {
        Producto producto = (Producto) getIntent().getSerializableExtra("producto");

        if (producto != null) {
            tvNombre.setText(producto.getNombre());
            tvDescripcion.setText(producto.getDescripcion() != null ?
                    producto.getDescripcion() : "Sin descripción");
            tvPrecio.setText("$" + String.format("%.2f", producto.getPrecio()));
            tvStock.setText("Stock: " + producto.getStock());

            String imagenUrl = producto.getImagen_url();
            if (imagenUrl != null && !imagenUrl.isEmpty()) {
                if (imagenUrl.startsWith("http")) {
                    Glide.with(this).load(imagenUrl).into(ivProducto);
                } else {
                    Glide.with(this).load(Constants.IMAGES_BASE_URL + imagenUrl).into(ivProducto);
                }
            }

            btnAgregarCarrito.setOnClickListener(v -> {
                Toast.makeText(this, "Agregado al carrito: " + producto.getNombre(),
                        Toast.LENGTH_SHORT).show();
            });
        }
    }
}