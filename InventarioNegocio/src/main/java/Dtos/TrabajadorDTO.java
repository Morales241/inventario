package Dtos;

/**
 * Transferencia de datos para trabajadores/empleados.
 * <p>
 * Representa la información básica de una persona empleada en la organización.
 * Contiene datos personales, número de nómina y referencia al puesto de trabajo.
 * El estado {@code activo} determina si el trabajador puede recibir asignaciones de equipos.
 * </p>
 */
public class TrabajadorDTO {

    private Long id;
    private String nombre;
    private String noNomina;
    private Boolean activo;

    private Long idPuesto;
    private String nombrePuesto;

    public TrabajadorDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNoNomina() {
        return noNomina;
    }

    public void setNoNomina(String noNomina) {
        this.noNomina = noNomina;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Long getIdPuesto() {
        return idPuesto;
    }

    public void setIdPuesto(Long idPuesto) {
        this.idPuesto = idPuesto;
    }

    public String getNombrePuesto() {
        return nombrePuesto;
    }

    public void setNombrePuesto(String nombrePuesto) {
        this.nombrePuesto = nombrePuesto;
    }

    @Override
    public String toString() {
        return id + ".- nombre=" + nombre + ", noNomina=" + noNomina + ", activo=" + activo + ", idPuesto=" + idPuesto + ", nombrePuesto=" + nombrePuesto;
    }
    
}
