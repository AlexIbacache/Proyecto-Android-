package com.example.proyectoandroid;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class MenuActivity extends AppCompatActivity {
    private Button btnM, btnR, btnVR, btnCerrarS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu);
        btnM = findViewById(R.id.btnMaquinaria);
        btnCerrarS = findViewById(R.id.btnCerrarS);
        btnR = findViewById(R.id.btnReparacion);
        btnVR = findViewById(R.id.btnReporte);
        btnM.setOnClickListener( v ->  {
            Intent intent = new Intent(MenuActivity.this, MaquinariaListActivity.class);
            startActivity(intent);
            finish();
        });
        btnR.setOnClickListener( v ->  {
            Intent intent = new Intent(MenuActivity.this, ReparacionParte.class);
            startActivity(intent);
            finish();
        });
        btnVR.setOnClickListener( v ->  {
            Intent intent = new Intent(MenuActivity.this, VerReportes.class);
            startActivity(intent);
            finish();
        });
        btnCerrarS.setOnClickListener( v ->  {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

    }
}