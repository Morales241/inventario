package Dtos;

import java.time.LocalDate;

public class EquipoEscritorioDTO extends EquipoBaseDTO {

    private String nombreEquipo;
    private LocalDate finalGarantia;
    private String cuenta;

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

    @Override
    public String toString() {
        return nombreEquipo + ", finalGarantia=" + finalGarantia + ", cuenta=" + cuenta;
    }
     
}
