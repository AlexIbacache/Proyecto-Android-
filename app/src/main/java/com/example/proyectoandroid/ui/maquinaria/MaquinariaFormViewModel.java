package com.example.proyectoandroid.ui.maquinaria;

import android.net.Uri;
import android.util.Log;
import androidx.annotation.Nullable;
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

    public void guardarMaquinaria(@Nullable String maquinariaId, String nombre, String numeroIdentificador, String descripcion, List<String> partes, @Nullable Uri photoUri) {
        Log.d(TAG, "Iniciando proceso de guardado para: " + nombre);
        if (nombre == null || nombre.trim().isEmpty() || numeroIdentificador == null || numeroIdentificador.trim().isEmpty() || _fechaIngresoCalendar.getValue() == null) {
            Log.e(TAG, "Validación fallida. Faltan campos requeridos.");
            _saveMaquinariaEvent.setValue(false);
            return;
        }

        final boolean isUpdating = maquinariaId != null;
        final String finalMaquinariaId = isUpdating ? maquinariaId : repository.getNewMaquinariaId();

        Maquinaria maquinariaParaGuardar;
        if (isUpdating) {
            maquinariaParaGuardar = _maquinariaCargada.getValue();
        } else {
            maquinariaParaGuardar = new Maquinaria();
            maquinariaParaGuardar.setDocumentId(finalMaquinariaId);
            maquinariaParaGuardar.setEstado(false);
        }

        maquinariaParaGuardar.setNombre(nombre);
        maquinariaParaGuardar.setNumeroIdentificador(numeroIdentificador);
        maquinariaParaGuardar.setDescripcion(descripcion);
        maquinariaParaGuardar.setPartesPrincipales(partes);
        maquinariaParaGuardar.setFechaIngreso(new Timestamp(_fechaIngresoCalendar.getValue().getTime()));

        if (photoUri != null) {
            repository.subirFotoMaquinaria(finalMaquinariaId, photoUri, new MaquinariaRepository.UploadImageCallback() {
                @Override
                public void onImageUploaded(String imageUrl) {
                    maquinariaParaGuardar.setImagenUrl(imageUrl);
                    saveOrUpdate(maquinariaParaGuardar, isUpdating);
                }

                @Override
                public void onUploadFailed(Exception e) {
                    Log.e(TAG, "Error al subir la imagen", e);
                    _saveMaquinariaEvent.setValue(false);
                }
            });
        } else {
            if (isUpdating) {
                maquinariaParaGuardar.setImagenUrl(_maquinariaCargada.getValue().getImagenUrl());
            }
            saveOrUpdate(maquinariaParaGuardar, isUpdating);
        }
    }

    private void saveOrUpdate(Maquinaria maquinaria, boolean isUpdating) {
        if (isUpdating) {
            repository.actualizarMaquinaria(maquinaria, success -> _saveMaquinariaEvent.postValue(success));
        } else {
            repository.guardarMaquinaria(maquinaria, success -> _saveMaquinariaEvent.postValue(success));
        }
    }
}
