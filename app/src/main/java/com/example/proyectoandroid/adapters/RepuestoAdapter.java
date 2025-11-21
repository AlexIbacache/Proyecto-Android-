package com.example.proyectoandroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.Repuesto;

import java.util.List;

public class RepuestoAdapter extends RecyclerView.Adapter<RepuestoAdapter.ViewHolder> {

    private final List<Repuesto> repuestos;
    private final OnItemDeleteListener deleteListener;

    public interface OnItemDeleteListener {
        void onDeleteClick(int position);
    }

    public RepuestoAdapter(List<Repuesto> repuestos, OnItemDeleteListener deleteListener) {
        this.repuestos = repuestos;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repuesto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Repuesto repuesto = repuestos.get(position);
        holder.bind(repuesto);

        if (deleteListener != null) {
            holder.btnEliminar.setVisibility(View.VISIBLE);
            holder.btnEliminar.setOnClickListener(v -> deleteListener.onDeleteClick(holder.getAdapterPosition()));
        } else {
            holder.btnEliminar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return repuestos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCodigo, tvCantidad;
        ImageButton btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRepuesto);
            tvCodigo = itemView.findViewById(R.id.tvCodigoRepuesto);
            tvCantidad = itemView.findViewById(R.id.tvCantidadRepuesto);
            btnEliminar = itemView.findViewById(R.id.btnEliminarRepuestoIndividual);
        }

        public void bind(Repuesto repuesto) {
            tvNombre.setText(repuesto.getNombre());
            tvCodigo.setText("Código: " + repuesto.getCodigo());
            tvCantidad.setText("Cantidad: " + repuesto.getCantidad());
        }
    }
}
