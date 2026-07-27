package org.alejandroecheverria.bean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



public class Consulta {
    private int codigoConsulta;
    private LocalDateTime fechaConsulta;
    private String motivo;
    private String diagnostico;
    private String observaciones;
    private int codigoMascota;
    private int codigoVeterinario;

    public Consulta() {
    }

    public Consulta(int codigoConsulta, LocalDateTime fechaConsulta, String motivo, String diagnostico, String observaciones, int codigoMascota, int codigoVeterinario) {
        this.codigoConsulta = codigoConsulta;
        this.fechaConsulta = fechaConsulta;
        this.motivo = motivo;
        this.diagnostico = diagnostico;
        this.observaciones = observaciones;
        this.codigoMascota = codigoMascota;
        this.codigoVeterinario = codigoVeterinario;
    }

    public int getCodigoConsulta() {
        return codigoConsulta;
    }

    public void setCodigoConsulta(int codigoConsulta) {
        this.codigoConsulta = codigoConsulta;
    }

    public LocalDateTime getFechaConsulta() {
        return fechaConsulta;
    }

    public void setFechaConsulta(LocalDateTime fechaConsulta) {
        this.fechaConsulta = fechaConsulta;
    }
    
    public String getNuevaFechaConsulta() {
        if (fechaConsulta == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return fechaConsulta.format(formatter);
    } 

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public int getCodigoMascota() {
        return codigoMascota;
    }

    public void setCodigoMascota(int codigoMascota) {
        this.codigoMascota = codigoMascota;
    }

    public int getCodigoVeterinario() {
        return codigoVeterinario;
    }

    public void setCodigoVeterinario(int codigoVeterinario) {
        this.codigoVeterinario = codigoVeterinario;
    }
    
    @Override
    public String toString(){
    
        return codigoConsulta + " | " + fechaConsulta + " | " + diagnostico + " | " + observaciones;
    }
    
    
    
}
