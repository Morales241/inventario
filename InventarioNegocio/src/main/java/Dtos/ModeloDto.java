package Dtos;

public class ModeloDto {
    private Long id;
    private String nombre;
    private String marca;
    private String especificaciones; 

    public ModeloDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getEspecificaciones() { return especificaciones; }
    public void setEspecificaciones(String especificaciones) { this.especificaciones = especificaciones; }
    
    @Override
    public String toString() { return marca + " " + nombre; }
}