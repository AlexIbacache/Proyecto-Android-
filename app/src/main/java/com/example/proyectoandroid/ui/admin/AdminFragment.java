package com.example.proyectoandroid.ui.admin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.data.LoggerRepository;
import com.example.proyectoandroid.ui.login.LoginActivity;
import com.example.proyectoandroid.util.AdminHelper;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Fragment exclusivo para administradores.
 * Muestra estadísticas y opciones de mantenimiento de logs.
 */
public class AdminFragment extends Fragment {

    private static final String TAG = "AdminFragment";

    // Views
    private TextView tvTotalLogs, tvOldestLog;
    private Button btnRefreshStats, btnDelete30Days, btnDelete60Days, btnDelete90Days, btnDeleteAll, btnLogout;

    // Repository
    private LoggerRepository loggerRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Verificar permisos de admin
        verifyAdminPermissions();

        // Inicializar vistas
        initializeViews(view);

        // Inicializar repository
        loggerRepository = new LoggerRepository();

        // Cargar estadísticas
        loadStatistics();

        // Configurar listeners
        setupListeners();
    }

    /**
     * Verifica que el usuario tenga permisos de administrador.
     */
    private void verifyAdminPermissions() {
        AdminHelper.isCurrentUserAdmin(isAdmin -> {
            if (!isAdmin) {
                Log.w(TAG, "Usuario sin permisos de admin intentó acceder al panel");
                Toast.makeText(getContext(), "Acceso denegado", Toast.LENGTH_SHORT).show();
                // Redirigir a login o cerrar sesión
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LoginActivity.class));
                if (getActivity() != null) {
                    getActivity().finish();
                }
            } else {
                Log.d(TAG, "Usuario admin verificado correctamente");
            }
        });
    }

    /**
     * Inicializa las vistas del fragment.
     */
    private void initializeViews(View view) {
        tvTotalLogs = view.findViewById(R.id.tvTotalLogs);
        tvOldestLog = view.findViewById(R.id.tvOldestLog);
        btnRefreshStats = view.findViewById(R.id.btnRefreshStats);
        btnDelete30Days = view.findViewById(R.id.btnDelete30Days);
        btnDelete60Days = view.findViewById(R.id.btnDelete60Days);
        btnDelete90Days = view.findViewById(R.id.btnDelete90Days);
        btnDeleteAll = view.findViewById(R.id.btnDeleteAll);
        btnLogout = view.findViewById(R.id.btnLogout);
    }

    /**
     * Configura los listeners de los botones.
     */
    private void setupListeners() {
        btnRefreshStats.setOnClickListener(v -> loadStatistics());
        btnDelete30Days.setOnClickListener(v -> confirmDeleteOldLogs(30));
        btnDelete60Days.setOnClickListener(v -> confirmDeleteOldLogs(60));
        btnDelete90Days.setOnClickListener(v -> confirmDeleteOldLogs(90));
        btnDeleteAll.setOnClickListener(v -> confirmDeleteAllLogs());
        btnLogout.setOnClickListener(v -> logout());
    }

    /**
     * Carga las estadísticas de logs.
     */
    private void loadStatistics() {
        Log.d(TAG, "Cargando estadísticas...");

        // Mostrar "Cargando..."
        tvTotalLogs.setText("Cargando...");
        tvOldestLog.setText("Cargando...");

        // Obtener total de logs
        loggerRepository.getLogCount(count -> {
            tvTotalLogs.setText(String.valueOf(count));
            Log.d(TAG, "Total de logs: " + count);
        });

        // Obtener log más antiguo
        loggerRepository.getOldestLogDate(date -> {
            if (date != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
                String formattedDate = sdf.format(date);
                tvOldestLog.setText(formattedDate);
                Log.d(TAG, "Log más antiguo: " + formattedDate);
            } else {
                tvOldestLog.setText("No hay logs");
                Log.d(TAG, "No hay logs en el sistema");
            }
        });
    }

    /**
     * Muestra confirmación antes de eliminar logs antiguos.
     */
    private void confirmDeleteOldLogs(int days) {
        new AlertDialog.Builder(getContext())
                .setTitle("Confirmar eliminación")
                .setMessage(
                        "¿Eliminar todos los logs mayores a " + days + " días?\n\nEsta acción no se puede deshacer.")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteOldLogs(days))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Elimina logs más antiguos que el número de días especificado.
     */
    private void deleteOldLogs(int days) {
        Log.d(TAG, "Eliminando logs mayores a " + days + " días...");

        loggerRepository.deleteOldLogs(days)
                .addOnSuccessListener(deletedCount -> {
                    String message = "Eliminados " + deletedCount + " logs";
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    Log.d(TAG, message);
                    loadStatistics(); // Recargar estadísticas
                })
                .addOnFailureListener(e -> {
                    String message = "Error al eliminar logs: " + e.getMessage();
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, message, e);
                });
    }

    /**
     * Muestra doble confirmación antes de eliminar TODOS los logs.
     */
    private void confirmDeleteAllLogs() {
        new AlertDialog.Builder(getContext())
                .setTitle("⚠️ ADVERTENCIA")
                .setMessage(
                        "¿Estás SEGURO de que quieres eliminar TODOS los logs?\n\nEsta acción NO se puede deshacer.")
                .setPositiveButton("SÍ, ELIMINAR TODO", (dialog, which) -> {
                    // Segunda confirmación
                    new AlertDialog.Builder(getContext())
                            .setTitle("Confirmación Final")
                            .setMessage("Última oportunidad. ¿Eliminar TODOS los logs?")
                            .setPositiveButton("Confirmar", (d, w) -> deleteAllLogs())
                            .setNegativeButton("Cancelar", null)
                            .show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Elimina TODOS los logs del sistema.
     */
    private void deleteAllLogs() {
        Log.w(TAG, "⚠️ ELIMINANDO TODOS LOS LOGS");

        loggerRepository.deleteAllLogs()
                .addOnSuccessListener(deletedCount -> {
                    String message = "⚠️ Eliminados TODOS los logs (" + deletedCount + ")";
                    Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
                    Log.w(TAG, message);
                    loadStatistics(); // Recargar estadísticas
                })
                .addOnFailureListener(e -> {
                    String message = "Error al eliminar logs: " + e.getMessage();
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, message, e);
                });
    }

    /**
     * Cierra la sesión del administrador.
     */
    private void logout() {
        new AlertDialog.Builder(getContext())
                .setTitle("Cerrar Sesión")
                .setMessage("¿Estás seguro de que quieres cerrar sesión?")
                .setPositiveButton("Cerrar Sesión", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getContext(), LoginActivity.class));
                    if (getActivity() != null) {
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
