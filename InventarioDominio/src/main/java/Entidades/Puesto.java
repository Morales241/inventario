package Entidades;

import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "Puesto")
public class Puesto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Puesto")
    private Integer idPuesto;

    @Column(name = "Nombre", nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "IdDepartamento")
    private Departamento departamento;

    @Column(name = "Trabajadores")
    @OneToMany(mappedBy = "puesto", cascade = CascadeType.ALL)
    private List<Trabajador> trabajadores;

    public Puesto(String nombre, Departamento departamento) {
        this.nombre = nombre;
        this.departamento = departamento;
        this.trabajadores = new ArrayList<>();
    }

    public Puesto() {
        this.trabajadores = new ArrayList<>();
    }

    public Integer getIdPuesto() {
        return idPuesto;
    }

    public void setIdPuesto(Integer idPuesto) {
        this.idPuesto = idPuesto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        this.departamento = departamento;
    }

    public List<Trabajador> getTrabajadores() {
        return trabajadores;
    }

    public void setTrabajadores(List<Trabajador> trabajadores) {
        this.trabajadores = trabajadores;
    }

    
}