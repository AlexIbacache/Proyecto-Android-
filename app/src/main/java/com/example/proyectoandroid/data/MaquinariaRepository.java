package com.example.proyectoandroid.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.model.Reparacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

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
                            Maquinaria maquinaria = doc.toObject(Maquinaria.class);
                            maquinarias.add(maquinaria);
                        }
                    }
                    maquinariaLiveData.postValue(maquinarias);
                });
        }
        return maquinariaLiveData;
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
                            // Si encontramos una, la devolvemos
                            callback.onComplete(task.getResult().getDocuments().get(0).toObject(Reparacion.class));
                        } else {
                            // Si no, devolvemos null
                            callback.onComplete(null);
                        }
                    });
        } else {
            callback.onComplete(null);
        }
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
                            Reparacion reparacion = doc.toObject(Reparacion.class);
                            reparaciones.add(reparacion);
                        }
                    }
                    reparacionesLiveData.postValue(reparaciones);
                });
        }
        return reparacionesLiveData;
    }

    public void guardarMaquinaria(Maquinaria maquinaria, FirestoreCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            db.collection("users").document(currentUser.getUid())
              .collection("maquinaria")
              .add(maquinaria)
              .addOnSuccessListener(documentReference -> callback.onComplete(true))
              .addOnFailureListener(e -> callback.onComplete(false));
        } else {
            callback.onComplete(false);
        }
    }

    public void guardarReparacion(String maquinariaId, Reparacion reparacion, FirestoreCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && maquinariaId != null) {
            db.collection("users").document(currentUser.getUid())
                    .collection("maquinaria").document(maquinariaId)
                    .collection("reparaciones")
                    .add(reparacion)
                    .addOnSuccessListener(documentReference -> callback.onComplete(true))
                    .addOnFailureListener(e -> callback.onComplete(false));
        } else {
            callback.onComplete(false);
        }
    }

    public void actualizarReparacion(String maquinariaId, Reparacion reparacion, FirestoreCallback callback) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && maquinariaId != null && reparacion.getDocumentId() != null) {
            db.collection("users").document(currentUser.getUid())
                    .collection("maquinaria").document(maquinariaId)
                    .collection("reparaciones").document(reparacion.getDocumentId())
                    .set(reparacion)
                    .addOnSuccessListener(aVoid -> callback.onComplete(true))
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
              .set(maquinaria)
              .addOnSuccessListener(aVoid -> callback.onComplete(true))
              .addOnFailureListener(e -> callback.onComplete(false));
        } else {
            callback.onComplete(false);
        }
    }

    public void eliminarMaquinaria(String maquinariaId) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && maquinariaId != null) {
            db.collection("users").document(currentUser.getUid())
              .collection("maquinaria").document(maquinariaId)
              .delete();
        }
    }
}
