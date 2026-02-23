package Dtos;

import java.time.LocalDate;

public class EquipoEscritorioDTO extends EquipoBaseDTO {

    private String nombreEquipo;
    private LocalDate finalGarantia;
    private String cuenta;    
    private String sisOpertativo;
    private String userRed;
    private Boolean mochila;
    private Boolean mouse;

    public EquipoEscritorioDTO() {
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }

    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public LocalDate getFinalGarantia() {
        return finalGarantia;
    }

    public void setFinalGarantia(LocalDate finalGarantia) {
        this.finalGarantia = finalGarantia;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getSisOpertativo() {
        return sisOpertativo;
    }

    public void setSisOpertativo(String sisOpertativo) {
        this.sisOpertativo = sisOpertativo;
    }

    public String getUserRed() {
        return userRed;
    }

    public void setUserRed(String userRed) {
        this.userRed = userRed;
    }

    public Boolean getMochila() {
        return mochila;
    }

    public void setMochila(Boolean mochila) {
        this.mochila = mochila;
    }

    public Boolean getMouse() {
        return mouse;
    }

    public void setMouse(Boolean mouse) {
        this.mouse = mouse;
    }

    @Override
    public String toString() {
        return nombreEquipo + ", finalGarantia=" + finalGarantia + ", cuenta=" + cuenta;
    }
     
}
