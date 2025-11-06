package com.example.proyectoandroid.ui.reparacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.Repuesto;

import java.util.List;
import java.util.Map;

public class ReparacionRepuestoAdapter extends RecyclerView.Adapter<ReparacionRepuestoAdapter.ReparacionRepuestoViewHolder> {

    private List<Repuesto> catalogoRepuestos;
    private Map<String, Integer> repuestosSeleccionados;

    public ReparacionRepuestoAdapter(List<Repuesto> catalogo, Map<String, Integer> seleccionados) {
        this.catalogoRepuestos = catalogo;
        this.repuestosSeleccionados = seleccionados;
    }

    @NonNull
    @Override
    public ReparacionRepuestoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repuesto_seleccion, parent, false);
        return new ReparacionRepuestoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReparacionRepuestoViewHolder holder, int position) {
        Repuesto repuesto = catalogoRepuestos.get(position);
        holder.tvNombreRepuesto.setText(repuesto.getNombre());

        boolean isSelected = repuestosSeleccionados.containsKey(repuesto.getDocumentId());
        holder.cbRepuesto.setChecked(isSelected);
        holder.etCantidad.setEnabled(isSelected);

        if (isSelected) {
            holder.etCantidad.setText(String.valueOf(repuestosSeleccionados.get(repuesto.getDocumentId())));
        } else {
            holder.etCantidad.setText("");
        }

        holder.cbRepuesto.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                holder.etCantidad.setEnabled(true);
                repuestosSeleccionados.put(repuesto.getDocumentId(), 1); // Cantidad por defecto
            } else {
                holder.etCantidad.setEnabled(false);
                holder.etCantidad.setText("");
                repuestosSeleccionados.remove(repuesto.getDocumentId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return catalogoRepuestos.size();
    }

    public void updateData(List<Repuesto> nuevosRepuestos) {
        this.catalogoRepuestos = nuevosRepuestos;
        notifyDataSetChanged();
    }

    public static class ReparacionRepuestoViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbRepuesto;
        TextView tvNombreRepuesto;
        EditText etCantidad;

        public ReparacionRepuestoViewHolder(@NonNull View itemView) {
            super(itemView);
            cbRepuesto = itemView.findViewById(R.id.cbRepuesto);
            tvNombreRepuesto = itemView.findViewById(R.id.tvNombreRepuesto);
            etCantidad = itemView.findViewById(R.id.etCantidad);
        }
    }
}
