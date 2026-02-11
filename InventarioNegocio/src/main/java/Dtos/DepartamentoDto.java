package Dtos;

/**
 * Transferencia de datos para departamentos de empresa.
 * <p>
 * Representa un departamento u área dentro de una sucursal de la organización.
 * Un departamento contiene múltiples puestos de trabajo.
 * </p>
 */
public class DepartamentoDto {

    private Long id;
    private String nombre;
    private Long idSucursal;

    public DepartamentoDto() {
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

    public Long getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(Long idSucursal) {
        this.idSucursal = idSucursal;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
