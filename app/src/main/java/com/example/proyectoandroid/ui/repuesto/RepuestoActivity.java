package com.example.proyectoandroid.ui.repuesto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.Repuesto;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RepuestoActivity extends AppCompatActivity {

    public static final String EXTRA_PARTE_NOMBRE = "com.example.proyectoandroid.EXTRA_PARTE_NOMBRE";
    public static final String EXTRA_REPUESTOS_LISTA = "com.example.proyectoandroid.EXTRA_REPUESTOS_LISTA";

    private RepuestoViewModel viewModel;
    private RepuestoAdapter adapter;
    private TextView tvTituloRepuesto;
    private TextInputEditText etNombreRepuesto, etCodigoRepuesto, etCantidadRepuesto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repuesto);

        viewModel = new ViewModelProvider(this).get(RepuestoViewModel.class);

        // Obtener datos del Intent
        String parteNombre = getIntent().getStringExtra(EXTRA_PARTE_NOMBRE);
        List<Repuesto> repuestosExistentes = (List<Repuesto>) getIntent().getSerializableExtra(EXTRA_REPUESTOS_LISTA);

        if (parteNombre == null || parteNombre.isEmpty()) {
            Toast.makeText(this, "Error: No se especificó la parte.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        
        // Inicializar el ViewModel con los datos recibidos
        viewModel.setParteNombre(parteNombre);
        if (repuestosExistentes != null) {
            viewModel.setInitialRepuestos(repuestosExistentes);
        }

        // Inicializar Vistas
        tvTituloRepuesto = findViewById(R.id.tvTituloRepuesto);
        etNombreRepuesto = findViewById(R.id.etNombreRepuesto);
        etCodigoRepuesto = findViewById(R.id.etCodigoRepuesto);
        etCantidadRepuesto = findViewById(R.id.etCantidadRepuesto);
        RecyclerView recyclerView = findViewById(R.id.recyclerViewRepuestos);
        MaterialButton btnAnadir = findViewById(R.id.btnAnadir);
        MaterialButton btnGuardar = findViewById(R.id.btnGuardarRepuestos);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RepuestoAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        setupListeners(btnAnadir, btnGuardar);
        setupObservers();
    }

    private void setupListeners(MaterialButton btnAnadir, MaterialButton btnGuardar) {
        adapter.setOnItemClickListener(repuesto -> viewModel.removeRepuesto(repuesto));

        btnAnadir.setOnClickListener(v -> {
            String nombre = etNombreRepuesto.getText().toString();
            String codigo = etCodigoRepuesto.getText().toString();
            String cantidad = etCantidadRepuesto.getText().toString();

            viewModel.addRepuesto(nombre, codigo, cantidad);
            clearInputFields();
        });

        btnGuardar.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(EXTRA_REPUESTOS_LISTA, (Serializable) viewModel.getRepuestos().getValue());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
    }

    private void setupObservers() {
        viewModel.getParteNombre().observe(this, nombre -> {
            tvTituloRepuesto.setText(String.format(Locale.getDefault(), "Repuestos para: %s", nombre));
        });

        viewModel.getRepuestos().observe(this, repuestos -> {
            adapter.updateData(repuestos);
        });
    }

    private void clearInputFields() {
        etNombreRepuesto.setText("");
        etCodigoRepuesto.setText("");
        etCantidadRepuesto.setText("");
        etNombreRepuesto.requestFocus();
    }
}
