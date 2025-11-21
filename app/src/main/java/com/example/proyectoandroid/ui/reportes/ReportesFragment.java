package com.example.proyectoandroid.ui.reportes;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.adapters.ReportesAdapter;
import com.example.proyectoandroid.ui.reportes.ReparacionesDialogAdapter;
import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.model.ParteReparada;
import com.example.proyectoandroid.model.Reparacion;
import com.example.proyectoandroid.model.Repuesto;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ReportesFragment extends Fragment implements ReportesAdapter.OnItemClickListener, ReportesAdapter.OnMaquinariaStatusChangeListener, ReportesAdapter.OnMaquinariaDeleteListener, ReparacionesDialogAdapter.OnReparacionDeleteListener, ReparacionesDialogAdapter.OnRepuestoDeleteFromDialogListener {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private ReportesViewModel reportesViewModel;
    private ReportesAdapter reportesAdapter;
    private AlertDialog reparacionDialog;
    private Maquinaria maquinaActual;
    private ReparacionesDialogAdapter reparacionesDialogAdapter;
    private Observer<List<Reparacion>> reparacionesObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportes, container, false);

        reportesViewModel = new ViewModelProvider(this).get(ReportesViewModel.class);

        RecyclerView rvReportes = view.findViewById(R.id.rvReportes);
        rvReportes.setLayoutManager(new LinearLayoutManager(getContext()));
        reportesAdapter = new ReportesAdapter(new ArrayList<>(), this, this, this);
        rvReportes.setAdapter(reportesAdapter);

        FloatingActionButton fabExportarExcel = view.findViewById(R.id.fabExportarExcel);
        fabExportarExcel.setOnClickListener(v -> {
            if (checkPermission()) {
                reportesViewModel.onExportExcelClicked();
            } else {
                requestPermission();
            }
        });

        MaterialCheckBox checkboxSelectAll = view.findViewById(R.id.checkbox_select_all_operative);
        checkboxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            reportesViewModel.setSelectAllOperative(isChecked);
        });

        observeViewModel();

        return view;
    }

    private void observeViewModel() {
        reportesViewModel.getMaquinariaList().observe(getViewLifecycleOwner(), maquinarias -> {
            if (maquinarias != null) {
                reportesAdapter.updateData(maquinarias);
            }
        });

        reportesViewModel.getShowToastEvent().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });

        reportesViewModel.getStartLocalExportEvent().observe(getViewLifecycleOwner(), this::startLocalExportProcess);
    }

    @Override
    public void onMaquinariaToggled(Maquinaria maquinaria, boolean isSelected) {
        reportesViewModel.toggleMaquinariaSelection(maquinaria);
    }

    @Override
    public void onItemClick(Maquinaria maquinaria) {
        this.maquinaActual = maquinaria;
        showReporteDetalleDialog(maquinaria, new ArrayList<>()); // Abrir con lista vacía inicial

        // Crear el observer que actualizará el diálogo
        reparacionesObserver = reparaciones -> {
            if (reparaciones != null && reparacionesDialogAdapter != null) {
                reparacionesDialogAdapter.updateData(reparaciones);
            }
        };

        reportesViewModel.getReparacionesDeMaquina(maquinaria.getDocumentId()).observe(getViewLifecycleOwner(), reparacionesObserver);
    }

    private void startLocalExportProcess(List<Maquinaria> selectedMaquinas) {
        if (selectedMaquinas == null || selectedMaquinas.isEmpty()) {
            return;
        }

        Map<Maquinaria, List<Reparacion>> exportData = new HashMap<>();
        AtomicInteger pendingRequests = new AtomicInteger(selectedMaquinas.size());

        for (Maquinaria maquina : selectedMaquinas) {
            reportesViewModel.getReparacionesDeMaquina(maquina.getDocumentId()).observe(this, reparaciones -> {
                if (reparaciones != null) {
                    exportData.put(maquina, reparaciones);
                    if (pendingRequests.decrementAndGet() == 0) {
                        saveExcelFile(exportData);
                    }
                }
            });
        }
    }

    private void showReporteDetalleDialog(Maquinaria maquinaria, List<Reparacion> initialReparaciones) {
        if (reparacionDialog != null && reparacionDialog.isShowing()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_reporte_detalle, null);
        builder.setView(dialogView);

        TextView tvNombreMaquinaDialog = dialogView.findViewById(R.id.tvNombreMaquinaDialog);
        TextView tvPartesMaquina = dialogView.findViewById(R.id.tvPartesMaquina);

        tvNombreMaquinaDialog.setText(maquinaria.getNombre());

        if (maquinaria.getPartesPrincipales() != null && !maquinaria.getPartesPrincipales().isEmpty()) {
            StringBuilder partesText = new StringBuilder();
            for (String parte : maquinaria.getPartesPrincipales()) {
                partesText.append("- ").append(parte).append("\n");
            }
            tvPartesMaquina.setText(partesText.toString().trim());
        } else {
            tvPartesMaquina.setText("No hay partes principales definidas.");
        }

        RecyclerView rvReparacionesDialog = dialogView.findViewById(R.id.rvReparacionesDialog);
        rvReparacionesDialog.setLayoutManager(new LinearLayoutManager(getContext()));

        reparacionesDialogAdapter = new ReparacionesDialogAdapter(getContext(), initialReparaciones, this, this);
        rvReparacionesDialog.setAdapter(reparacionesDialogAdapter);

        builder.setPositiveButton("Cerrar", (dialog, which) -> {
            // Detener el observer cuando se cierra el diálogo
            if (maquinaActual != null && reparacionesObserver != null) {
                reportesViewModel.getReparacionesDeMaquina(maquinaActual.getDocumentId()).removeObserver(reparacionesObserver);
            }
            dialog.dismiss();
        });

        reparacionDialog = builder.create();
        reparacionDialog.show();
    }

    @Override
    public void onReparacionDelete(Reparacion reparacion) {
        if (maquinaActual != null) {
            new AlertDialog.Builder(requireContext())
                .setTitle("Eliminar Reparación")
                .setMessage("¿Estás seguro de que quieres eliminar esta reparación? Esta acción no se puede deshacer.")
                .setPositiveButton("Sí, Eliminar", (dialog, which) -> {
                    reportesViewModel.deleteReparacion(reparacion.getDocumentId(), maquinaActual.getDocumentId());
                })
                .setNegativeButton("Cancelar", null)
                .show();
        }
    }

    @Override
    public void onRepuestoDelete(Reparacion reparacion, String parteNombre, Repuesto repuesto) {
        reportesViewModel.eliminarRepuestoDeReparacion(maquinaActual.getDocumentId(), reparacion.getDocumentId(), parteNombre, repuesto);
    }

    @Override
    public void onStatusChange(Maquinaria maquinaria) {
        String nuevoEstado = maquinaria.isEstado() ? "No Operativa" : "Operativa";
        new AlertDialog.Builder(requireContext())
            .setTitle("Cambiar Estado")
            .setMessage("¿Estás seguro de que quieres cambiar el estado de " + maquinaria.getNombre() + " a " + nuevoEstado + "?")
            .setPositiveButton("Sí, Cambiar", (dialog, which) -> {
                reportesViewModel.cambiarEstadoMaquinaria(maquinaria);
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    @Override
    public void onDelete(Maquinaria maquinaria) {
        new AlertDialog.Builder(requireContext())
            .setTitle("Eliminar Reporte Completo")
            .setMessage("¿Estás seguro de que quieres eliminar todos los reportes de " + maquinaria.getNombre() + "? La maquinaria no se eliminará, solo su historial de reparaciones.")
            .setPositiveButton("Sí, Eliminar Reportes", (dialog, which) -> {
                reportesViewModel.eliminarReporteCompleto(maquinaria);
            })
            .setNegativeButton("Cancelar", null)
            .show();
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            return true;
        } else {
            return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                reportesViewModel.onExportExcelClicked();
            } else {
                Toast.makeText(getContext(), "Permiso denegado. No se puede guardar el archivo.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveExcelFile(Map<Maquinaria, List<Reparacion>> exportData) {
        XSSFWorkbook workbook = new XSSFWorkbook();
        String fileName;
        List<Maquinaria> selectedMaquinas = new ArrayList<>(exportData.keySet());

        String timeStamp = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date());
        if (selectedMaquinas.size() == 1) {
            Maquinaria maquina = selectedMaquinas.get(0);
            fileName = "reportes_" + timeStamp + "_" + maquina.getNumeroIdentificador() + ".xlsx";
            XSSFSheet sheet = workbook.createSheet(maquina.getNombre());
            populateSheetWithRepairs(sheet, maquina, exportData.get(maquina));
        } else {
            String folio = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
            fileName = "reporte_" + timeStamp + "_" + folio + ".xlsx";
            for (Maquinaria maquina : selectedMaquinas) {
                XSSFSheet sheet = workbook.createSheet(maquina.getNombre());
                populateSheetWithRepairs(sheet, maquina, exportData.get(maquina));
            }
        }

        try {
            OutputStream fos;
            String pathForToast;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/Reportes Reparaciones Maquina");
                Uri uri = requireContext().getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                if (uri == null) throw new IOException("No se pudo crear el archivo en MediaStore");
                fos = requireContext().getContentResolver().openOutputStream(uri);
                pathForToast = "Guardado en Descargas.";
            } else {
                File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Reportes Reparaciones Maquina");
                if (!dir.exists()) dir.mkdirs();
                File file = new File(dir, fileName);
                fos = new FileOutputStream(file);
                pathForToast = file.getAbsolutePath();
            }

            if (fos == null) throw new IOException("El stream de salida es nulo.");

            try (OutputStream finalFos = fos) {
                workbook.write(finalFos);
            } finally {
                workbook.close();
            }

            Toast.makeText(getContext(), "Excel " + pathForToast, Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Error al guardar el archivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void populateSheetWithRepairs(XSSFSheet sheet, Maquinaria maquina, List<Reparacion> reparaciones) {
        int rowNum = 0;
        Workbook workbook = sheet.getWorkbook();

        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);

        CellStyle dataCellStyle = workbook.createCellStyle();
        dataCellStyle.setBorderTop(BorderStyle.THIN);
        dataCellStyle.setBorderBottom(BorderStyle.THIN);
        dataCellStyle.setBorderLeft(BorderStyle.THIN);
        dataCellStyle.setBorderRight(BorderStyle.THIN);

        CellStyle sectionHeaderStyle = workbook.createCellStyle();
        Font sectionHeaderFont = workbook.createFont();
        sectionHeaderFont.setBold(true);
        sectionHeaderFont.setColor(IndexedColors.WHITE.getIndex());
        sectionHeaderStyle.setFont(sectionHeaderFont);
        sectionHeaderStyle.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        sectionHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        sectionHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        sectionHeaderStyle.setBorderTop(BorderStyle.THIN);
        sectionHeaderStyle.setBorderBottom(BorderStyle.THIN);
        sectionHeaderStyle.setBorderLeft(BorderStyle.THIN);
        sectionHeaderStyle.setBorderRight(BorderStyle.THIN);

        CellStyle subHeaderStyle = workbook.createCellStyle();
        Font subHeaderFont = workbook.createFont();
        subHeaderFont.setBold(true);
        subHeaderStyle.setFont(subHeaderFont);
        subHeaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        subHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        subHeaderStyle.setBorderTop(BorderStyle.THIN);
        subHeaderStyle.setBorderBottom(BorderStyle.THIN);
        subHeaderStyle.setBorderLeft(BorderStyle.THIN);
        subHeaderStyle.setBorderRight(BorderStyle.THIN);

        for (Reparacion reparacion : reparaciones) {
            String[] mainHeaders = {"ID Máquina", "Nombre Máquina", "Fecha de Reparación", "Notas"};
            String[] mainData = {
                maquina.getNumeroIdentificador(),
                maquina.getNombre(),
                new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(reparacion.getFecha().toDate()),
                reparacion.getNotas()
            };

            for (int i = 0; i < mainHeaders.length; i++) {
                Row row = sheet.createRow(rowNum++);
                Cell headerCell = row.createCell(0);
                headerCell.setCellValue(mainHeaders[i]);
                headerCell.setCellStyle(headerStyle);

                Cell dataCell = row.createCell(1);
                dataCell.setCellValue(mainData[i]);
                dataCell.setCellStyle(dataCellStyle);
            }
            
            rowNum++;

            if (reparacion.getPartesReparadas() != null && !reparacion.getPartesReparadas().isEmpty()) {
                for (ParteReparada parte : reparacion.getPartesReparadas()) {
                    Row parteHeaderRow = sheet.createRow(rowNum++);
                    Cell parteHeaderCell = parteHeaderRow.createCell(0);
                    parteHeaderCell.setCellValue(parte.getNombreParte());
                    parteHeaderCell.setCellStyle(sectionHeaderStyle);
                    sheet.addMergedRegion(new CellRangeAddress(rowNum - 1, rowNum - 1, 0, 2));

                    Row repuestoHeaderRow = sheet.createRow(rowNum++);
                    String[] repuestoHeaders = {"Nombre", "Código/N parte", "Cantidad"};
                    for (int i = 0; i < repuestoHeaders.length; i++) {
                        Cell cell = repuestoHeaderRow.createCell(i);
                        cell.setCellValue(repuestoHeaders[i]);
                        cell.setCellStyle(subHeaderStyle);
                    }

                    List<Repuesto> repuestos = parte.getRepuestos();
                    if (repuestos != null) {
                        Collections.sort(repuestos, Comparator.comparing(Repuesto::getNombre));
                        for (Repuesto repuesto : repuestos) {
                            Row repuestoRow = sheet.createRow(rowNum++);
                            repuestoRow.createCell(0).setCellValue(repuesto.getNombre());
                            repuestoRow.createCell(1).setCellValue(repuesto.getCodigo());
                            repuestoRow.createCell(2).setCellValue(repuesto.getCantidad());
                            for (int i = 0; i < 3; i++) {
                                repuestoRow.getCell(i).setCellStyle(dataCellStyle);
                            }
                        }
                    }
                    rowNum++;
                }
            }
            rowNum += 2; 
        }
    }
}
