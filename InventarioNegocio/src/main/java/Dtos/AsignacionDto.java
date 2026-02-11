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
public class AsignacionDto {

    private Long id;
    private LocalDate fechaEntrega;
    private LocalDate fechaDevolucion;

    private String nombreTrabajador;
    private String descripcionEquipo;

    public AsignacionDto() {
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

    public String getNombreTrabajador() {
        return nombreTrabajador;
    }

    public void setNombreTrabajador(String nombreTrabajador) {
        this.nombreTrabajador = nombreTrabajador;
    }

    public String getDescripcionEquipo() {
        return descripcionEquipo;
    }

    public void setDescripcionEquipo(String descripcionEquipo) {
        this.descripcionEquipo = descripcionEquipo;
    }
}
