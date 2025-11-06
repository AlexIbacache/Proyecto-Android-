package com.example.proyectoandroid.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentId;

import java.util.List;

public class Reparacion {

    @DocumentId
    private String documentId;
    private Timestamp fecha;
    private String notas;
    // Cambiamos el Map por una lista de nuestro nuevo objeto
    private List<ParteReparada> partesReparadas;

    // Constructor vacío requerido para Firestore
    public Reparacion() {}

    public Reparacion(Timestamp fecha, String notas, List<ParteReparada> partesReparadas) {
        this.fecha = fecha;
        this.notas = notas;
        this.partesReparadas = partesReparadas;
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
}
