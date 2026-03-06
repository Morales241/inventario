package Dtos;

import java.time.LocalDate;

/**
 * Transferencia de datos para asignaciones/préstamos de equipos.
 * <p>
 * Representa el registro de entrega de un equipo a un Usuario.
 * Contiene fechas del ciclo de vida del préstamo y referencias al Usuario y equipo.
 * Permite rastrear qué equipos tiene cada empleado y cuándo fueron entregados/devueltos.
 * </p>
 */
public class AsignacionDTO {

    private Long id;
    private Long version;

    private LocalDate fechaEntrega;
    private LocalDate fechaDevolucion;

    private Long idUsuario;
    private String nombreUsuario;

    private Long idEquipo;
    private String GRY;

    public AsignacionDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public LocalDate getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public Long getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Long idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getIdentificadorEquipo() {
        return GRY;
    }

    public void setIdentificadorEquipo(String GRY) {
        this.GRY = GRY;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return id + ".- fechaEntrega=" + fechaEntrega + ", fechaDevolucion=" + fechaDevolucion +  ", nombreUsuario=" + nombreUsuario + ", GRY=" + GRY;
    }
    
}
