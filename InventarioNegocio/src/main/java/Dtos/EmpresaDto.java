package Dtos;

/**
 * Transferencia de datos para empresas.
 * <p>
 * Representa la estructura de nivel superior de la organización.
 * Una empresa puede tener más sucursales, que a su vez contienen departamentos y puestos.
 * </p>
 */
public class EmpresaDto {

    private Long id;
    private String nombre;

    public EmpresaDto() {
    }

    public EmpresaDto(Long id, String nombre) {
        this.id = id;
        this.nombre = nombre;
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

    @Override
    public String toString() {
        return nombre;
    }
}
