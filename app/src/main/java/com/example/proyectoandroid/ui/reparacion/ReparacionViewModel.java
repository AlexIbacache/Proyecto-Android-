package com.example.proyectoandroid.ui.reparacion;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectoandroid.data.MaquinariaRepository;
import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.model.ParteReparada;
import com.example.proyectoandroid.model.Reparacion;
import com.example.proyectoandroid.model.Repuesto;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReparacionViewModel extends ViewModel {

    private static final String TAG = "ReparacionViewModel";
    private final MaquinariaRepository repository;
    private final LiveData<List<Maquinaria>> maquinarias;
    private final MutableLiveData<Maquinaria> maquinariaSeleccionada = new MutableLiveData<>();
    private final MutableLiveData<Date> fecha = new MutableLiveData<>();
    private final MutableLiveData<List<ParteReparada>> partesReparadas = new MutableLiveData<>();
    private final MutableLiveData<String> showToastEvent = new MutableLiveData<>();
    private final MutableLiveData<Reparacion> reparacionAbierta = new MutableLiveData<>();

    public ReparacionViewModel() {
        repository = new MaquinariaRepository();
        maquinarias = repository.getMaquinariaList();
    }

    public LiveData<String> getShowToastEvent() {
        return showToastEvent;
    }

    public LiveData<List<Maquinaria>> getMaquinarias() {
        return maquinarias;
    }

    public LiveData<Reparacion> getReparacionAbierta() {
        return reparacionAbierta;
    }

    public void onMaquinariaSelected(Maquinaria maquinaria) {
        maquinariaSeleccionada.setValue(maquinaria);
        if (maquinaria == null || maquinaria.getDocumentId() == null) {
            partesReparadas.setValue(new ArrayList<>());
            reparacionAbierta.setValue(null);
            return;
        }

        repository.getReparacionAbierta(maquinaria.getDocumentId(), reparacion -> {
            if (reparacion != null) {
                reparacionAbierta.setValue(reparacion);
                partesReparadas.setValue(reparacion.getPartesReparadas());
                fecha.setValue(reparacion.getFecha().toDate());
            } else {
                reparacionAbierta.setValue(null);
                List<ParteReparada> nuevasPartes = new ArrayList<>();
                if (maquinaria.getPartesPrincipales() != null) {
                    for (String nombreParte : maquinaria.getPartesPrincipales()) {
                        nuevasPartes.add(new ParteReparada(nombreParte, new ArrayList<>()));
                    }
                }
                partesReparadas.setValue(nuevasPartes);
            }
        });
    }

    public void setFecha(Date date) {
        fecha.setValue(date);
    }

    public LiveData<List<ParteReparada>> getPartesReparadas() {
        return partesReparadas;
    }

    public void actualizarRepuestosParaParte(String parteNombre, List<Repuesto> repuestos) {
        List<ParteReparada> currentPartes = partesReparadas.getValue();
        if (currentPartes != null) {
            for (ParteReparada parte : currentPartes) {
                if (parte.getNombreParte().equals(parteNombre)) {
                    parte.setRepuestos(repuestos);
                    break;
                }
            }
            partesReparadas.setValue(currentPartes);
        }
    }

    public void eliminarRepuestoDeParte(String parteNombre, Repuesto repuesto) {
        List<ParteReparada> currentPartes = partesReparadas.getValue();
        if (currentPartes != null) {
            for (ParteReparada parte : currentPartes) {
                if (parte.getNombreParte().equals(parteNombre)) {
                    if (parte.getRepuestos() != null) {
                        parte.getRepuestos().remove(repuesto);
                        partesReparadas.setValue(currentPartes); // Notificar a la UI
                        showToastEvent.setValue("Repuesto eliminado.");
                        break;
                    }
                }
            }
        }
    }

    public void guardarReparacion(String notas) {
        Maquinaria maquina = maquinariaSeleccionada.getValue();
        Date fechaReparacion = fecha.getValue();
        List<ParteReparada> partes = partesReparadas.getValue();

        if (!validarDatos(maquina, fechaReparacion, partes)) return;

        Reparacion reparacionExistente = reparacionAbierta.getValue();
        if (reparacionExistente != null) {
            reparacionExistente.setNotas(notas);
            reparacionExistente.setPartesReparadas(partes);
            repository.actualizarReparacion(maquina.getDocumentId(), reparacionExistente, success -> {
                if (success) showToastEvent.setValue("Cambios guardados con éxito.");
                else showToastEvent.setValue("Error al guardar los cambios.");
            });
        } else {
            Reparacion nuevaReparacion = new Reparacion(new Timestamp(fechaReparacion), notas, partes);
            repository.guardarReparacion(maquina.getDocumentId(), nuevaReparacion, success -> {
                if (success) {
                    showToastEvent.setValue("Reparación guardada con éxito.");
                    maquina.setEstado(false); // No operativa al crear reparación
                    repository.actualizarMaquinaria(maquina, updateSuccess -> onMaquinariaSelected(maquina));
                } else {
                    showToastEvent.setValue("Error al guardar la reparación.");
                }
            });
        }
    }

    public void finalizarReparacion() {
        Maquinaria maquina = maquinariaSeleccionada.getValue();
        Reparacion reparacion = reparacionAbierta.getValue();

        if (maquina != null && reparacion != null) {
            reparacion.setEstado("Cerrada");
            repository.actualizarReparacion(maquina.getDocumentId(), reparacion, success -> {
                if (success) {
                    showToastEvent.setValue("Reparación finalizada.");
                    maquina.setEstado(true); // Operativa al finalizar
                    repository.actualizarMaquinaria(maquina, updateSuccess -> onMaquinariaSelected(maquina));
                } else {
                    showToastEvent.setValue("Error al finalizar la reparación.");
                }
            });
        } else {
            showToastEvent.setValue("No hay una reparación abierta para finalizar.");
        }
    }

    private boolean validarDatos(Maquinaria maquina, Date fechaReparacion, List<ParteReparada> partes) {
        if (maquina == null) {
            showToastEvent.setValue("Debes seleccionar una maquinaria.");
            return false;
        }
        if (fechaReparacion == null) {
            showToastEvent.setValue("Debes seleccionar una fecha.");
            return false;
        }
        boolean hasRepuestos = false;
        if (partes != null) {
            for (ParteReparada parte : partes) {
                if (parte.getRepuestos() != null && !parte.getRepuestos().isEmpty()) {
                    hasRepuestos = true;
                    break;
                }
            }
        }
        if (!hasRepuestos) {
            showToastEvent.setValue("Debes añadir al menos un repuesto para poder guardar.");
            return false;
        }
        return true;
    }
}
