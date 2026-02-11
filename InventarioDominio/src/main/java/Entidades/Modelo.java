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
        uniqueConstraints = @UniqueConstraint(
                columnNames = {
                    "marca",
                    "nombre",
                    "memoriaRam",
                    "almacenamiento",
                    "procesador"
                }
        )
)
public class Modelo extends AuditoriaBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "Id_Modelo")
    private Long idModelo;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "Nombre")
    private String nombre;

    @Column(name = "Marca")
    private String marca;

    @Column(name = "MemoriaRam")
    private int memoriaRam;

    @Column(name = "Almacenamiento")
    private int almacenamiento;

    @Column(name = "Procesador")
    private String procesador;

    @Column(name = "Equipos")
    @OneToMany(mappedBy = "modelo")
    private List<EquipoDeComputo> equipos;

    public Modelo(String marca, String nombre, int memoriaRam, int almacenamiento, String preocesador) {
        this.marca = marca;
        this.nombre = nombre;
        this.memoriaRam = memoriaRam;
        this.almacenamiento = almacenamiento;
        this.procesador = preocesador;
        this.equipos = new ArrayList<>();
    }

    public Modelo() {
        this.equipos = new ArrayList<>();
    }

    public Long getId() {
        return idModelo;
    }

    public void setId(Long id) {
        this.idModelo = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public int getMemoriaRam() {
        return memoriaRam;
    }

    public void setMemoriaRam(int memoriaRam) {
        this.memoriaRam = memoriaRam;
    }

    public int getAlmacenamiento() {
        return almacenamiento;
    }

    public void setAlmacenamiento(int almacenamiento) {
        this.almacenamiento = almacenamiento;
    }

    public String getPreocesador() {
        return procesador;
    }

    public void setPreocesador(String preocesador) {
        this.procesador = preocesador;
    }

    public List<EquipoDeComputo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<EquipoDeComputo> equipos) {
        this.equipos = equipos;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getProcesador() {
        return procesador;
    }

    public void setProcesador(String procesador) {
        this.procesador = procesador;
    }

    public Long getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(Long idModelo) {
        this.idModelo = idModelo;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
