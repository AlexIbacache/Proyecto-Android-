package com.example.proyectoandroid.ui.repuesto;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectoandroid.model.Repuesto;

import java.util.ArrayList;
import java.util.List;

public class RepuestoViewModel extends ViewModel {

    private static final String TAG = "RepuestoViewModel";
    private final MutableLiveData<List<Repuesto>> _repuestos = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Repuesto>> getRepuestos() {
        return _repuestos;
    }

    private final MutableLiveData<String> _parteNombre = new MutableLiveData<>();
    public LiveData<String> getParteNombre() {
        return _parteNombre;
    }

    public RepuestoViewModel() {
        Log.d(TAG, "ViewModel inicializado");
    }

    public void setParteNombre(String nombre) {
        Log.d(TAG, "setParteNombre llamado con: " + nombre);
        _parteNombre.setValue(nombre);
    }

    public void setInitialRepuestos(List<Repuesto> repuestos) {
        if (repuestos != null) {
            Log.d(TAG, "setInitialRepuestos llamado con " + repuestos.size() + " repuestos.");
            _repuestos.setValue(new ArrayList<>(repuestos));
        } else {
            Log.d(TAG, "setInitialRepuestos llamado con lista nula.");
        }
    }

    public void addRepuesto(String nombre, String codigo, String cantidadStr) {
        Log.d(TAG, "addRepuesto llamado con: " + nombre + ", " + codigo + ", " + cantidadStr);
        if (nombre == null || nombre.trim().isEmpty() ||
            codigo == null || codigo.trim().isEmpty() ||
            cantidadStr == null || cantidadStr.trim().isEmpty()) {
            Log.w(TAG, "La validación de addRepuesto falló: los campos están vacíos.");
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) {
                Log.w(TAG, "addRepuesto falló: la cantidad debe ser positiva.");
                return;
            }

            Repuesto nuevoRepuesto = new Repuesto(nombre.trim(), codigo.trim(), cantidad);

            List<Repuesto> currentList = _repuestos.getValue();
            if (currentList != null) {
                currentList.add(nuevoRepuesto);
                _repuestos.setValue(currentList);
                Log.d(TAG, "Repuesto agregado con éxito. Nuevo tamaño de la lista: " + currentList.size());
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "addRepuesto falló: NumberFormatException para la cadena de cantidad: " + cantidadStr, e);
        }
    }

    public void removeRepuesto(Repuesto repuesto) {
        if (repuesto != null) {
            Log.d(TAG, "removeRepuesto llamado para: " + repuesto.getNombre());
            List<Repuesto> currentList = _repuestos.getValue();
            if (currentList != null) {
                currentList.remove(repuesto);
                _repuestos.setValue(currentList);
                Log.d(TAG, "Repuesto eliminado. Nuevo tamaño de la lista: " + currentList.size());
            }
        } else {
            Log.w(TAG, "removeRepuesto llamado con repuesto nulo.");
        }
    }
}
