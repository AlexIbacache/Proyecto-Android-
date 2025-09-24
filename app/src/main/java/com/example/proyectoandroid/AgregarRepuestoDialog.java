package com.example.proyectoandroid;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

public class AgregarRepuestoDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_agregar_repuesto, null);
        builder.setView(view)
                .setTitle("Agregar repuesto")
                .setPositiveButton("Aceptar", (dialog, id) -> {
                    // No guardar nada por ahora
                })
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.dismiss());

        return builder.create();
    }
}


