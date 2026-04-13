package Dtos;

import java.time.LocalDateTime;

/**
 * Transferencia de datos para modelos de equipos en el catálogo.
 * <p>
 * Contiene especificaciones técnicas estandarizadas para un modelo de equipo.
 * Permite categorizar y buscar equipos por características hardware (RAM,
 * almacenamiento, procesador). Actualmente se usa para todos los tipos de
 * equipos (escritorio, móvil, otro).
 * </p>
 */
public class ModeloDTO {

    private Long idModelo;
    private String nombre;
    private String marca;
    private Integer memoriaRam;
    private Integer almacenamiento;
    private String procesador;

    // Campos de auditoría
    private String creadoPor;
    private LocalDateTime fechaCreacion;
    private String modificadoPor;
    private LocalDateTime fechaModificacion;

    public ModeloDTO() {
    }

    public Long getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(Long idModelo) {
        this.idModelo = idModelo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Integer getMemoriaRam() {
        return memoriaRam;
    }

    public void setMemoriaRam(Integer memoriaRam) {
        this.memoriaRam = memoriaRam;
    }

    public Integer getAlmacenamiento() {
        return almacenamiento;
    }

    public void setAlmacenamiento(Integer almacenamiento) {
        this.almacenamiento = almacenamiento;
    }

    public String getProcesador() {
        return procesador;
    }

    public void setProcesador(String procesador) {
        this.procesador = procesador;
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
        return idModelo + ".- " + nombre + ", marca=" + marca + ", memoria Ram=" + memoriaRam + ", almacenamiento=" + almacenamiento + ", procesador=" + procesador;
    }
}
