package com.example.proyectoandroid.ui.reparacion;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.model.Reparacion;

public class SharedReparacionViewModel extends ViewModel {

    private static final String TAG = "SharedReparacionVM";
    private final MutableLiveData<Reparacion> reparacionParaEditar = new MutableLiveData<>();
    private final MutableLiveData<Maquinaria> maquinaDeLaReparacion = new MutableLiveData<>();

    public void setReparacionParaEditar(Reparacion reparacion, Maquinaria maquinaria) {
        Log.d(TAG, "setReparacionParaEditar llamado. ID de Reparación: " + (reparacion != null ? reparacion.getDocumentId() : "nulo") + ", Maquinaria: " + (maquinaria != null ? maquinaria.getNombre() : "nula"));
        reparacionParaEditar.setValue(reparacion);
        maquinaDeLaReparacion.setValue(maquinaria);
    }

    public LiveData<Reparacion> getReparacionParaEditar() {
        return reparacionParaEditar;
    }

    public LiveData<Maquinaria> getMaquinaDeLaReparacion() {
        return maquinaDeLaReparacion;
    }

    public void clearData() {
        Log.d(TAG, "clearData llamado. Limpiando datos de reparación compartidos.");
        reparacionParaEditar.setValue(null);
        maquinaDeLaReparacion.setValue(null);
    }
}
