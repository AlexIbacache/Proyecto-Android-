package com.example.proyectoandroid.data;

import com.example.proyectoandroid.model.Maquinaria;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MaquinariaRepository {

    public List<Maquinaria> getMaquinarias() {
        List<Maquinaria> lista = new ArrayList<>();
        lista.add(new Maquinaria("Excavadora Caterpillar", Arrays.asList("Cuchara Principal", "Motor Diesel", "Sistema Hidráulico", "Orugas de Acero")));
        lista.add(new Maquinaria("Tractor John Deere", Arrays.asList("Ruedas Delanteras", "Ruedas Traseras", "Cabina del Operador", "Motor")));
        lista.add(new Maquinaria("Grúa Liebherr", Arrays.asList("Pluma Principal", "Contrapeso", "Gancho de Carga", "Sistema de Elevación")));
        return lista;
    }

    // Métodos para agregar, modificar, eliminar, etc. se implementarían aquí,
    // interactuando con la fuente de datos (Firestore, Room, etc.).
    public void eliminarMaquinaria(Maquinaria maquinariaAEliminar) {
        // Lógica de eliminación (ej. llamada a Firestore)
    }
}
