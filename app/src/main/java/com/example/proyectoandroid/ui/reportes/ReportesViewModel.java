package com.example.proyectoandroid.ui.reportes;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectoandroid.util.SingleLiveEvent;

public class ReportesViewModel extends ViewModel {

    private final SingleLiveEvent<String> _showToastEvent = new SingleLiveEvent<>();
    public LiveData<String> getShowToastEvent() { return _showToastEvent; }

    public void onExportExcelClicked() {
        // Lógica para exportar a Excel
        _showToastEvent.setValue("Función próximamente: Exportar Excel");
    }
}
