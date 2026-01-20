/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "EquipoDeComputo")
@Inheritance(strategy = InheritanceType.JOINED) // Estrategia clave para tu SQL
public class EquipoDeComputo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_EquipoDeComputo")
    private Integer idEquipo;

    @Column(nullable = false)
    private String condicion;
    
    @Column(nullable = false)
    private String marca;
    
    @Column(nullable = false)
    private Integer gri;
    
    @Column(nullable = false)
    private String factura;
    
    @Column(name = "AnioCompra", nullable = false)
    private Integer anioCompra;
    
    @Column(nullable = false)
    private String modelo;
    
    @Column(nullable = false)
    private String estado;
    
    @Column
    private String observaciones;
    
    @Column(name = "FechaEntrega")
    private LocalDate fechaEntrega;

    @ManyToOne
    @JoinColumn(name = "idTrabajador")
    private Trabajador trabajadorAsignado;

    // Generar Getters y Setters

    public Integer getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(Integer idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getCondicion() {
        return condicion;
    }

    public void setCondicion(String condicion) {
        this.condicion = condicion;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
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

    public Integer getAnioCompra() {
        return anioCompra;
    }

    public void setAnioCompra(Integer anioCompra) {
        this.anioCompra = anioCompra;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public LocalDate getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDate fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Trabajador getTrabajadorAsignado() {
        return trabajadorAsignado;
    }

    public void setTrabajadorAsignado(Trabajador trabajadorAsignado) {
        this.trabajadorAsignado = trabajadorAsignado;
    }
    
}