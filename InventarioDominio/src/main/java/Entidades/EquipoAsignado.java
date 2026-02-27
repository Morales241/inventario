package Entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author JMorales
 */
@Entity
@Table(name = "EquiposAsignados")
public class EquipoAsignado extends AuditoriaBase implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @ManyToOne(optional = false)
    @JoinColumn(name = "IdEquipo")
    private EquipoDeComputo equipoDeComputo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "IdUsuario")
    private Usuario Usuario;

    @Column(nullable = false)
    private LocalDate fechaEntrega;

    private LocalDate fechaDevolucion;

    public EquipoAsignado() {
    }

    public EquipoAsignado(EquipoDeComputo equipo, Usuario Usuario) {
        this.equipoDeComputo = equipo;
        this.Usuario = Usuario;
        this.fechaEntrega = LocalDate.now();
    }

    public boolean estaActiva() {
        return fechaDevolucion == null;
    }

    public void devolver() {
        if (fechaDevolucion != null) {
            throw new IllegalStateException("La asignación ya fue cerrada.");
        }
        this.fechaDevolucion = LocalDate.now();
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

    public EquipoDeComputo getEquipoDeComputo() {
        return equipoDeComputo;
    }

    public void setEquipoDeComputo(EquipoDeComputo equipoDeComputo) {
        this.equipoDeComputo = equipoDeComputo;
    }

    public Usuario getUsuario() {
        return Usuario;
    }

    public void setUsuario(Usuario Usuario) {
        this.Usuario = Usuario;
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

    
}
