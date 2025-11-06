package com.example.proyectoandroid.ui.reportes;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.model.Maquinaria;
import com.google.android.material.chip.Chip;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReportesAdapter extends RecyclerView.Adapter<ReportesAdapter.ReporteViewHolder> {

    private List<Maquinaria> maquinariaList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Maquinaria maquinaria);
    }

    public ReportesAdapter(List<Maquinaria> maquinariaList, OnItemClickListener listener) {
        this.maquinariaList = maquinariaList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReporteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reporte_maquinaria, parent, false);
        return new ReporteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReporteViewHolder holder, int position) {
        Maquinaria maquinaria = maquinariaList.get(position);
        holder.bind(maquinaria, listener);
    }

    @Override
    public int getItemCount() {
        return maquinariaList.size();
    }

    static class ReporteViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNumeroIdentificador;
        private TextView tvNombreMaquina;
        private TextView tvFechaIngreso;
        private Chip chipEstado;

        public ReporteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroIdentificador = itemView.findViewById(R.id.tvNumeroIdentificador);
            tvNombreMaquina = itemView.findViewById(R.id.tvNombreMaquina);
            tvFechaIngreso = itemView.findViewById(R.id.tvFechaIngreso);
            chipEstado = itemView.findViewById(R.id.chipEstado);
        }

        public void bind(final Maquinaria maquinaria, final OnItemClickListener listener) {
            tvNumeroIdentificador.setText(maquinaria.getNumeroIdentificador());
            tvNombreMaquina.setText(maquinaria.getNombre());

            SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM, yyyy", Locale.getDefault());
            if (maquinaria.getFechaIngreso() != null) {
                tvFechaIngreso.setText(sdf.format(maquinaria.getFechaIngreso().toDate()));
            }

            if (maquinaria.isEstado()) {
                chipEstado.setText("Operativa");
                chipEstado.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_green_light)));
            } else {
                chipEstado.setText("En Reparación");
                chipEstado.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(itemView.getContext(), android.R.color.holo_red_light)));
            }

            itemView.setOnClickListener(v -> listener.onItemClick(maquinaria));
        }
    }
}
