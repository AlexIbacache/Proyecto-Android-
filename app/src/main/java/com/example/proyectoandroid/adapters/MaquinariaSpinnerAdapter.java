package com.example.proyectoandroid.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.proyectoandroid.model.Maquinaria;
import java.util.List;

public class MaquinariaSpinnerAdapter extends ArrayAdapter<Maquinaria> {

    public MaquinariaSpinnerAdapter(@NonNull Context context, @NonNull List<Maquinaria> maquinarias) {
        super(context, android.R.layout.simple_spinner_item, maquinarias);
        setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setText(getItem(position).getNombre());
        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        TextView view = (TextView) super.getDropDownView(position, convertView, parent);
        view.setText(getItem(position).getNombre());
        return view;
    }
}
