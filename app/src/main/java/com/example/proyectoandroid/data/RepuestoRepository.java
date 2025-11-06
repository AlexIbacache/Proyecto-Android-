package com.example.proyectoandroid.data;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.model.Repuesto;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RepuestoRepository {

    private static final String TAG = "RepuestoRepository";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public LiveData<List<Repuesto>> getCatalogoRepuestos() {
        MutableLiveData<List<Repuesto>> repuestosLiveData = new MutableLiveData<>();
        Log.d(TAG, "Obteniendo catálogo de repuestos");

        db.collection("repuestos") // Asumiendo que tienes una colección "repuestos" a nivel raíz
            .addSnapshotListener((snapshots, e) -> {
                if (e != null) {
                    Log.e(TAG, "Error al obtener el catálogo de repuestos", e);
                    repuestosLiveData.postValue(null);
                    return;
                }

                List<Repuesto> repuestos = new ArrayList<>();
                if (snapshots != null) {
                    for (QueryDocumentSnapshot doc : snapshots) {
                        Repuesto repuesto = doc.toObject(Repuesto.class);
                        repuestos.add(repuesto);
                    }
                    Log.d(TAG, "Catálogo de repuestos obtenido con éxito. Cantidad: " + repuestos.size());
                }
                repuestosLiveData.postValue(repuestos);
            });

        return repuestosLiveData;
    }
}
