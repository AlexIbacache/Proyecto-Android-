package com.example.proyectoandroid.ui.reparacion;

import android.app.Application;
import android.util.Log;

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

    private static final String TAG = "ReparacionViewModel";
    private final MaquinariaRepository maquinariaRepository;
    private final LiveData<List<Maquinaria>> maquinarias;
    private final Map<String, List<Repuesto>> repuestosPorParte = new HashMap<>();

    private Maquinaria maquinaSeleccionada;
    private Reparacion reparacionEnEdicion;

    private final MutableLiveData<String> _selectedDate = new MutableLiveData<>();
    public LiveData<String> getSelectedDate() { return _selectedDate; }

    private final MutableLiveData<int[]> _showDatePickerEvent = new MutableLiveData<>();
    public LiveData<int[]> getShowDatePickerEvent() { return _showDatePickerEvent; }

    private final MutableLiveData<Boolean> _reparacionGuardadaState = new MutableLiveData<>();
    public LiveData<Boolean> getReparacionGuardadaState() { return _reparacionGuardadaState; }

    private final MutableLiveData<Boolean> _reparacionFinalizadaState = new MutableLiveData<>();
    public LiveData<Boolean> getReparacionFinalizadaState() { return _reparacionFinalizadaState; }

    private final MutableLiveData<String> _maquinaASeleccionar = new MutableLiveData<>();
    public LiveData<String> getMaquinaASeleccionar() { return _maquinaASeleccionar; }

    private final MutableLiveData<String> _notasEdicion = new MutableLiveData<>();
    public LiveData<String> getNotasEdicion() { return _notasEdicion; }

    private final MutableLiveData<List<String>> partesMaquinaria = new MutableLiveData<>();

    public ReparacionViewModel(@NonNull Application application) {
        super(application);
        maquinariaRepository = new MaquinariaRepository();
        maquinarias = maquinariaRepository.getMaquinariaList();
        Log.d(TAG, "ViewModel inicializado");
    }

    public boolean isEditMode() {
        return reparacionEnEdicion != null;
    }

    public LiveData<List<Maquinaria>> getMaquinarias() { return maquinarias; }

    public LiveData<List<String>> getPartesMaquinaria() { return partesMaquinaria; }

    public void onMaquinariaSelected(Maquinaria maquinaria) {
        this.maquinaSeleccionada = maquinaria;
        if (maquinaria == null) {
            Log.d(TAG, "Selección de maquinaria borrada.");
            limpiarFormulario();
            return;
        }
        Log.d(TAG, "Maquinaria seleccionada: " + maquinaria.getNombre() + ". Comprobando si hay reparaciones abiertas.");
        maquinariaRepository.getReparacionAbierta(maquinaria.getDocumentId(), reparacion -> {
            if (reparacion != null) {
                Log.d(TAG, "Reparación abierta encontrada, cargando para editar.");
                cargarReparacionParaEdicion(reparacion, maquinaria);
            } else {
                Log.d(TAG, "No se encontraron reparaciones abiertas, preparando para una nueva reparación.");
                limpiarParaNuevaReparacion(maquinaria);
            }
        });
    }

    public void cargarReparacionParaEdicion(Reparacion reparacion, Maquinaria maquinaria) {
        Log.d(TAG, "cargarReparacionParaEdicion llamado para el ID de reparación: " + reparacion.getDocumentId());
        this.reparacionEnEdicion = reparacion;
        this.maquinaSeleccionada = maquinaria;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        _selectedDate.postValue(sdf.format(reparacion.getFecha().toDate()));
        _notasEdicion.postValue(reparacion.getNotas());

        repuestosPorParte.clear();
        if (reparacion.getPartesReparadas() != null) {
            Log.d(TAG, "Cargando " + reparacion.getPartesReparadas().size() + " partes reparadas.");
            for (ParteReparada parte : reparacion.getPartesReparadas()) {
                repuestosPorParte.put(parte.getNombreParte(), new ArrayList<>(parte.getRepuestos()));
            }
        }
        _maquinaASeleccionar.postValue(maquinaria.getDocumentId());
        partesMaquinaria.postValue(maquinaria.getPartesPrincipales());
    }

    private void limpiarParaNuevaReparacion(Maquinaria maquinaria) {
        Log.d(TAG, "limpiarParaNuevaReparacion para la máquina: " + maquinaria.getNombre());
        this.reparacionEnEdicion = null;
        this.maquinaSeleccionada = maquinaria;
        _selectedDate.postValue(null);
        _notasEdicion.postValue(null);
        partesMaquinaria.postValue(maquinaria.getPartesPrincipales());
        repuestosPorParte.clear();
    }

    public void onFechaClicked() {
        Log.d(TAG, "onFechaClicked llamado");
        Calendar calendar = Calendar.getInstance();
        _showDatePickerEvent.setValue(new int[]{calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)});
    }

    public void onDateSelected(String date) { 
        Log.d(TAG, "onDateSelected: " + date);
        _selectedDate.setValue(date); 
    }

    public void actualizarRepuestosParaParte(String parte, List<Repuesto> repuestos) {
        Log.d(TAG, "actualizarRepuestosParaParte para la parte '" + parte + "' con " + repuestos.size() + " repuestos.");
        repuestosPorParte.put(parte, repuestos);
    }

    public List<Repuesto> getRepuestosParaParte(String parte) {
        return repuestosPorParte.get(parte);
    }

    public void guardarReparacion(String notas) {
        Log.d(TAG, "guardarReparacion llamado.");
        if (maquinaSeleccionada == null || _selectedDate.getValue() == null || _selectedDate.getValue().isEmpty()) {
            Log.e(TAG, "Falló el guardado: No se seleccionó máquina o fecha.");
            _reparacionGuardadaState.setValue(false);
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
            Log.d(TAG, "Guardando con " + partesReparadasList.size() + " partes reparadas.");

            if (reparacionEnEdicion != null) {
                Log.d(TAG, "Actualizando reparación existente con ID: " + reparacionEnEdicion.getDocumentId());
                reparacionEnEdicion.setFecha(timestamp);
                reparacionEnEdicion.setNotas(notas);
                reparacionEnEdicion.setPartesReparadas(partesReparadasList);

                maquinariaRepository.actualizarReparacion(maquinaSeleccionada.getDocumentId(), reparacionEnEdicion, success -> {
                    Log.d(TAG, "Resultado de actualizarReparacion: " + success);
                    _reparacionGuardadaState.postValue(success);
                });
            } else {
                Log.d(TAG, "Creando nueva reparación.");
                Reparacion nuevaReparacion = new Reparacion(timestamp, notas, partesReparadasList);
                maquinariaRepository.guardarReparacion(maquinaSeleccionada.getDocumentId(), nuevaReparacion, success -> {
                    Log.d(TAG, "Resultado de guardarReparacion: " + success);
                    if (success) {
                        maquinaSeleccionada.setEstado(false); // En Reparación
                        maquinariaRepository.actualizarMaquinaria(maquinaSeleccionada, updateSuccess -> _reparacionGuardadaState.postValue(updateSuccess));
                    } else {
                        _reparacionGuardadaState.postValue(false);
                    }
                });
            }

        } catch (ParseException e) {
            Log.e(TAG, "Error al analizar la fecha para guardar.", e);
            _reparacionGuardadaState.setValue(false);
        }
    }

    public void finalizarReparacion() {
        Log.d(TAG, "finalizarReparacion llamado.");
        if (reparacionEnEdicion != null && maquinaSeleccionada != null) {
            Log.d(TAG, "Finalizando reparación con ID: " + reparacionEnEdicion.getDocumentId());
            reparacionEnEdicion.setEstado("Cerrada");
            maquinariaRepository.actualizarReparacion(maquinaSeleccionada.getDocumentId(), reparacionEnEdicion, success -> {
                Log.d(TAG, "Resultado de finalizarReparacion: " + success);
                if (success) {
                    maquinaSeleccionada.setEstado(true); // Operativa
                    maquinariaRepository.actualizarMaquinaria(maquinaSeleccionada, updateSuccess -> _reparacionFinalizadaState.postValue(updateSuccess));
                } else {
                    _reparacionFinalizadaState.postValue(false);
                }
            });
        } else {
            Log.w(TAG, "Falló la finalización de la reparación: No hay reparación en modo de edición o no se ha seleccionado ninguna máquina.");
        }
    }

    public void resetSaveState() {
        Log.d(TAG, "resetSaveState llamado");
        _reparacionGuardadaState.setValue(null);
        _reparacionFinalizadaState.setValue(null);
    }

    public void limpiarFormulario() {
        Log.d(TAG, "limpiarFormulario llamado");
        maquinaSeleccionada = null;
        reparacionEnEdicion = null;
        _selectedDate.setValue(null);
        _notasEdicion.setValue(null);
        partesMaquinaria.setValue(new ArrayList<>());
        repuestosPorParte.clear();
        _maquinaASeleccionar.setValue(null);
    }
}
