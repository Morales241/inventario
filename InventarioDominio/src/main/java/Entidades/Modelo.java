package Entidades;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *  
 * @author JMorales
 */
@Entity
public class Modelo extends AuditoriaBase implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "Marca")
    private String marca;
    
    @Column(name = "MemoriaRam")
    private String memoriaRam;
    
    @Column(name = "Almacenamiento")
    private String almacenamiento;
    
    @Column(name = "Procesador")
    private String procesador;
    
    @Column(name = "NoSerie")
    private String noSerie;
    
    @Column(name = "Equipos")
    @OneToMany(mappedBy = "modelo") 
    private List<EquipoDeComputo> equipos;

    public Modelo(String marca, String memoriaRam, String almacenamiento, String preocesador, String noSerie) {
        this.marca = marca;
        this.memoriaRam = memoriaRam;
        this.almacenamiento = almacenamiento;
        this.procesador = preocesador;
        this.noSerie = noSerie;
        this.equipos = new ArrayList<>();
    }

    public Modelo() {
        this.equipos = new ArrayList<>();
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getMemoriaRam() {
        return memoriaRam;
    }

    public void setMemoriaRam(String memoriaRam) {
        this.memoriaRam = memoriaRam;
    }

    public String getAlmacenamiento() {
        return almacenamiento;
    }

    public void setAlmacenamiento(String almacenamiento) {
        this.almacenamiento = almacenamiento;
    }

    public String getPreocesador() {
        return procesador;
    }

    public void setPreocesador(String preocesador) {
        this.procesador = preocesador;
    }

    public String getNoSerie() {
        return noSerie;
    }

    public void setNoSerie(String noSerie) {
        this.noSerie = noSerie;
    }

    public List<EquipoDeComputo> getEquipos() {
        return equipos;
    }

    public void setEquipos(List<EquipoDeComputo> equipos) {
        this.equipos = equipos;
    }
    
    
}
