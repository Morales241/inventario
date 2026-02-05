package Entidades;

import Enums.TipoOtroEquipo;
import jakarta.persistence.*;

@Entity
@Table(name = "OtroEquipo")
@PrimaryKeyJoinColumn(name = "id_OtroEquipo")
@DiscriminatorValue("OTROEQUIPO")
public class OtroEquipo extends EquipoDeComputo {

    @Enumerated(EnumType.STRING)
    @Column(name = "Tipo", nullable = false)
    private TipoOtroEquipo tipo;

    @Column(name = "TituloCampoExtra", nullable = false)
    private String tituloCampoExtra;

    @Column(name = "TituloCampoExtra2", nullable = false)
    private String tituloCampoExtra2;

    @Column(name = "ContenidoCampoExtra", nullable = false)
    private String contenidoCampoExtra;

    @Column(name = "ContenidoCampoExtra2", nullable = false)
    private String contenidoCampoExtra2;

    public OtroEquipo(TipoOtroEquipo tipo, String tituloCampoExtra, String tituloCampoExtra2, String contenidoCampoExtra, String contenidoCampoExtra2) {
        this.tipo = tipo;
        this.tituloCampoExtra = tituloCampoExtra;
        this.tituloCampoExtra2 = tituloCampoExtra2;
        this.contenidoCampoExtra = contenidoCampoExtra;
        this.contenidoCampoExtra2 = contenidoCampoExtra2;
    }

    public OtroEquipo() {
    }

    public TipoOtroEquipo getTipo() {
        return tipo;
    }

    public void setTipo(TipoOtroEquipo tipo) {
        this.tipo = tipo;
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
