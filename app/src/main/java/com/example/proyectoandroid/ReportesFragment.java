package com.example.proyectoandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ReportesFragment extends Fragment {

    private FloatingActionButton fabExportarExcel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reportes, container, false);

        fabExportarExcel = view.findViewById(R.id.fabExportarExcel);

        fabExportarExcel.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Función próximamente: Exportar Excel", Toast.LENGTH_SHORT).show();
        });

        // Aquí configurarías la lógica para poblar tu TableLayout con datos reales de Firestore.

        return view;
    }
}
