package com.example.proyectoandroid.model;

import com.google.firebase.Timestamp;
import java.util.List;

public class Reparacion {
    private Timestamp fecha;
    private String notas;
    private List<RepuestoUsado> repuestosUsados;

    public Reparacion() {}

    // Getters and Setters
    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public List<RepuestoUsado> getRepuestosUsados() { return repuestosUsados; }
    public void setRepuestosUsados(List<RepuestoUsado> repuestosUsados) { this.repuestosUsados = repuestosUsados; }
}
