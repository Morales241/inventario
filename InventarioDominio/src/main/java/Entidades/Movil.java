package Entidades;

import jakarta.persistence.*;

@Entity
@Table(name = "Movil")
@PrimaryKeyJoinColumn(name = "id_Movil")
@DiscriminatorValue("MOVIL")
public class Movil extends EquipoDeComputo {

    @Column(nullable = false)
    private Boolean cargador;

    @Column(name = "NumCelular", nullable = false)
    private String numCelular;

    @Column(nullable = false)
    private Boolean funda;
    
    @Column(nullable = false)
    private Boolean manosLibres;

    public Movil() {
    }

    public Movil(Boolean cargador, String numCelular, Boolean funda, Boolean manosLibres) {
        this.cargador = cargador;
        this.numCelular = numCelular;
        this.funda = funda;
        this.manosLibres = manosLibres;
    }

    public Boolean getCargador() {
        return cargador;
    }

    public void setCargador(Boolean cargador) {
        this.cargador = cargador;
    }

    public String getNumCelular() {
        return numCelular;
    }

    public void setNumCelular(String numCelular) {
        this.numCelular = numCelular;
    }

    public Boolean getFunda() {
        return funda;
    }

    public void setFunda(Boolean funda) {
        this.funda = funda;
    }

    public Boolean getManosLibres() {
        return manosLibres;
    }

    public void setManosLibres(Boolean manosLibres) {
        this.manosLibres = manosLibres;
    }
    
}
