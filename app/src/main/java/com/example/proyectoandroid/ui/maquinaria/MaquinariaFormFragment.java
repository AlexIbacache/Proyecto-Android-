package com.example.proyectoandroid.ui.maquinaria;

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
import androidx.lifecycle.ViewModelProvider;

import com.example.proyectoandroid.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MaquinariaFormFragment extends Fragment {

    private MaquinariaFormViewModel viewModel;
    private TextInputEditText etFechaIngreso, etNombreMaquinaria;
    private LinearLayout containerPartes;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maquinaria_form, container, false);

        setupViews(view);
        setupViewModel();
        setupObservers();
        setupClickListeners();

        return view;
    }

    private void setupViews(View view) {
        etNombreMaquinaria = view.findViewById(R.id.etNombreMaquinaria);
        etFechaIngreso = view.findViewById(R.id.etFechaIngreso);
        containerPartes = view.findViewById(R.id.containerPartes);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(MaquinariaFormViewModel.class);
    }

    private void setupObservers() {
        viewModel.fechaIngreso.observe(getViewLifecycleOwner(), fecha -> {
            etFechaIngreso.setText(fecha);
        });

        viewModel.getAddParteViewEvent().observe(getViewLifecycleOwner(), aVoid -> {
            agregarParteView();
        });

        viewModel.getShowMaxPartesToastEvent().observe(getViewLifecycleOwner(), aVoid -> {
            Toast.makeText(getContext(), "Máximo de partes alcanzado", Toast.LENGTH_SHORT).show();
        });

        viewModel.getSaveMaquinariaEvent().observe(getViewLifecycleOwner(), success -> {
            if (success) {
                Toast.makeText(getContext(), "Maquinaria guardada con éxito", Toast.LENGTH_SHORT).show();
                getParentFragmentManager().popBackStack();
            } else {
                Toast.makeText(getContext(), "Error al guardar. Revisa los datos.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        etFechaIngreso.setOnClickListener(v -> showDatePickerDialog());

        Button btnGuardarMaquinaria = getView().findViewById(R.id.btnGuardarMaquinaria);
        btnGuardarMaquinaria.setOnClickListener(v -> {
            // Recolectar datos y pasarlos al ViewModel
            String nombre = etNombreMaquinaria.getText().toString();
            // Aquí recolectarías las partes del containerPartes
            viewModel.guardarMaquinaria(/* nombre, listaDePartes */);
        });

        FloatingActionButton fabAgregarParte = getView().findViewById(R.id.fabAgregarParte);
        fabAgregarParte.setOnClickListener(v -> {
            viewModel.agregarParte();
        });
    }

    private void agregarParteView() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View parteView = inflater.inflate(R.layout.item_parte_editable, containerPartes, false);

        ImageButton btnRemover = parteView.findViewById(R.id.btnRemoverParte);
        btnRemover.setOnClickListener(v -> {
            containerPartes.removeView(parteView);
            viewModel.removerParte();
            Toast.makeText(getContext(), "Parte removida", Toast.LENGTH_SHORT).show();
        });

        containerPartes.addView(parteView);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    viewModel.setFechaIngreso(year, month, dayOfMonth);
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }
}
