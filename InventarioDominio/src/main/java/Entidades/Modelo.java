package Entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author JMorales
 */
@Entity
@Table(
    name = "Modelo",
    uniqueConstraints = @UniqueConstraint(
        columnNames = {
            "Marca",
            "Nombre",
            "MemoriaRam",
            "Almacenamiento",
            "Procesador"
        }
    )
)
public class Modelo extends AuditoriaBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Version
    private Long version;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private Integer memoriaRam;

    @Column(nullable = false)
    private Integer almacenamiento;

    @Column(nullable = false)
    private String procesador;

    @OneToMany(mappedBy = "modelo")
    private List<EquipoDeComputo> equipos = new ArrayList<>();

    public Modelo() {
    }

    public Modelo(String nombre, String marca, Integer memoriaRam, Integer almacenamiento, String procesador) {
        this.nombre = nombre;
        this.marca = marca;
        this.memoriaRam = memoriaRam;
        this.almacenamiento = almacenamiento;
        this.procesador = procesador;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Integer getMemoriaRam() {
        return memoriaRam;
    }

    public void setMemoriaRam(Integer memoriaRam) {
        this.memoriaRam = memoriaRam;
    }

    public Integer getAlmacenamiento() {
        return almacenamiento;
    }

    public void setAlmacenamiento(Integer almacenamiento) {
        this.almacenamiento = almacenamiento;
    }

    public String getProcesador() {
        return procesador;
    }

    public void setProcesador(String procesador) {
        this.procesador = procesador;
    }

    public List<EquipoDeComputo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<EquipoDeComputo> equipos) {
        this.equipos = equipos;
    }

    
}
