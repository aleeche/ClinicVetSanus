package org.alejandroecheverria.bean;

import java.util.Date;


public class Tratamiento {
    private int codigoTratamiento;
    private String descripcion;
    private Date fechaInicio;
    private Date fechaFin;
    private String medicamentosIndicados;
    private int codigoConsulta;

    public Tratamiento() {
    }

    public Tratamiento(int codigoTratamiento, String descripcion, Date fechaInicio, Date fechaFin, String medicamentosIndicados, int codigoConsulta) {
        this.codigoTratamiento = codigoTratamiento;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.medicamentosIndicados = medicamentosIndicados;
        this.codigoConsulta = codigoConsulta;
    }

    public int getCodigoTratamiento() {
        return codigoTratamiento;
    }

    public void setCodigoTratamiento(int codigoTratamiento) {
        this.codigoTratamiento = codigoTratamiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getMedicamentosIndicados() {
        return medicamentosIndicados;
    }

    public void setMedicamentosIndicados(String medicamentosIndicados) {
        this.medicamentosIndicados = medicamentosIndicados;
    }

    public int getCodigoConsulta() {
        return codigoConsulta;
    }

    public void setCodigoConsulta(int codigoConsulta) {
        this.codigoConsulta = codigoConsulta;
    }
    
    
    
    
}
