package com.example.proyectoandroid.ui.maquinaria;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.data.MaquinariaRepository;

import java.util.ArrayList;
import java.util.List;

public class MaquinariaViewModel extends ViewModel {

    private final MaquinariaRepository repository;

    // LiveData para la lista de maquinarias (pobla el Spinner)
    private final MutableLiveData<List<Maquinaria>> _maquinarias = new MutableLiveData<>();
    public final LiveData<List<Maquinaria>> maquinarias = _maquinarias;

    // LiveData para las partes de la maquinaria seleccionada (actualiza el RecyclerView)
    private final MutableLiveData<List<String>> _partesSeleccionadas = new MutableLiveData<>();
    public final LiveData<List<String>> partesSeleccionadas = _partesSeleccionadas;

    // LiveData para controlar la visibilidad de los botones de acción (modificar, eliminar)
    private final MutableLiveData<Boolean> _botonesDeAccionVisibles = new MutableLiveData<>(false);
    public final LiveData<Boolean> botonesDeAccionVisibles = _botonesDeAccionVisibles;

    public MaquinariaViewModel() {
        repository = new MaquinariaRepository();
        cargarMaquinarias();
    }

    private void cargarMaquinarias() {
        List<Maquinaria> data = repository.getMaquinarias();
        _maquinarias.setValue(data);
    }

    public void onMaquinaSeleccionada(int position) {
        if (position > 0 && _maquinarias.getValue() != null) {
            // Adjust position because the first item is a placeholder
            Maquinaria maquina = _maquinarias.getValue().get(position - 1);
            _partesSeleccionadas.setValue(maquina.getPartes());
            _botonesDeAccionVisibles.setValue(true);
        } else {
            // No maquinaria selected or placeholder selected
            _partesSeleccionadas.setValue(new ArrayList<>());
            _botonesDeAccionVisibles.setValue(false);
        }
    }

    public void eliminarMaquinaSeleccionada(int position) {
        if (position > 0 && _maquinarias.getValue() != null) {
            List<Maquinaria> listaActual = new ArrayList<>(_maquinarias.getValue());
            // Adjust position to remove the correct item
            listaActual.remove(position - 1);
            repository.eliminarMaquinaria(listaActual.get(position - 2)); // Assuming repository needs the item to delete
            _maquinarias.setValue(listaActual);
            // After deletion, reset selection and hide buttons
            onMaquinaSeleccionada(0);
        }
    }
}
