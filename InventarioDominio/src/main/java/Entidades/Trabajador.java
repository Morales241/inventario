package Entidades;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "Trabajador")
public class Trabajador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Trabajador")
    private Integer idTrabajador;

    @Column(name = "Nombre", nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "IdPuesto", nullable = false)
    private Puesto puesto;

    @Column(name = "EquiposAsignados")
    @OneToMany(mappedBy = "trabajador") 
    private List<EquipoAsignado> equiposAsignados;

    public Trabajador(String nombre, Puesto puesto) {
        this.nombre = nombre;
        this.puesto = puesto;
        this.equiposAsignados = new ArrayList<>();
    }

    public Trabajador() {
        this.equiposAsignados = new ArrayList<>();
    }

    public Integer getIdTrabajador() {
        return idTrabajador;
    }

    public void setIdTrabajador(Integer idTrabajador) {
        this.idTrabajador = idTrabajador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Puesto getPuesto() {
        return puesto;
    }

    public void setPuesto(Puesto puesto) {
        this.puesto = puesto;
    }

    public List<EquipoAsignado> getEquiposAsignados() {
        return equiposAsignados;
    }

    public void setEquiposAsignados(List<EquipoAsignado> equiposAsignados) {
        this.equiposAsignados = equiposAsignados;
    }
    
    

}