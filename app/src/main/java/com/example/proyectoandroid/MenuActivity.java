package com.example.proyectoandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MenuActivity extends AppCompatActivity {
    private static final String TAG = "MenuActivity";
    private TextView tvUbicacion;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView tvEmail;
    private FirebaseAuth mAuth;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    obtenerUbicacion();
                } else {
                    Toast.makeText(this, "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();

        Button btnCerrarS = findViewById(R.id.btnCerrarS);
        tvUbicacion = findViewById(R.id.tvUbicacion);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        tvEmail = findViewById(R.id.tvEmail);

        cargarDatosUsuario();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        btnCerrarS.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MenuActivity.this, LoginActivity.class));
            finish();
        });

        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_maquinaria) {
                startActivity(new Intent(MenuActivity.this, MaquinariaListActivity.class));
                return true;
            } else if (itemId == R.id.nav_reparacion) {
                startActivity(new Intent(MenuActivity.this, ReparacionParte.class));
                return true;
            } else if (itemId == R.id.nav_reportes) {
                startActivity(new Intent(MenuActivity.this, VerReportes.class));
                return true;
            }
            return false;
        });

        verificarPermisoYObtenerUbicacion();
    }

    private void cargarDatosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvEmail.setText(user.getEmail());
        }
    }

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
            if (addresses != null && !addresses.isEmpty()) {
                Address a = addresses.get(0);
                String nombre = a.getSubLocality();
                if (nombre == null) nombre = a.getLocality();
                if (nombre == null) nombre = a.getSubAdminArea();
                if (nombre == null) nombre = a.getAdminArea();
                tvUbicacion.setText("Ubicación: " + nombre);
            } else {
                tvUbicacion.setText("Error al obtener ubicación");
            }
        } catch (IOException e) {
            tvUbicacion.setText("Error al obtener ubicación");
        }
    }
}
