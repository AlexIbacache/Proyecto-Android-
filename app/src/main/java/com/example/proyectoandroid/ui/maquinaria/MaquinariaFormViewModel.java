package com.example.proyectoandroid.ui.maquinaria;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectoandroid.data.MaquinariaRepository;
import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.util.SingleLiveEvent;
import com.google.firebase.Timestamp;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MaquinariaFormViewModel extends ViewModel {

    private static final String TAG = "MaquinariaFormVM";
    private final MaquinariaRepository repository;
    private static final int MAX_PARTES = 10;

    // Para la maquinaria que se está editando
    private MutableLiveData<Maquinaria> _maquinariaCargada = new MutableLiveData<>();
    public LiveData<Maquinaria> maquinariaCargada = _maquinariaCargada;

    private final MutableLiveData<Calendar> _fechaIngresoCalendar = new MutableLiveData<>();
    private final MutableLiveData<String> _fechaIngreso = new MutableLiveData<>();
    public LiveData<String> fechaIngreso = _fechaIngreso;

    private final MutableLiveData<Integer> _partesCount = new MutableLiveData<>(0);
    public LiveData<Integer> partesCount = _partesCount;

    private final SingleLiveEvent<Void> _addParteViewEvent = new SingleLiveEvent<>();
    public LiveData<Void> getAddParteViewEvent() { return _addParteViewEvent; }

    private final SingleLiveEvent<Void> _showMaxPartesToastEvent = new SingleLiveEvent<>();
    public LiveData<Void> getShowMaxPartesToastEvent() { return _showMaxPartesToastEvent; }

    private final SingleLiveEvent<Boolean> _saveMaquinariaEvent = new SingleLiveEvent<>();
    public LiveData<Boolean> getSaveMaquinariaEvent() { return _saveMaquinariaEvent; }

    public MaquinariaFormViewModel() {
        this.repository = new MaquinariaRepository();
        Log.d(TAG, "ViewModel creado");
    }

    public void cargarMaquinaria(String maquinariaId) {
        Log.d(TAG, "cargarMaquinaria llamado para el ID: " + maquinariaId);
        repository.getMaquinariaById(maquinariaId).observeForever(maquinaria -> {
            if (maquinaria != null) {
                Log.d(TAG, "Maquinaria cargada: " + maquinaria.getNombre());
                _maquinariaCargada.setValue(maquinaria);
                if (maquinaria.getFechaIngreso() != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(maquinaria.getFechaIngreso().toDate());
                    setFechaIngreso(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
                }
            } else {
                Log.w(TAG, "Maquinaria con ID " + maquinariaId + " no encontrada.");
            }
        });
    }

    public void setFechaIngreso(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        _fechaIngresoCalendar.setValue(calendar);

        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
        Log.d(TAG, "setFechaIngreso: " + selectedDate);
        _fechaIngreso.setValue(selectedDate);
    }

    public void agregarParte() {
        int currentPartes = _partesCount.getValue() != null ? _partesCount.getValue() : 0;
        Log.d(TAG, "agregarParte llamado. Partes actuales: " + currentPartes);
        if (currentPartes < MAX_PARTES) {
            _partesCount.setValue(currentPartes + 1);
            _addParteViewEvent.call();
        } else {
            Log.w(TAG, "No se pueden agregar más partes. Límite alcanzado: " + MAX_PARTES);
            _showMaxPartesToastEvent.call();
        }
    }

    public void removerParte() {
        int currentPartes = _partesCount.getValue() != null ? _partesCount.getValue() : 0;
        Log.d(TAG, "removerParte llamado. Partes actuales: " + currentPartes);
        if (currentPartes > 0) {
            _partesCount.setValue(currentPartes - 1);
        }
    }

    public void guardarMaquinaria(String nombre, String numeroIdentificador, String descripcion, List<String> partes) {
        Log.d(TAG, "guardarMaquinaria llamado para: " + nombre);
        if (nombre == null || nombre.trim().isEmpty() || numeroIdentificador == null || numeroIdentificador.trim().isEmpty() || _fechaIngresoCalendar.getValue() == null) {
            Log.e(TAG, "La validación para guardar la maquinaria falló. Faltan campos requeridos.");
            _saveMaquinariaEvent.setValue(false);
            return;
        }

        Maquinaria maquinariaParaGuardar;
        boolean isUpdating = _maquinariaCargada.getValue() != null;
        Log.d(TAG, isUpdating ? "Actualizando maquinaria existente." : "Guardando nueva maquinaria.");

        if (isUpdating) {
            maquinariaParaGuardar = _maquinariaCargada.getValue();
        } else {
            maquinariaParaGuardar = new Maquinaria();
            maquinariaParaGuardar.setEstado(false); // Estado por defecto para nuevas máquinas
        }

        maquinariaParaGuardar.setNombre(nombre);
        maquinariaParaGuardar.setNumeroIdentificador(numeroIdentificador);
        maquinariaParaGuardar.setDescripcion(descripcion);
        maquinariaParaGuardar.setPartesPrincipales(partes);
        maquinariaParaGuardar.setFechaIngreso(new Timestamp(_fechaIngresoCalendar.getValue().getTime()));

        if (isUpdating) {
            repository.actualizarMaquinaria(maquinariaParaGuardar, success -> {
                Log.d(TAG, "Resultado de actualizarMaquinaria: " + success);
                _saveMaquinariaEvent.postValue(success);
            });
        } else {
            repository.guardarMaquinaria(maquinariaParaGuardar, success -> {
                Log.d(TAG, "Resultado de guardarMaquinaria: " + success);
                _saveMaquinariaEvent.postValue(success);
            });
        }
    }
}
