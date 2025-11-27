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
    private TextView tvNombre;
    private ProfileViewModel profileViewModel;
    private FusedLocationProviderClient fusedLocationClient;

    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    // Correcto: El fragmento se encarga de llamar a su propio método de UI/HW
                    getDeviceLocation();
                } else {
                    Toast.makeText(getContext(), "Permiso de ubicación denegado", Toast.LENGTH_SHORT).show();
                }
            });

    // Stats Views
    private TextView tvCountMaquinaria, tvCountReparacionesAbiertas, tvCountReparacionesCerradas, tvCountReportes;
    private android.widget.ProgressBar progressMaquinaria, progressReparacionesAbiertas, progressReparacionesCerradas,
            progressReportes;
    private com.example.proyectoandroid.data.LoggerRepository loggerRepository;
    private com.example.proyectoandroid.data.MaquinariaRepository maquinariaRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        loggerRepository = new com.example.proyectoandroid.data.LoggerRepository();
        maquinariaRepository = new com.example.proyectoandroid.data.MaquinariaRepository();

        // Initialize Views
        tvNombre = view.findViewById(R.id.tvNombre);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvUbicacion = view.findViewById(R.id.tvUbicacion);
        Button btnCerrarS = view.findViewById(R.id.btnCerrarS);

        // Initialize Stats Views
        tvCountMaquinaria = view.findViewById(R.id.tvCountMaquinaria);
        progressMaquinaria = view.findViewById(R.id.progressMaquinaria);

        tvCountReparacionesAbiertas = view.findViewById(R.id.tvCountReparacionesAbiertas);
        progressReparacionesAbiertas = view.findViewById(R.id.progressReparacionesAbiertas);

        tvCountReparacionesCerradas = view.findViewById(R.id.tvCountReparacionesCerradas);
        progressReparacionesCerradas = view.findViewById(R.id.progressReparacionesCerradas);

        tvCountReportes = view.findViewById(R.id.tvCountReportes);
        progressReportes = view.findViewById(R.id.progressReportes);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());

        profileViewModel.getUserName().observe(getViewLifecycleOwner(), name -> {
            tvNombre.setText(name != null && !name.isEmpty() ? name : "Nombre no disponible");
        });

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
        loadUserStats();

        return view;
    }

    private void loadUserStats() {
        com.google.firebase.auth.FirebaseUser user = com.google.firebase.auth.FirebaseAuth.getInstance()
                .getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Cargar estadísticas de Maquinaria (Logs)
            loggerRepository.getUserEntityCount(userId,
                    com.example.proyectoandroid.util.UserActionLogger.ENTITY_MAQUINARIA, count -> {
                        if (tvCountMaquinaria != null) {
                            animateProgress(progressMaquinaria, tvCountMaquinaria, count, 50);
                        }
                    });

            // Cargar estadísticas de Reparaciones (Datos Reales: Abiertas vs Cerradas)
            maquinariaRepository.getReparacionStats(userId, (abiertas, cerradas) -> {
                if (tvCountReparacionesAbiertas != null) {
                    animateProgress(progressReparacionesAbiertas, tvCountReparacionesAbiertas, abiertas, 20);
                }
                if (tvCountReparacionesCerradas != null) {
                    animateProgress(progressReparacionesCerradas, tvCountReparacionesCerradas, cerradas, 50);
                }
            });

            // Cargar estadísticas de Reportes (Logs)
            loggerRepository.getUserEntityCount(userId,
                    com.example.proyectoandroid.util.UserActionLogger.ENTITY_REPORTE, count -> {
                        if (tvCountReportes != null) {
                            animateProgress(progressReportes, tvCountReportes, count, 50);
                        }
                    });
        }
    }

    private void animateProgress(android.widget.ProgressBar progressBar, TextView textView, int value, int max) {
        if (progressBar == null || textView == null)
            return;

        progressBar.setMax(max);

        // Animar ProgressBar
        android.animation.ObjectAnimator animation = android.animation.ObjectAnimator.ofInt(progressBar, "progress", 0,
                value);
        animation.setDuration(1000); // 1 segundo
        animation.setInterpolator(new android.view.animation.DecelerateInterpolator());
        animation.start();

        // Animar TextView (Contador)
        android.animation.ValueAnimator animator = android.animation.ValueAnimator.ofInt(0, value);
        animator.setDuration(1000);
        animator.addUpdateListener(animation1 -> textView.setText(animation1.getAnimatedValue().toString()));
        animator.start();
    }

    private void verificarPermisoYObtenerUbicacion() {
        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
