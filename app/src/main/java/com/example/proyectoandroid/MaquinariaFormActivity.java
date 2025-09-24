package com.example.proyectoandroid;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MaquinariaFormActivity extends AppCompatActivity {
    private EditText etFechaIngreso;
    private LinearLayout containerPartes;
    private int contadorPartes = 0;
    private static final int MAX_PARTES = 10;  // Límite opcional para partes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maquinaria_form);

        etFechaIngreso = findViewById(R.id.etFechaIngreso);
        containerPartes = findViewById(R.id.containerPartes);
        FloatingActionButton fabAgregarParte = findViewById(R.id.fabAgregarParte);
        FloatingActionButton btnVolverFM = findViewById(R.id.btnVolverFM);
        btnVolverFM.setOnClickListener(v ->{
            Intent intent = new Intent(MaquinariaFormActivity.this, MaquinariaListActivity.class);
            startActivity(intent);
            finish();
        });
        // Configurar DatePicker
        etFechaIngreso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Fecha actual
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                // DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        MaquinariaFormActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                // Formatear la fecha como dd/mm/yyyy
                                String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d",
                                        selectedDay, selectedMonth + 1, selectedYear);
                                etFechaIngreso.setText(selectedDate);
                            }
                        },
                        year, month, day
                );
                datePickerDialog.show();
            }
        });

        // Configurar OnClickListener para el FAB (agregar partes)
        fabAgregarParte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (contadorPartes >= MAX_PARTES) {
                    Toast.makeText(MaquinariaFormActivity.this, "Máximo de partes alcanzado (" + MAX_PARTES + ")", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Crear un nuevo LinearLayout horizontal para cada parte (EditText + botón remover)
                LinearLayout layoutParte = new LinearLayout(MaquinariaFormActivity.this);
                layoutParte.setOrientation(LinearLayout.HORIZONTAL);
                layoutParte.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                layoutParte.setPadding(0, 8, 0, 8);

                EditText etParte = new EditText(MaquinariaFormActivity.this);
                etParte.setId(View.generateViewId());
                etParte.setLayoutParams(new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                etParte.setHint("Nombre de la parte");
                etParte.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
                etParte.setPadding(16, 12, 16, 12);

                ImageButton btnRemover = new ImageButton(MaquinariaFormActivity.this);
                btnRemover.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
                btnRemover.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                btnRemover.setPadding(8, 8, 8, 8);
                btnRemover.setBackground(null);

                // OnClick para remover esta parte
                final int posicion = containerPartes.getChildCount();
                btnRemover.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        containerPartes.removeViewAt(posicion);
                        contadorPartes--;
                        Toast.makeText(MaquinariaFormActivity.this, "Parte removida", Toast.LENGTH_SHORT).show();
                    }
                });

                // Agregar EditText y botón al layout de la parte
                layoutParte.addView(etParte);
                layoutParte.addView(btnRemover);

                // Agregar el layout de la parte al contenedor
                containerPartes.addView(layoutParte);
                contadorPartes++;

                Toast.makeText(MaquinariaFormActivity.this, "Parte agregada", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Método para validar fecha (llámalo en el botón Guardar)
    private boolean validarFecha() {
        String fechaTexto = etFechaIngreso.getText().toString().trim();
        if (fechaTexto.isEmpty()) {
            Toast.makeText(this, "La fecha es obligatoria", Toast.LENGTH_SHORT).show();
            return false;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        sdf.setLenient(false);
        try {
            sdf.parse(fechaTexto);
            return true;
        } catch (ParseException e) {
            Toast.makeText(this, "Fecha inválida. Use dd/mm/yyyy", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // Método opcional para obtener todas las partes al guardar
    private void guardarPartes() {
        for (int i = 0; i < containerPartes.getChildCount(); i++) {
            View child = containerPartes.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout parteLayout = (LinearLayout) child;
                EditText et = (EditText) parteLayout.getChildAt(0);  // Primer hijo es el EditText
                String nombreParte = et.getText().toString().trim();
                if (!nombreParte.isEmpty()) {
                }
            }
        }
    }

    // Ejemplo de método para el botón Guardar (agrega esto en onCreate si se tiene el botón)
    private void configurarBotonGuardar() {
        // findViewById(R.id.btnGuardarMaquinaria).setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         if (validarFecha()) {
        //             guardarPartes();
        //             // Procede a guardar toda la maquinaria...
        //             Toast.makeText(MaquinariaFormActivity.this, "Maquinaria guardada", Toast.LENGTH_SHORT).show();
        //         }
        //     }
        // });
    }
}