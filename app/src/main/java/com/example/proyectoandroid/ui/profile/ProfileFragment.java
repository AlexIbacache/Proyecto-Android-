package com.example.proyectoandroid.ui.profile;

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
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.ui.login.LoginActivity;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProfileFragment extends Fragment {

    private TextView tvUbicacion;
    private TextView tvEmail;
    private ProfileViewModel profileViewModel;
    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Correcto: El fragmento se encarga de llamar a su propio método de UI/HW
                    getDeviceLocation();
                } else {
                    Toast.makeText(getContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        tvEmail = view.findViewById(R.id.tvEmail);
        tvUbicacion = view.findViewById(R.id.tvUbicacion);
        Button btnCerrarS = view.findViewById(R.id.btnCerrarS);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        profileViewModel.getUserEmail().observe(getViewLifecycleOwner(), email -> {
            tvEmail.setText(email);
        });

        profileViewModel.getLocation().observe(getViewLifecycleOwner(), locationName -> {
            tvUbicacion.setText(locationName);
        });

        profileViewModel.getLogoutEvent().observe(getViewLifecycleOwner(), aVoid -> {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            requireActivity().finish();
        });

        btnCerrarS.setOnClickListener(v -> {
            profileViewModel.logout();
        });

        // Iniciar la carga de datos
        profileViewModel.loadUserData();
        verificarPermisoYObtenerUbicacion();

        return view;
    }

    private void verificarPermisoYObtenerUbicacion() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Correcto: El fragmento se encarga de llamar a su propio método de UI/HW
            getDeviceLocation();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    public void getDeviceLocation() {
        fusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity(), location -> {
            if (location != null) {
                profileViewModel.onLocationReceived(location);
            } else {
                profileViewModel.onLocationFailed("Ubicación no disponible");
            }
        });
    }
}
