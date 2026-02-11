package Dtos;

import Enums.TipoEquipo;

/**
 *
 * @author JMorales
 */
public class OtroEquipoDTO extends EquipoBaseDTO{

    private String tituloCampoExtra;
    private String tituloCampoExtra2;
    private String contenidoCampoExtra;
    private String contenidoCampoExtra2;
    
    public OtroEquipoDTO() {
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
