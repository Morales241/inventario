package Entidades;

import Enums.TipoEquipo;
import jakarta.persistence.*;

@Entity
@Table(name = "OtrosEquipos")
@PrimaryKeyJoinColumn(name = "id_OtroEquipo")
@DiscriminatorValue("OTROEQUIPO")
public class OtroEquipo extends EquipoDeComputo {

    @Column(name = "TituloCampoExtra", nullable = false)
    private String tituloCampoExtra;

    @Column(name = "TituloCampoExtra2")
    private String tituloCampoExtra2;

    @Column(name = "ContenidoCampoExtra", nullable = false)
    private String contenidoCampoExtra;

    @Column(name = "ContenidoCampoExtra2")
    private String contenidoCampoExtra2;

    public OtroEquipo(String tituloCampoExtra, String tituloCampoExtra2, String contenidoCampoExtra, String contenidoCampoExtra2) {
        this.tituloCampoExtra = tituloCampoExtra;
        this.tituloCampoExtra2 = tituloCampoExtra2;
        this.contenidoCampoExtra = contenidoCampoExtra;
        this.contenidoCampoExtra2 = contenidoCampoExtra2;
    }

    public OtroEquipo() {
    }

    public String getTituloCampoExtra() {
        return tituloCampoExtra;
    }

    public void setTituloCampoExtra(String tituloCampoExtra) {
        this.tituloCampoExtra = tituloCampoExtra;
    }

    public String getTituloCampoExtra2() {
        return tituloCampoExtra2;
    }

    public void setTituloCampoExtra2(String tituloCampoExtra2) {
        this.tituloCampoExtra2 = tituloCampoExtra2;
    }

    public String getContenidoCampoExtra() {
        return contenidoCampoExtra;
    }

    public void setContenidoCampoExtra(String contenidoCampoExtra) {
        this.contenidoCampoExtra = contenidoCampoExtra;
    }

    public String getContenidoCampoExtra2() {
        return contenidoCampoExtra2;
    }

    public void setContenidoCampoExtra2(String contenidoCampoExtra2) {
        this.contenidoCampoExtra2 = contenidoCampoExtra2;
    }

}
