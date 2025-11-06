package com.example.proyectoandroid.ui.maquinaria;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.Maquinaria;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MaquinariaFormFragment extends Fragment {

    private MaquinariaFormViewModel viewModel;
    private TextInputEditText etFechaIngreso, etNombreMaquinaria, etNumeroIdentificador, etDescripcion;
    private LinearLayout containerPartes;
    private String maquinariaId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            maquinariaId = getArguments().getString("maquinariaId");
        }
        return inflater.inflate(R.layout.fragment_maquinaria_form, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        setupViewModel();
        setupObservers();
        setupClickListeners(view);

        if (maquinariaId != null) {
            viewModel.cargarMaquinaria(maquinariaId);
        }
    }

    private void setupViews(View view) {
        etNombreMaquinaria = view.findViewById(R.id.etNombreMaquinaria);
        etNumeroIdentificador = view.findViewById(R.id.etNumeroIdentificador);
        etFechaIngreso = view.findViewById(R.id.etFechaIngreso);
        etDescripcion = view.findViewById(R.id.etDescripcion);
        containerPartes = view.findViewById(R.id.containerPartes);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(MaquinariaFormViewModel.class);
    }

    private void setupObservers() {
        viewModel.maquinariaCargada.observe(getViewLifecycleOwner(), maquinaria -> {
            if (maquinaria != null) {
                etNombreMaquinaria.setText(maquinaria.getNombre());
                etNumeroIdentificador.setText(maquinaria.getNumeroIdentificador());
                etDescripcion.setText(maquinaria.getDescripcion());

                containerPartes.removeAllViews();
                if (maquinaria.getPartesPrincipales() != null) {
                    for (String parte : maquinaria.getPartesPrincipales()) {
                        agregarParteView(parte);
                    }
                }
            }
        });

        viewModel.fechaIngreso.observe(getViewLifecycleOwner(), fecha -> etFechaIngreso.setText(fecha));
        viewModel.getAddParteViewEvent().observe(getViewLifecycleOwner(), aVoid -> agregarParteView(null));
        viewModel.getShowMaxPartesToastEvent().observe(getViewLifecycleOwner(), aVoid -> {
            if (isAdded()) {
                Toast.makeText(getContext(), "Máximo de partes alcanzado", Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getSaveMaquinariaEvent().observe(getViewLifecycleOwner(), success -> {
            if (isAdded()) {
                if (success) {
                    Toast.makeText(getContext(), "Maquinaria guardada con éxito", Toast.LENGTH_SHORT).show();
                    getParentFragmentManager().popBackStack();
                } else {
                    Toast.makeText(getContext(), "Error al guardar. Revisa los datos.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupClickListeners(View view) {
        etFechaIngreso.setOnClickListener(v -> showDatePickerDialog());

        Button btnGuardarMaquinaria = view.findViewById(R.id.btnGuardarMaquinaria);
        btnGuardarMaquinaria.setOnClickListener(v -> {
            String nombre = etNombreMaquinaria.getText().toString();
            String numeroId = etNumeroIdentificador.getText().toString();
            String descripcion = etDescripcion.getText().toString();
            List<String> partes = new ArrayList<>();
            for (int i = 0; i < containerPartes.getChildCount(); i++) {
                View parteView = containerPartes.getChildAt(i);
                EditText etNombreParte = parteView.findViewById(R.id.etNombreParte);
                partes.add(etNombreParte.getText().toString());
            }
            viewModel.guardarMaquinaria(nombre, numeroId, descripcion, partes);
        });

        FloatingActionButton fabAgregarParte = view.findViewById(R.id.fabAgregarParte);
        fabAgregarParte.setOnClickListener(v -> viewModel.agregarParte());
    }

    private void agregarParteView(@Nullable String nombreParte) {
        if (isAdded()) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View parteView = inflater.inflate(R.layout.item_parte_editable, containerPartes, false);
            
            EditText etNombreParte = parteView.findViewById(R.id.etNombreParte);
            if (nombreParte != null) {
                etNombreParte.setText(nombreParte);
            }

            ImageButton btnRemover = parteView.findViewById(R.id.btnRemoverParte);
            btnRemover.setOnClickListener(v -> {
                containerPartes.removeView(parteView);
                viewModel.removerParte();
                if(isAdded()){
                    Toast.makeText(getContext(), "Parte removida", Toast.LENGTH_SHORT).show();
                }
            });

            containerPartes.addView(parteView);
        }
    }

    private void showDatePickerDialog() {
        if (isAdded()) {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    requireContext(),
                    (v, year, month, dayOfMonth) -> viewModel.setFechaIngreso(year, month, dayOfMonth),
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            );
            datePickerDialog.show();
        }
    }
}
