package Entidades;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "Departamentos")
public class Departamento extends AuditoriaBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Departamento")
    private Long idDepartamento;

    @Column(name = "Nombre", nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "Id_Sucursal", nullable = false)
    private Sucursal sucursal;
    
    @OneToMany(mappedBy = "departamento", cascade = CascadeType.ALL)
    private List<Puesto> puestos;

    public Departamento(String nombre, Sucursal sucursal) {
        this.nombre = nombre;
        this.sucursal = sucursal;
        this.puestos = new ArrayList<>();
    }

    public Departamento() {
        this.puestos = new ArrayList<>();
    }
    
    public Long getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Long idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Sucursal getSucursal() {
        return sucursal;
    }

    public void setSucursal(Sucursal sucursal) {
        this.sucursal = sucursal;
    }

    public List<Puesto> getPuestos() {
        return puestos;
    }

    public void setPuestos(List<Puesto> puestos) {
        this.puestos = puestos;
    }
}