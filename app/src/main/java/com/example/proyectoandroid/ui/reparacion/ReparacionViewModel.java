package com.example.proyectoandroid.ui.reparacion;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.proyectoandroid.data.MaquinariaRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReparacionViewModel extends AndroidViewModel {

    private final MaquinariaRepository maquinariaRepository;

    private final MutableLiveData<List<String>> _maquinarias = new MutableLiveData<>();
    public LiveData<List<String>> getMaquinarias() {
        return _maquinarias;
    }

    private final MutableLiveData<String> _selectedDate = new MutableLiveData<>();
    public LiveData<String> getSelectedDate() {
        return _selectedDate;
    }

    private final MutableLiveData<int[]> _showDatePickerEvent = new MutableLiveData<>();
    public LiveData<int[]> getShowDatePickerEvent() {
        return _showDatePickerEvent;
    }

    private final MutableLiveData<Void> _navigateToRepuestoFragmentEvent = new MutableLiveData<>();
    public LiveData<Void> getNavigateToRepuestoFragmentEvent() {
        return _navigateToRepuestoFragmentEvent;
    }

    public ReparacionViewModel(@NonNull Application application) {
        super(application);
        maquinariaRepository = new MaquinariaRepository();
    }

    public void loadMaquinarias() {
        List<String> nombresMaquinaria = new ArrayList<>();
        // Aquí se simulan los datos, en un caso real se obtendrían del repositorio
        nombresMaquinaria.add("Excavadora hidráulica");
        nombresMaquinaria.add("Perforadora jumbo");
        nombresMaquinaria.add("Camión");
        nombresMaquinaria.add("Bulldozer (Topadora)");
        _maquinarias.setValue(nombresMaquinaria);
    }

    public void onMaquinariaSelected(int position) {
        // Manejar la selección de maquinaria, si es necesario almacenar en ViewModel
    }

    public void onFechaClicked() {
        Calendar calendar = Calendar.getInstance();
        _showDatePickerEvent.setValue(new int[]{calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)});
    }

    public void onDateSelected(String date) {
        _selectedDate.setValue(date);
    }

    public void onRepuestoButtonClicked() {
        _navigateToRepuestoFragmentEvent.setValue(null);
    }
}
