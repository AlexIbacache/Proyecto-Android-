package com.example.proyectoandroid.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.model.Reparacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
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
