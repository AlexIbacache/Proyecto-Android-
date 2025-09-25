package com.example.proyectoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class RegistrarFormActivity extends AppCompatActivity {
    private Button btnRegistrarU;
    private TextView tvIniciarSesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar_form);
        tvIniciarSesion = findViewById(R.id.tvIniciarSesion);
        btnRegistrarU = findViewById(R.id.btnRegistrarU);
        btnRegistrarU.setOnClickListener( v ->{
            Intent intent = new Intent(RegistrarFormActivity.this, ConfirmacionCorreo.class);
            startActivity(intent);
            finish();    });

        tvIniciarSesion.setOnClickListener( v ->{
            Intent intent = new Intent(RegistrarFormActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();    });
    }
}