package com.example.proyectoandroid.ui.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.ui.maquinaria.MaquinariaFragment;
import com.example.proyectoandroid.ui.profile.ProfileFragment;
import com.example.proyectoandroid.ui.reparacion.ReparacionFragment;
import com.example.proyectoandroid.ui.reportes.ReportesFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_perfil) {
                selectedFragment = new ProfileFragment();
            } else if (itemId == R.id.nav_maquinaria) {
                selectedFragment = new MaquinariaFragment();
            } else if (itemId == R.id.nav_reparacion) {
                selectedFragment = new ReparacionFragment();
            } else if (itemId == R.id.nav_reportes) {
                selectedFragment = new ReportesFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return true;
        });

        // Cargar el fragmento de perfil por defecto al iniciar la actividad
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_perfil);
        }
    }
}
