package com.example.proyectoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ReparacionRepuesto extends AppCompatActivity {
    private FloatingActionButton btnVolverMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reparacion_repuesto);
        FloatingActionButton fabAgregarRepuesto = findViewById(R.id.fabAgregarRepuesto);
        fabAgregarRepuesto.setOnClickListener(v -> {
            AgregarRepuestoDialog dialog = new AgregarRepuestoDialog();
            dialog.show(getSupportFragmentManager(), "AgregarRepuestoDialog");
        });
        btnVolverMenu = findViewById(R.id.btnVolverMenu);
        btnVolverMenu.setOnClickListener(view -> {
            Intent intent = new Intent(ReparacionRepuesto.this, ReparacionParte.class);
            startActivity(intent);
            finish();
        });
        Button guardar = findViewById(R.id.btnGuardarReparacion);
        guardar.setOnClickListener(view -> {
            Intent intent = new Intent(ReparacionRepuesto.this, ReparacionParte.class);
            startActivity(intent);
            finish();
        });

    }
}