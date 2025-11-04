package com.example.proyectoandroid.ui.maquinaria;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectoandroid.data.MaquinariaRepository;
import com.example.proyectoandroid.util.SingleLiveEvent;

import java.util.Calendar;
import java.util.Locale;

public class MaquinariaFormViewModel extends ViewModel {

    private final MaquinariaRepository repository;
    private static final int MAX_PARTES = 10;

    private final MutableLiveData<String> _fechaIngreso = new MutableLiveData<>();
    public LiveData<String> fechaIngreso = _fechaIngreso;

    private final MutableLiveData<Integer> _partesCount = new MutableLiveData<>(0);
    public LiveData<Integer> partesCount = _partesCount;

    // Eventos para la comunicación con la Vista
    private final SingleLiveEvent<Void> _addParteViewEvent = new SingleLiveEvent<>();
    public LiveData<Void> getAddParteViewEvent() { return _addParteViewEvent; }

    private final SingleLiveEvent<Void> _showMaxPartesToastEvent = new SingleLiveEvent<>();
    public LiveData<Void> getShowMaxPartesToastEvent() { return _showMaxPartesToastEvent; }

    private final SingleLiveEvent<Boolean> _saveMaquinariaEvent = new SingleLiveEvent<>();
    public LiveData<Boolean> getSaveMaquinariaEvent() { return _saveMaquinariaEvent; }

    public MaquinariaFormViewModel() {
        this.repository = new MaquinariaRepository();
    }

    public void onFechaIngresoClicked() {
        // La lógica para mostrar el diálogo está en la vista.
        // El ViewModel puede proporcionar datos iniciales si es necesario.
    }

    public void setFechaIngreso(int year, int month, int dayOfMonth) {
        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
        _fechaIngreso.setValue(selectedDate);
    }

    public void agregarParte() {
        int currentPartes = _partesCount.getValue() != null ? _partesCount.getValue() : 0;
        if (currentPartes < MAX_PARTES) {
            _partesCount.setValue(currentPartes + 1);
            _addParteViewEvent.call(); // Dispara el evento para que el Fragment cree la vista
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

    public void guardarMaquinaria(/* Aquí recibirías los datos de la maquinaria desde el Fragment */) {
        // Lógica para recolectar datos de las vistas y guardar en el repositorio...
        // Por ahora, simulamos un guardado exitoso.
        _saveMaquinariaEvent.setValue(true);
    }
}
