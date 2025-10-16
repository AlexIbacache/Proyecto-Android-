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

public class MaquinariaListActivity extends AppCompatActivity {
    private Button btnAgregarM, btnVolver, btnModificarM;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maquinaria_list);
        btnVolver = findViewById(R.id.btnVolverLM);
        btnAgregarM = findViewById(R.id.btnAgregarMaquinaria);
        btnModificarM = findViewById(R.id.btnModificarM);
        btnAgregarM.setOnClickListener( v -> {Intent intent = new Intent(MaquinariaListActivity.this, MaquinariaFormActivity.class);
        startActivity(intent);
        finish();
        });
        btnVolver.setOnClickListener( v -> {Intent intent = new Intent(MaquinariaListActivity.this, MenuActivity.class);
            startActivity(intent);
            finish();
        });
        btnModificarM.setOnClickListener(v -> {
            Toast.makeText(MaquinariaListActivity.this, "Función próximamente...", Toast.LENGTH_SHORT).show();
        });
    }
}