package Entidades;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "Puestos")
public class Puesto extends AuditoriaBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Puesto")
    private Long idPuesto;

    @Column(name = "Nombre", nullable = false)
    private String nombre;
    
    @Version
    private Long version;

    @ManyToOne
    @JoinColumn(name = "IdDepartamento")
    private Departamento departamento;

    @Column(name = "Usuarios")
    @OneToMany(mappedBy = "puesto", cascade = CascadeType.ALL)
    private List<Usuario> Usuarios;

    public Puesto(String nombre, Departamento departamento) {
        this.nombre = nombre;
        this.departamento = departamento;
        this.Usuarios = new ArrayList<>();
    }

    public Puesto() {
        this.Usuarios = new ArrayList<>();
    }

    public Long getIdPuesto() {
        return idPuesto;
    }

    public void setIdPuesto(Long idPuesto) {
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

    public List<Usuario> getUsuarios() {
        return Usuarios;
    }

    public void setUsuarios(List<Usuario> Usuarios) {
        this.Usuarios = Usuarios;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}