package com.example.proyectoandroid.ui.reparacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ReparacionPartesAdapter extends RecyclerView.Adapter<ReparacionPartesAdapter.ReparacionParteViewHolder> {

    private List<String> partes;

    public ReparacionPartesAdapter(List<String> partes) {
        this.partes = partes;
    }

    @NonNull
    @Override
    public ReparacionParteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reparacion_parte, parent, false);
        return new ReparacionParteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReparacionParteViewHolder holder, int position) {
        String parte = partes.get(position);
        holder.nombreParte.setText(parte);

        holder.btnAnadirRepuesto.setOnClickListener(v -> {
            // Lógica para abrir el diálogo de añadir repuesto
            Toast.makeText(v.getContext(), "Añadir repuesto para: " + parte, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return partes.size();
    }

    public void updateData(List<String> nuevasPartes) {
        this.partes = nuevasPartes;
        notifyDataSetChanged();
    }

    public static class ReparacionParteViewHolder extends RecyclerView.ViewHolder {
        TextView nombreParte;
        MaterialButton btnAnadirRepuesto;

        public ReparacionParteViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreParte = itemView.findViewById(R.id.tvNombreParteReparacion);
            btnAnadirRepuesto = itemView.findViewById(R.id.btnAnadirRepuesto);
        }
    }
}
