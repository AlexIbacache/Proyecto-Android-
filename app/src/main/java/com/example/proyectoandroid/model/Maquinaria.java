package com.example.proyectoandroid.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.util.List;

// Plain Old Java Object (POJO) para representar una Maquinaria
public class Maquinaria {

    @DocumentId
    private String documentId;
    private String nombre;
    private String numeroIdentificador;
    private Timestamp fechaIngreso;
    private String descripcion;
    private List<String> partesPrincipales;
    private boolean estado;

    // Constructor vacío requerido para Firestore
    public Maquinaria() {}

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNumeroIdentificador() {
        return numeroIdentificador;
    }

    public void setNumeroIdentificador(String numeroIdentificador) {
        this.numeroIdentificador = numeroIdentificador;
    }

    public Timestamp getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Timestamp fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<String> getPartesPrincipales() {
        return partesPrincipales;
    }

    public void setPartesPrincipales(List<String> partesPrincipales) {
        this.partesPrincipales = partesPrincipales;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
