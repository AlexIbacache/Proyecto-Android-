package com.example.proyectoandroid.ui.reparacion;

import android.app.DatePickerDialog;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReparacionFragment extends Fragment {

    private ReparacionViewModel viewModel;
    private Spinner spinnerMaquinaria;
    private TextInputEditText etFecha, etNotas;
    private RecyclerView recyclerViewPartes;
    private ReparacionPartesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reparacion, container, false);

        viewModel = new ViewModelProvider(this).get(ReparacionViewModel.class);

        // Inicializar vistas
        spinnerMaquinaria = view.findViewById(R.id.spinnerMaquinaria);
        etFecha = view.findViewById(R.id.etFecha);
        etNotas = view.findViewById(R.id.etNotas);
        recyclerViewPartes = view.findViewById(R.id.recyclerViewPartesReparacion);
        MaterialButton btnGuardar = view.findViewById(R.id.btnGuardarReparacion);

        setupRecyclerView();
        setupSpinnerWithMockData(); // Usamos datos de ejemplo

        // Configurar Listeners
        etFecha.setOnClickListener(v -> showDatePickerDialog());
        btnGuardar.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Guardar reparación... (Función próximamente)", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    private void setupRecyclerView() {
        recyclerViewPartes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReparacionPartesAdapter(new ArrayList<>());
        recyclerViewPartes.setAdapter(adapter);
    }

    private void setupSpinnerWithMockData() {
        List<String> nombresMaquinas = new ArrayList<>(Arrays.asList("Seleccione una máquina...", "Excavadora 320D", "Cargador Frontal 980H"));
        
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, nombresMaquinas) {
            @Override
            public boolean isEnabled(int position) {
                return position != 0;
            }

            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                if (position == 0) {
                    tv.setTextColor(Color.GRAY);
                } else {
                    tv.setTextColor(Color.BLACK);
                }
                return view;
            }
        };

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaquinaria.setAdapter(spinnerAdapter);

        spinnerMaquinaria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                 if (parent.getChildAt(0) instanceof TextView) {
                    if (position == 0) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                    }
                }

                if (position > 0) {
                    List<String> partes = (position == 1)
                        ? Arrays.asList("Cuchara Principal", "Motor Diesel", "Sistema Hidráulico")
                        : Arrays.asList("Neumáticos", "Cabina del Operador", "Motor");
                    adapter.updateData(partes);
                } else {
                    adapter.updateData(new ArrayList<>());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                adapter.updateData(new ArrayList<>());
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (v, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                    etFecha.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
