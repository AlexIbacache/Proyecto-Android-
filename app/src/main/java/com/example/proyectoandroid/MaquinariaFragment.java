package com.example.proyectoandroid;

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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MaquinariaFragment extends Fragment {

    private FloatingActionButton fabAgregarM;
    private ImageButton btnModificarM, btnEliminarM;
    private RecyclerView recyclerViewPartes;
    private Spinner spinnerMaquinarias;

    private List<Maquinaria> listaMaquinariaEjemplo = new ArrayList<>();
    private PartesAdapter partesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maquinaria, container, false);

        // 1. Inicializar Vistas
        fabAgregarM = view.findViewById(R.id.fabAgregarMaquinaria);
        btnModificarM = view.findViewById(R.id.btnModificarMaquinaria);
        btnEliminarM = view.findViewById(R.id.btnEliminarMaquinaria);
        recyclerViewPartes = view.findViewById(R.id.recyclerViewMaquinarias);
        spinnerMaquinarias = view.findViewById(R.id.spinnerMaquinarias);

        // 2. Configurar Listeners
        fabAgregarM.setOnClickListener(v -> navigateToFormFragment());
        btnModificarM.setOnClickListener(v -> modificarMaquinariaSeleccionada());
        btnEliminarM.setOnClickListener(v -> eliminarMaquinariaSeleccionada());

        // 3. Configurar el RecyclerView
        setupRecyclerView();

        // 4. Crear datos y configurar el Spinner
        setupSpinnerAndData();

        return view;
    }

    private void setupSpinnerAndData() {
        listaMaquinariaEjemplo.clear();
        listaMaquinariaEjemplo.add(new Maquinaria("Excavadora Caterpillar", Arrays.asList("Cuchara Principal", "Motor Diesel", "Sistema Hidráulico", "Orugas de Acero")));
        listaMaquinariaEjemplo.add(new Maquinaria("Tractor John Deere", Arrays.asList("Ruedas Delanteras", "Ruedas Traseras", "Cabina del Operador", "Motor")));
        listaMaquinariaEjemplo.add(new Maquinaria("Grúa Liebherr", Arrays.asList("Pluma Principal", "Contrapeso", "Gancho de Carga", "Sistema de Elevación")));

        List<String> nombres = new ArrayList<>();
        nombres.add("Seleccione una maquinaria...");
        for (Maquinaria m : listaMaquinariaEjemplo) {
            nombres.add(m.getNombre());
        }

        // Usar el layout personalizado para el texto blanco
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, nombres);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaquinarias.setAdapter(spinnerAdapter);

        spinnerMaquinarias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                boolean isHintSelected = position == 0;
                btnModificarM.setVisibility(isHintSelected ? View.GONE : View.VISIBLE);
                btnEliminarM.setVisibility(isHintSelected ? View.GONE : View.VISIBLE);

                if (!isHintSelected) {
                    Maquinaria maquinaSeleccionada = listaMaquinariaEjemplo.get(position - 1);
                    partesAdapter.updateData(maquinaSeleccionada.getPartes());
                } else {
                    partesAdapter.updateData(new ArrayList<>());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                partesAdapter.updateData(new ArrayList<>());
                btnModificarM.setVisibility(View.GONE);
                btnEliminarM.setVisibility(View.GONE);
            }
        });

        btnModificarM.setVisibility(View.GONE);
        btnEliminarM.setVisibility(View.GONE);
    }

    private void modificarMaquinariaSeleccionada() {
        int position = spinnerMaquinarias.getSelectedItemPosition();
        if (position > 0) {
            Maquinaria maquinaParaModificar = listaMaquinariaEjemplo.get(position - 1);
            Toast.makeText(getContext(), "Modificar: " + maquinaParaModificar.getNombre(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Seleccione una maquinaria para modificar", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarMaquinariaSeleccionada() {
        int position = spinnerMaquinarias.getSelectedItemPosition();
        if (position > 0) {
            String nombreMaquina = listaMaquinariaEjemplo.get(position - 1).getNombre();
            new AlertDialog.Builder(getContext())
                    .setTitle("Eliminar Maquinaria")
                    .setMessage("¿Estás seguro de que quieres eliminar '" + nombreMaquina + "'?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        listaMaquinariaEjemplo.remove(position - 1);
                        setupSpinnerAndData();
                        spinnerMaquinarias.setSelection(0);
                        Toast.makeText(getContext(), nombreMaquina + " ha sido eliminada.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        } else {
            Toast.makeText(getContext(), "Seleccione una maquinaria para eliminar", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupRecyclerView() {
        recyclerViewPartes.setLayoutManager(new LinearLayoutManager(getContext()));
        partesAdapter = new PartesAdapter(new ArrayList<>(), getContext());
        recyclerViewPartes.setAdapter(partesAdapter);
    }

    private void navigateToFormFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new MaquinariaFormFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
