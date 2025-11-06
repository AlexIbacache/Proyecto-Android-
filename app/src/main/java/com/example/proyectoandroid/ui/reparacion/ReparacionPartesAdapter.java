package com.example.proyectoandroid.ui.reparacion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class ReparacionPartesAdapter extends RecyclerView.Adapter<ReparacionPartesAdapter.ReparacionParteViewHolder> {

    public static final String EXTRA_PARTE_NOMBRE_PARA_ADAPTADOR = "com.example.proyectoandroid.EXTRA_PARTE_NOMBRE_PARA_ADAPTADOR";

    private List<String> partes;
    private ReparacionViewModel viewModel;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onAnadirRepuestoClick(String parte);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ReparacionPartesAdapter(List<String> partes, ReparacionViewModel viewModel) {
        this.partes = partes;
        this.viewModel = viewModel;
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
        holder.bind(parte, listener);

        // Aquí puedes actualizar la UI para mostrar cuántos repuestos hay, si quieres.
        // int cantidadRepuestos = viewModel.getCantidadRepuestosParaParte(parte);
        // holder.tvCantidadRepuestos.setText(String.valueOf(cantidadRepuestos));
    }

    @Override
    public int getItemCount() {
        return partes.size();
    }

    public void updateData(List<String> nuevasPartes) {
        this.partes.clear();
        this.partes.addAll(nuevasPartes);
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

        public void bind(final String parte, final OnItemClickListener listener) {
            nombreParte.setText(parte);
            btnAnadirRepuesto.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onAnadirRepuestoClick(parte);
                    }
                }
            });
        }
    }
}
