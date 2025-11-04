package com.example.proyectoandroid.ui.reparacion.repuesto;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.proyectoandroid.R;

public class AgregarRepuestoDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_agregar_repuesto, null);

        final EditText etNombreRepuesto = view.findViewById(R.id.etNombreRepuesto);
        final EditText etCantidad = view.findViewById(R.id.etCantidad);

        builder.setView(view)
                .setTitle("Agregar Repuesto")
                .setPositiveButton("Guardar", (dialog, id) -> {
                    String nombre = etNombreRepuesto.getText().toString().trim();
                    String cantidadStr = etCantidad.getText().toString().trim();

                    if (nombre.isEmpty() || cantidadStr.isEmpty()) {
                        Toast.makeText(getContext(), "Por favor, ingrese nombre y cantidad", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    try {
                        int cantidad = Integer.parseInt(cantidadStr);
                        // Aquí se llamaría al ViewModel del Fragmento padre para agregar el repuesto
                        Toast.makeText(getContext(), "Repuesto guardado (simulado): " + nombre, Toast.LENGTH_SHORT).show();
                    } catch (NumberFormatException e) {
                        Toast.makeText(getContext(), "Cantidad inválida", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", (dialog, id) -> {
                    AgregarRepuestoDialog.this.getDialog().cancel();
                });

        return builder.create();
    }
}
