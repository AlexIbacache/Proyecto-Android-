package com.example.proyectoandroid.ui.reparacion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.adapters.MaquinariaSpinnerAdapter;
import com.example.proyectoandroid.adapters.ParteReparadaAdapter;
import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.model.Repuesto;
import com.example.proyectoandroid.ui.repuesto.RepuestoActivity;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReparacionFragment extends Fragment implements ParteReparadaAdapter.OnRepuestoDeleteListener {

    private ReparacionViewModel reparacionViewModel;
    private TextInputEditText etFecha, etNotas;
    private RecyclerView rvPartesReparadas;
    private ParteReparadaAdapter adapter;
    private ActivityResultLauncher<Intent> repuestoActivityResultLauncher;
    private Spinner spinnerMaquinaria;
    private Button btnGuardarReparacion, btnFinalizarReparacion;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_reparacion, container, false);

        reparacionViewModel = new ViewModelProvider(this).get(ReparacionViewModel.class);

        etFecha = root.findViewById(R.id.etFecha);
        etNotas = root.findViewById(R.id.etNotas);
        rvPartesReparadas = root.findViewById(R.id.recyclerViewPartesReparacion);
        spinnerMaquinaria = root.findViewById(R.id.spinnerMaquinaria);
        btnGuardarReparacion = root.findViewById(R.id.btnGuardarReparacion);
        btnFinalizarReparacion = root.findViewById(R.id.btnFinalizarReparacion);

        setupRecyclerView();
        setupDatePicker();
        setupObservers();
        setupClickListeners();

        repuestoActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {
                        List<Repuesto> repuestos = (List<Repuesto>) result.getData().getSerializableExtra(RepuestoActivity.EXTRA_REPUESTOS_LISTA);
                        String parteNombre = result.getData().getStringExtra(RepuestoActivity.EXTRA_PARTE_NOMBRE);
                        reparacionViewModel.actualizarRepuestosParaParte(parteNombre, repuestos);
                    }
                });

        return root;
    }

    private void setupRecyclerView() {
        adapter = new ParteReparadaAdapter(new ArrayList<>(), this::onAddRepuestoClick, this);
        rvPartesReparadas.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPartesReparadas.setAdapter(adapter);
    }

    private void setupDatePicker() {
        etFecha.setOnClickListener(v -> {
            MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText("Seleccionar fecha")
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build();

            datePicker.addOnPositiveButtonClickListener(selection -> {
                reparacionViewModel.setFecha(new Date(selection));
                etFecha.setText(datePicker.getHeaderText());
            });

            datePicker.show(getParentFragmentManager(), "MATERIAL_DATE_PICKER");
        });
    }

    private void setupObservers() {
        reparacionViewModel.getPartesReparadas().observe(getViewLifecycleOwner(), partes -> {
            adapter.setPartes(partes);
        });

        reparacionViewModel.getMaquinarias().observe(getViewLifecycleOwner(), maquinarias -> {
            if (maquinarias != null) {
                ArrayList<Maquinaria> spinnerList = new ArrayList<>();
                spinnerList.add(new Maquinaria(null, "Seleccione una maquinaria"));
                spinnerList.addAll(maquinarias);

                MaquinariaSpinnerAdapter maquinariaAdapter = new MaquinariaSpinnerAdapter(requireContext(), spinnerList);
                spinnerMaquinaria.setAdapter(maquinariaAdapter);
                setupSpinnerListener();
            }
        });

        reparacionViewModel.getShowToastEvent().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        reparacionViewModel.getReparacionAbierta().observe(getViewLifecycleOwner(), reparacion -> {
            btnFinalizarReparacion.setVisibility(reparacion != null ? View.VISIBLE : View.GONE);
            btnGuardarReparacion.setText(reparacion != null ? "Guardar Cambios" : "Guardar Reparación");
            if (reparacion != null) {
                etNotas.setText(reparacion.getNotas());
            } else {
                etNotas.setText(""); // Limpiar notas si no hay reparación abierta
            }
        });
    }

    private void setupClickListeners() {
        btnGuardarReparacion.setOnClickListener(v -> {
            reparacionViewModel.guardarReparacion(etNotas.getText().toString());
        });

        btnFinalizarReparacion.setOnClickListener(v -> {
            mostrarDialogoConfirmacion();
        });
    }

    private void setupSpinnerListener() {
        spinnerMaquinaria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Maquinaria maquinaria = (Maquinaria) parent.getItemAtPosition(position);
                    reparacionViewModel.onMaquinariaSelected(maquinaria);
                } else {
                    reparacionViewModel.onMaquinariaSelected(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                reparacionViewModel.onMaquinariaSelected(null);
            }
        });
    }

    private void mostrarDialogoConfirmacion() {
        new AlertDialog.Builder(requireContext())
                .setTitle("Finalizar Reparación")
                .setMessage("¿Estás seguro de que deseas marcar esta reparación como finalizada? No podrás volver a editarla.")
                .setPositiveButton("Sí, Finalizar", (dialog, which) -> {
                    reparacionViewModel.finalizarReparacion();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void onAddRepuestoClick(String parte, List<Repuesto> repuestosExistentes) {
        Intent intent = new Intent(getContext(), RepuestoActivity.class);
        intent.putExtra(RepuestoActivity.EXTRA_PARTE_NOMBRE, parte);
        if (repuestosExistentes != null && !repuestosExistentes.isEmpty()) {
            intent.putExtra(RepuestoActivity.EXTRA_REPUESTOS_LISTA, (Serializable) repuestosExistentes);
        }
        repuestoActivityResultLauncher.launch(intent);
    }

    @Override
    public void onRepuestoDelete(String parteNombre, Repuesto repuesto) {
        reparacionViewModel.eliminarRepuestoDeParte(parteNombre, repuesto);
    }
}
