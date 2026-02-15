package com.example.tiendaandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.tiendaandroid.R;
import com.example.tiendaandroid.models.Producto;
import com.example.tiendaandroid.utils.Constants;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ProductoViewHolder> {

    private List<Producto> productos;
    private Context context;
    private OnProductoClickListener listener;

    public interface OnProductoClickListener {
        void onProductoClick(Producto producto);
        void onAgregarCarrito(Producto producto);
    }

    public ProductoAdapter(List<Producto> productos, Context context, OnProductoClickListener listener) {
        this.productos = productos;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto, parent, false);
        return new ProductoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductoViewHolder holder, int position) {
        Producto producto = productos.get(position);

        // Configurar datos del producto
        holder.tvNombre.setText(producto.getNombre());
        holder.tvDescripcion.setText(producto.getDescripcion() != null ?
                producto.getDescripcion() : "Sin descripción");
        holder.tvPrecio.setText("$" + String.format("%.2f", producto.getPrecio()));

        // CORREGIDO: Usar getEstado() en lugar de isEstado()
        boolean activo = producto.getEstado() == 1;
        String stockText = "Stock: " + (activo ? producto.getStock() : "Agotado");
        holder.tvStock.setText(stockText);

        // CORREGIDO: Usar getEstado()
        if (!activo || producto.getStock() <= 0) {
            holder.btnAgregarCarrito.setEnabled(false);
            holder.btnAgregarCarrito.setAlpha(0.3f);
        } else {
            holder.btnAgregarCarrito.setEnabled(true);
            holder.btnAgregarCarrito.setAlpha(1.0f);
        }

        // Cargar imagen con Glide
        String imagenUrl = producto.getImagen_url();
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            // Si es una URL completa
            if (imagenUrl.startsWith("http")) {
                Glide.with(context)
                        .load(imagenUrl)
                        .placeholder(R.drawable.ic_product_placeholder)
                        .error(R.drawable.ic_product_placeholder)
                        .into(holder.ivImagen);
            } else {
                // Usar la URL base de Constants
                Glide.with(context)
                        .load(Constants.IMAGES_BASE_URL + imagenUrl)
                        .placeholder(R.drawable.ic_product_placeholder)
                        .error(R.drawable.ic_product_placeholder)
                        .into(holder.ivImagen);
            }
        } else {
            holder.ivImagen.setImageResource(R.drawable.ic_product_placeholder);
        }

        // Configurar clicks
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onProductoClick(producto);
                }
            }
        });

        holder.btnAgregarCarrito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onAgregarCarrito(producto);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return productos != null ? productos.size() : 0;
    }

    public void actualizarLista(List<Producto> nuevosProductos) {
        this.productos = nuevosProductos;
        notifyDataSetChanged();
    }

    static class ProductoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagen;
        TextView tvNombre, tvDescripcion, tvPrecio, tvStock;
        ImageButton btnAgregarCarrito;

        ProductoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.ivProductoImagen);
            tvNombre = itemView.findViewById(R.id.tvProductoNombre);
            tvDescripcion = itemView.findViewById(R.id.tvProductoDescripcion);
            tvPrecio = itemView.findViewById(R.id.tvProductoPrecio);
            tvStock = itemView.findViewById(R.id.tvProductoStock);
            btnAgregarCarrito = itemView.findViewById(R.id.btnAgregarCarrito);
        }
    }
}