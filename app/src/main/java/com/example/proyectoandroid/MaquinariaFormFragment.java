package com.example.proyectoandroid;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class MaquinariaFormFragment extends Fragment {

    private TextInputEditText etFechaIngreso;
    private LinearLayout containerPartes;
    private int contadorPartes = 0;
    private static final int MAX_PARTES = 10;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maquinaria_form, container, false);

        etFechaIngreso = view.findViewById(R.id.etFechaIngreso);
        containerPartes = view.findViewById(R.id.containerPartes);
        FloatingActionButton fabAgregarParte = view.findViewById(R.id.fabAgregarParte);
        Button btnGuardarMaquinaria = view.findViewById(R.id.btnGuardarMaquinaria);

        btnGuardarMaquinaria.setOnClickListener(v -> {
            // Lógica para guardar la maquinaria
            Toast.makeText(getContext(), "Guardando maquinaria... (Función próximamente)", Toast.LENGTH_SHORT).show();
            // Aquí deberías volver al fragmento anterior
            getParentFragmentManager().popBackStack();
        });

        etFechaIngreso.setOnClickListener(v -> showDatePickerDialog());

        fabAgregarParte.setOnClickListener(v -> {
            if (contadorPartes >= MAX_PARTES) {
                Toast.makeText(getContext(), "Máximo de partes alcanzado (" + MAX_PARTES + ")", Toast.LENGTH_SHORT).show();
                return;
            }
            agregarParteView();
        });

        return view;
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
                    etFechaIngreso.setText(selectedDate);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void agregarParteView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View parteView = inflater.inflate(R.layout.item_parte_editable, containerPartes, false);

        ImageButton btnRemover = parteView.findViewById(R.id.btnRemoverParte);
        btnRemover.setOnClickListener(v -> {
            containerPartes.removeView(parteView);
            contadorPartes--;
            Toast.makeText(getContext(), "Parte removida", Toast.LENGTH_SHORT).show();
        });

        containerPartes.addView(parteView);
        contadorPartes++;
        Toast.makeText(getContext(), "Parte agregada", Toast.LENGTH_SHORT).show();
    }
}
