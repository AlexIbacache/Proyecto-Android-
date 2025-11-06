package com.example.proyectoandroid.ui.reparacion.repuesto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.Repuesto;
import com.example.proyectoandroid.ui.reparacion.ReparacionRepuestoAdapter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReparacionRepuestoFragment extends Fragment {

    private ReparacionRepuestoViewModel viewModel;
    private RecyclerView rvRepuestosCatalogo;
    private ReparacionRepuestoAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reparacion_repuesto, container, false);

        viewModel = new ViewModelProvider(this).get(ReparacionRepuestoViewModel.class);

        rvRepuestosCatalogo = view.findViewById(R.id.rvRepuestosCatalogo);
        MaterialButton btnConfirmar = view.findViewById(R.id.btnConfirmarRepuestos);

        setupRecyclerView();
        loadMockData();

        btnConfirmar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Repuestos confirmados (Función próximamente)", Toast.LENGTH_SHORT).show();
            getParentFragmentManager().popBackStack();
        });

        return view;
    }

    private void setupRecyclerView() {
        rvRepuestosCatalogo.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReparacionRepuestoAdapter(new ArrayList<>(), new HashMap<>());
        rvRepuestosCatalogo.setAdapter(adapter);
    }

    private void loadMockData() {
        List<Repuesto> repuestosDeEjemplo = new ArrayList<>();
        repuestosDeEjemplo.add(new Repuesto("repuesto_1", "Filtro de Aire", "FA-123"));
        repuestosDeEjemplo.add(new Repuesto("repuesto_2", "Bujía de Iridio", "BJ-456"));
        repuestosDeEjemplo.add(new Repuesto("repuesto_3", "Pastillas de Freno", "PF-789"));

        adapter.updateData(repuestosDeEjemplo);
    }
}
