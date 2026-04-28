package Dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
    private Integer gry;
    private String identificadorEquipo;

    // Campos de auditoría
    private String creadoPor;
    private LocalDateTime fechaCreacion;
    private String modificadoPor;
    private LocalDateTime fechaModificacion;

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

    public Integer getGry() {
        return gry;
    }

    public void setGry(Integer gry) {
        this.gry = gry;
    }

    public String getGryFormateado() {
        return formatGry(gry);
    }

    public static String formatGry(Integer gry) {
        return gry != null ? String.format("%05d", gry) : null;
    }

    public String getIdentificadorEquipo() {
        return identificadorEquipo;
    }

    public void setIdentificadorEquipo(String identificadorEquipo) {
        this.identificadorEquipo = identificadorEquipo;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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
        return id + ".- fechaEntrega=" + fechaEntrega + ", fechaDevolucion=" + fechaDevolucion +  ", nombreUsuario=" + nombreUsuario + ", GRY=" + gry + ", identificadorEquipo=" + identificadorEquipo;
    }
    
}
