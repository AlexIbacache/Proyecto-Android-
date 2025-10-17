package com.example.proyectoandroid;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.Calendar;
import java.util.Locale;

public class ReparacionFragment extends Fragment {

    private ImageButton btnMotor, btnBomba, btnBrazo;
    private EditText etFecha;
    private Spinner spinnerMaquinaria;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reparacion, container, false);

        spinnerMaquinaria = view.findViewById(R.id.spinnerMaquinaria);
        btnMotor = view.findViewById(R.id.btnMotor);
        btnBrazo = view.findViewById(R.id.btnBrazo);
        btnBomba = view.findViewById(R.id.btnBomba);
        etFecha = view.findViewById(R.id.etFecha);

        // Configurar Spinner con el layout personalizado para texto blanco
        String[] maquinariaArray = {"Excavadora hidráulica", "Perforadora jumbo", "Camión", "Bulldozer (Topadora)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.custom_spinner_item, maquinariaArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaquinaria.setAdapter(adapter);

        // Configurar listeners de los botones para navegar al fragmento de repuestos
        View.OnClickListener listener = v -> navigateToRepuestoFragment();
        btnMotor.setOnClickListener(listener);
        btnBrazo.setOnClickListener(listener);
        btnBomba.setOnClickListener(listener);

        // Configurar DatePickerDialog para el EditText de fecha
        etFecha.setOnClickListener(v -> showDatePickerDialog());

        return view;
    }

    private void navigateToRepuestoFragment() {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, new ReparacionRepuestoFragment());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, month + 1, year);
                    etFecha.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
