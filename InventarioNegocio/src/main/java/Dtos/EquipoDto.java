package Dtos;

import Enums.CondicionFisica;
import Enums.EstadoEquipo;
import java.time.LocalDate;

public class EquipoDto {

    private Long id;
    private Integer gri;
    private String serie;
    private String tipoEquipo;
    private CondicionFisica condicion;
    private EstadoEquipo estado;
    private String factura;

    private String nombreModelo;
    private String ubicacionSucursal;

    private String detalleTecnico;
    private String usuarioAsignadoActual;

    public EquipoDto() {
    }

    // Getters y Setters...
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getGri() {
        return gri;
    }

    public void setGri(Integer gri) {
        this.gri = gri;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }

    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public CondicionFisica getCondicion() {
        return condicion;
    }

    public void setCondicion(CondicionFisica condicion) {
        this.condicion = condicion;
    }

    public EstadoEquipo getEstado() {
        return estado;
    }

    public void setEstado(EstadoEquipo estado) {
        this.estado = estado;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public String getNombreModelo() {
        return nombreModelo;
    }

    public void setNombreModelo(String nombreModelo) {
        this.nombreModelo = nombreModelo;
    }

    public String getUbicacionSucursal() {
        return ubicacionSucursal;
    }

    public void setUbicacionSucursal(String ubicacionSucursal) {
        this.ubicacionSucursal = ubicacionSucursal;
    }

    public String getDetalleTecnico() {
        return detalleTecnico;
    }

    public void setDetalleTecnico(String detalleTecnico) {
        this.detalleTecnico = detalleTecnico;
    }

    public String getUsuarioAsignadoActual() {
        return usuarioAsignadoActual;
    }

    public void setUsuarioAsignadoActual(String usuarioAsignadoActual) {
        this.usuarioAsignadoActual = usuarioAsignadoActual;
    }
}
