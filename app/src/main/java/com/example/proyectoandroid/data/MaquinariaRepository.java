package com.example.proyectoandroid.data;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.model.ParteReparada;
import com.example.proyectoandroid.model.Reparacion;
import com.example.proyectoandroid.model.Repuesto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class MaquinariaRepository {

    private static final String TAG = "MaquinariaRepository";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public interface FirestoreCallback {
        void onComplete(boolean success);
    }
    
    public interface ReparacionCallback {
        void onComplete(Reparacion reparacion);
    }

    public LiveData<List<Maquinaria>> getMaquinariaList() {
        MutableLiveData<List<Maquinaria>> maquinariaLiveData = new MutableLiveData<>();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "getMaquinariaList llamado");

        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).collection("maquinaria")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error al obtener la lista de maquinaria", e);
                        maquinariaLiveData.postValue(null);
                        return;
                    }
                    List<Maquinaria> maquinarias = new ArrayList<>();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Maquinaria maquinaria = doc.toObject(Maquinaria.class);
                            maquinarias.add(maquinaria);
                        }
                        Log.d(TAG, "Lista de maquinaria obtenida con éxito. Cantidad: " + maquinarias.size());
                    }
                    maquinariaLiveData.postValue(maquinarias);
                });
        } else {
            Log.w(TAG, "getMaquinariaList: No se encontró un usuario actual");
        }
        return maquinariaLiveData;
    }
    
    public void getReparacionAbierta(String maquinariaId, ReparacionCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "getReparacionAbierta llamado para maquinariaId: " + maquinariaId);
        if (currentUser != null && maquinariaId != null) {
            db.collection("users").document(currentUser.getUid())
                    .collection("maquinaria").document(maquinariaId)
                    .collection("reparaciones")
                    .whereEqualTo("estado", "Abierta")
                    .limit(1)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            Reparacion reparacion = task.getResult().getDocuments().get(0).toObject(Reparacion.class);
                            Log.d(TAG, "Reparación abierta encontrada: " + reparacion.getDocumentId());
                            callback.onComplete(reparacion);
                        } else {
                            Log.d(TAG, "No se encontró una reparación abierta o la tarea falló.", task.getException());
                            callback.onComplete(null);
                        }
                    });
        } else {
            Log.w(TAG, "getReparacionAbierta: El usuario o el ID de la maquinaria es nulo");
            callback.onComplete(null);
        }
    }

    public LiveData<Maquinaria> getMaquinariaById(String maquinariaId) {
        MutableLiveData<Maquinaria> maquinariaLiveData = new MutableLiveData<>();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "getMaquinariaById llamado para el id: " + maquinariaId);

        if (currentUser != null && maquinariaId != null) {
            db.collection("users").document(currentUser.getUid()).collection("maquinaria").document(maquinariaId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Maquinaria encontrada para el id: " + maquinariaId);
                        maquinariaLiveData.postValue(documentSnapshot.toObject(Maquinaria.class));
                    } else {
                        Log.w(TAG, "No se encontró maquinaria para el id: " + maquinariaId);
                        maquinariaLiveData.postValue(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener la maquinaria por id: " + maquinariaId, e);
                    maquinariaLiveData.postValue(null);
                });
        } else {
             Log.w(TAG, "getMaquinariaById: El usuario o el ID de la maquinaria es nulo");
        }
        return maquinariaLiveData;
    }

    public LiveData<List<Reparacion>> getReparacionesDeMaquina(String maquinariaId) {
        MutableLiveData<List<Reparacion>> reparacionesLiveData = new MutableLiveData<>();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "getReparacionesDeMaquina para maquinariaId: " + maquinariaId);

        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                .collection("maquinaria").document(maquinariaId)
                .collection("reparaciones")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Log.e(TAG, "Error al obtener las reparaciones para la maquinariaId: " + maquinariaId, e);
                        reparacionesLiveData.postValue(null);
                        return;
                    }
                    List<Reparacion> reparaciones = new ArrayList<>();
                    if (snapshots != null) {
                        for (QueryDocumentSnapshot doc : snapshots) {
                            Reparacion reparacion = doc.toObject(Reparacion.class);
                            reparaciones.add(reparacion);
                        }
                         Log.d(TAG, "Reparaciones obtenidas. Cantidad: " + reparaciones.size());
                    }
                    reparacionesLiveData.postValue(reparaciones);
                });
        } else {
            Log.w(TAG, "getReparacionesDeMaquina: No se encontró un usuario actual");
        }
        return reparacionesLiveData;
    }

    public void guardarMaquinaria(Maquinaria maquinaria, FirestoreCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "guardarMaquinaria llamado para: " + maquinaria.getNombre());
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
              .collection("maquinaria")
              .add(maquinaria)
              .addOnSuccessListener(documentReference -> {
                  Log.d(TAG, "Maquinaria guardada con el ID: " + documentReference.getId());
                  callback.onComplete(true);
              })
              .addOnFailureListener(e -> {
                  Log.e(TAG, "Error al guardar la maquinaria", e);
                  callback.onComplete(false);
              });
        } else {
            Log.w(TAG, "guardarMaquinaria: No se encontró un usuario actual");
            callback.onComplete(false);
        }
    }

    public void guardarReparacion(String maquinariaId, Reparacion reparacion, FirestoreCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "guardarReparacion llamado para maquinariaId: " + maquinariaId);
        if (currentUser != null && maquinariaId != null) {
            db.collection("users").document(currentUser.getUid())
                    .collection("maquinaria").document(maquinariaId)
                    .collection("reparaciones")
                    .add(reparacion)
                    .addOnSuccessListener(documentReference -> {
                        Log.d(TAG, "Reparación guardada con el ID: " + documentReference.getId());
                        callback.onComplete(true);
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error al guardar la reparación", e);
                        callback.onComplete(false);
                    });
        } else {
            Log.w(TAG, "guardarReparacion: El usuario o el ID de la maquinaria es nulo");
            callback.onComplete(false);
        }
    }

    public void actualizarReparacion(String maquinariaId, Reparacion reparacion, FirestoreCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "actualizarReparacion llamado para reparacionId: " + reparacion.getDocumentId());
        if (currentUser != null && maquinariaId != null && reparacion.getDocumentId() != null) {
            db.collection("users").document(currentUser.getUid())
                    .collection("maquinaria").document(maquinariaId)
                    .collection("reparaciones").document(reparacion.getDocumentId())
                    .set(reparacion)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Reparación actualizada con éxito.");
                        callback.onComplete(true);
                    })
                    .addOnFailureListener(e -> {
                         Log.e(TAG, "Error al actualizar la reparación", e);
                        callback.onComplete(false);
                    });
        } else {
             Log.w(TAG, "actualizarReparacion: El usuario, el ID de la maquinaria o el ID de la reparación es nulo");
            callback.onComplete(false);
        }
    }

    public void actualizarMaquinaria(Maquinaria maquinaria, FirestoreCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "actualizarMaquinaria llamado para: " + maquinaria.getDocumentId());
        if (currentUser != null && maquinaria.getDocumentId() != null) {
            db.collection("users").document(currentUser.getUid())
              .collection("maquinaria").document(maquinaria.getDocumentId())
              .set(maquinaria)
              .addOnSuccessListener(aVoid -> {
                  Log.d(TAG, "Maquinaria actualizada con éxito.");
                  callback.onComplete(true);
              })
              .addOnFailureListener(e -> {
                  Log.e(TAG, "Error al actualizar la maquinaria", e);
                  callback.onComplete(false);
              });
        } else {
            Log.w(TAG, "actualizarMaquinaria: El usuario o el ID de la maquinaria es nulo");
            callback.onComplete(false);
        }
    }

    public void eliminarMaquinaria(String maquinariaId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.d(TAG, "eliminarMaquinaria llamado para: " + maquinariaId);
        if (currentUser != null && maquinariaId != null) {
            db.collection("users").document(currentUser.getUid())
              .collection("maquinaria").document(maquinariaId)
              .delete()
              .addOnSuccessListener(aVoid -> Log.d(TAG, "Maquinaria eliminada con éxito: " + maquinariaId))
              .addOnFailureListener(e -> Log.e(TAG, "Error al eliminar la maquinaria: " + maquinariaId, e));
        } else {
            Log.w(TAG, "eliminarMaquinaria: El usuario o el ID de la maquinaria es nulo");
        }
    }

    public Task<Void> deleteReparacion(String userId, String maquinaId, String reparacionId) {
        Log.d(TAG, "deleteReparacion llamado para userId: " + userId + ", maquinaId: " + maquinaId + ", reparacionId: " + reparacionId);
        return db.collection("users").document(userId)
                .collection("maquinaria").document(maquinaId)
                .collection("reparaciones").document(reparacionId)
                .delete();
    }

    public void eliminarRepuestoDeReparacion(String maquinaId, String reparacionId, String parteNombre, Repuesto repuesto, FirestoreCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onComplete(false);
            return;
        }

        DocumentReference reparacionRef = db.collection("users").document(currentUser.getUid())
                .collection("maquinaria").document(maquinaId)
                .collection("reparaciones").document(reparacionId);

        db.runTransaction(transaction -> {
            Reparacion reparacion = transaction.get(reparacionRef).toObject(Reparacion.class);
            if (reparacion != null && reparacion.getPartesReparadas() != null) {
                for (ParteReparada parte : reparacion.getPartesReparadas()) {
                    if (parte.getNombreParte().equals(parteNombre)) {
                        if (parte.getRepuestos() != null) {
                            parte.getRepuestos().remove(repuesto);
                            break; 
                        }
                    }
                }
                transaction.set(reparacionRef, reparacion);
            }
            return null;
        }).addOnSuccessListener(aVoid -> callback.onComplete(true))
          .addOnFailureListener(e -> callback.onComplete(false));
    }

    public void eliminarTodosLosReportesDeMaquina(String maquinariaId, FirestoreCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            callback.onComplete(false);
            return;
        }

        db.collection("users").document(currentUser.getUid())
                .collection("maquinaria").document(maquinariaId)
                .collection("reparaciones")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    WriteBatch batch = db.batch();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        batch.delete(doc.getReference());
                    }
                    batch.commit()
                        .addOnSuccessListener(aVoid -> callback.onComplete(true))
                        .addOnFailureListener(e -> callback.onComplete(false));
                })
                .addOnFailureListener(e -> callback.onComplete(false));
    }
}
