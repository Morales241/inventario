package Entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JMorales
 */
@Entity
@Table(name = "Sucursal")
public class Sucursal implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Sucursal")
    private Integer idSucursal;

    @ManyToOne
    @JoinColumn(name = "IdEmpresa")
    private Empresa empresa;
    
    @Column(name = "Ubicacion")
    private String ubicacion;
    
    @Column(name = "Nombre")
    private String Nombre;
    
    
    @Column(name = "Departamentos")
    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
    private List<Departamento> departamentos;
    
    @Column(name = "Equipos")
    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
    private List<EquipoDeComputo> equipos;
    

    public Sucursal(Empresa empresa, String ubicacion, String Nombre) {
        this.empresa = empresa;
        this.ubicacion = ubicacion;
        this.Nombre = Nombre;
        this.departamentos = new ArrayList<>();
        this.equipos = new ArrayList<>();
    }

    public Sucursal() {
        this.departamentos = new ArrayList<>();
        this.equipos = new ArrayList<>();
    }

    public Integer getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Integer idSucursal) {
        this.idSucursal = idSucursal;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String Nombre) {
        this.Nombre = Nombre;
    }

    public List<Departamento> getDepartamento() {
        return departamentos;
    }

    public void setDepartamento(List<Departamento> departamento) {
        this.departamentos = departamento;
    }

    public List<Departamento> getDepartamentos() {
        return departamentos;
    }

    public void setDepartamentos(List<Departamento> departamentos) {
        this.departamentos = departamentos;
    }

    public List<EquipoDeComputo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<EquipoDeComputo> equipos) {
        this.equipos = equipos;
    }

}
