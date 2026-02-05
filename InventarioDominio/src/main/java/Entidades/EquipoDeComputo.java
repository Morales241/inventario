package Entidades;

import Enums.CondicionFisica;
import Enums.EstadoEquipo;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "EquipoDeComputo")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TipoEquipo", discriminatorType = DiscriminatorType.STRING)
public class EquipoDeComputo extends AuditoriaBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_EquipoDeComputo")
    private Long idEquipo;

    @Enumerated(EnumType.STRING)
    @Column(name = "Condicion", nullable = false)
    private CondicionFisica condicion;

    @Column(name = "GRI", nullable = false)
    private Integer gri;

    @Column(name = "Factura")
    private String factura;

    @Enumerated(EnumType.STRING)
    @Column(name = "Estado", nullable = false)
    private EstadoEquipo estado;

    @Column(name = "Observaciones")
    private String observaciones;

    @Column(name = "FechaCompra")
    private LocalDate fechaCompra;

    @ManyToOne
    @JoinColumn(name = "IdSucursal")
    private Sucursal sucursal;

    @ManyToOne
    @JoinColumn(name = "IdModelo")
    private Modelo modelo;

    @Column(name = "EquiposAsignados")
    @OneToMany(mappedBy = "equipoDeComputo")
    private List<EquipoAsignado> equiposAsignados;

    public EquipoDeComputo(CondicionFisica condicion, Integer gri, String factura, EstadoEquipo estado, String observaciones, LocalDate fechaCompra, Sucursal sucursal, Modelo modelo) {
        this.condicion = condicion;
        this.gri = gri;
        this.factura = factura;
        this.estado = estado;
        this.observaciones = observaciones;
        this.fechaCompra = fechaCompra;
        this.sucursal = sucursal;
        this.modelo = modelo;
        this.equiposAsignados = new ArrayList<>();
    }

    public EquipoDeComputo() {
        this.equiposAsignados = new ArrayList<>();
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

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    public List<EquipoAsignado> getEquiposAsignados() {
        return equiposAsignados;
    }

    public void setEquiposAsignados(List<EquipoAsignado> equiposAsignados) {
        this.equiposAsignados = equiposAsignados;
    }

}
