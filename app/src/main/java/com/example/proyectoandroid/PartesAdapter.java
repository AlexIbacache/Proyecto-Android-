package com.example.proyectoandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PartesAdapter extends RecyclerView.Adapter<PartesAdapter.ParteViewHolder> {

    private List<String> partes;
    private Context context;

    public PartesAdapter(List<String> partes, Context context) {
        this.partes = partes;
        this.context = context;
    }

    @NonNull
    @Override
    public ParteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_parte, parent, false);
        return new ParteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParteViewHolder holder, int position) {
        String parte = partes.get(position);
        holder.tvNombreParte.setText(parte);

        // Listener para el botón Modificar
        holder.btnModificarParte.setOnClickListener(v -> {
            Toast.makeText(context, "Modificar: " + parte, Toast.LENGTH_SHORT).show();
            // Aquí irá la lógica para abrir un diálogo o fragmento de edición
        });

        // Listener para el botón Eliminar
        holder.btnEliminarParte.setOnClickListener(v -> {
            Toast.makeText(context, "Eliminar: " + parte, Toast.LENGTH_SHORT).show();
            // Aquí irá la lógica para eliminar la parte de Firestore y actualizar la lista
        });
    }

    @Override
    public int getItemCount() {
        return partes.size();
    }

    // Método para actualizar los datos del adaptador
    public void updateData(List<String> nuevasPartes) {
        this.partes = nuevasPartes;
        notifyDataSetChanged(); // Notifica al RecyclerView que los datos han cambiado
    }

    // Clase ViewHolder que contiene las vistas de la fila
    public static class ParteViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreParte;
        ImageButton btnModificarParte;
        ImageButton btnEliminarParte;

        public ParteViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreParte = itemView.findViewById(R.id.tvNombreParte);
            btnModificarParte = itemView.findViewById(R.id.btnModificarParte);
            btnEliminarParte = itemView.findViewById(R.id.btnEliminarParte);
        }
    }
}
