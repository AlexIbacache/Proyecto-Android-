package com.example.proyectoandroid.ui.main;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.ui.maquinaria.MaquinariaFragment;
import com.example.proyectoandroid.ui.profile.ProfileFragment;
import com.example.proyectoandroid.ui.reparacion.ReparacionFragment;
import com.example.proyectoandroid.ui.reportes.ReportesFragment;
import com.example.proyectoandroid.util.AdminHelper;
import com.example.proyectoandroid.util.UserActionLogger;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Verificar rol del usuario actual
        checkUserRole();

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Verificar si es admin para mostrar interfaz exclusiva
        AdminHelper.isCurrentUserAdmin(isAdmin -> {
            if (isAdmin) {
                // ADMIN: Mostrar solo AdminFragment y ocultar navegación
                Log.d("MainActivity", "Usuario ADMIN - Mostrando panel exclusivo");
                bottomNavigationView.setVisibility(View.GONE);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new com.example.proyectoandroid.ui.admin.AdminFragment())
                        .commit();
            } else {
                // USUARIO NORMAL: Mostrar navegación normal
                Log.d("MainActivity", "Usuario NORMAL - Mostrando navegación estándar");
                bottomNavigationView.setVisibility(View.VISIBLE);
                setupNormalNavigation(bottomNavigationView, savedInstanceState);
            }
        });
    }

    /**
     * Configura la navegación normal para usuarios no-admin.
     */
    private void setupNormalNavigation(BottomNavigationView bottomNavigationView, Bundle savedInstanceState) {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            String screenName = "";
            int itemId = item.getItemId();

            if (itemId == R.id.nav_perfil) {
                selectedFragment = new ProfileFragment();
                screenName = "Perfil";
            } else if (itemId == R.id.nav_maquinaria) {
                selectedFragment = new MaquinariaFragment();
                screenName = "Maquinaria";
            } else if (itemId == R.id.nav_reparacion) {
                selectedFragment = new ReparacionFragment();
                screenName = "Reparación";
            } else if (itemId == R.id.nav_reportes) {
                selectedFragment = new ReportesFragment();
                screenName = "Reportes";
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment)
                        .commit();
                UserActionLogger.logNavigate(screenName);
            }
            return true;
        });

        // Cargar el fragmento de perfil por defecto al iniciar la actividad
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.nav_perfil);
        }
    }

    /**
     * Verifica y muestra el rol del usuario actual en Logcat.
     */
    private void checkUserRole() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            AdminHelper.isCurrentUserAdmin(isAdmin -> {
                String role = isAdmin ? "ADMINISTRADOR" : "USUARIO NORMAL";
                Log.d("ROLE_CHECK", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
                Log.d("ROLE_CHECK", "Usuario: " + currentUser.getEmail());
                Log.d("ROLE_CHECK", "Rol: " + role);
                Log.d("ROLE_CHECK", "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            });
        }
    }
}
