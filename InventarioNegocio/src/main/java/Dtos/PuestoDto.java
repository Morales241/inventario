package Dtos;

/**
 * Transferencia de datos para puestos de trabajo.
 * <p>
 * Representa un puesto laboral específicos dentro de un departamento.
 * Los trabajadores están asignados a puestos de trabajo específicos.
 * </p>
 */
public class PuestoDto {

    private Long id;
    private String nombre;
    private Long idDepartamento;
    private String nombreDepartamento;

    public PuestoDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Long getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(Long idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getNombreDepartamento() {
        return nombreDepartamento;
    }

    public void setNombreDepartamento(String nombreDepartamento) {
        this.nombreDepartamento = nombreDepartamento;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
