package org.alejandroecheverria.bean;


import java.util.Date;
import java.sql.Time;




public class Cita {
    private int codigoCita;
    private Date fechaCita;
    private Time horaCita;
    private String motivo;
    private String estado;
    private int codigoMascota;
    private int codigoVeterinario;

    public Cita() {
    }

    public Cita(int codigoCita, Date fechaCita, Time horaCita, String motivo, String estado, int codigoMascota, int codigoVeterinario) {
        this.codigoCita = codigoCita;
        this.fechaCita = fechaCita;
        this.horaCita = horaCita;
        this.motivo = motivo;
        this.estado = estado;
        this.codigoMascota = codigoMascota;
        this.codigoVeterinario = codigoVeterinario;
    }

    public int getCodigoCita() {
        return codigoCita;
    }

    public void setCodigoCita(int codigoCita) {
        this.codigoCita = codigoCita;
    }

    public Date getFechaCita() {
        return fechaCita;
    }

    public void setFechaCita(Date fechaCita) {
        this.fechaCita = fechaCita;
    }

    public Time getHoraCita() {
        return horaCita;
    }

    public void setHoraCita(Time horaCita) {
        this.horaCita = horaCita;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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
    
    
    
}
