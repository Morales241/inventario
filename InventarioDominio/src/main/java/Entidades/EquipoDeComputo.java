package Entidades;

import Enums.CondicionFisica;
import Enums.EstadoEquipo;
import Enums.TipoEquipo;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "EquiposDeComputo")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TipoEquipo", discriminatorType = DiscriminatorType.STRING)
public class EquipoDeComputo extends AuditoriaBase implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false, unique = true)
    private Integer gry;
    
    @Column(nullable = false, unique = true)
    private String indetificador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEquipo estado = EstadoEquipo.EN_STOCK;

    @Column(name = "Factura")
    private String factura;
    
    @Column(name = "Condicion")
    private CondicionFisica condicion;
    
    @Column(name = "Tipo")
    private TipoEquipo tipo;
    
    @Column(name = "FechaCompra")
    private LocalDate fechaCompra;
    
    @Column(name = "Observaciones")
    private String observaciones;
    
    @Column(name = "Precio")
    private Double precio;
    
    @ManyToOne(optional = false)
    @JoinColumn(name = "IdModelo")
    private Modelo modelo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "IdSucursal")
    private Sucursal sucursal;

    @OneToMany(
            mappedBy = "equipoDeComputo",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<EquipoAsignado> asignaciones = new ArrayList<>();

    public boolean tieneAsignacionActiva() {
        return asignaciones.stream()
                .anyMatch(EquipoAsignado::estaActiva);
    }

    public void asignarA(Usuario trabajador) {
        if (tieneAsignacionActiva()) {
            throw new IllegalStateException("El equipo ya está asignado.");
        }

        EquipoAsignado asignacion = new EquipoAsignado(this, trabajador);
        asignaciones.add(asignacion);
        this.estado = EstadoEquipo.ASIGNADO;
    }

    public void devolverEquipo() {
        EquipoAsignado activa = asignaciones.stream()
                .filter(EquipoAsignado::estaActiva)
                .findFirst()
                .orElseThrow(()
                        -> new IllegalStateException("No tiene asignación activa."));

        activa.devolver();
        this.estado = EstadoEquipo.EN_STOCK;
    }

    public Double getPrecio() {
        return precio;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getGry() {
        return gry;
    }

    public void setGry(Integer gry) {
        this.gry = gry;
    }

    public EstadoEquipo getEstado() {
        return estado;
    }

    public void setEstado(EstadoEquipo estado) {
        this.estado = estado;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public List<EquipoAsignado> getAsignaciones() {
        return asignaciones;
    }

    public void setAsignaciones(List<EquipoAsignado> asignaciones) {
        this.asignaciones = asignaciones;
    }

    public String getIndetificador() {
        return indetificador;
    }

    public void setIndetificador(String indetificador) {
        this.indetificador = indetificador;
    }

    public String getFactura() {
        return factura;
    }

    public void setFactura(String factura) {
        this.factura = factura;
    }

    public LocalDate getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(LocalDate fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public CondicionFisica getCondicion() {
        return condicion;
    }

    public void setCondicion(CondicionFisica condicion) {
        this.condicion = condicion;
    }

    public TipoEquipo getTipo() {
        return tipo;
    }

    public void setTipo(TipoEquipo tipo) {
        this.tipo = tipo;
    }
}
