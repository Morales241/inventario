package Dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Transferencia de datos para equipos de cómputo.
 * <p>
 * Representa la información base común a todos los tipos de equipos. Contiene
 * propiedades administrativas (GRY, factura, estado) y técnicas (modelo,
 * condición). Es la clase base para DTOs específicos de cada tipo de equipo.
 * </p>
 *
 * @author JMorales
 */
public class EquipoBaseDTO {

    private Long idEquipo;
    private Long version;
    private Integer gry;
    private String identificador;
    private String estado;
    private String factura;
    private String condicion;
    private String tipo;
    private LocalDate fechaCompra;
    private String observaciones;
    private Long idSucursal;
    private String nombreSucursal;
    private Long idModelo;
    private String nombreModelo;
    private Double precio;

    // Campos de auditoría
    private String creadoPor;
    private LocalDateTime fechaCreacion;
    private String modificadoPor;
    private LocalDateTime fechaModificacion;

    public EquipoBaseDTO() {
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
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

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Long getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Long idSucursal) {
        this.idSucursal = idSucursal;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public Long getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(Long idModelo) {
        this.idModelo = idModelo;
    }

    public String getNombreModelo() {
        return nombreModelo;
    }

    public void setNombreModelo(String nombreModelo) {
        this.nombreModelo = nombreModelo;
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
        return idEquipo + ".- gry=" + gry + ", estado=" + estado + ", condicion=" + condicion + ", factura=" + factura + ", observaciones=" + observaciones + ", fechaCompra=" + fechaCompra + ", identificador=" + identificador + ", tipo=" + tipo + ", idSucursal=" + idSucursal + ", nombreSucursal=" + nombreSucursal + ", idModelo=" + idModelo + ", nombreModelo=" + nombreModelo;
    }

}
