package Entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "EquiposDeEscritorio")
@PrimaryKeyJoinColumn(name = "id_EquipoDeEscritorio") 
@DiscriminatorValue("EQUIPODEESCRITORIO")
public class EquipoDeEscritorio extends EquipoDeComputo {

    @Column(name = "Nombre", nullable = false)
    private String nombreEquipo;

    @Column(name = "FinalGarantia")
    private LocalDate finalGarantia;

    @Column()
    private String cuenta;
    
    @Column()
    private String sisOpertativo;
    
    @Column()
    private String userRed;
    
    @Column()
    private Boolean mochila;
    
    @Column()
    private Boolean mouse;
    
    public EquipoDeEscritorio() {
    }

    public EquipoDeEscritorio(String nombreEquipo, LocalDate finalGarantia, String cuenta, String sisOpertativo, String userRed, Boolean mochila, Boolean mouse) {
        this.nombreEquipo = nombreEquipo;
        this.finalGarantia = finalGarantia;
        this.cuenta = cuenta;
        this.sisOpertativo = sisOpertativo;
        this.userRed = userRed;
        this.mochila = mochila;
        this.mouse = mouse;
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
}
