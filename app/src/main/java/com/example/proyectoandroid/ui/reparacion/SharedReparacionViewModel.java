package com.example.proyectoandroid.ui.reparacion;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.model.Reparacion;

public class SharedReparacionViewModel extends ViewModel {

    private final MutableLiveData<Reparacion> reparacionParaEditar = new MutableLiveData<>();
    private final MutableLiveData<Maquinaria> maquinaDeLaReparacion = new MutableLiveData<>();

    public void setReparacionParaEditar(Reparacion reparacion, Maquinaria maquinaria) {
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
        reparacionParaEditar.setValue(null);
        maquinaDeLaReparacion.setValue(null);
    }
}
