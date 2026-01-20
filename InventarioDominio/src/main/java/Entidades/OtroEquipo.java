/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;
import jakarta.persistence.*;

@Entity
@Table(name = "OtroEquipo")
@PrimaryKeyJoinColumn(name = "id_OtroEquipo")
public class OtroEquipo extends EquipoDeComputo {

    @Column(name = "NoSerie", nullable = false)
    private String noSerie;
    
    @Column(nullable = false)
    private String tipo;
    
    @Column(name = "TituloCampoExtra", nullable = false)
    private String tituloCampoExtra;
    
    @Column(name = "TituloCampoExtra2", nullable = false)
    private String tituloCampoExtra2;
    
    @Column(name = "ContenidoCampoExtra", nullable = false)
    private String contenidoCampoExtra;
    
    @Column(name = "ContenidoCampoExtra2", nullable = false)
    private String contenidoCampoExtra2;

    // Generar Getters y Setters

    public String getNoSerie() {
        return noSerie;
    }

    public void setNoSerie(String noSerie) {
        this.noSerie = noSerie;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
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