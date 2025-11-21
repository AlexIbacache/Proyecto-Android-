package com.example.proyectoandroid.model;

import com.google.firebase.firestore.DocumentId;
import java.io.Serializable;
import java.util.Objects;

public class Repuesto implements Serializable {

    @DocumentId
    private String documentId;
    private String nombre;
    private String codigo;
    private int cantidad;

    // Constructor vacío requerido para Firestore
    public Repuesto() {}

    public Repuesto(String nombre, String codigo, int cantidad) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.cantidad = cantidad;
    }

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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Repuesto repuesto = (Repuesto) o;
        return Objects.equals(documentId, repuesto.documentId) &&
               Objects.equals(nombre, repuesto.nombre) &&
               Objects.equals(codigo, repuesto.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(documentId, nombre, codigo);
    }
}
