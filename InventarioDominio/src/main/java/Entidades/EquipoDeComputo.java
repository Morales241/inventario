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
@Table(name = "EquipoDeComputo")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TipoEquipo", discriminatorType = DiscriminatorType.STRING)
public class EquipoDeComputo extends AuditoriaBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_EquipoDeComputo")
    private Long idEquipo;

    @Version
    @Column(name = "version")
    private Long version;

    @Enumerated(EnumType.STRING)
    @Column(name = "Condicion", nullable = false)
    private CondicionFisica condicion;

    @Column(name = "GRY", nullable = false, unique = true)
    private Integer gry;

    @Column(name = "Factura")
    private String factura;

    @Enumerated(EnumType.STRING)
    @Column(name = "Estado", nullable = false)
    private EstadoEquipo estado;

    @Column(name = "Observaciones")
    private String observaciones;

    @Column(name = "FechaCompra")
    private LocalDate fechaCompra;

    @Column(name = "Identificador")
    private String identificador;

    @ManyToOne
    @JoinColumn(name = "IdModelo")
    private Modelo modelo;

    @Column(name = "EquiposAsignados")
    @OneToMany(mappedBy = "equipoDeComputo")
    private List<EquipoAsignado> equiposAsignados;

    @Enumerated(EnumType.STRING)
    @Column(name = "Tipo", nullable = false)
    private TipoEquipo tipo;

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

    public Integer getGry() {
        return gry;
    }

    public void setGry(Integer gry) {
        this.gry = gry;
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

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public TipoEquipo getTipo() {
        return tipo;
    }

    public void setTipo(TipoEquipo tipo) {
        this.tipo = tipo;
    }

    public void validarDisponible() {
        if (this.estado == EstadoEquipo.ASIGNADO) {
            throw new IllegalStateException("El equipo ya está asignado.");
        }
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public boolean tieneAsignacionActiva() {
        return this.equiposAsignados.stream()
                .anyMatch(a -> a.getFechaDevolucion() == null);
    }

    public void marcarComoAsignado() {
        this.estado = EstadoEquipo.ASIGNADO;
    }

    public void marcarComoDisponible() {
        this.estado = EstadoEquipo.EN_STOCK;
    }

}
