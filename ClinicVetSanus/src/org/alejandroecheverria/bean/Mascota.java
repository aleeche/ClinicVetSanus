package org.alejandroecheverria.bean;

import java.util.Date;


public class Mascota {
    private int codigoMascota;
    private String nombreMascota;
    private String especie;
    private String raza;
    private String sexo;
    private Date fechaNacimiento;
    private String color;
    private float pesoActualKg;
    private int codigoCliente;

    public Mascota() {
    }

    public Mascota(int codigoMascota, String nombreMascota, String especie, String raza, String sexo, Date fechaNacimiento, String color, float pesoActualKg, int codigoCliente) {
        this.codigoMascota = codigoMascota;
        this.nombreMascota = nombreMascota;
        this.especie = especie;
        this.raza = raza;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.color = color;
        this.pesoActualKg = pesoActualKg;
        this.codigoCliente = codigoCliente;
    }

    public int getCodigoMascota() {
        return codigoMascota;
    }

    public void setCodigoMascota(int codigoMascota) {
        this.codigoMascota = codigoMascota;
    }

    public String getNombreMascota() {
        return nombreMascota;
    }

    public void setNombreMascota(String nombreMascota) {
        this.nombreMascota = nombreMascota;
    }

    public String getEspecie() {
        return especie;
    }

    public void setEspecie(String especie) {
        this.especie = especie;
    }

    public String getRaza() {
        return raza;
    }

    public void setRaza(String raza) {
        this.raza = raza;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public float getPesoActualKg() {
        return pesoActualKg;
    }

    public void setPesoActualKg(float pesoActualKg) {
        this.pesoActualKg = pesoActualKg;
    }

    public int getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(int codigoCliente) {
        this.codigoCliente = codigoCliente;
    }
    
    @Override
    public String toString(){
    
        return codigoMascota + " | " + nombreMascota + " | " + especie + " | " + raza + " | " + color;
    }
    
}

