package com.example.proyectoandroid.ui.reportes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.proyectoandroid.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ReportesFragment extends Fragment {

    private ReportesViewModel reportesViewModel;
    private FloatingActionButton fabExportarExcel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportes, container, false);

        reportesViewModel = new ViewModelProvider(this).get(ReportesViewModel.class);

        fabExportarExcel = view.findViewById(R.id.fabExportarExcel);

        fabExportarExcel.setOnClickListener(v -> {
            reportesViewModel.onExportExcelClicked();
        });

        reportesViewModel.getShowToastEvent().observe(getViewLifecycleOwner(), message -> {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        });

        // Aquí configurarías la lógica para poblar tu TableLayout con datos reales de Firestore.
        // Esto se hará a través de LiveData del ReportesViewModel.

        return view;
    }
}
