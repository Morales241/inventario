package Entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;
import java.time.LocalDate;

/**
 *
 * @author JMorales
 */
@Entity
public class EquipoAsignado extends AuditoriaBase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id_Equipo_Asignado")
    private Long idEquipoAsignado;

    @Column(name = "FechaEntrega")
    private LocalDate fechaEntrega;
    
    @Column(name = "FechaDevolucion")
    private LocalDate fechaDevolucion;
    
    @ManyToOne
    @JoinColumn(name = "IdTrabajador")
    private Trabajador trabajador;
    
    @ManyToOne
    @JoinColumn(name = "IdEquipoDeComputo")
    private EquipoDeComputo equipoDeComputo;

    public EquipoAsignado() {
    }

    public EquipoAsignado(LocalDate fechaEntrega, LocalDate fechaDevolucion, Trabajador trabajador, EquipoDeComputo equipoDeComputo) {
        this.fechaEntrega = fechaEntrega;
        this.fechaDevolucion = fechaDevolucion;
        this.trabajador = trabajador;
        this.equipoDeComputo = equipoDeComputo;
    }

    public Long getId() {
        return idEquipoAsignado;
    }

    public void setId(Long id) {
        this.idEquipoAsignado = id;
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

    public Trabajador getTrabajador() {
        return trabajador;
    }

    public void setTrabajador(Trabajador trabajador) {
        this.trabajador = trabajador;
    }

    public EquipoDeComputo getEquipoDeComputo() {
        return equipoDeComputo;
    }

    public void setEquipoDeComputo(EquipoDeComputo equipoDeComputo) {
        this.equipoDeComputo = equipoDeComputo;
    }
    
    
}
