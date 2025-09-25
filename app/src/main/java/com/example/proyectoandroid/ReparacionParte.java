package com.example.proyectoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ReparacionParte extends AppCompatActivity {
    private ImageButton btnMotor, btnBomba, btnBrazo;
    private Button btnVolverFM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reparacion_parte);
        Spinner spinnerMaquinaria = findViewById(R.id.spinnerMaquinaria);
        String[] maquinariaArray = {"Excavadora hidráulica", "Perforadora jumbo", "Camión", "Bulldozer (Topadora)"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, maquinariaArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMaquinaria.setAdapter(adapter);
        btnMotor = findViewById(R.id.btnMotor);
        btnBrazo = findViewById(R.id.btnBrazo);
        btnBomba = findViewById(R.id.btnBomba);
        btnVolverFM = findViewById(R.id.btnVolverFM);
        btnVolverFM.setOnClickListener(view -> {
            Intent intent = new Intent(ReparacionParte.this, MenuActivity.class);
            startActivity(intent);
        });
        btnMotor.setOnClickListener(view -> {
            Intent intent = new Intent(ReparacionParte.this, ReparacionRepuesto.class);
            startActivity(intent);
        });
        btnBrazo.setOnClickListener(view -> {
            Intent intent = new Intent(ReparacionParte.this, ReparacionRepuesto.class);
            startActivity(intent);
        });
        btnBomba.setOnClickListener(view -> {
            Intent intent = new Intent(ReparacionParte.this, ReparacionRepuesto.class);
            startActivity(intent);
        });

        Button guardar = findViewById(R.id.btnGuardarParte);
        guardar.setOnClickListener(view -> {
            Intent intent = new Intent(ReparacionParte.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });

    }
}