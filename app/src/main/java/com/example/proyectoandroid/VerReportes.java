package com.example.proyectoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class VerReportes extends AppCompatActivity {
    private Button btnVolverM;
    private Button btnExportarExcel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_ver_reportes);
        btnVolverM = findViewById(R.id.btnVolverLM);
        btnExportarExcel = findViewById(R.id.btnExportarExcel);

        btnVolverM.setOnClickListener(v -> {
            Intent intent = new Intent(VerReportes.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });

        // --- Promesa para el botón Exportar a Excel ---
        btnExportarExcel.setOnClickListener(v -> {
            Toast.makeText(VerReportes.this, "Función próximamente...", Toast.LENGTH_SHORT).show();
        });
    }
}
