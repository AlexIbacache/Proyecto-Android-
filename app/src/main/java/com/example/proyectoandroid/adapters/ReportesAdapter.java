package com.example.proyectoandroid.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
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

public class ReportesAdapter extends RecyclerView.Adapter<ReportesAdapter.ViewHolder> {

    private List<Maquinaria> maquinarias;
    private final OnItemClickListener listener;
    private final OnMaquinariaStatusChangeListener statusChangeListener;
    private final OnMaquinariaDeleteListener deleteListener;

    public interface OnItemClickListener {
        void onItemClick(Maquinaria maquinaria);
        void onMaquinariaToggled(Maquinaria maquinaria, boolean isSelected);
    }

    public interface OnMaquinariaStatusChangeListener {
        void onStatusChange(Maquinaria maquinaria);
    }

    public interface OnMaquinariaDeleteListener {
        void onDelete(Maquinaria maquinaria);
    }

    public ReportesAdapter(List<Maquinaria> maquinarias, OnItemClickListener listener, OnMaquinariaStatusChangeListener statusChangeListener, OnMaquinariaDeleteListener deleteListener) {
        this.maquinarias = maquinarias;
        this.listener = listener;
        this.statusChangeListener = statusChangeListener;
        this.deleteListener = deleteListener;
    }

    public ReportesAdapter(List<Maquinaria> maquinarias, OnItemClickListener listener) {
        this(maquinarias, listener, null, null);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reporte_maquinaria, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Maquinaria maquinaria = maquinarias.get(position);
        holder.bind(maquinaria, listener, statusChangeListener, deleteListener);
    }

    @Override
    public int getItemCount() {
        return maquinarias.size();
    }

    public void updateData(List<Maquinaria> newMaquinarias) {
        maquinarias.clear();
        maquinarias.addAll(newMaquinarias);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvNumero, tvFecha;
        Chip chipEstado;
        CheckBox checkboxSelect;
        ImageButton btnEliminar;
        View infoContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreMaquina);
            tvNumero = itemView.findViewById(R.id.tvNumeroIdentificador);
            tvFecha = itemView.findViewById(R.id.tvFechaIngreso);
            chipEstado = itemView.findViewById(R.id.chipEstado);
            checkboxSelect = itemView.findViewById(R.id.checkbox_maquinaria_select);
            btnEliminar = itemView.findViewById(R.id.btnEliminarMaquinaria);
            infoContainer = itemView.findViewById(R.id.info_container);
        }

        public void bind(final Maquinaria maquinaria, final OnItemClickListener listener, final OnMaquinariaStatusChangeListener statusListener, final OnMaquinariaDeleteListener deleteListener) {
            tvNombre.setText(maquinaria.getNombre());
            tvNumero.setText("EQ: " + maquinaria.getNumeroIdentificador());

            if (maquinaria.getFechaIngreso() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd 'de' MMMM, yyyy", Locale.getDefault());
                tvFecha.setText(sdf.format(maquinaria.getFechaIngreso().toDate()));
            } else {
                tvFecha.setText("Fecha no disponible");
            }

            Context context = itemView.getContext();
            if (maquinaria.isEstado()) {
                chipEstado.setText("Operativa");
                chipEstado.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_green_light)));
                checkboxSelect.setEnabled(true);
            } else {
                chipEstado.setText("No Operativa");
                chipEstado.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(context, android.R.color.holo_red_light)));
                checkboxSelect.setEnabled(false);
            }

            checkboxSelect.setOnCheckedChangeListener(null);
            checkboxSelect.setChecked(maquinaria.isSelected());
            checkboxSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(buttonView.isEnabled()) {
                    listener.onMaquinariaToggled(maquinaria, isChecked);
                }
            });

            infoContainer.setOnClickListener(v -> listener.onItemClick(maquinaria));
            chipEstado.setOnClickListener(v -> statusListener.onStatusChange(maquinaria));
            btnEliminar.setOnClickListener(v -> deleteListener.onDelete(maquinaria));
        }
    }
}
