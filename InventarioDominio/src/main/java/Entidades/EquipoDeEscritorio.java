package Entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "EquipoDeEscritorio")
@PrimaryKeyJoinColumn(name = "id_EquipoDeEscritorio") 
@DiscriminatorValue("EQUIPODEESCRITORIO")
public class EquipoDeEscritorio extends EquipoDeComputo {

    @Column(name = "Nombre", nullable = false)
    private String nombreEquipo;

    @Column(name = "FinalGarantia")
    private LocalDate finalGarantia;

    @Column()
    private String cuenta;

    public EquipoDeEscritorio() {
    }

    public EquipoDeEscritorio(String nombreEquipo, LocalDate finalGarantia, String cuenta) {
        this.nombreEquipo = nombreEquipo;
        this.finalGarantia = finalGarantia;
        this.cuenta = cuenta;
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
}
