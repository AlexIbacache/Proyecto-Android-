package com.example.proyectoandroid.ui.reportes;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.Transformations;

import com.example.proyectoandroid.data.MaquinariaRepository;
import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.model.Reparacion;
import com.example.proyectoandroid.util.SingleLiveEvent;

import java.util.List;

public class ReportesViewModel extends ViewModel {

    private static final String TAG = "ReportesViewModel";
    private final MaquinariaRepository maquinariaRepository;
    private final SingleLiveEvent<String> _showToastEvent = new SingleLiveEvent<>();
    public LiveData<String> getShowToastEvent() { return _showToastEvent; }

    private LiveData<List<Maquinaria>> maquinariaList;
    private final MutableLiveData<Maquinaria> selectedMaquinaria = new MutableLiveData<>();
    private final LiveData<List<Reparacion>> reparacionesDeMaquina;

    public ReportesViewModel() {
        maquinariaRepository = new MaquinariaRepository();
        Log.d(TAG, "ViewModel inicializado. Obteniendo la lista de maquinaria.");
        maquinariaList = maquinariaRepository.getMaquinariaList();

        reparacionesDeMaquina = Transformations.switchMap(selectedMaquinaria, maquina -> {
            if (maquina == null) {
                Log.d(TAG, "No se ha seleccionado ninguna maquinaria, se devuelve un LiveData vacío para las reparaciones.");
                return new MutableLiveData<>(); // Return empty LiveData
            }
            Log.d(TAG, "Maquinaria seleccionada: " + maquina.getNombre() + ". Obteniendo sus reparaciones.");
            return maquinariaRepository.getReparacionesDeMaquina(maquina.getDocumentId());
        });
    }

    public LiveData<List<Maquinaria>> getMaquinariaList() {
        return maquinariaList;
    }

    public void selectMaquinaria(Maquinaria maquinaria) {
        if (maquinaria != null) {
            Log.d(TAG, "selectMaquinaria llamada para: " + maquinaria.getNombre());
        } else {
            Log.d(TAG, "selectMaquinaria llamada con nulo.");
        }
        selectedMaquinaria.setValue(maquinaria);
    }

    public LiveData<Maquinaria> getSelectedMaquinaria() {
        return selectedMaquinaria;
    }

    public LiveData<List<Reparacion>> getReparacionesDeMaquina() {
        return reparacionesDeMaquina;
    }

    public void onExportExcelClicked() {
        Log.d(TAG, "onExportExcelClicked llamado.");
        // Lógica para exportar a Excel
        _showToastEvent.setValue("Función próximamente: Exportar Excel");
    }
}
