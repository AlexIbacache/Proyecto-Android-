package com.example.proyectoandroid.model;

import java.io.Serializable;
import java.util.List;

// POJO para encapsular una parte y su lista de repuestos
public class ParteReparada implements Serializable {
    private String nombreParte;
    private List<Repuesto> repuestos;

    // Constructor vacío requerido para Firestore y Serializacion
    public ParteReparada() {}

    public ParteReparada(String nombreParte, List<Repuesto> repuestos) {
        this.nombreParte = nombreParte;
        this.repuestos = repuestos;
    }

    // Getters y Setters
    public String getNombreParte() {
        return nombreParte;
    }

    public void setNombreParte(String nombreParte) {
        this.nombreParte = nombreParte;
    }

    public List<Repuesto> getRepuestos() {
        return repuestos;
    }

    public void setRepuestos(List<Repuesto> repuestos) {
        this.repuestos = repuestos;
    }
}
