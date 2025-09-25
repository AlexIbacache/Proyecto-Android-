package com.example.proyectoandroid;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ConfirmacionCorreo extends AppCompatActivity {
    private Button siguiente;
    private TextView volver, iniciarSesion;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirmacion_correo);
        siguiente = findViewById(R.id.btnSiguienteCC);
        volver = findViewById(R.id.tvVolver);
        iniciarSesion = findViewById(R.id.tvIniciarSesioncc);
        siguiente.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmacionCorreo.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        volver.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmacionCorreo.this, RegistrarFormActivity.class);
            startActivity(intent);
            finish();
        });
        iniciarSesion.setOnClickListener(v -> {
            Intent intent = new Intent(ConfirmacionCorreo.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }
}