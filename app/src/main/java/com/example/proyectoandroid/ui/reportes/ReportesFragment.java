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
import com.example.proyectoandroid.model.Reparacion;
import com.example.proyectoandroid.model.RepuestoUsado;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ReportesFragment extends Fragment implements ReportesAdapter.OnItemClickListener {

    private ReportesViewModel reportesViewModel;
    private RecyclerView rvReportes;
    private ReportesAdapter adapter;
    private List<Maquinaria> maquinariaList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportes, container, false);

        reportesViewModel = new ViewModelProvider(this).get(ReportesViewModel.class);

        rvReportes = view.findViewById(R.id.rvReportes);
        rvReportes.setLayoutManager(new LinearLayoutManager(getContext()));

        // --- INICIO: Código de ejemplo para previsualización ---
        createMockData(); // Creamos los datos de ejemplo
        adapter = new ReportesAdapter(maquinariaList, this);
        rvReportes.setAdapter(adapter);
        // --- FIN: Código de ejemplo para previsualización ---

        FloatingActionButton fabExportarExcel = view.findViewById(R.id.fabExportarExcel);
        fabExportarExcel.setOnClickListener(v -> reportesViewModel.onExportExcelClicked());

        // observeViewModel(); // Comentamos esto para no sobreescribir los datos de ejemplo

        return view;
    }

    private void createMockData() {
        maquinariaList.clear();

        Maquinaria maquina1 = new Maquinaria();
        maquina1.setId("mock_id_1");
        maquina1.setNombre("Excavadora 320D");
        maquina1.setNumeroIdentificador("EQ: 101");
        maquina1.setFechaIngreso(new Timestamp(new Date()));
        maquina1.setEstado(true); // Operativa
        maquina1.setPartesPrincipales(Arrays.asList("Cuchara Principal", "Motor Diesel", "Sistema Hidráulico"));

        Maquinaria maquina2 = new Maquinaria();
        maquina2.setId("mock_id_2");
        maquina2.setNombre("Cargador Frontal 980H");
        maquina2.setNumeroIdentificador("EQ: 205");
        maquina2.setFechaIngreso(new Timestamp(new Date(System.currentTimeMillis() - 86400000 * 5))); // 5 días antes
        maquina2.setEstado(false); // En reparación
        maquina2.setPartesPrincipales(Arrays.asList("Neumáticos", "Cabina del Operador", "Motor"));

        maquinariaList.add(maquina1);
        maquinariaList.add(maquina2);
    }

    private void observeViewModel() {
        reportesViewModel.getMaquinariaList().observe(getViewLifecycleOwner(), maquinarias -> {
            if (maquinarias != null) {
                maquinariaList.clear();
                maquinariaList.addAll(maquinarias);
                adapter.notifyDataSetChanged();
            }
        });

        reportesViewModel.getShowToastEvent().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });

        reportesViewModel.getReparacionesDeMaquina().observe(getViewLifecycleOwner(), reparaciones -> {
            Maquinaria selectedMachine = reportesViewModel.getSelectedMaquinaria().getValue();
            if (reparaciones != null && selectedMachine != null) {
                showReporteDetalleDialog(selectedMachine, reparaciones);
                reportesViewModel.selectMaquinaria(null); // Clear selection
            }
        });
    }

    @Override
    public void onItemClick(Maquinaria maquinaria) {
        // --- INICIO: Lógica de ejemplo para el diálogo modal ---
        List<Reparacion> reparacionesEjemplo = new ArrayList<>();
        if (maquinaria.getId().equals("mock_id_1")) {
            // No hay reparaciones para la máquina 1
        } else {
            Reparacion reparacion = new Reparacion();
            reparacion.setNotas("Se cambió el filtro de aire y se revisó el aceite.");

            RepuestoUsado repuesto1 = new RepuestoUsado();
            repuesto1.setNombreRepuesto("Filtro de Aire K-123");
            repuesto1.setCantidad(1);

            RepuestoUsado repuesto2 = new RepuestoUsado();
            repuesto2.setNombreRepuesto("Perno de Seguridad 3/4\"");
            repuesto2.setCantidad(8);

            reparacion.setRepuestosUsados(Arrays.asList(repuesto1, repuesto2));
            reparacionesEjemplo.add(reparacion);
        }
        showReporteDetalleDialog(maquinaria, reparacionesEjemplo);
        // --- FIN: Lógica de ejemplo para el diálogo modal ---

        // reportesViewModel.selectMaquinaria(maquinaria); // Comentamos la llamada al ViewModel
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

        // Partes principales
        if (maquinaria.getPartesPrincipales() != null && !maquinaria.getPartesPrincipales().isEmpty()) {
            StringBuilder partesText = new StringBuilder();
            for (String parte : maquinaria.getPartesPrincipales()) {
                partesText.append("- ").append(parte).append("\n");
            }
            tvPartesMaquina.setText(partesText.toString());
        } else {
            tvPartesMaquina.setText("No hay partes principales registradas.");
        }

        // Repuestos usados
        StringBuilder repuestosText = new StringBuilder();
        if (reparaciones != null && !reparaciones.isEmpty()) {
            for (Reparacion reparacion : reparaciones) {
                if (reparacion.getRepuestosUsados() != null && !reparacion.getRepuestosUsados().isEmpty()) {
                    for (RepuestoUsado repuesto : reparacion.getRepuestosUsados()) {
                        repuestosText.append("- ")
                                     .append(repuesto.getNombreRepuesto())
                                     .append(" (x").append(repuesto.getCantidad()).append(")\n");
                    }
                }
            }
        } 

        if (repuestosText.length() > 0) {
            tvRepuestosUsados.setText(repuestosText.toString());
        } else {
            tvRepuestosUsados.setText("No se han utilizado repuestos en esta máquina.");
        }


        builder.setPositiveButton("Cerrar", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
