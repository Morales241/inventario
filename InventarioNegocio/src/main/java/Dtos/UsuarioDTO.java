package Dtos;

import java.time.LocalDateTime;

/**
 * Transferencia de datos para trabajadores/empleados.
 * <p>
 * Representa la información básica de una persona empleada en la organización.
 * Contiene datos personales, número de nómina y referencia al puesto de
 * trabajo. El estado {@code activo} determina si el trabajador puede recibir
 * asignaciones de equipos.
 * </p>
 */
public class UsuarioDTO {

    private Long id;
    private String nombre;
    private String noNomina;
    private Boolean activo;
    private Long version;
    private Long idPuesto;
    private String nombrePuesto;
    private int numeroDeEquipos;

    // Campos de auditoría
    private String creadoPor;
    private LocalDateTime fechaCreacion;
    private String modificadoPor;
    private LocalDateTime fechaModificacion;

    public UsuarioDTO() {
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public int getNumeroDeEquipos() {
        return numeroDeEquipos;
    }

    public void setNumeroDeEquipos(int numeroDeEquipos) {
        this.numeroDeEquipos = numeroDeEquipos;
    }

    // Getters/Setters de auditoría
    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(String modificadoPor) {
        this.modificadoPor = modificadoPor;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    @Override
    public String toString() {
        return id + ".- nombre=" + nombre + ", noNomina=" + noNomina + ", activo=" + activo + ", idPuesto=" + idPuesto + ", nombrePuesto=" + nombrePuesto;
    }

}
