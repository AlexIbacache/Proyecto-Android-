package com.example.proyectoandroid.ui.repuesto;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectoandroid.model.Repuesto;

import java.util.ArrayList;
import java.util.List;

public class RepuestoViewModel extends ViewModel {

    private final MutableLiveData<List<Repuesto>> _repuestos = new MutableLiveData<>(new ArrayList<>());
    public LiveData<List<Repuesto>> getRepuestos() {
        return _repuestos;
    }

    private final MutableLiveData<String> _parteNombre = new MutableLiveData<>();
    public LiveData<String> getParteNombre() {
        return _parteNombre;
    }

    public void setParteNombre(String nombre) {
        _parteNombre.setValue(nombre);
    }

    // --- NUEVO MÉTODO ---
    // Para inicializar la lista con los repuestos que ya existían
    public void setInitialRepuestos(List<Repuesto> repuestos) {
        if (repuestos != null) {
            _repuestos.setValue(new ArrayList<>(repuestos)); // Usamos new ArrayList para evitar problemas de referencia
        }
    }

    public void addRepuesto(String nombre, String codigo, String cantidadStr) {
        if (nombre == null || nombre.trim().isEmpty() ||
            codigo == null || codigo.trim().isEmpty() ||
            cantidadStr == null || cantidadStr.trim().isEmpty()) {
            // Podrías emitir un evento de error aquí para notificar a la UI
            return;
        }

        try {
            int cantidad = Integer.parseInt(cantidadStr);
            if (cantidad <= 0) return; // No añadir si la cantidad es cero o negativa

            Repuesto nuevoRepuesto = new Repuesto(nombre.trim(), codigo.trim(), cantidad);

            List<Repuesto> currentList = _repuestos.getValue();
            if (currentList != null) {
                currentList.add(nuevoRepuesto);
                _repuestos.setValue(currentList); // Notificar a los observadores
            }
        } catch (NumberFormatException e) {
            // Manejar error de conversión de número
        }
    }

    public void removeRepuesto(Repuesto repuesto) {
        List<Repuesto> currentList = _repuestos.getValue();
        if (currentList != null) {
            currentList.remove(repuesto);
            _repuestos.setValue(currentList);
        }
    }
}
