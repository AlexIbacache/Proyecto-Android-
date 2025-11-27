package com.example.proyectoandroid.util;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Clase utilitaria para verificar permisos de administrador.
 */
public class AdminHelper {

    private static final String TAG = "AdminHelper";
    private static final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Interface para recibir el resultado de la verificación de admin.
     */
    public interface OnAdminCheckListener {
        void onResult(boolean isAdmin);
    }

    /**
     * Verifica si un usuario tiene rol de administrador.
     * 
     * @param userId   ID del usuario a verificar
     * @param listener Callback con el resultado
     */
    public static void isAdmin(String userId, OnAdminCheckListener listener) {
        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "ID de usuario nulo o vacío");
            listener.onResult(false);
            return;
        }

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        boolean isAdmin = "admin".equals(role);
                        Log.d(TAG, "Usuario " + userId + " - Role: " + role + " - Es admin: " + isAdmin);
                        listener.onResult(isAdmin);
                    } else {
                        Log.w(TAG, "Documento de usuario no encontrado: " + userId);
                        listener.onResult(false);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al verificar rol de admin", e);
                    listener.onResult(false);
                });
    }

    /**
     * Verifica si el usuario actualmente autenticado es admin.
     * 
     * @param listener Callback con el resultado
     */
    public static void isCurrentUserAdmin(OnAdminCheckListener listener) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId == null) {
            Log.w(TAG, "No hay usuario autenticado");
            listener.onResult(false);
            return;
        }

        isAdmin(userId, listener);
    }
}
