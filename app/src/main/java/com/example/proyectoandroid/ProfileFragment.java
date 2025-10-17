package com.example.proyectoandroid;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private TextView tvUbicacion;
    private TextView tvEmail;
    private FusedLocationProviderClient fusedLocationClient;
    private FirebaseAuth mAuth;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    obtenerUbicacion();
                } else {
                    Toast.makeText(getContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();

        // Inicializar vistas con los nuevos IDs dentro de las tarjetas
        tvEmail = view.findViewById(R.id.tvEmail);
        tvUbicacion = view.findViewById(R.id.tvUbicacion);
        Button btnCerrarS = view.findViewById(R.id.btnCerrarS);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        // Cargar datos del usuario
        cargarDatosUsuario();

        // Obtener ubicación
        verificarPermisoYObtenerUbicacion();

        // Configurar botón de cerrar sesión
        btnCerrarS.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        return view;
    }

    private void cargarDatosUsuario() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            tvEmail.setText(user.getEmail());
        }
    }

    private void verificarPermisoYObtenerUbicacion() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            obtenerUbicacion();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    private void obtenerUbicacion() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                obtenerNombreUbicacion(location);
            } else {
                tvUbicacion.setText("Ubicación no disponible");
            }
        });
    }

    private void obtenerNombreUbicacion(Location location) {
        Geocoder geocoder = new Geocoder(requireContext(), new Locale("es", "CL"));
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address a = addresses.get(0);
                String nombre = a.getSubLocality();
                if (nombre == null) nombre = a.getLocality();
                if (nombre == null) nombre = a.getSubAdminArea();
                if (nombre == null) nombre = a.getAdminArea();
                tvUbicacion.setText(nombre != null ? nombre : "Nombre de ubicación no encontrado");
            } else {
                tvUbicacion.setText("Dirección no encontrada");
            }
        } catch (IOException e) {
            tvUbicacion.setText("Error de geocodificación");
        }
    }
}
