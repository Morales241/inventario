/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "EquipoDeEscritorio")
@PrimaryKeyJoinColumn(name = "id_EquipoDeEscritorio") // Une con la PK del padre
public class EquipoDeEscritorio extends EquipoDeComputo {

    @Column(name = "Nombre", nullable = false)
    private String nombreEquipo;

    @Column(name = "NoSerie", nullable = false)
    private String noSerie;

    @Column(name = "FinalGarantia")
    private String finalGarantia;

    @Column(nullable = false)
    private String cuenta;
    
    @Column(nullable = false)
    private String disco;
    
    @Column(nullable = false)
    private String procesador;

    // Generar Getters y Setters

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public String getNoSerie() {
        return noSerie;
    }

    public void setNoSerie(String noSerie) {
        this.noSerie = noSerie;
    }

    public String getFinalGarantia() {
        return finalGarantia;
    }

    public void setFinalGarantia(String finalGarantia) {
        this.finalGarantia = finalGarantia;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getDisco() {
        return disco;
    }

    public void setDisco(String disco) {
        this.disco = disco;
    }

    public String getProcesador() {
        return procesador;
    }

    public void setProcesador(String procesador) {
        this.procesador = procesador;
    }
    
}
