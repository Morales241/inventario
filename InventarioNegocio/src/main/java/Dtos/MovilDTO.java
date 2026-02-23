package Dtos;

/**
 *
 * @author JMorales
 */
public class MovilDTO extends EquipoBaseDTO{
    
    private Boolean cargador;
    private String numCelular;
    private Boolean funda;
    private Boolean manosLibres;
    private String correoCuenta;
    private String contrasenaCuenta;

    public MovilDTO() {
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

    public String getCorreoCuenta() {
        return correoCuenta;
    }

    public void setCorreoCuenta(String correoCuenta) {
        this.correoCuenta = correoCuenta;
    }

    public String getContrasenaCuenta() {
        return contrasenaCuenta;
    }

    public void setContrasenaCuenta(String contrasenaCuenta) {
        this.contrasenaCuenta = contrasenaCuenta;
    }

    public void setManosLibres(Boolean manosLibres) {
        this.manosLibres = manosLibres;
    }
}
