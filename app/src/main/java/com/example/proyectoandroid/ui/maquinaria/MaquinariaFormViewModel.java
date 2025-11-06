package com.example.proyectoandroid.ui.maquinaria;

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
    }

    public void cargarMaquinaria(String maquinariaId) {
        repository.getMaquinariaById(maquinariaId).observeForever(maquinaria -> {
            _maquinariaCargada.setValue(maquinaria);
            if (maquinaria != null && maquinaria.getFechaIngreso() != null) {
                Calendar cal = Calendar.getInstance();
                cal.setTime(maquinaria.getFechaIngreso().toDate());
                setFechaIngreso(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            }
        });
    }

    public void setFechaIngreso(int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        _fechaIngresoCalendar.setValue(calendar);

        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
        _fechaIngreso.setValue(selectedDate);
    }

    public void agregarParte() {
        int currentPartes = _partesCount.getValue() != null ? _partesCount.getValue() : 0;
        if (currentPartes < MAX_PARTES) {
            _partesCount.setValue(currentPartes + 1);
            _addParteViewEvent.call();
        } else {
            _showMaxPartesToastEvent.call();
        }
    }

    public void removerParte() {
        int currentPartes = _partesCount.getValue() != null ? _partesCount.getValue() : 0;
        if (currentPartes > 0) {
            _partesCount.setValue(currentPartes - 1);
        }
    }

    public void guardarMaquinaria(String nombre, String numeroIdentificador, String descripcion, List<String> partes) {
        if (nombre == null || nombre.trim().isEmpty() || numeroIdentificador == null || numeroIdentificador.trim().isEmpty() || _fechaIngresoCalendar.getValue() == null) {
            _saveMaquinariaEvent.setValue(false);
            return;
        }

        Maquinaria maquinariaParaGuardar;
        boolean isUpdating = _maquinariaCargada.getValue() != null;

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
            repository.actualizarMaquinaria(maquinariaParaGuardar, success -> _saveMaquinariaEvent.postValue(success));
        } else {
            repository.guardarMaquinaria(maquinariaParaGuardar, success -> _saveMaquinariaEvent.postValue(success));
        }
    }
}
