package com.example.proyectoandroid.data;

import android.net.Uri;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.model.ParteReparada;
import com.example.proyectoandroid.model.Reparacion;
import com.example.proyectoandroid.model.Repuesto;
import com.example.proyectoandroid.util.UserActionLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class MaquinariaRepository {

    private static final String TAG = "MaquinariaRepository";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();

    public interface FirestoreCallback {
        void onComplete(boolean success);
    }

    public interface UploadImageCallback {
        void onImageUploaded(String imageUrl);

        void onUploadFailed(Exception e);
    }

    public interface ReparacionCallback {
        void onComplete(Reparacion reparacion);
    }

    public String getNewMaquinariaId() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        return db.collection("users").document(currentUser.getUid())
                .collection("maquinaria").document().getId();
    }

    public LiveData<List<Maquinaria>> getMaquinariaList() {
        MutableLiveData<List<Maquinaria>> maquinariaLiveData = new MutableLiveData<>();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid()).collection("maquinaria")
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            maquinariaLiveData.postValue(null);
                            return;
                        }
                        List<Maquinaria> maquinarias = new ArrayList<>();
                        if (snapshots != null) {
                            for (QueryDocumentSnapshot doc : snapshots) {
                                maquinarias.add(doc.toObject(Maquinaria.class));
                            }
                        }
                        maquinariaLiveData.postValue(maquinarias);
                    });
        }
        return maquinariaLiveData;
    }

    public LiveData<Maquinaria> getMaquinariaById(String maquinariaId) {
        MutableLiveData<Maquinaria> maquinariaLiveData = new MutableLiveData<>();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && maquinariaId != null) {
            db.collection("users").document(currentUser.getUid()).collection("maquinaria").document(maquinariaId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            maquinariaLiveData.postValue(documentSnapshot.toObject(Maquinaria.class));
                        } else {
                            maquinariaLiveData.postValue(null);
                        }
                    })
                    .addOnFailureListener(e -> maquinariaLiveData.postValue(null));
        }
        return maquinariaLiveData;
    }

    public void guardarMaquinaria(Maquinaria maquinaria, FirestoreCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && maquinaria.getDocumentId() != null) {
            db.collection("users").document(currentUser.getUid())
                    .collection("maquinaria").document(maquinaria.getDocumentId())
                    .set(maquinaria)
                    .addOnSuccessListener(aVoid -> {
                        UserActionLogger.logCreateMaquinaria(maquinaria.getDocumentId(),
                                maquinaria.getNombre() != null ? maquinaria.getNombre() : "Sin nombre");
                        callback.onComplete(true);
                    })
                    .addOnFailureListener(e -> callback.onComplete(false));
        } else {
            callback.onComplete(false);
        }
    }

    public void actualizarMaquinaria(Maquinaria maquinaria, FirestoreCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && maquinaria.getDocumentId() != null) {
            db.collection("users").document(currentUser.getUid())
                    .collection("maquinaria").document(maquinaria.getDocumentId())
                    .set(maquinaria, SetOptions.merge())
                    .addOnSuccessListener(aVoid -> {
                        UserActionLogger.logUpdateMaquinaria(maquinaria.getDocumentId(),
                                maquinaria.getNombre() != null ? maquinaria.getNombre() : "Sin nombre");
                        callback.onComplete(true);
                    })
                    .addOnFailureListener(e -> callback.onComplete(false));
        } else {
            callback.onComplete(false);
        }
    }

    public void subirFotoMaquinaria(String maquinariaId, Uri photoUri, UploadImageCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null || maquinariaId == null) {
            callback.onUploadFailed(new Exception("Usuario no autenticado o ID de maquinaria nulo."));
            return;
        }

        StorageReference storageRef = storage.getReference()
                .child("maquinaria_imagenes/" + currentUser.getUid() + "/" + maquinariaId + ".jpg");

        UploadTask uploadTask = storageRef.putFile(photoUri);
        uploadTask.addOnFailureListener(callback::onUploadFailed).addOnSuccessListener(taskSnapshot -> {
            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                UserActionLogger.logUploadImage(maquinariaId, "Imagen de maquinaria");
                callback.onImageUploaded(uri.toString());
            }).addOnFailureListener(callback::onUploadFailed);
        });
    }

    public void getReparacionAbierta(String maquinariaId, ReparacionCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
                            callback.onComplete(reparacion);
                        } else {
                            callback.onComplete(null);
                        }
                    });
        } else {
            callback.onComplete(null);
        }
    }

    public LiveData<List<Reparacion>> getReparacionesDeMaquina(String maquinariaId) {
        MutableLiveData<List<Reparacion>> reparacionesLiveData = new MutableLiveData<>();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
                    .collection("maquinaria").document(maquinariaId)
                    .collection("reparaciones")
                    .addSnapshotListener((snapshots, e) -> {
                        if (e != null) {
                            reparacionesLiveData.postValue(null);
                            return;
                        }
                        List<Reparacion> reparaciones = new ArrayList<>();
                        if (snapshots != null) {
                            for (QueryDocumentSnapshot doc : snapshots) {
                                reparaciones.add(doc.toObject(Reparacion.class));
                            }
                        }
                        reparacionesLiveData.postValue(reparaciones);
                    });
        }
        return reparacionesLiveData;
    }

    public void guardarReparacion(String maquinariaId, Reparacion reparacion, FirestoreCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && maquinariaId != null) {
            db.collection("users").document(currentUser.getUid())
                    .collection("maquinaria").document(maquinariaId)
                    .collection("reparaciones")
                    .add(reparacion)
                    .addOnSuccessListener(documentReference -> {
                        UserActionLogger.logCreateReparacion(documentReference.getId(),
                                "Maquinaria ID: " + maquinariaId);
                        callback.onComplete(true);
                    })
                    .addOnFailureListener(e -> callback.onComplete(false));
        }
    }

    public void actualizarReparacion(String maquinariaId, Reparacion reparacion, FirestoreCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && maquinariaId != null && reparacion.getDocumentId() != null) {
            db.collection("users").document(currentUser.getUid())
                    .collection("maquinaria").document(maquinariaId)
                    .collection("reparaciones").document(reparacion.getDocumentId())
                    .set(reparacion)
                    .addOnSuccessListener(aVoid -> {
                        UserActionLogger.logUpdateReparacion(reparacion.getDocumentId(),
                                "Maquinaria ID: " + maquinariaId);
                        callback.onComplete(true);
                    })
                    .addOnFailureListener(e -> callback.onComplete(false));
        }
    }

    public void eliminarMaquinaria(String maquinariaId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && maquinariaId != null) {
            UserActionLogger.logDeleteMaquinaria(maquinariaId, "Maquinaria ID: " + maquinariaId);
            db.collection("users").document(currentUser.getUid())
                    .collection("maquinaria").document(maquinariaId)
                    .delete();
        }
    }

    public Task<Void> deleteReparacion(String userId, String maquinaId, String reparacionId) {
        UserActionLogger.logDeleteReparacion(reparacionId, "Maquinaria ID: " + maquinaId);
        return db.collection("users").document(userId)
                .collection("maquinaria").document(maquinaId)
                .collection("reparaciones").document(reparacionId)
                .delete();
    }

    public void eliminarRepuestoDeReparacion(String maquinaId, String reparacionId, String parteNombre,
            Repuesto repuesto, FirestoreCallback callback) {
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
