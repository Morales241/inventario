package Dtos;

/**
 * Transferencia de datos para departamentos de empresa.
 * <p>
 * Representa un departamento u área dentro de una sucursal de la organización.
 * Un departamento contiene múltiples puestos de trabajo.
 * </p>
 */
public class DepartamentoDTO {

    private Long id;
    private String nombre;
    private Long version;

    private Long idSucursal;
    private String nombreSucursal; 

    public DepartamentoDTO() {
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

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return id + ".- nombre=" + nombre + ",  nombreSucursal=" + nombreSucursal;
    }
    
}
