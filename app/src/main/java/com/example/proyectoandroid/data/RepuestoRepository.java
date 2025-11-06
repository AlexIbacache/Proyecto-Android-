package com.example.proyectoandroid.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.model.Repuesto;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RepuestoRepository {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Repuesto>> getCatalogoRepuestos() {
        MutableLiveData<List<Repuesto>> repuestosLiveData = new MutableLiveData<>();

        db.collection("repuestos") // Asumiendo que tienes una colección "repuestos" a nivel raíz
            .addSnapshotListener((snapshots, e) -> {
                if (e != null) {
                    repuestosLiveData.postValue(null);
                    return;
                }

                List<Repuesto> repuestos = new ArrayList<>();
                if (snapshots != null) {
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Repuesto repuesto = doc.toObject(Repuesto.class);
                        repuestos.add(repuesto);
                    }
                }
                repuestosLiveData.postValue(repuestos);
            });

        return repuestosLiveData;
    }
}
