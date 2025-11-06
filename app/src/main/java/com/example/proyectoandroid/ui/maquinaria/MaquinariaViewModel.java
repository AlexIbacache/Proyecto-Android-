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

    // LiveData para la lista de maquinarias, obtenida directamente del repositorio
    public final LiveData<List<Maquinaria>> maquinarias;

    // LiveData para las partes de la maquinaria seleccionada (actualiza el RecyclerView)
    private final MutableLiveData<List<String>> _partesSeleccionadas = new MutableLiveData<>();
    public final LiveData<List<String>> partesSeleccionadas = _partesSeleccionadas;

    // LiveData para controlar la visibilidad de los botones de acción (modificar, eliminar)
    private final MutableLiveData<Boolean> _botonesDeAccionVisibles = new MutableLiveData<>(false);
    public final LiveData<Boolean> botonesDeAccionVisibles = _botonesDeAccionVisibles;

    public MaquinariaViewModel() {
        repository = new MaquinariaRepository();
        // Se asigna directamente el LiveData del repositorio. La UI observará este LiveData.
        maquinarias = repository.getMaquinariaList();
    }

    public void onMaquinaSeleccionada(int position) {
        List<Maquinaria> listaMaquinarias = maquinarias.getValue();
        // La posición 0 es el hint "Seleccione una maquinaria"
        if (position > 0 && listaMaquinarias != null && (position - 1) < listaMaquinarias.size()) {
            Maquinaria maquina = listaMaquinarias.get(position - 1);
            // Usar getPartesPrincipales() en lugar de getPartes()
            _partesSeleccionadas.setValue(maquina.getPartesPrincipales() != null ? maquina.getPartesPrincipales() : new ArrayList<>());
            _botonesDeAccionVisibles.setValue(true);
        } else {
            // No hay maquinaria seleccionada o se seleccionó el placeholder
            _partesSeleccionadas.setValue(new ArrayList<>());
            _botonesDeAccionVisibles.setValue(false);
        }
    }

    public void eliminarMaquinaSeleccionada(int position) {
        List<Maquinaria> listaMaquinarias = maquinarias.getValue();
         // La posición 0 es el hint "Seleccione una maquinaria"
        if (position > 0 && listaMaquinarias != null && (position - 1) < listaMaquinarias.size()) {
            Maquinaria maquinaAEliminar = listaMaquinarias.get(position - 1);
            // Llamar al repositorio con el ID de la máquina
            repository.eliminarMaquinaria(maquinaAEliminar.getId());
            // La lista en la UI se actualizará automáticamente gracias al listener de Firestore que está en el repositorio.
            // Solo necesitamos resetear la selección.
            onMaquinaSeleccionada(0);
        }
    }
}
