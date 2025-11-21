package com.example.proyectoandroid.ui.reportes;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.proyectoandroid.data.MaquinariaRepository;
import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.model.Reparacion;
import com.example.proyectoandroid.model.Repuesto;
import com.example.proyectoandroid.util.SingleLiveEvent;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ReportesViewModel extends ViewModel {

    private static final String TAG = "ReportesViewModel";
    private final MaquinariaRepository maquinariaRepository;
    private final SingleLiveEvent<String> _showToastEvent = new SingleLiveEvent<>();
    public LiveData<String> getShowToastEvent() { return _showToastEvent; }

    private final MediatorLiveData<List<Maquinaria>> _maquinariaList = new MediatorLiveData<>();
    public LiveData<List<Maquinaria>> getMaquinariaList() { return _maquinariaList; }

    private final SingleLiveEvent<List<Maquinaria>> _startLocalExportEvent = new SingleLiveEvent<>();
    public LiveData<List<Maquinaria>> getStartLocalExportEvent() { return _startLocalExportEvent; }

    private final Set<String> _selectedMaquinariaIds = new HashSet<>();

    public ReportesViewModel() {
        maquinariaRepository = new MaquinariaRepository();
        LiveData<List<Maquinaria>> source = maquinariaRepository.getMaquinariaList();

        _maquinariaList.addSource(source, maquinarias -> {
            if (maquinarias != null) {
                for (Maquinaria m : maquinarias) {
                    m.setSelected(_selectedMaquinariaIds.contains(m.getDocumentId()));
                }
                _maquinariaList.setValue(maquinarias);
            }
        });
    }

    public void toggleMaquinariaSelection(Maquinaria maquinaria) {
        if (!maquinaria.isEstado()) return;

        if (_selectedMaquinariaIds.contains(maquinaria.getDocumentId())) {
            _selectedMaquinariaIds.remove(maquinaria.getDocumentId());
            maquinaria.setSelected(false);
        } else {
            _selectedMaquinariaIds.add(maquinaria.getDocumentId());
            maquinaria.setSelected(true);
        }
        _maquinariaList.setValue(_maquinariaList.getValue());
    }

    public void setSelectAllOperative(boolean select) {
        List<Maquinaria> allMachines = _maquinariaList.getValue();
        if (allMachines == null) return;

        for (Maquinaria maquina : allMachines) {
            if (maquina.isEstado()) { // Solo afecta a las operativas
                if (select) {
                    _selectedMaquinariaIds.add(maquina.getDocumentId());
                    maquina.setSelected(true);
                } else {
                    _selectedMaquinariaIds.remove(maquina.getDocumentId());
                    maquina.setSelected(false);
                }
            }
        }
        _maquinariaList.setValue(allMachines); // Disparar actualización de la UI
    }

    public void onExportExcelClicked() {
        Log.d(TAG, "onExportExcelClicked para exportación local.");

        if (_selectedMaquinariaIds.isEmpty()) {
            _showToastEvent.setValue("Por favor, selecciona al menos una maquinaria para exportar.");
            return;
        }

        List<Maquinaria> allMachines = _maquinariaList.getValue();
        if (allMachines == null) {
            _showToastEvent.setValue("Error: lista de maquinarias no disponible.");
            return;
        }

        List<Maquinaria> selectedMachines = new ArrayList<>();
        for (Maquinaria m : allMachines) {
            if (_selectedMaquinariaIds.contains(m.getDocumentId())) {
                selectedMachines.add(m);
            }
        }

        _startLocalExportEvent.setValue(selectedMachines);
    }

    public LiveData<List<Reparacion>> getReparacionesDeMaquina(String maquinaId) {
        return maquinariaRepository.getReparacionesDeMaquina(maquinaId);
    }
    
    public List<Maquinaria> getSelectedMaquinarias() {
        List<Maquinaria> allMachines = _maquinariaList.getValue();
        if (allMachines == null) {
            return new ArrayList<>();
        }
        List<Maquinaria> selectedMachines = new ArrayList<>();
        for (Maquinaria m : allMachines) {
            if (_selectedMaquinariaIds.contains(m.getDocumentId())) {
                selectedMachines.add(m);
            }
        }
        return selectedMachines;
    }

    public void deleteReparacion(String reparacionId, String maquinaId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (userId == null) {
            _showToastEvent.setValue("Usuario no autenticado para eliminar la reparación.");
            return;
        }

        maquinariaRepository.deleteReparacion(userId, maquinaId, reparacionId)
            .addOnSuccessListener(aVoid -> {
                _showToastEvent.setValue("Reparación eliminada con éxito.");
            })
            .addOnFailureListener(e -> {
                _showToastEvent.setValue("Error al eliminar la reparación: " + e.getMessage());
                Log.e(TAG, "Error deleting repair: " + e.getMessage(), e);
            });
    }

    public void eliminarRepuestoDeReparacion(String maquinaId, String reparacionId, String parteNombre, Repuesto repuesto) {
        maquinariaRepository.eliminarRepuestoDeReparacion(maquinaId, reparacionId, parteNombre, repuesto, success -> {
            if (success) {
                _showToastEvent.setValue("Repuesto eliminado con éxito.");
            } else {
                _showToastEvent.setValue("Error al eliminar el repuesto.");
            }
        });
    }

    public void cambiarEstadoMaquinaria(Maquinaria maquinaria) {
        maquinaria.setEstado(!maquinaria.isEstado()); // Invertir estado
        maquinariaRepository.actualizarMaquinaria(maquinaria, success -> {
            if (success) {
                _showToastEvent.setValue("Estado de " + maquinaria.getNombre() + " actualizado.");
            } else {
                _showToastEvent.setValue("Error al actualizar el estado.");
                maquinaria.setEstado(!maquinaria.isEstado()); // Revertir en caso de error
            }
        });
    }

    public void eliminarReporteCompleto(Maquinaria maquinaria) {
        maquinariaRepository.eliminarTodosLosReportesDeMaquina(maquinaria.getDocumentId(), success -> {
            if(success) {
                // Una vez eliminados los reportes, actualizamos el estado de la máquina
                maquinaria.setEstado(false); // No Operativa
                maquinariaRepository.actualizarMaquinaria(maquinaria, updateSuccess -> {
                    if (updateSuccess) {
                        _showToastEvent.setValue("Reportes de " + maquinaria.getNombre() + " eliminados. La máquina ahora está No Operativa.");
                    } else {
                        _showToastEvent.setValue("Reportes eliminados, pero falló al actualizar el estado.");
                    }
                });
            } else {
                _showToastEvent.setValue("Error al eliminar los reportes.");
            }
        });
    }
}
