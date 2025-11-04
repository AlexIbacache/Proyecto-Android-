package com.example.proyectoandroid.ui.maquinaria;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MaquinariaFragment extends Fragment {

    private MaquinariaViewModel viewModel;
    private PartesAdapter partesAdapter;
    private ArrayAdapter<String> spinnerAdapter;
    private Spinner spinnerMaquinarias;
    private boolean isSpinnerInitialSetup = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maquinaria, container, false);

        spinnerMaquinarias = view.findViewById(R.id.spinnerMaquinarias);
        RecyclerView recyclerViewPartes = view.findViewById(R.id.recyclerViewMaquinarias);
        FloatingActionButton fabAgregarM = view.findViewById(R.id.fabAgregarMaquinaria);
        ImageButton btnModificarM = view.findViewById(R.id.btnModificarMaquinaria);
        ImageButton btnEliminarM = view.findViewById(R.id.btnEliminarMaquinaria);

        setupRecyclerView(recyclerViewPartes);

        viewModel = new ViewModelProvider(this).get(MaquinariaViewModel.class);

        observeViewModel(btnModificarM, btnEliminarM);

        setupListeners(btnModificarM, btnEliminarM, fabAgregarM);

        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        partesAdapter = new PartesAdapter(new ArrayList<>(), getContext());
        recyclerView.setAdapter(partesAdapter);
    }

    private void observeViewModel(ImageButton btnModificar, ImageButton btnEliminar) {
        viewModel.maquinarias.observe(getViewLifecycleOwner(), maquinarias -> {
            List<String> nombres = new ArrayList<>();
            nombres.add("Seleccione una maquinaria...");
            for (com.example.proyectoandroid.model.Maquinaria m : maquinarias) {
                nombres.add(m.getNombre());
            }
            spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, nombres);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMaquinarias.setAdapter(spinnerAdapter);
        });

        viewModel.partesSeleccionadas.observe(getViewLifecycleOwner(), partes -> {
            partesAdapter.updateData(partes);
        });

        viewModel.botonesDeAccionVisibles.observe(getViewLifecycleOwner(), isVisible -> {
            btnModificar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            btnEliminar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        });
    }

    private void setupListeners(ImageButton btnModificar, ImageButton btnEliminar, FloatingActionButton fabAgregar) {
        spinnerMaquinarias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 if (isSpinnerInitialSetup) {
                    isSpinnerInitialSetup = false;
                    return;
                }
                viewModel.onMaquinaSeleccionada(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                 viewModel.onMaquinaSeleccionada(0);
            }
        });

        btnEliminar.setOnClickListener(v -> {
            int position = spinnerMaquinarias.getSelectedItemPosition();
            new AlertDialog.Builder(getContext())
                    .setTitle("Eliminar Maquinaria")
                    .setMessage("¿Estás seguro?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        viewModel.eliminarMaquinaSeleccionada(position);
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        fabAgregar.setOnClickListener(v -> navigateToFormFragment());

        btnModificar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Modificar... (Función próximamente)", Toast.LENGTH_SHORT).show();
        });
    }

    private void navigateToFormFragment() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new MaquinariaFormFragment())
                .addToBackStack(null)
                .commit();
    }
}
