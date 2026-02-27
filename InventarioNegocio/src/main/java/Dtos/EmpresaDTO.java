package Dtos;

/**
 * Transferencia de datos para empresas.
 * <p>
 * Representa la estructura de nivel superior de la organización.
 * Una empresa puede tener más sucursales, que a su vez contienen departamentos y puestos.
 * </p>
 */
public class EmpresaDTO {

    private Long id;
    private String nombre;
    private Integer totalSucursales;
    private Long version;

    public EmpresaDTO() {
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

    public Integer getTotalSucursales() {
        return totalSucursales;
    }

    public void setTotalSucursales(Integer totalSucursales) {
        this.totalSucursales = totalSucursales;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
