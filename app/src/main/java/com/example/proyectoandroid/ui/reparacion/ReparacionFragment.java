package com.example.proyectoandroid.ui.reparacion;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.ui.reparacion.repuesto.ReparacionRepuestoFragment;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReparacionFragment extends Fragment {

    private ReparacionViewModel viewModel;
    private EditText etFecha;
    private Spinner spinnerMaquinaria;
    private ImageButton btnMotor, btnBomba, btnBrazo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reparacion, container, false);

        viewModel = new ViewModelProvider(this).get(ReparacionViewModel.class);

        spinnerMaquinaria = view.findViewById(R.id.spinnerMaquinaria);
        btnMotor = view.findViewById(R.id.btnMotor);
        btnBrazo = view.findViewById(R.id.btnBrazo);
        btnBomba = view.findViewById(R.id.btnBomba);
        etFecha = view.findViewById(R.id.etFecha);

        viewModel.getMaquinarias().observe(getViewLifecycleOwner(), maquinarias -> {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, maquinarias);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerMaquinaria.setAdapter(adapter);
        });

        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), date -> {
            etFecha.setText(date);
        });

        viewModel.getShowDatePickerEvent().observe(getViewLifecycleOwner(), yearMonthDay -> {
            showDatePickerDialog(yearMonthDay[0], yearMonthDay[1], yearMonthDay[2]);
        });

        viewModel.getNavigateToRepuestoFragmentEvent().observe(getViewLifecycleOwner(), aVoid -> {
            navigateToRepuestoFragment();
        });

        spinnerMaquinaria.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                viewModel.onMaquinariaSelected(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        etFecha.setOnClickListener(v -> viewModel.onFechaClicked());

        View.OnClickListener listener = v -> viewModel.onRepuestoButtonClicked();
        btnMotor.setOnClickListener(listener);
        btnBrazo.setOnClickListener(listener);
        btnBomba.setOnClickListener(listener);

        viewModel.loadMaquinarias();

        return view;
    }

    private void showDatePickerDialog(int year, int month, int dayOfMonth) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", selectedDayOfMonth, selectedMonth + 1, selectedYear);
                    viewModel.onDateSelected(selectedDate);
                },
                year, month, dayOfMonth
        );
        datePickerDialog.show();
    }

    private void navigateToRepuestoFragment() {
        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new ReparacionRepuestoFragment())
                .addToBackStack(null)
                .commit();
    }
}
