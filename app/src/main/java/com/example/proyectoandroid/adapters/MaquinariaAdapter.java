package com.example.proyectoandroid.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.proyectoandroid.model.Maquinaria;
import com.example.proyectoandroid.R;

import java.util.List;

public class MaquinariaAdapter extends ArrayAdapter<Maquinaria> {

    private final LayoutInflater inflater;
    private final List<Maquinaria> maquinarias;

    public MaquinariaAdapter(@NonNull Context context, @NonNull List<Maquinaria> maquinarias) {
        super(context, R.layout.spinner_item_custom, maquinarias);
        inflater = LayoutInflater.from(context);
        this.maquinarias = maquinarias;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return createItemView(position, convertView, parent);
    }

    private View createItemView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_item_custom, parent, false);
        }
        TextView textView = convertView.findViewById(android.R.id.text1); // Using default TextView ID for simplicity
        Maquinaria maquinaria = getItem(position);
        if (maquinaria != null) {
            textView.setText(maquinaria.getNombre());
        } else {
            textView.setText("Selecciona una maquinaria"); // Placeholder or empty state
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return maquinarias.size();
    }

    @Nullable
    @Override
    public Maquinaria getItem(int position) {
        return maquinarias.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateMaquinarias(List<Maquinaria> newMaquinarias) {
        maquinarias.clear();
        maquinarias.addAll(newMaquinarias);
        notifyDataSetChanged();
    }
}
