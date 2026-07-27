package org.alejandroecheverria.bean;



public class Receta {
    private int codigoReceta;
    private String dosis;
    private String frecuencia;
    private int duracionDosis;
    private String indicaciones;
    private int codigoConsulta;
    private int codigoMedicamento;

    public Receta() {
    }

    public Receta(int codigoReceta, String dosis, String frecuencia, int duracionDosis, String indicaciones, int codigoConsulta, int codigoMedicamento) {
        this.codigoReceta = codigoReceta;
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.duracionDosis = duracionDosis;
        this.indicaciones = indicaciones;
        this.codigoConsulta = codigoConsulta;
        this.codigoMedicamento = codigoMedicamento;
    }

    public int getCodigoReceta() {
        return codigoReceta;
    }

    public void setCodigoReceta(int codigoReceta) {
        this.codigoReceta = codigoReceta;
    }

    public String getDosis() {
        return dosis;
    }

    public void setDosis(String dosis) {
        this.dosis = dosis;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public int getDuracionDosis() {
        return duracionDosis;
    }

    public void setDuracionDosis(int duracionDosis) {
        this.duracionDosis = duracionDosis;
    }

    public String getIndicaciones() {
        return indicaciones;
    }

    public void setIndicaciones(String indicaciones) {
        this.indicaciones = indicaciones;
    }

    public int getCodigoConsulta() {
        return codigoConsulta;
    }

    public void setCodigoConsulta(int codigoConsulta) {
        this.codigoConsulta = codigoConsulta;
    }

    public int getCodigoMedicamento() {
        return codigoMedicamento;
    }

    public void setCodigoMedicamento(int codigoMedicamento) {
        this.codigoMedicamento = codigoMedicamento;
    }
    
    
    
}
