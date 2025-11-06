package com.example.proyectoandroid.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.util.List;

public class Reparacion {

    @DocumentId
    private String documentId;
    private Timestamp fecha;
    private String notas;
    private List<ParteReparada> partesReparadas;
    private String estado; // "Abierta" o "Cerrada"

    // Constructor vacío requerido para Firestore
    public Reparacion() {}

    public Reparacion(Timestamp fecha, String notas, List<ParteReparada> partesReparadas) {
        this.fecha = fecha;
        this.notas = notas;
        this.partesReparadas = partesReparadas;
        this.estado = "Abierta"; // Por defecto, una nueva reparación está abierta
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public Timestamp getFecha() {
        return fecha;
    }

    public void setFecha(Timestamp fecha) {
        this.fecha = fecha;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<ParteReparada> getPartesReparadas() {
        return partesReparadas;
    }

    public void setPartesReparadas(List<ParteReparada> partesReparadas) {
        this.partesReparadas = partesReparadas;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
