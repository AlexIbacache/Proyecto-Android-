package com.example.proyectoandroid.ui.maquinaria;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.Maquinaria;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MaquinariaFragment extends Fragment {

    private MaquinariaViewModel viewModel;
    private PartesAdapter partesAdapter;
    private ArrayAdapter<String> spinnerAdapter;
    private Spinner spinnerMaquinarias;
    private boolean isSpinnerInitialSetup = true;
    private List<Maquinaria> mockMaquinariaList = new ArrayList<>(); // Lista para datos de ejemplo

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maquinaria, container, false);

        spinnerMaquinarias = view.findViewById(R.id.spinnerMaquinarias);
        RecyclerView recyclerViewPartes = view.findViewById(R.id.recyclerViewMaquinarias);
        MaterialButton btnRegistrar = view.findViewById(R.id.btnGuardarMaquinaria);
        ImageButton btnModificarM = view.findViewById(R.id.btnModificarMaquinaria);
        ImageButton btnEliminarM = view.findViewById(R.id.btnEliminarMaquinaria);

        setupRecyclerView(recyclerViewPartes);

        viewModel = new ViewModelProvider(this).get(MaquinariaViewModel.class);

        // --- INICIO: Código de ejemplo para previsualización ---
        createMockDataAndSetupSpinner();
        // --- FIN: Código de ejemplo para previsualización ---

        // observeViewModel(btnModificarM, btnEliminarM); // Comentado para usar datos de ejemplo

        setupListeners(btnModificarM, btnEliminarM, btnRegistrar);

        return view;
    }

    private void createMockDataAndSetupSpinner() {
        // Crear datos de ejemplo
        Maquinaria maquina1 = new Maquinaria();
        maquina1.setNombre("Excavadora 320D");
        maquina1.setPartesPrincipales(Arrays.asList("Cuchara Principal", "Motor Diesel", "Sistema Hidráulico", "Orugas de Acero"));

        Maquinaria maquina2 = new Maquinaria();
        maquina2.setNombre("Cargador Frontal 980H");
        maquina2.setPartesPrincipales(Arrays.asList("Neumáticos", "Cabina del Operador", "Motor", "Balde de Carga"));

        mockMaquinariaList.add(maquina1);
        mockMaquinariaList.add(maquina2);

        // Poblar el Spinner
        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione una maquinaria...");
        for (Maquinaria m : mockMaquinariaList) {
            nombres.add(m.getNombre());
        }

        spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, nombres) {
            @Override
            public boolean isEnabled(int position) {
                // Deshabilita el primer item (el "hint")
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    // Estilo para el "hint"
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaquinarias.setAdapter(spinnerAdapter);
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        partesAdapter = new PartesAdapter(new ArrayList<>());
        recyclerView.setAdapter(partesAdapter);
    }

    private void observeViewModel(ImageButton btnModificar, ImageButton btnEliminar) {
        viewModel.maquinarias.observe(getViewLifecycleOwner(), maquinarias -> {
            List<String> nombres = new ArrayList<>();
            nombres.add("Seleccione una maquinaria...");
            for (Maquinaria m : maquinarias) {
                nombres.add(m.getNombre());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, nombres);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMaquinarias.setAdapter(adapter);
        });

        viewModel.partesSeleccionadas.observe(getViewLifecycleOwner(), partes -> {
            partesAdapter.updateData(partes);
        });

        viewModel.botonesDeAccionVisibles.observe(getViewLifecycleOwner(), isVisible -> {
            btnModificar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
            btnEliminar.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        });
    }

    private void setupListeners(ImageButton btnModificar, ImageButton btnEliminar, MaterialButton btnRegistrar) {
        spinnerMaquinarias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 if (position == 0) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                 } else {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                 }

                if (isSpinnerInitialSetup) {
                    isSpinnerInitialSetup = false;
                    return;
                }
                // --- Lógica para datos de ejemplo ---
                if (position > 0) {
                    Maquinaria maquinaSeleccionada = mockMaquinariaList.get(position - 1);
                    partesAdapter.updateData(maquinaSeleccionada.getPartesPrincipales());
                    btnModificar.setVisibility(View.VISIBLE);
                    btnEliminar.setVisibility(View.VISIBLE);
                } else {
                    partesAdapter.updateData(new ArrayList<>());
                    btnModificar.setVisibility(View.GONE);
                    btnEliminar.setVisibility(View.GONE);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                 partesAdapter.updateData(new ArrayList<>());
                 btnModificar.setVisibility(View.GONE);
                 btnEliminar.setVisibility(View.GONE);
            }
        });

        btnEliminar.setOnClickListener(v -> {
            int position = spinnerMaquinarias.getSelectedItemPosition();
            new AlertDialog.Builder(getContext())
                    .setTitle("Eliminar Maquinaria")
                    .setMessage("¿Estás seguro?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        Toast.makeText(getContext(), "Eliminado (ejemplo)", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        btnRegistrar.setOnClickListener(v -> navigateToFormFragment());

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
