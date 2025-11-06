package com.example.proyectoandroid.ui.maquinaria;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectoandroid.R;

import java.util.List;

public class PartesAdapter extends RecyclerView.Adapter<PartesAdapter.ParteViewHolder> {

    private List<String> partes;

    public PartesAdapter(List<String> partes) {
        this.partes = partes;
    }

    @NonNull
    @Override
    public ParteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_parte, parent, false);
        return new ParteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParteViewHolder holder, int position) {
        String parte = partes.get(position);
        holder.tvNombreParte.setText(parte);

        holder.btnModificarParte.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Modificar: " + parte, Toast.LENGTH_SHORT).show();
        });

        holder.btnEliminarParte.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Eliminar: " + parte, Toast.LENGTH_SHORT).show();
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

    public static class ParteViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPartIcon;
        TextView tvNombreParte;
        ImageButton btnModificarParte;
        ImageButton btnEliminarParte;

        public ParteViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPartIcon = itemView.findViewById(R.id.ivPartIcon);
            tvNombreParte = itemView.findViewById(R.id.tvNombreParte);
            btnModificarParte = itemView.findViewById(R.id.btnModificarParte);
            btnEliminarParte = itemView.findViewById(R.id.btnEliminarParte);
        }
    }
}
