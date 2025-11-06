package com.example.proyectoandroid.ui.reportes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.model.ParteReparada;
import com.example.proyectoandroid.model.Reparacion;
import com.example.proyectoandroid.model.Repuesto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ReportesFragment extends Fragment implements ReportesAdapter.OnItemClickListener {

    private ReportesViewModel reportesViewModel;
    private ReportesAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportes, container, false);

        reportesViewModel = new ViewModelProvider(this).get(ReportesViewModel.class);

        RecyclerView rvReportes = view.findViewById(R.id.rvReportes);
        rvReportes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReportesAdapter(new ArrayList<>(), this);
        rvReportes.setAdapter(adapter);

        observeViewModel();

        FloatingActionButton fabExportarExcel = view.findViewById(R.id.fabExportarExcel);
        fabExportarExcel.setOnClickListener(v -> reportesViewModel.onExportExcelClicked());

        return view;
    }

    private void observeViewModel() {
        reportesViewModel.getMaquinariaList().observe(getViewLifecycleOwner(), maquinarias -> {
            if (maquinarias != null) {
                adapter.updateData(maquinarias);
            }
        });

        reportesViewModel.getShowToastEvent().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });

        reportesViewModel.getReparacionesDeMaquina().observe(getViewLifecycleOwner(), reparaciones -> {
            Maquinaria selectedMachine = reportesViewModel.getSelectedMaquinaria().getValue();
            if (reparaciones != null && selectedMachine != null) {
                showReporteDetalleDialog(selectedMachine, reparaciones);
                reportesViewModel.selectMaquinaria(null);
            }
        });
    }

    @Override
    public void onItemClick(Maquinaria maquinaria) {
        reportesViewModel.selectMaquinaria(maquinaria);
    }

    private void showReporteDetalleDialog(Maquinaria maquinaria, List<Reparacion> reparaciones) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_reporte_detalle, null);
        builder.setView(dialogView);

        TextView tvNombreMaquinaDialog = dialogView.findViewById(R.id.tvNombreMaquinaDialog);
        TextView tvPartesMaquina = dialogView.findViewById(R.id.tvPartesMaquina);
        TextView tvRepuestosUsados = dialogView.findViewById(R.id.tvRepuestosUsados);

        tvNombreMaquinaDialog.setText(maquinaria.getNombre());

        if (maquinaria.getPartesPrincipales() != null && !maquinaria.getPartesPrincipales().isEmpty()) {
            StringBuilder partesText = new StringBuilder();
            for (String parte : maquinaria.getPartesPrincipales()) {
                partesText.append("- ").append(parte).append("\n");
            }
            tvPartesMaquina.setText(partesText.toString());
        } else {
            tvPartesMaquina.setText("No hay partes principales registradas.");
        }

        // --- Lógica de Lectura Corregida ---
        StringBuilder repuestosText = new StringBuilder();
        if (reparaciones != null && !reparaciones.isEmpty()) {
            for (Reparacion reparacion : reparaciones) {
                if (reparacion.getPartesReparadas() != null && !reparacion.getPartesReparadas().isEmpty()) {
                    // Iteramos sobre la nueva lista de objetos ParteReparada
                    for (ParteReparada parteReparada : reparacion.getPartesReparadas()) {
                        String nombreParte = parteReparada.getNombreParte();
                        List<Repuesto> repuestosEnParte = parteReparada.getRepuestos();
                        repuestosText.append("\nParte: ").append(nombreParte).append("\n");

                        if (repuestosEnParte != null && !repuestosEnParte.isEmpty()) {
                            for (Repuesto repuesto : repuestosEnParte) {
                                repuestosText.append("- ")
                                        .append(repuesto.getNombre())
                                        .append(" (x").append(repuesto.getCantidad()).append(")\n");
                            }
                        }
                    }
                }
            }
        }
        // --- Fin de la Lógica de Lectura ---

        if (repuestosText.length() > 0) {
            tvRepuestosUsados.setText(repuestosText.toString());
        } else {
            tvRepuestosUsados.setText("No se han registrado reparaciones para esta máquina.");
        }

        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
