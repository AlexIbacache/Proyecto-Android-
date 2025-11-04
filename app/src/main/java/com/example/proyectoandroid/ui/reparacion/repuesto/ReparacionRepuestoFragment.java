package com.example.proyectoandroid.ui.reparacion.repuesto;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class ReparacionRepuestoFragment extends Fragment {

    private ReparacionRepuestoViewModel viewModel;
    private RecyclerView rvRepuestos;
    private FloatingActionButton fabAgregarRepuesto;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reparacion_repuesto, container, false);

        viewModel = new ViewModelProvider(this).get(ReparacionRepuestoViewModel.class);

        rvRepuestos = view.findViewById(R.id.rvRepuestos);
        fabAgregarRepuesto = view.findViewById(R.id.fabAgregarRepuesto);

        fabAgregarRepuesto.setOnClickListener(v -> {
            viewModel.onAgregarRepuestoClicked();
        });

        viewModel.getShowAgregarRepuestoDialogEvent().observe(getViewLifecycleOwner(), aVoid -> {
            AgregarRepuestoDialog dialog = new AgregarRepuestoDialog();
            dialog.show(getChildFragmentManager(), "AgregarRepuestoDialog");
        });

        // Aquí configurarías el RecyclerView para los repuestos, observando LiveData del ViewModel

        return view;
    }
}
