package com.example.proyectoandroid.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.ParteReparada;
import com.example.proyectoandroid.model.Repuesto;

import java.util.List;

public class ParteReparadaAdapter extends RecyclerView.Adapter<ParteReparadaAdapter.ViewHolder> {

    private List<ParteReparada> partes;
    private final OnAddRepuestoClickListener addListener;
    private final OnRepuestoDeleteListener deleteListener;

    public interface OnAddRepuestoClickListener {
        void onAddRepuestoClick(String parteNombre, List<Repuesto> repuestosExistentes);
    }

    public interface OnRepuestoDeleteListener {
        void onRepuestoDelete(String parteNombre, Repuesto repuesto);
    }

    public ParteReparadaAdapter(List<ParteReparada> partes, @Nullable OnAddRepuestoClickListener addListener, @Nullable OnRepuestoDeleteListener deleteListener) {
        this.partes = partes;
        this.addListener = addListener;
        this.deleteListener = deleteListener;
    }

    // Constructor simplificado para cuando no se necesita el listener de borrado
    public ParteReparadaAdapter(List<ParteReparada> partes, @Nullable OnAddRepuestoClickListener addListener) {
        this(partes, addListener, null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reparacion_parte, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ParteReparada parte = partes.get(position);
        holder.bind(parte, addListener, deleteListener);
    }

    @Override
    public int getItemCount() {
        return partes.size();
    }

    public void setPartes(List<ParteReparada> newPartes) {
        this.partes = newPartes;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreParte;
        Button btnAnadirRepuesto;
        RecyclerView rvRepuestos;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreParte = itemView.findViewById(R.id.tvNombreParteReparacion);
            btnAnadirRepuesto = itemView.findViewById(R.id.btnAnadirRepuesto);
            rvRepuestos = itemView.findViewById(R.id.rvRepuestosDeParte);
        }

        public void bind(final ParteReparada parte, @Nullable final OnAddRepuestoClickListener addListener, @Nullable final OnRepuestoDeleteListener deleteListener) {
            tvNombreParte.setText(parte.getNombreParte());

            if (addListener != null) {
                btnAnadirRepuesto.setVisibility(View.VISIBLE);
                btnAnadirRepuesto.setOnClickListener(v -> addListener.onAddRepuestoClick(parte.getNombreParte(), parte.getRepuestos()));
            } else {
                btnAnadirRepuesto.setVisibility(View.GONE);
            }

            // Configurar el RecyclerView anidado para los repuestos
            rvRepuestos.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            RepuestoAdapter repuestoAdapter = new RepuestoAdapter(parte.getRepuestos(), position -> {
                if (deleteListener != null) {
                    Repuesto repuestoAEliminar = parte.getRepuestos().get(position);
                    deleteListener.onRepuestoDelete(parte.getNombreParte(), repuestoAEliminar);
                }
            });
            rvRepuestos.setAdapter(repuestoAdapter);
        }
    }
}
