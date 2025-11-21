package com.example.proyectoandroid.ui.reportes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;
import com.example.proyectoandroid.adapters.ParteReparadaAdapter;
import com.example.proyectoandroid.model.Reparacion;
import com.example.proyectoandroid.model.Repuesto;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReparacionesDialogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_ITEM = 1;

    private List<Reparacion> reparaciones;
    private final Context context;
    private final OnReparacionDeleteListener deleteListener;
    private final OnRepuestoDeleteFromDialogListener repuestoDeleteListener;
    private boolean isLoading = true;

    public interface OnReparacionDeleteListener {
        void onReparacionDelete(Reparacion reparacion);
    }

    public interface OnRepuestoDeleteFromDialogListener {
        void onRepuestoDelete(Reparacion reparacion, String parteNombre, Repuesto repuesto);
    }

    public ReparacionesDialogAdapter(Context context, List<Reparacion> reparaciones, OnReparacionDeleteListener deleteListener, OnRepuestoDeleteFromDialogListener repuestoDeleteListener) {
        this.context = context;
        this.reparaciones = reparaciones;
        this.deleteListener = deleteListener;
        this.repuestoDeleteListener = repuestoDeleteListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_reparacion_dialog_placeholder, parent, false);
            return new LoadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_reparacion_dialog, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            Reparacion reparacion = reparaciones.get(position);
            ((ItemViewHolder) holder).bind(reparacion, context, repuestoDeleteListener);
            ((ItemViewHolder) holder).btnEliminar.setOnClickListener(v -> deleteListener.onReparacionDelete(reparacion));
        }
    }

    @Override
    public int getItemCount() {
        return isLoading ? 5 : reparaciones.size(); // Muestra 5 esqueletos mientras carga
    }

    @Override
    public int getItemViewType(int position) {
        return isLoading ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void updateData(List<Reparacion> newReparaciones) {
        this.isLoading = false;
        this.reparaciones.clear();
        this.reparaciones.addAll(newReparaciones);
        notifyDataSetChanged();
    }

    // ViewHolder para los datos reales
    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvNotas;
        RecyclerView rvPartes;
        ImageButton btnEliminar;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFechaReparacion);
            tvNotas = itemView.findViewById(R.id.tvNotasReparacion);
            rvPartes = itemView.findViewById(R.id.rvPartesReparadasDialog);
            btnEliminar = itemView.findViewById(R.id.btnEliminarReparacion);
        }

        public void bind(Reparacion reparacion, Context context, OnRepuestoDeleteFromDialogListener listener) {
            if (reparacion.getFecha() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvFecha.setText("Fecha: " + sdf.format(reparacion.getFecha().toDate()));
            }
            tvNotas.setText("Notas: " + reparacion.getNotas());

            rvPartes.setLayoutManager(new LinearLayoutManager(context));
            ParteReparadaAdapter partesAdapter = new ParteReparadaAdapter(reparacion.getPartesReparadas(), null, (parteNombre, repuesto) -> {
                listener.onRepuestoDelete(reparacion, parteNombre, repuesto);
            });
            rvPartes.setAdapter(partesAdapter);
        }
    }

    // ViewHolder para el esqueleto de carga
    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
