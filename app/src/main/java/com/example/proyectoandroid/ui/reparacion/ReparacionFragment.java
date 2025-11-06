package com.example.proyectoandroid.ui.reparacion;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
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
import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.model.Repuesto;
import com.example.proyectoandroid.ui.repuesto.RepuestoActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReparacionFragment extends Fragment implements ReparacionPartesAdapter.OnItemClickListener {

    private ReparacionViewModel viewModel;
    private SharedReparacionViewModel sharedViewModel;
    private Spinner spinnerMaquinaria;
    private TextInputEditText etFecha, etNotas;
    private RecyclerView recyclerViewPartes;
    private ReparacionPartesAdapter adapter;
    private List<Maquinaria> maquinariaList = new ArrayList<>();
    private ActivityResultLauncher<Intent> repuestoActivityResultLauncher;
    private String parteSeleccionada;
    private MaterialButton btnFinalizarReparacion;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedReparacionViewModel.class);

        repuestoActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        List<Repuesto> repuestos = (List<Repuesto>) result.getData().getSerializableExtra(RepuestoActivity.EXTRA_REPUESTOS_LISTA);
                        if (repuestos != null && parteSeleccionada != null) {
                            viewModel.actualizarRepuestosParaParte(parteSeleccionada, repuestos);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reparacion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(ReparacionViewModel.class);

        spinnerMaquinaria = view.findViewById(R.id.spinnerMaquinaria);
        etFecha = view.findViewById(R.id.etFecha);
        etNotas = view.findViewById(R.id.etNotas);
        recyclerViewPartes = view.findViewById(R.id.recyclerViewPartesReparacion);
        MaterialButton btnGuardar = view.findViewById(R.id.btnGuardarReparacion);
        btnFinalizarReparacion = view.findViewById(R.id.btnFinalizarReparacion);

        setupRecyclerView();
        setupSpinner();
        setupObservers();

        sharedViewModel.getReparacionParaEditar().observe(getViewLifecycleOwner(), reparacion -> {
            if (reparacion != null) {
                Maquinaria maquina = sharedViewModel.getMaquinaDeLaReparacion().getValue();
                if (maquina != null) {
                    viewModel.cargarReparacionParaEdicion(reparacion, maquina);
                    sharedViewModel.clearData(); 
                }
            }
        });

        etFecha.setOnClickListener(v -> viewModel.onFechaClicked());
        btnGuardar.setOnClickListener(v -> {
            String notas = etNotas.getText() != null ? etNotas.getText().toString() : "";
            viewModel.guardarReparacion(notas);
        });
        
        btnFinalizarReparacion.setOnClickListener(v -> showConfirmarFinalizarDialog());
    }
    
    private void showConfirmarFinalizarDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_confirmar_finalizar_reparacion, null);
        builder.setView(dialogView);

        final AlertDialog dialog = builder.create();

        MaterialButton btnSi = dialogView.findViewById(R.id.btnDialogSi);
        MaterialButton btnNo = dialogView.findViewById(R.id.btnDialogNo);

        btnSi.setOnClickListener(v -> {
            viewModel.finalizarReparacion();
            dialog.dismiss();
        });

        btnNo.setOnClickListener(v -> {
            dialog.dismiss();
        });

        dialog.show();
    }

    private void setupRecyclerView() {
        recyclerViewPartes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReparacionPartesAdapter(new ArrayList<>(), viewModel);
        adapter.setOnItemClickListener(this);
        recyclerViewPartes.setAdapter(adapter);
    }

    private void setupSpinner() {
        spinnerMaquinaria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getChildAt(0) instanceof TextView) {
                    ((TextView) parent.getChildAt(0)).setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                }

                if (position > 0) {
                    Maquinaria selected = maquinariaList.get(position - 1);
                    viewModel.onMaquinariaSelected(selected);
                } else {
                    viewModel.onMaquinariaSelected(null);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                viewModel.onMaquinariaSelected(null);
            }
        });
    }

    private void setupObservers() {
        viewModel.getMaquinarias().observe(getViewLifecycleOwner(), maquinarias -> {
             if (maquinarias != null && !maquinarias.isEmpty()) {
                this.maquinariaList.clear();
                this.maquinariaList.addAll(maquinarias);

                List<String> nombresMaquinas = new ArrayList<>();
                nombresMaquinas.add("Seleccione una máquina...");
                for (Maquinaria m : maquinarias) {
                    nombresMaquinas.add(m.getNombre());
                }

                ArrayAdapter<String> spinnerAdapter = createSpinnerAdapter(nombresMaquinas);
                spinnerMaquinaria.setAdapter(spinnerAdapter);
                spinnerMaquinaria.setEnabled(true);

                viewModel.getMaquinaASeleccionar().observe(getViewLifecycleOwner(), maquinaId -> {
                    if (maquinaId != null) {
                        for (int i = 0; i < maquinariaList.size(); i++) {
                            if (maquinariaList.get(i).getDocumentId().equals(maquinaId)) {
                                spinnerMaquinaria.setSelection(i + 1);
                                break;
                            }
                        }
                    } else {
                        spinnerMaquinaria.setSelection(0);
                    }
                });
            } else {
                this.maquinariaList.clear();
                List<String> emptyList = new ArrayList<>();
                emptyList.add("No hay máquinas disponibles");
                ArrayAdapter<String> emptyAdapter = createSpinnerAdapter(emptyList);
                spinnerMaquinaria.setAdapter(emptyAdapter);
                spinnerMaquinaria.setEnabled(false);
            }
        });

        viewModel.getPartesMaquinaria().observe(getViewLifecycleOwner(), partes -> {
            adapter.updateData(partes != null ? partes : new ArrayList<>());
            // Controlar visibilidad del botón Finalizar
            btnFinalizarReparacion.setVisibility(viewModel.isEditMode() ? View.VISIBLE : View.GONE);
        });

        viewModel.getShowDatePickerEvent().observe(getViewLifecycleOwner(), e -> showDatePickerDialog());

        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            etFecha.setText(date);
        });

        viewModel.getReparacionGuardadaState().observe(getViewLifecycleOwner(), success -> {
            if (success == null) return;
            if (success) {
                if (viewModel.isEditMode()) {
                    Toast.makeText(getContext(), "Cambios guardados con éxito", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Reparación guardada con éxito", Toast.LENGTH_SHORT).show();
                    limpiarFormularioCompleto();
                }
            } else {
                Toast.makeText(getContext(), "Error al guardar. Verifique los datos.", Toast.LENGTH_LONG).show();
            }
            viewModel.resetSaveState();
        });
        
        viewModel.getReparacionFinalizadaState().observe(getViewLifecycleOwner(), success -> {
            if (success == null) return;
            if (success) {
                Maquinaria maquinaSeleccionada = null;
                int selectedPosition = spinnerMaquinaria.getSelectedItemPosition();
                if (selectedPosition > 0 && maquinariaList != null && !maquinariaList.isEmpty()) {
                    maquinaSeleccionada = maquinariaList.get(selectedPosition - 1);
                }

                if (maquinaSeleccionada != null) {
                    String mensaje = "Reparación finalizada y guardada para la máquina " + maquinaSeleccionada.getNombre() + " (ID: " + maquinaSeleccionada.getDocumentId() + ")";
                    Toast.makeText(getContext(), mensaje, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getContext(), "Reparación Finalizada", Toast.LENGTH_SHORT).show();
                }
                limpiarFormularioCompleto();
            } else {
                Toast.makeText(getContext(), "Error al finalizar la reparación.", Toast.LENGTH_SHORT).show();
            }
            viewModel.resetSaveState();
        });
        
        viewModel.getNotasEdicion().observe(getViewLifecycleOwner(), notas -> {
            etNotas.setText(notas);
        });
    }

    private void limpiarFormularioCompleto() {
        viewModel.limpiarFormulario();
        // No es necesario limpiar el spinner aquí, el observer de maquinaASeleccionar se encargará
    }

    private ArrayAdapter<String> createSpinnerAdapter(List<String> items) {
        return new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, items) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                return view;
            }
        };
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(
                requireContext(),
                (v, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                    viewModel.onDateSelected(selectedDate);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    @Override
    public void onAnadirRepuestoClick(String parte) {
        this.parteSeleccionada = parte;
        Intent intent = new Intent(getActivity(), RepuestoActivity.class);
        intent.putExtra(RepuestoActivity.EXTRA_PARTE_NOMBRE, parte);

        List<Repuesto> repuestosExistentes = viewModel.getRepuestosParaParte(parte);
        if (repuestosExistentes != null && !repuestosExistentes.isEmpty()) {
            intent.putExtra(RepuestoActivity.EXTRA_REPUESTOS_LISTA, (Serializable) repuestosExistentes);
        }

        repuestoActivityResultLauncher.launch(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (isRemoving() || (getParentFragment() != null && getParentFragment().isRemoving())) {
            viewModel.limpiarFormulario();
        }
    }
}
