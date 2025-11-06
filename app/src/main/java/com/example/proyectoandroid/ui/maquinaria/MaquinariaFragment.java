package com.example.proyectoandroid.ui.maquinaria;

import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.Maquinaria;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class MaquinariaFragment extends Fragment implements PartesAdapter.OnPartInteractionListener {

    private MaquinariaViewModel viewModel;
    private PartesAdapter partesAdapter;
    private Spinner spinnerMaquinarias;
    private List<Maquinaria> maquinariaList = new ArrayList<>();

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

        observeViewModel(btnModificarM, btnEliminarM);
        setupListeners(btnModificarM, btnEliminarM, btnRegistrar);

        return view;
    }

    private void setupRecyclerView(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        partesAdapter = new PartesAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(partesAdapter);
    }

    private void observeViewModel(ImageButton btnModificar, ImageButton btnEliminar) {
        viewModel.getMaquinarias().observe(getViewLifecycleOwner(), maquinarias -> {
            if (maquinarias != null && getContext() != null) {
                maquinariaList.clear();
                maquinariaList.addAll(maquinarias);
                
                List<String> nombres = new ArrayList<>();
                nombres.add("Seleccione una maquinaria..."); // Hint para el spinner
                for (Maquinaria m : maquinarias) {
                    nombres.add(m.getNombre());
                }

                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_custom, nombres);
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerMaquinarias.setAdapter(spinnerAdapter);

                if (maquinarias.isEmpty()) {
                    Toast.makeText(getContext(), "No hay maquinarias registradas", Toast.LENGTH_SHORT).show();
                }
            } else if (isAdded()) {
                Toast.makeText(getContext(), "Error al cargar las maquinarias", Toast.LENGTH_SHORT).show();
            }
        });

        viewModel.partesSeleccionadas.observe(getViewLifecycleOwner(), partes -> {
            if (partes != null) {
                partesAdapter.updateData(partes);
            }
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
                    viewModel.onMaquinaSeleccionada(null);
                } else {
                    viewModel.onMaquinaSeleccionada(maquinariaList.get(position - 1));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.onMaquinaSeleccionada(null);
            }
        });

        btnEliminar.setOnClickListener(v -> {
            int position = spinnerMaquinarias.getSelectedItemPosition();
            if (position > 0 && position <= maquinariaList.size()) {
                Maquinaria maquinaAEliminar = maquinariaList.get(position - 1);
                new AlertDialog.Builder(getContext())
                        .setTitle("Eliminar Maquinaria")
                        .setMessage("¿Estás seguro de que quieres eliminar '" + maquinaAEliminar.getNombre() + "'?")
                        .setPositiveButton("Eliminar", (dialog, which) -> viewModel.eliminarMaquinaSeleccionada(maquinaAEliminar))
                        .setNegativeButton("Cancelar", null).show();
            }
        });

        btnRegistrar.setOnClickListener(v -> navigateToFormFragment(null));

        btnModificar.setOnClickListener(v -> {
            int position = spinnerMaquinarias.getSelectedItemPosition();
            if (position > 0 && position <= maquinariaList.size()) {
                Maquinaria maquinaAEditar = maquinariaList.get(position - 1);
                navigateToFormFragment(maquinaAEditar.getDocumentId());
            } else {
                Toast.makeText(getContext(), "Selecciona una maquinaria para editar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToFormFragment(@Nullable String maquinariaId) {
        MaquinariaFormFragment formFragment = new MaquinariaFormFragment();
        if (maquinariaId != null) {
            Bundle args = new Bundle();
            args.putString("maquinariaId", maquinariaId);
            formFragment.setArguments(args);
        }
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, formFragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onEditPart(int position, String currentName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Editar Nombre de la Parte");

        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(currentName);
        builder.setView(input);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoNombre = input.getText().toString();
            viewModel.actualizarParte(position, nuevoNombre);
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    @Override
    public void onDeletePart(int position) {
        new AlertDialog.Builder(getContext())
                .setTitle("Eliminar Parte")
                .setMessage("¿Estás seguro de que quieres eliminar esta parte?")
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    viewModel.eliminarParte(position);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
