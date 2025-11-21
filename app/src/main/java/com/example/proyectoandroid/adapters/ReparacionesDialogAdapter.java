package com.example.proyectoandroid.adapters;

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
import com.example.proyectoandroid.model.Reparacion;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReparacionesDialogAdapter extends RecyclerView.Adapter<ReparacionesDialogAdapter.ViewHolder> {

    private final List<Reparacion> reparaciones;
    private final Context context;
    private final OnReparacionDeleteListener deleteListener;

    public interface OnReparacionDeleteListener {
        void onReparacionDelete(Reparacion reparacion);
    }

    public ReparacionesDialogAdapter(Context context, List<Reparacion> reparaciones, OnReparacionDeleteListener deleteListener) {
        this.context = context;
        this.reparaciones = reparaciones;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_reparacion_dialog, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Reparacion reparacion = reparaciones.get(position);
        holder.bind(reparacion, context);
        holder.btnEliminar.setOnClickListener(v -> deleteListener.onReparacionDelete(reparacion));
    }

    @Override
    public int getItemCount() {
        return reparaciones.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvNotas;
        RecyclerView rvPartes;
        ImageButton btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFechaReparacion);
            tvNotas = itemView.findViewById(R.id.tvNotasReparacion);
            rvPartes = itemView.findViewById(R.id.rvPartesReparadasDialog);
            btnEliminar = itemView.findViewById(R.id.btnEliminarReparacion);
        }

        public void bind(Reparacion reparacion, Context context) {
            if (reparacion.getFecha() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                tvFecha.setText("Fecha: " + sdf.format(reparacion.getFecha().toDate()));
            }
            tvNotas.setText("Notas: " + reparacion.getNotas());

            rvPartes.setLayoutManager(new LinearLayoutManager(context));
            // Usamos el ParteReparadaAdapter, pero sin listener de borrado para los repuestos
            ParteReparadaAdapter partesAdapter = new ParteReparadaAdapter(reparacion.getPartesReparadas(), null);
            rvPartes.setAdapter(partesAdapter);
        }
    }
}
