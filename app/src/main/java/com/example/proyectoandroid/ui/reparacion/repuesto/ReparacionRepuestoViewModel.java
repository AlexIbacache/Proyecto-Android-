package com.example.proyectoandroid.ui.reparacion.repuesto;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.data.RepuestoRepository;
import com.example.proyectoandroid.model.Repuesto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReparacionRepuestoViewModel extends AndroidViewModel {

    private static final String TAG = "ReparacionRepuestoVM";
    private final RepuestoRepository repuestoRepository;
    private final LiveData<List<Repuesto>> catalogoRepuestos;

    private final MutableLiveData<Map<String, Integer>> _repuestosSeleccionados = new MutableLiveData<>(new HashMap<>());
    public LiveData<Map<String, Integer>> getRepuestosSeleccionados() {
        return _repuestosSeleccionados;
    }

    public ReparacionRepuestoViewModel(@NonNull Application application) {
        super(application);
        repuestoRepository = new RepuestoRepository();
        Log.d(TAG, "ViewModel inicializado, obteniendo catálogo de repuestos.");
        catalogoRepuestos = repuestoRepository.getCatalogoRepuestos();
    }

    public LiveData<List<Repuesto>> getCatalogoRepuestos() {
        return catalogoRepuestos;
    }

    public void actualizarSeleccion(String repuestoId, int cantidad) {
        Map<String, Integer> currentSelection = _repuestosSeleccionados.getValue();
        if (currentSelection != null) {
            Log.d(TAG, "actualizarSeleccion: Id de repuesto=" + repuestoId + ", cantidad=" + cantidad);
            currentSelection.put(repuestoId, cantidad);
            _repuestosSeleccionados.setValue(currentSelection);
        }
    }

    public void eliminarSeleccion(String repuestoId) {
        Map<String, Integer> currentSelection = _repuestosSeleccionados.getValue();
        if (currentSelection != null) {
            Log.d(TAG, "eliminarSeleccion: Id de repuesto=" + repuestoId);
            currentSelection.remove(repuestoId);
            _repuestosSeleccionados.setValue(currentSelection);
        }
    }
}
