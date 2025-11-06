package com.example.proyectoandroid.ui.maquinaria;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.proyectoandroid.data.MaquinariaRepository;
import com.example.proyectoandroid.model.Maquinaria;

import java.util.ArrayList;
import java.util.List;

public class MaquinariaViewModel extends ViewModel {

    private static final String TAG = "MaquinariaViewModel";
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
        Log.d(TAG, "ViewModel inicializado, LiveData de maquinarias creado.");
    }

    public LiveData<List<Maquinaria>> getMaquinarias() {
        return maquinarias;
    }

    public void onMaquinaSeleccionada(Maquinaria maquina) {
        if (maquina != null) {
            Log.d(TAG, "onMaquinaSeleccionada: " + maquina.getNombre());
        } else {
            Log.d(TAG, "onMaquinaSeleccionada: nulo");
        }
        _maquinaSeleccionada.setValue(maquina);
    }

    public void eliminarMaquinaSeleccionada(Maquinaria maquinaAEliminar) {
        if (maquinaAEliminar != null) {
            Log.d(TAG, "eliminarMaquinaSeleccionada llamada para: " + maquinaAEliminar.getNombre());
            repository.eliminarMaquinaria(maquinaAEliminar.getDocumentId());
            onMaquinaSeleccionada(null); // Limpia la selección
        } else {
            Log.w(TAG, "eliminarMaquinaSeleccionada llamada con maquinaria nula.");
        }
    }

    public void eliminarParte(int position) {
        Maquinaria maquinaActual = _maquinaSeleccionada.getValue();
        if (maquinaActual != null && maquinaActual.getPartesPrincipales() != null && position < maquinaActual.getPartesPrincipales().size()) {
            String parteEliminada = maquinaActual.getPartesPrincipales().get(position);
            Log.d(TAG, "Intentando eliminar la parte '" + parteEliminada + "' en la posición " + position);
            maquinaActual.getPartesPrincipales().remove(position);
            repository.actualizarMaquinaria(maquinaActual, success -> {
                if(success) {
                    Log.d(TAG, "Parte eliminada y maquinaria actualizada con éxito.");
                } else {
                    Log.e(TAG, "Falló la actualización de la maquinaria después de eliminar la parte.");
                }
            });
        } else {
            Log.w(TAG, "eliminarParte falló: Estado o posición inválidos.");
        }
    }

    public void actualizarParte(int position, String nuevoNombre) {
        Maquinaria maquinaActual = _maquinaSeleccionada.getValue();
        if (maquinaActual != null && maquinaActual.getPartesPrincipales() != null && position < maquinaActual.getPartesPrincipales().size()) {
            if (nuevoNombre != null && !nuevoNombre.trim().isEmpty()) {
                String parteAntigua = maquinaActual.getPartesPrincipales().get(position);
                Log.d(TAG, "Intentando actualizar la parte '" + parteAntigua + "' a '" + nuevoNombre + "'");
                maquinaActual.getPartesPrincipales().set(position, nuevoNombre);
                repository.actualizarMaquinaria(maquinaActual, success -> {
                    if(success) {
                        Log.d(TAG, "Parte actualizada y maquinaria actualizada con éxito.");
                    } else {
                        Log.e(TAG, "Falló la actualización de la maquinaria después de actualizar la parte.");
                    }
                }); 
            }
        } else {
            Log.w(TAG, "actualizarParte falló: Estado o posición inválidos.");
        }
    }
}
