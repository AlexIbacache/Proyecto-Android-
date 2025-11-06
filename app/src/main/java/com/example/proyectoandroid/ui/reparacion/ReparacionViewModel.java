package com.example.proyectoandroid.ui.reparacion;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.data.MaquinariaRepository;
import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.model.ParteReparada;
import com.example.proyectoandroid.model.Reparacion;
import com.example.proyectoandroid.model.Repuesto;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReparacionViewModel extends AndroidViewModel {

    private final MaquinariaRepository maquinariaRepository;
    private final LiveData<List<Maquinaria>> maquinarias;
    private final MutableLiveData<List<String>> partesMaquinaria = new MutableLiveData<>();
    private final Map<String, List<Repuesto>> repuestosPorParte = new HashMap<>();

    private Maquinaria maquinaSeleccionada;

    private final MutableLiveData<String> _selectedDate = new MutableLiveData<>();
    public LiveData<String> getSelectedDate() {
        return _selectedDate;
    }

    private final MutableLiveData<int[]> _showDatePickerEvent = new MutableLiveData<>();
    public LiveData<int[]> getShowDatePickerEvent() {
        return _showDatePickerEvent;
    }

    private final MutableLiveData<Boolean> _reparacionGuardadaState = new MutableLiveData<>();
    public LiveData<Boolean> getReparacionGuardadaState() {
        return _reparacionGuardadaState;
    }

    public ReparacionViewModel(@NonNull Application application) {
        super(application);
        maquinariaRepository = new MaquinariaRepository();
        maquinarias = maquinariaRepository.getMaquinariaList();
    }

    public LiveData<List<Maquinaria>> getMaquinarias() {
        return maquinarias;
    }

    public LiveData<List<String>> getPartesMaquinaria() {
        return partesMaquinaria;
    }

    public void onMaquinariaSelected(Maquinaria maquinaria) {
        this.maquinaSeleccionada = maquinaria;
        if (maquinaria != null && maquinaria.getPartesPrincipales() != null) {
            partesMaquinaria.setValue(maquinaria.getPartesPrincipales());
        } else {
            partesMaquinaria.setValue(new ArrayList<>());
        }
        repuestosPorParte.clear();
    }

    public void onFechaClicked() {
        Calendar calendar = Calendar.getInstance();
        _showDatePickerEvent.setValue(new int[]{calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)});
    }

    public void onDateSelected(String date) {
        _selectedDate.setValue(date);
    }

    public void actualizarRepuestosParaParte(String parte, List<Repuesto> repuestos) {
        repuestosPorParte.put(parte, repuestos);
    }

    public List<Repuesto> getRepuestosParaParte(String parte) {
        return repuestosPorParte.get(parte);
    }

    public int getCantidadRepuestosParaParte(String parte) {
        List<Repuesto> repuestos = getRepuestosParaParte(parte);
        return repuestos != null ? repuestos.size() : 0;
    }

    public void guardarReparacion(String notas) {
        if (maquinaSeleccionada == null || _selectedDate.getValue() == null || _selectedDate.getValue().isEmpty()) {
            _reparacionGuardadaState.setValue(false); // Datos incompletos
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = sdf.parse(_selectedDate.getValue());
            Timestamp timestamp = new Timestamp(date);

            List<ParteReparada> partesReparadasList = new ArrayList<>();
            for (Map.Entry<String, List<Repuesto>> entry : repuestosPorParte.entrySet()) {
                partesReparadasList.add(new ParteReparada(entry.getKey(), entry.getValue()));
            }

            Reparacion nuevaReparacion = new Reparacion(timestamp, notas, partesReparadasList);

            maquinariaRepository.guardarReparacion(maquinaSeleccionada.getDocumentId(), nuevaReparacion, success -> {
                _reparacionGuardadaState.postValue(success);
            });

        } catch (ParseException e) {
            _reparacionGuardadaState.setValue(false); // Error en formato de fecha
        }
    }

    public void resetSaveState() {
        _reparacionGuardadaState.setValue(null);
    }

    public void limpiarFormulario() {
        maquinaSeleccionada = null;
        _selectedDate.setValue(null);
        partesMaquinaria.setValue(new ArrayList<>());
        repuestosPorParte.clear();
    }
}
