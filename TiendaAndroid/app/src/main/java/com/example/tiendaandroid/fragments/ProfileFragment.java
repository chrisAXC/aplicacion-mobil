package com.example.tiendaandroid.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import com.example.tiendaandroid.R;
import com.example.tiendaandroid.models.Usuario;
import com.example.tiendaandroid.utils.SharedPrefManager;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView tvNombre = view.findViewById(R.id.tvNombre);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView tvTelefono = view.findViewById(R.id.tvTelefono);

        Usuario usuario = SharedPrefManager.getInstance(getContext()).getUser();
        if (usuario != null) {
            tvNombre.setText("Nombre: " + usuario.getNombre());
            tvEmail.setText("Email: " + usuario.getEmail());
            tvTelefono.setText("Teléfono: " + (usuario.getTelefono() != null ? usuario.getTelefono() : "No registrado"));
        }

        return view;
    }
}