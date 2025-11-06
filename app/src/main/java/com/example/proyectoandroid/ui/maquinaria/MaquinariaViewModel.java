package com.example.proyectoandroid.ui.maquinaria;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.proyectoandroid.data.MaquinariaRepository;
import com.example.proyectoandroid.model.Maquinaria;

import java.util.ArrayList;
import java.util.List;

public class MaquinariaViewModel extends ViewModel {

    private final MaquinariaRepository repository;
    private final LiveData<List<Maquinaria>> maquinarias;

    // Guarda la máquina completa que se ha seleccionado en el Spinner
    private final MutableLiveData<Maquinaria> _maquinaSeleccionada = new MutableLiveData<>();

    // Transforma la máquina seleccionada en su lista de partes
    public final LiveData<List<String>> partesSeleccionadas = Transformations.map(_maquinaSeleccionada, maquina -> 
        maquina != null ? maquina.getPartesPrincipales() : new ArrayList<>()
    );

    // Transforma la selección en la visibilidad de los botones
    public final LiveData<Boolean> botonesDeAccionVisibles = Transformations.map(_maquinaSeleccionada, maquina -> maquina != null);

    public MaquinariaViewModel() {
        repository = new MaquinariaRepository();
        maquinarias = repository.getMaquinariaList();
    }

    public LiveData<List<Maquinaria>> getMaquinarias() {
        return maquinarias;
    }

    public void onMaquinaSeleccionada(Maquinaria maquina) {
        _maquinaSeleccionada.setValue(maquina);
    }

    public void eliminarMaquinaSeleccionada(Maquinaria maquinaAEliminar) {
        if (maquinaAEliminar != null) {
            repository.eliminarMaquinaria(maquinaAEliminar.getId());
            onMaquinaSeleccionada(null); // Limpia la selección
        }
    }

    public void eliminarParte(int position) {
        Maquinaria maquinaActual = _maquinaSeleccionada.getValue();
        if (maquinaActual != null && maquinaActual.getPartesPrincipales() != null && position < maquinaActual.getPartesPrincipales().size()) {
            maquinaActual.getPartesPrincipales().remove(position);
            repository.actualizarMaquinaria(maquinaActual, success -> {}); // Actualiza en Firestore
        }
    }

    public void actualizarParte(int position, String nuevoNombre) {
        Maquinaria maquinaActual = _maquinaSeleccionada.getValue();
        if (maquinaActual != null && maquinaActual.getPartesPrincipales() != null && position < maquinaActual.getPartesPrincipales().size()) {
            if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
                maquinaActual.getPartesPrincipales().set(position, nuevoNombre);
                repository.actualizarMaquinaria(maquinaActual, success -> {}); // Actualiza en Firestore
            }
        }
    }
}
