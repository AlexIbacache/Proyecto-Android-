package com.example.proyectoandroid.ui.repuesto;

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
import java.util.Locale;

public class RepuestoAdapter extends RecyclerView.Adapter<RepuestoAdapter.RepuestoViewHolder> {

    private List<Repuesto> repuestos;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onDeleteClick(Repuesto repuesto);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public RepuestoAdapter(List<Repuesto> repuestos) {
        this.repuestos = repuestos;
    }

    @NonNull
    @Override
    public RepuestoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repuesto, parent, false);
        return new RepuestoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RepuestoViewHolder holder, int position) {
        Repuesto repuesto = repuestos.get(position);
        holder.bind(repuesto, listener);
    }

    @Override
    public int getItemCount() {
        return repuestos.size();
    }

    public void updateData(List<Repuesto> nuevosRepuestos) {
        this.repuestos = nuevosRepuestos;
        notifyDataSetChanged();
    }

    public static class RepuestoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCodigo, tvCantidad;
        ImageButton btnEliminar;

        public RepuestoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreRepuestoItem);
            tvCodigo = itemView.findViewById(R.id.tvCodigoRepuestoItem);
            tvCantidad = itemView.findViewById(R.id.tvCantidadRepuestoItem);
            btnEliminar = itemView.findViewById(R.id.btnEliminarRepuesto);
        }

        public void bind(final Repuesto repuesto, final OnItemClickListener listener) {
            tvNombre.setText(repuesto.getNombre());
            tvCodigo.setText(String.format(Locale.getDefault(), "Código: %s", repuesto.getCodigo()));
            tvCantidad.setText(String.format(Locale.getDefault(), "Cant: %d", repuesto.getCantidad()));

            btnEliminar.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onDeleteClick(repuesto);
                    }
                }
            });
        }
    }
}
