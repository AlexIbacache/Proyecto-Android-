package com.example.proyectoandroid;

import java.util.List;

// Plain Old Java Object (POJO) para representar una Maquinaria
public class Maquinaria {
    private String nombre;
    private List<String> partes;

    // Constructor vacío requerido para Firestore (aunque no lo usemos ahora, es buena práctica)
    public Maquinaria() {}

    public Maquinaria(String nombre, List<String> partes) {
        this.nombre = nombre;
        this.partes = partes;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<String> getPartes() {
        return partes;
    }

    public void setPartes(List<String> partes) {
        this.partes = partes;
    }
}
