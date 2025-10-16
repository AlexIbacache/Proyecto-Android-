package com.example.proyectoandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MenuActivity extends AppCompatActivity {
    private Button btnM, btnR, btnVR, btnCerrarS;
    private TextView tvUbicacion;
    private FusedLocationProviderClient fusedLocationClient;
    //permisos
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    obtenerUbicacion();
                } else {
                    Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnM = findViewById(R.id.btnMaquinaria);
        btnCerrarS = findViewById(R.id.btnCerrarS);
        btnR = findViewById(R.id.btnReparacion);
        btnVR = findViewById(R.id.btnReporte);
        tvUbicacion = findViewById(R.id.tvUbicacion);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnM.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, MaquinariaListActivity.class));
            finish();
        });
        btnR.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, ReparacionParte.class));
            finish();
        });
        btnVR.setOnClickListener(v -> {
            startActivity(new Intent(MenuActivity.this, VerReportes.class));
            finish();
        });
        btnCerrarS.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MenuActivity.this, LoginActivity.class));
            finish();
        });

        verificarPermisoYObtenerUbicacion();
    }
    //Funciones para obtener ubicacion y permisos
    private void verificarPermisoYObtenerUbicacion() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }
    @SuppressLint("MissingPermission")
    private void obtenerUbicacion() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                obtenerNombreUbicacion(location);
            } else {
                tvUbicacion.setText("Ubicación no disponible");
            }
        });
    }
    private void obtenerNombreUbicacion(Location location) {
        Geocoder geocoder = new Geocoder(this, new Locale("es", "CL"));
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (!addresses.isEmpty()) {
                Address a = addresses.get(0);
                String nombre = a.getSubLocality(); // más específico (barrios)
                if (nombre == null) nombre = a.getLocality(); // ciudad
                if (nombre == null) nombre = a.getSubAdminArea();
                if (nombre == null) nombre = a.getAdminArea();
                tvUbicacion.setText("Ubicación: " + nombre);
            }
        } catch (IOException e) {
            tvUbicacion.setText("Error al obtener ubicación");
        }
    }

}
