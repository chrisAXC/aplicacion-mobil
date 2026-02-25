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
import com.example.tiendaandroid.models.CarritoItem;
import com.example.tiendaandroid.utils.Constants;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CarritoAdapter extends RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder> {

    private List<CarritoItem> items;
    private Context context;
    private OnCarritoListener listener;

    public interface OnCarritoListener {
        void onCantidadCambiada(CarritoItem item, int nuevaCantidad);
        void onEliminarItem(CarritoItem item);
        void onItemClick(CarritoItem item);
    }

    public CarritoAdapter(List<CarritoItem> items, Context context, OnCarritoListener listener) {
        this.items = items;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CarritoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_carrito, parent, false);
        return new CarritoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarritoViewHolder holder, int position) {
        CarritoItem item = items.get(position);

        holder.tvNombre.setText(item.getNombre_producto());
        holder.tvPrecio.setText("$" + String.format(Locale.getDefault(), "%.2f", item.getPrecio_producto()));
        holder.tvCantidad.setText(String.valueOf(item.getCantidad()));
        holder.tvSubtotal.setText("$" + String.format(Locale.getDefault(), "%.2f", item.getSubtotal()));

        // Cargar imagen
        String imagenUrl = item.getImagen_producto();
        if (imagenUrl != null && !imagenUrl.isEmpty()) {
            if (imagenUrl.startsWith("http")) {
                Glide.with(context).load(imagenUrl)
                        .placeholder(R.drawable.ic_product_placeholder)
                        .error(R.drawable.ic_product_placeholder)
                        .into(holder.ivImagen);
            } else {
                Glide.with(context).load(Constants.IMAGES_BASE_URL + imagenUrl)
                        .placeholder(R.drawable.ic_product_placeholder)
                        .error(R.drawable.ic_product_placeholder)
                        .into(holder.ivImagen);
            }
        } else {
            holder.ivImagen.setImageResource(R.drawable.ic_product_placeholder);
        }

        // Botones de cantidad
        holder.btnSumar.setOnClickListener(v -> {
            if (item.getCantidad() < item.getStock_producto()) {
                listener.onCantidadCambiada(item, item.getCantidad() + 1);
            } else {
                Toast.makeText(context, "Stock máximo disponible", Toast.LENGTH_SHORT).show();
            }
        });

        holder.btnRestar.setOnClickListener(v -> {
            if (item.getCantidad() > 1) {
                listener.onCantidadCambiada(item, item.getCantidad() - 1);
            }
        });

        // Botón eliminar
        holder.btnEliminar.setOnClickListener(v ->
                listener.onEliminarItem(item)
        );

        // Click en el item
        holder.itemView.setOnClickListener(v ->
                listener.onItemClick(item)
        );
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public void actualizarLista(List<CarritoItem> nuevosItems) {
        this.items = nuevosItems;
        notifyDataSetChanged();
    }

    static class CarritoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagen;
        TextView tvNombre, tvPrecio, tvCantidad, tvSubtotal;
        ImageButton btnRestar, btnSumar, btnEliminar;

        CarritoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.ivCarritoImagen);
            tvNombre = itemView.findViewById(R.id.tvCarritoNombre);
            tvPrecio = itemView.findViewById(R.id.tvCarritoPrecio);
            tvCantidad = itemView.findViewById(R.id.tvCarritoCantidad);
            tvSubtotal = itemView.findViewById(R.id.tvCarritoSubtotal);
            btnRestar = itemView.findViewById(R.id.btnRestarCantidad);
            btnSumar = itemView.findViewById(R.id.btnSumarCantidad);
            btnEliminar = itemView.findViewById(R.id.btnEliminarItem);
        }
    }
}