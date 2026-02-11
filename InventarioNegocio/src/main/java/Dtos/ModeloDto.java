package Dtos;

/**
 * Transferencia de datos para modelos de equipos en el catálogo.
 * <p>
 * Contiene especificaciones técnicas estandarizadas para un modelo de equipo.
 * Permite categorizar y buscar equipos por características hardware (RAM, almacenamiento, procesador).
 * Actualmente se usa para todos los tipos de equipos (escritorio, móvil, otro).
 * </p>
 */
public class ModeloDto {

    private Long idModelo;
    private String nombre;
    private String marca;
    private int memoriaRam;
    private int almacenamiento;
    private String procesador;

    public ModeloDto() {
    }

    public Long getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(Long idModelo) {
        this.idModelo = idModelo;
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

    public String getProcesador() {
        return procesador;
    }

    public void setProcesador(String procesador) {
        this.procesador = procesador;
    }

    @Override
    public String toString() {
        return idModelo + ".- " + nombre + ", marca=" + marca + ", memoria Ram=" + memoriaRam + ", almacenamiento=" + almacenamiento + ", procesador=" + procesador;
    }
}