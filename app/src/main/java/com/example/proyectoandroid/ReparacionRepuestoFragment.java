package com.example.proyectoandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ReparacionRepuestoFragment extends Fragment {

    private RecyclerView rvRepuestos;
    private FloatingActionButton fabAgregarRepuesto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reparacion_repuesto, container, false);

        rvRepuestos = view.findViewById(R.id.rvRepuestos);
        fabAgregarRepuesto = view.findViewById(R.id.fabAgregarRepuesto);

        fabAgregarRepuesto.setOnClickListener(v -> {
            // Usamos getChildFragmentManager() porque estamos lanzando un dialog desde otro fragment
            AgregarRepuestoDialog dialog = new AgregarRepuestoDialog();
            dialog.show(getChildFragmentManager(), "AgregarRepuestoDialog");
        });

        // Aquí configurarías el RecyclerView para los repuestos
        // setupRecyclerView();

        return view;
    }
}
