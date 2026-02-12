package Dtos;

import java.time.LocalDate;

/**
 * Transferencia de datos para asignaciones/préstamos de equipos.
 * <p>
 * Representa el registro de entrega de un equipo a un trabajador.
 * Contiene fechas del ciclo de vida del préstamo y referencias al trabajador y equipo.
 * Permite rastrear qué equipos tiene cada empleado y cuándo fueron entregados/devueltos.
 * </p>
 */
public class AsignacionDTO {

    private Long id;

    private LocalDate fechaEntrega;
    private LocalDate fechaDevolucion;

    private Long idTrabajador;
    private String nombreTrabajador;

    private Long idEquipo;
    private String identificadorEquipo;

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

    public Long getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(Long idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public String getNombreTrabajador() {
        return nombreTrabajador;
    }

    public void setNombreTrabajador(String nombreTrabajador) {
        this.nombreTrabajador = nombreTrabajador;
    }

    public Long getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Long idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getIdentificadorEquipo() {
        return identificadorEquipo;
    }

    public void setIdentificadorEquipo(String identificadorEquipo) {
        this.identificadorEquipo = identificadorEquipo;
    }

    @Override
    public String toString() {
        return id + ".- fechaEntrega=" + fechaEntrega + ", fechaDevolucion=" + fechaDevolucion +  ", nombreTrabajador=" + nombreTrabajador + ", identificadorEquipo=" + identificadorEquipo;
    }
    
}
