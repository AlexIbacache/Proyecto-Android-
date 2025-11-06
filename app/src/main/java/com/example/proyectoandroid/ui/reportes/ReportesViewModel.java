package com.example.proyectoandroid.ui.reportes;

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

    private final MaquinariaRepository maquinariaRepository;
    private final SingleLiveEvent<String> _showToastEvent = new SingleLiveEvent<>();
    public LiveData<String> getShowToastEvent() { return _showToastEvent; }

    private LiveData<List<Maquinaria>> maquinariaList;
    private final MutableLiveData<Maquinaria> selectedMaquinaria = new MutableLiveData<>();
    private final LiveData<List<Reparacion>> reparacionesDeMaquina;

    public ReportesViewModel() {
        maquinariaRepository = new MaquinariaRepository();
        maquinariaList = maquinariaRepository.getMaquinariaList();

        reparacionesDeMaquina = Transformations.switchMap(selectedMaquinaria, maquina -> {
            if (maquina == null) {
                return new MutableLiveData<>(); // Return empty LiveData
            }
            return maquinariaRepository.getReparacionesDeMaquina(maquina.getDocumentId());
        });
    }

    public LiveData<List<Maquinaria>> getMaquinariaList() {
        return maquinariaList;
    }

    public void selectMaquinaria(Maquinaria maquinaria) {
        selectedMaquinaria.setValue(maquinaria);
    }

    public LiveData<Maquinaria> getSelectedMaquinaria() {
        return selectedMaquinaria;
    }

    public LiveData<List<Reparacion>> getReparacionesDeMaquina() {
        return reparacionesDeMaquina;
    }

    public void onExportExcelClicked() {
        // Lógica para exportar a Excel
        _showToastEvent.setValue("Función próximamente: Exportar Excel");
    }
}
