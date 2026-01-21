package Entidades;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author tacot
 */
@Entity
@Table(name = "Empresa")
public class Empresa extends AuditoriaBase implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Empresa")
    private Long idEmpresa;

    @Column(name = "Nombre", nullable = false)
    private String nombre;

    @Column(name = "Sucursales")
    @OneToMany(mappedBy = "empresa", cascade = CascadeType.ALL)
    private List<Sucursal> sucursales;

    public Empresa(String nombre, List<Sucursal> sucursales) {
        this.nombre = nombre;
        this.sucursales = sucursales;
    }

    public Empresa(String nombre) {
        this.nombre = nombre;
        this.sucursales = new ArrayList<>();
    }

    public Empresa() {
        this.sucursales = new ArrayList<>();
    }
    
    public Long getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<Sucursal> getSucursales() {
        return sucursales;
    }

    public void setSucursales(List<Sucursal> sucursales) {
        this.sucursales = sucursales;
    }
   
}
