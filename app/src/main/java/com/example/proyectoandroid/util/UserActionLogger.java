package com.example.proyectoandroid.util;

import android.util.Log;

import com.example.proyectoandroid.data.LoggerRepository;
import com.example.proyectoandroid.model.LogEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Clase utilitaria singleton para simplificar el logging de acciones de
 * usuario.
 * Proporciona métodos estáticos para registrar diferentes tipos de acciones.
 */
public class UserActionLogger {

    private static final String TAG = "UserActionLogger";
    private static LoggerRepository loggerRepository;

    // Tipos de acciones
    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_LOGOUT = "LOGOUT";
    public static final String ACTION_REGISTER = "REGISTER";
    public static final String ACTION_CREATE = "CREATE";
    public static final String ACTION_UPDATE = "UPDATE";
    public static final String ACTION_DELETE = "DELETE";
    public static final String ACTION_VIEW = "VIEW";
    public static final String ACTION_UPLOAD = "UPLOAD";
    public static final String ACTION_NAVIGATE = "NAVIGATE";

    // Tipos de entidades
    public static final String ENTITY_USER = "USER";
    public static final String ENTITY_MAQUINARIA = "MAQUINARIA";
    public static final String ENTITY_REPARACION = "REPARACION";
    public static final String ENTITY_REPUESTO = "REPUESTO";
    public static final String ENTITY_IMAGE = "IMAGE";
    public static final String ENTITY_REPORTE = "REPORTE";
    public static final String ENTITY_SCREEN = "SCREEN";

    private UserActionLogger() {
    }

    /**
     * Obtiene la instancia del LoggerRepository (lazy initialization).
     */
    private static LoggerRepository getLoggerRepository() {
        if (loggerRepository == null) {
            loggerRepository = new LoggerRepository();
            Log.d(TAG, "LoggerRepository inicializado");
        }
        return loggerRepository;
    }

    /**
     * Registra una acción genérica.
     */
    public static void logAction(String actionType, String actionDescription,
            String entityType, String entityId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser == null) {
            Log.w(TAG, "No se puede registrar acción: usuario no autenticado - " + actionDescription);
            return;
        }

        String userId = currentUser.getUid();
        String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "Sin email";
        String userName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Sin nombre";

        LogEntry logEntry = new LogEntry(userId, userEmail, userName, actionType,
                actionDescription, entityType, entityId);

        try {
            getLoggerRepository().logAction(logEntry);
        } catch (Exception e) {
            Log.e(TAG, "Error al registrar acción: " + actionDescription, e);
        }
    }

    /**
     * Registra una acción de login.
     */
    public static void logLogin(String email) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            logAction(ACTION_LOGIN, "Usuario inició sesión", ENTITY_USER, currentUser.getUid());
        }
    }

    /**
     * Registra una acción de logout.
     */
    public static void logLogout() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();
            String userEmail = currentUser.getEmail() != null ? currentUser.getEmail() : "Sin email";
            String userName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Sin nombre";

            LogEntry logEntry = new LogEntry(userId, userEmail, userName, ACTION_LOGOUT,
                    "Usuario cerró sesión", ENTITY_USER, userId);
            try {
                getLoggerRepository().logAction(logEntry);
            } catch (Exception e) {
                Log.e(TAG, "Error al registrar logout", e);
            }
        }
    }

    /**
     * Registra una acción de registro de nuevo usuario.
     */
    public static void logRegister(String email, String name) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            logAction(ACTION_REGISTER, "Nuevo usuario registrado: " + name, ENTITY_USER, currentUser.getUid());
        }
    }

    /**
     * Registra la creación de una entidad.
     */
    public static void logCreate(String entityType, String entityId, String description) {
        logAction(ACTION_CREATE, description, entityType, entityId);
    }

    /**
     * Registra la actualización de una entidad.
     */
    public static void logUpdate(String entityType, String entityId, String description) {
        logAction(ACTION_UPDATE, description, entityType, entityId);
    }

    /**
     * Registra la eliminación de una entidad.
     */
    public static void logDelete(String entityType, String entityId, String description) {
        logAction(ACTION_DELETE, description, entityType, entityId);
    }

    /**
     * Registra la visualización de una entidad.
     */
    public static void logView(String entityType, String entityId, String description) {
        logAction(ACTION_VIEW, description, entityType, entityId);
    }

    /**
     * Registra la subida de un archivo.
     */
    public static void logUpload(String entityType, String entityId, String description) {
        logAction(ACTION_UPLOAD, description, entityType, entityId);
    }

    /**
     * Registra la navegación a una pantalla.
     */
    public static void logNavigate(String screenName) {
        logAction(ACTION_NAVIGATE, "Navegó a: " + screenName, ENTITY_SCREEN, screenName);
    }

    // Métodos específicos para Maquinaria
    public static void logCreateMaquinaria(String maquinariaId, String nombre) {
        logCreate(ENTITY_MAQUINARIA, maquinariaId, "Creó maquinaria: " + nombre);
    }

    public static void logUpdateMaquinaria(String maquinariaId, String nombre) {
        logUpdate(ENTITY_MAQUINARIA, maquinariaId, "Actualizó maquinaria: " + nombre);
    }

    public static void logDeleteMaquinaria(String maquinariaId, String nombre) {
        logDelete(ENTITY_MAQUINARIA, maquinariaId, "Eliminó maquinaria: " + nombre);
    }

    public static void logViewMaquinaria(String maquinariaId, String nombre) {
        logView(ENTITY_MAQUINARIA, maquinariaId, "Visualizó maquinaria: " + nombre);
    }

    // Métodos específicos para Reparaciones
    public static void logCreateReparacion(String reparacionId, String maquinariaNombre) {
        logCreate(ENTITY_REPARACION, reparacionId, "Creó reparación para: " + maquinariaNombre);
    }

    public static void logUpdateReparacion(String reparacionId, String maquinariaNombre) {
        logUpdate(ENTITY_REPARACION, reparacionId, "Actualizó reparación de: " + maquinariaNombre);
    }

    public static void logDeleteReparacion(String reparacionId, String maquinariaNombre) {
        logDelete(ENTITY_REPARACION, reparacionId, "Eliminó reparación de: " + maquinariaNombre);
    }

    // Métodos específicos para Repuestos
    public static void logCreateRepuesto(String repuestoId, String nombre) {
        logCreate(ENTITY_REPUESTO, repuestoId, "Creó repuesto: " + nombre);
    }

    public static void logUpdateRepuesto(String repuestoId, String nombre) {
        logUpdate(ENTITY_REPUESTO, repuestoId, "Actualizó repuesto: " + nombre);
    }

    public static void logDeleteRepuesto(String repuestoId, String nombre) {
        logDelete(ENTITY_REPUESTO, repuestoId, "Eliminó repuesto: " + nombre);
    }

    // Método para subida de imágenes
    public static void logUploadImage(String maquinariaId, String maquinariaNombre) {
        logUpload(ENTITY_IMAGE, maquinariaId, "Subió imagen para maquinaria: " + maquinariaNombre);
    }

    // Métodos específicos para Reportes
    public static void logCreateReporte(String nombreArchivo) {
        logCreate(ENTITY_REPORTE, "EXPORT_" + System.currentTimeMillis(), "Generó reporte: " + nombreArchivo);
    }
}
