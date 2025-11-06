package com.example.proyectoandroid.model;

import com.google.firebase.firestore.DocumentReference;

public class RepuestoUsado {
    private DocumentReference repuestoRef;
    private String nombreRepuesto;
    private long cantidad;

    public RepuestoUsado() {}

    // Getters and Setters
    public DocumentReference getRepuestoRef() { return repuestoRef; }
    public void setRepuestoRef(DocumentReference repuestoRef) { this.repuestoRef = repuestoRef; }
    public String getNombreRepuesto() { return nombreRepuesto; }
    public void setNombreRepuesto(String nombreRepuesto) { this.nombreRepuesto = nombreRepuesto; }
    public long getCantidad() { return cantidad; }
    public void setCantidad(long cantidad) { this.cantidad = cantidad; }
}
