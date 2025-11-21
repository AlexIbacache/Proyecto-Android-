package com.example.proyectoandroid.ui.repuesto;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.adapters.RepuestoAdapter;
import com.example.proyectoandroid.model.Repuesto;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RepuestoActivity extends AppCompatActivity implements RepuestoAdapter.OnItemDeleteListener {

    public static final String EXTRA_REPUESTOS_LISTA = "com.example.proyectoandroid.EXTRA_REPUESTOS_LISTA";
    public static final String EXTRA_PARTE_NOMBRE = "com.example.proyectoandroid.EXTRA_PARTE_NOMBRE";

    private TextInputEditText etNombreRepuesto, etCodigoRepuesto, etCantidadRepuesto;
    private MaterialButton btnAnadir;
    private MaterialButton btnGuardarRepuestos;
    private RecyclerView recyclerViewRepuestos;
    private TextView tvTituloRepuesto;
    private RepuestoAdapter adapter;
    private List<Repuesto> repuestosList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repuesto);

        // Inicialización de vistas
        etNombreRepuesto = findViewById(R.id.etNombreRepuesto);
        etCodigoRepuesto = findViewById(R.id.etCodigoRepuesto);
        etCantidadRepuesto = findViewById(R.id.etCantidadRepuesto);
        btnAnadir = findViewById(R.id.btnAnadir);
        btnGuardarRepuestos = findViewById(R.id.btnGuardarRepuestos);
        recyclerViewRepuestos = findViewById(R.id.recyclerViewRepuestos);
        tvTituloRepuesto = findViewById(R.id.tvTituloRepuesto);

        // Cargar datos del Intent
        if (getIntent().hasExtra(EXTRA_REPUESTOS_LISTA)) {
            List<Repuesto> existentes = (List<Repuesto>) getIntent().getSerializableExtra(EXTRA_REPUESTOS_LISTA);
            if (existentes != null) {
                repuestosList.addAll(existentes);
            }
        }

        String parteNombre = getIntent().getStringExtra(EXTRA_PARTE_NOMBRE);
        if (parteNombre != null && !parteNombre.isEmpty()) {
            tvTituloRepuesto.setText("Repuestos para: " + parteNombre);
        }

        setupRecyclerView();

        // Listener para el botón de añadir
        btnAnadir.setOnClickListener(v -> anadirRepuestoALista());

        // Listener para el botón de guardar
        btnGuardarRepuestos.setOnClickListener(v -> guardarYVolver());
    }

    private void setupRecyclerView() {
        recyclerViewRepuestos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RepuestoAdapter(repuestosList, this);
        recyclerViewRepuestos.setAdapter(adapter);
    }

    private void anadirRepuestoALista() {
        String nombre = etNombreRepuesto.getText().toString().trim();
        String codigo = etCodigoRepuesto.getText().toString().trim();
        String cantidadStr = etCantidadRepuesto.getText().toString().trim();

        if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(codigo) || TextUtils.isEmpty(cantidadStr)) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                Toast.makeText(this, "La cantidad debe ser mayor que cero", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "La cantidad no es un número válido", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear y añadir el repuesto a la lista
        Repuesto nuevoRepuesto = new Repuesto(nombre, codigo, cantidad);
        repuestosList.add(nuevoRepuesto);
        adapter.notifyItemInserted(repuestosList.size() - 1);

        // Limpiar los campos del formulario
        etNombreRepuesto.setText("");
        etCodigoRepuesto.setText("");
        etCantidadRepuesto.setText("");
        etNombreRepuesto.requestFocus(); // Poner el foco de nuevo en el nombre
    }

    @Override
    public void onDeleteClick(int position) {
        if (position >= 0 && position < repuestosList.size()) {
            repuestosList.remove(position);
            adapter.notifyItemRemoved(position);
        }
    }

    private void guardarYVolver() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(EXTRA_REPUESTOS_LISTA, (Serializable) repuestosList);
        // Devolver el nombre de la parte también es una buena práctica
        returnIntent.putExtra(EXTRA_PARTE_NOMBRE, getIntent().getStringExtra(EXTRA_PARTE_NOMBRE));
        setResult(Activity.RESULT_OK, returnIntent);
        finish(); // Cerrar esta actividad y volver a la anterior
    }
}
