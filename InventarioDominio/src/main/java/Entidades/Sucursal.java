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
import jakarta.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JMorales
 */
@Entity
@Table(name = "Sucursales")
public class Sucursal extends AuditoriaBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Sucursal")
    private Long idSucursal;

    @Version
    private Long version;
    
    @ManyToOne
    @JoinColumn(name = "IdEmpresa")
    private Empresa empresa;

    @Column(name = "Ubicacion")
    private String ubicacion;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Departamentos")
    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL)
    private List<Departamento> departamentos;

    @OneToMany(mappedBy = "sucursal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EquipoDeComputo> equipos = new ArrayList<>();

    public Sucursal(Empresa empresa, String ubicacion, String Nombre, List<EquipoDeComputo> equipos) {
        this.empresa = empresa;
        this.ubicacion = ubicacion;
        this.nombre = Nombre;
        this.departamentos = new ArrayList<>();
        this.equipos = equipos;
        this.equipos = new ArrayList<>();
    }

    public Sucursal() {
        this.departamentos = new ArrayList<>();
        this.equipos = new ArrayList<>();
    }

    public Long getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Long idSucursal) {
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
        return nombre;
    }

    public void setNombre(String Nombre) {
        this.nombre = Nombre;
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
