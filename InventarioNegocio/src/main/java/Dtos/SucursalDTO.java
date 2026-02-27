package Dtos;

/**
 * Transferencia de datos para sucursales de empresa.
 * <p>
 * Representa una ubicación física de la empresa con su información de localización.
 * Una sucursal pertenece a una empresa y puede contener médios
 departamentos, empleados y equipos.
 * </p>
 */
public class SucursalDTO {

    private Long id;
    private String nombre;
    private String ubicacion;
    private Long idEmpresa;
    private String nombreEmpresa;
    private Long version;

    public SucursalDTO() {
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

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public Long getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Long idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return nombre + ", Ubicación: "+ ubicacion;
    }
}
