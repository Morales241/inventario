package Entidades;
import jakarta.persistence.*;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "Departamento")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Departamento")
    private Integer idDepartamento;

    @Column(name = "Nombre", nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "Id_Sucursal", nullable = false)
    private Sucursal sucursal;

    
    @Column(name = "Puestos")
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