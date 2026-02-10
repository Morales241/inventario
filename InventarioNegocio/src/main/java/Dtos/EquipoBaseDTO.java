package Dtos;

import Enums.CondicionFisica;
import Enums.EstadoEquipo;
import java.time.LocalDate;

/**
 *
 * @author JMorales
 */
public class EquipoBaseDTO {
    private Long idEquipo;
    private CondicionFisica condicion;
    private Integer gri;
    private String factura;
    private EstadoEquipo estado;
    private String observaciones;
    private LocalDate fechaCompra;
    private String identificador;
    
    private Long idSucursal;
    private String nombreSucursal;
    private Long idModelo;
    private String nombreModelo;

    public EquipoBaseDTO() {
    }

    public Long getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Long idEquipo) {
        this.idEquipo = idEquipo;
    }

    public CondicionFisica getCondicion() {
        return condicion;
    }

    public void setCondicion(CondicionFisica condicion) {
        this.condicion = condicion;
    }

    public Integer getGri() {
        return gri;
    }

    public void setGri(Integer gri) {
        this.gri = gri;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public EstadoEquipo getEstado() {
        return estado;
    }

    public void setEstado(EstadoEquipo estado) {
        this.estado = estado;
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

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }
    
}
