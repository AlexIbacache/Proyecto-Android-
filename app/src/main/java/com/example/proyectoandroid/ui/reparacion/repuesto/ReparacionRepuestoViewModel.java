package com.example.proyectoandroid.ui.reparacion.repuesto;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ReparacionRepuestoViewModel extends ViewModel {

    private final MutableLiveData<Void> _showAgregarRepuestoDialogEvent = new MutableLiveData<>();
    public LiveData<Void> getShowAgregarRepuestoDialogEvent() {
        return _showAgregarRepuestoDialogEvent;
    }

    public void onAgregarRepuestoClicked() {
        _showAgregarRepuestoDialogEvent.setValue(null);
    }

    // Aquí iría la lógica para obtener y manejar la lista de repuestos,
    // así como la lógica para agregar, editar y eliminar repuestos.
}
