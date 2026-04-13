package Dtos;

import java.time.LocalDateTime;

/**
 * Transferencia de datos para empresas.
 * <p>
 * Representa la estructura de nivel superior de la organización.
 * Una empresa puede tener más sucursales, que a su vez contienen departamentos y puestos.
 * </p>
 */
public class EmpresaDTO {

    private Long id;
    private String nombre;
    private Integer totalSucursales;
    private Long version;

    // Campos de auditoría
    private String creadoPor;
    private LocalDateTime fechaCreacion;
    private String modificadoPor;
    private LocalDateTime fechaModificacion;

    public EmpresaDTO() {
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

    public Integer getTotalSucursales() {
        return totalSucursales;
    }

    public void setTotalSucursales(Integer totalSucursales) {
        this.totalSucursales = totalSucursales;
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
        return nombre;
    }
}
