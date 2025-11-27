package com.example.proyectoandroid.data;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.model.LogEntry;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Repositorio para manejar operaciones de logging en Firestore.
 * Almacena logs de acciones de usuario en la colección 'historial-logs'.
 */
public class LoggerRepository {

    private static final String TAG = "LoggerRepository";
    private static final String COLLECTION_NAME = "historial-logs";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    /**
     * Registra una acción en Firestore de forma asíncrona.
     * 
     * @param logEntry Entrada de log a registrar
     */
    public void logAction(LogEntry logEntry) {
        Log.d(TAG, "🟢 LoggerRepository.logAction() INICIADO");

        if (logEntry == null) {
            Log.w(TAG, "⚠️ Intento de registrar un log nulo");
            return;
        }

        Log.d(TAG, "🟢 Intentando guardar log en Firestore - Acción: " + logEntry.getActionType() +
                " | Usuario: " + logEntry.getUserEmail() +
                " | Descripción: " + logEntry.getActionDescription());

        Log.d(TAG, "🟢 Llamando a db.collection(" + COLLECTION_NAME + ").add()");

        db.collection(COLLECTION_NAME)
                .add(logEntry)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "✅ Log registrado exitosamente en Firestore: " + documentReference.getId() +
                            " - Acción: " + logEntry.getActionType() +
                            " - Usuario: " + logEntry.getUserEmail());
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "❌ ERROR al registrar log en Firestore - Acción: " + logEntry.getActionType() +
                            " | Usuario: " + logEntry.getUserEmail(), e);
                    Log.e(TAG, "Detalles del error: " + e.getMessage());
                    if (e.getCause() != null) {
                        Log.e(TAG, "Causa: " + e.getCause().getMessage());
                    }
                });

        Log.d(TAG, "🟢 db.collection().add() llamado (asíncrono)");
    }

    /**
     * Obtiene todos los logs de un usuario específico.
     * 
     * @param userId ID del usuario
     * @return LiveData con la lista de logs del usuario
     */
    public LiveData<List<LogEntry>> getUserLogs(String userId) {
        MutableLiveData<List<LogEntry>> logsLiveData = new MutableLiveData<>();

        if (userId == null || userId.isEmpty()) {
            Log.w(TAG, "ID de usuario nulo o vacío");
            logsLiveData.setValue(new ArrayList<>());
            return logsLiveData;
        }

        db.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error al obtener logs del usuario: " + userId, e);
                        logsLiveData.postValue(new ArrayList<>());
                        return;
                    }

                    List<LogEntry> logs = new ArrayList<>();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            LogEntry log = doc.toObject(LogEntry.class);
                            logs.add(log);
                        }
                    }
                    logsLiveData.postValue(logs);
                });

        return logsLiveData;
    }

    /**
     * Obtiene todos los logs del sistema (útil para administradores).
     * 
     * @return LiveData con la lista de todos los logs
     */
    public LiveData<List<LogEntry>> getAllLogs() {
        MutableLiveData<List<LogEntry>> logsLiveData = new MutableLiveData<>();

        db.collection(COLLECTION_NAME)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(100) // Limitar a los últimos 100 logs para evitar sobrecarga
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error al obtener todos los logs", e);
                        logsLiveData.postValue(new ArrayList<>());
                        return;
                    }

                    List<LogEntry> logs = new ArrayList<>();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            LogEntry log = doc.toObject(LogEntry.class);
                            logs.add(log);
                        }
                    }
                    logsLiveData.postValue(logs);
                });

        return logsLiveData;
    }

    /**
     * Obtiene logs filtrados por tipo de acción.
     * 
     * @param actionType Tipo de acción a filtrar
     * @return LiveData con la lista de logs filtrados
     */
    public LiveData<List<LogEntry>> getLogsByActionType(String actionType) {
        MutableLiveData<List<LogEntry>> logsLiveData = new MutableLiveData<>();

        if (actionType == null || actionType.isEmpty()) {
            Log.w(TAG, "Tipo de acción nulo o vacío");
            logsLiveData.setValue(new ArrayList<>());
            return logsLiveData;
        }

        db.collection(COLLECTION_NAME)
                .whereEqualTo("actionType", actionType)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error al obtener logs por tipo de acción: " + actionType, e);
                        logsLiveData.postValue(new ArrayList<>());
                        return;
                    }

                    List<LogEntry> logs = new ArrayList<>();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            LogEntry log = doc.toObject(LogEntry.class);
                            logs.add(log);
                        }
                    }
                    logsLiveData.postValue(logs);
                });

        return logsLiveData;
    }

    /**
     * Obtiene logs filtrados por tipo de entidad.
     * 
     * @param entityType Tipo de entidad a filtrar
     * @return LiveData con la lista de logs filtrados
     */
    public LiveData<List<LogEntry>> getLogsByEntityType(String entityType) {
        MutableLiveData<List<LogEntry>> logsLiveData = new MutableLiveData<>();

        if (entityType == null || entityType.isEmpty()) {
            Log.w(TAG, "Tipo de entidad nulo o vacío");
            logsLiveData.setValue(new ArrayList<>());
            return logsLiveData;
        }

        db.collection(COLLECTION_NAME)
                .whereEqualTo("entityType", entityType)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error al obtener logs por tipo de entidad: " + entityType, e);
                        logsLiveData.postValue(new ArrayList<>());
                        return;
                    }

                    List<LogEntry> logs = new ArrayList<>();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            LogEntry log = doc.toObject(LogEntry.class);
                            logs.add(log);
                        }
                    }
                    logsLiveData.postValue(logs);
                });

        return logsLiveData;
    }

    // ========== MÉTODOS DE MANTENIMIENTO (SOLO ADMIN) ==========

    /**
     * Interface para recibir el conteo de logs.
     */
    public interface OnCountListener {
        void onCount(int count);
    }

    /**
     * Interface para recibir la fecha del log más antiguo.
     */
    public interface OnDateListener {
        void onDate(Date date);
    }

    /**
     * Obtiene el total de logs en la colección.
     * 
     * @param listener Callback con el conteo
     */
    public void getLogCount(OnCountListener listener) {
        db.collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int count = querySnapshot.size();
                    Log.d(TAG, "Total de logs: " + count);
                    listener.onCount(count);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al contar logs", e);
                    listener.onCount(0);
                });
    }

    /**
     * Obtiene la fecha del log más antiguo.
     * 
     * @param listener Callback con la fecha
     */
    public void getOldestLogDate(OnDateListener listener) {
        db.collection(COLLECTION_NAME)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        LogEntry oldestLog = querySnapshot.getDocuments().get(0).toObject(LogEntry.class);
                        if (oldestLog != null && oldestLog.getTimestamp() != null) {
                            Date date = oldestLog.getTimestamp().toDate();
                            Log.d(TAG, "Log más antiguo: " + date);
                            listener.onDate(date);
                        } else {
                            listener.onDate(null);
                        }
                    } else {
                        Log.d(TAG, "No hay logs en la colección");
                        listener.onDate(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener log más antiguo", e);
                    listener.onDate(null);
                });
    }

    /**
     * Elimina logs más antiguos que el número de días especificado.
     * 
     * @param daysOld Número de días (logs más antiguos serán eliminados)
     * @return Task con el número de logs eliminados
     */
    public Task<Integer> deleteOldLogs(int daysOld) {
        TaskCompletionSource<Integer> taskSource = new TaskCompletionSource<>();

        // Calcular fecha límite
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -daysOld);
        Date cutoffDate = calendar.getTime();
        com.google.firebase.Timestamp cutoffTimestamp = new com.google.firebase.Timestamp(cutoffDate);

        Log.d(TAG, "Eliminando logs anteriores a: " + cutoffDate);

        db.collection(COLLECTION_NAME)
                .whereLessThan("timestamp", cutoffTimestamp)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int count = querySnapshot.size();
                    if (count == 0) {
                        Log.d(TAG, "No hay logs antiguos para eliminar");
                        taskSource.setResult(0);
                        return;
                    }

                    // Eliminar en lotes (Firestore permite máximo 500 por lote)
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        batch.delete(doc.getReference());
                    }

                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Eliminados " + count + " logs antiguos");
                                taskSource.setResult(count);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error al eliminar logs antiguos", e);
                                taskSource.setException(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar logs antiguos", e);
                    taskSource.setException(e);
                });

        return taskSource.getTask();
    }

    /**
     * Elimina TODOS los logs de la colección.
     * ⚠️ USAR CON PRECAUCIÓN - Esta acción no se puede deshacer.
     * 
     * @return Task con el número de logs eliminados
     */
    public Task<Integer> deleteAllLogs() {
        TaskCompletionSource<Integer> taskSource = new TaskCompletionSource<>();

        Log.w(TAG, "⚠️ ELIMINANDO TODOS LOS LOGS");

        db.collection(COLLECTION_NAME)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int count = querySnapshot.size();
                    if (count == 0) {
                        Log.d(TAG, "No hay logs para eliminar");
                        taskSource.setResult(0);
                        return;
                    }

                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        batch.delete(doc.getReference());
                    }

                    batch.commit()
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Eliminados TODOS los logs (" + count + ")");
                                taskSource.setResult(count);
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error al eliminar todos los logs", e);
                                taskSource.setException(e);
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al buscar logs para eliminar", e);
                    taskSource.setException(e);
                });

        return taskSource.getTask();
    }

    /**
     * Obtiene el conteo de logs de creación de una entidad específica para un
     * usuario.
     * Útil para estadísticas de perfil.
     *
     * @param userId     ID del usuario
     * @param entityType Tipo de entidad (MAQUINARIA, REPARACION, REPORTE)
     * @param listener   Callback con el conteo
     */
    public void getUserEntityCount(String userId, String entityType, OnCountListener listener) {
        if (userId == null || entityType == null) {
            listener.onCount(0);
            return;
        }

        db.collection(COLLECTION_NAME)
                .whereEqualTo("userId", userId)
                .whereEqualTo("actionType", "CREATE")
                .whereEqualTo("entityType", entityType)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    int count = querySnapshot.size();
                    Log.d(TAG, "Conteo para " + entityType + ": " + count);
                    listener.onCount(count);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al contar logs de usuario", e);
                    listener.onCount(0);
                });
    }
}
