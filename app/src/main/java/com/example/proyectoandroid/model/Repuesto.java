package com.example.proyectoandroid.model;

import com.google.firebase.firestore.DocumentId;

public class Repuesto {
    @DocumentId
    private String id;
    private String nombre;
    private String codigoNParte;

    public Repuesto() {
        // Constructor vacío requerido por Firestore
    }

    public Repuesto(String id, String nombre, String codigoNParte) {
        this.id = id;
        this.nombre = nombre;
        this.codigoNParte = codigoNParte;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCodigoNParte() {
        return codigoNParte;
    }

    public void setCodigoNParte(String codigoNParte) {
        this.codigoNParte = codigoNParte;
    }
}
