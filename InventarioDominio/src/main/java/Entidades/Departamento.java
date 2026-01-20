/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Entidades;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "Departamento")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_Departamento")
    private Integer idDepartamento;

    @Column(name = "Nombre", nullable = false)
    private String nombre;

    // Relación: Muchos Departamentos -> Una Empresa
    @ManyToOne
    @JoinColumn(name = "id_Empresa", nullable = false)
    private Empresa empresa;

    // Relación: Un Departamento -> Muchos Puestos
    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL)
    private List<Puesto> puestos = new ArrayList<>();

    // Generar Getters y Setters

    public Integer getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Integer idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public List<Puesto> getPuestos() {
        return puestos;
    }

    public void setPuestos(List<Puesto> puestos) {
        this.puestos = puestos;
    }
    
}