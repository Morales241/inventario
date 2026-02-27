package Implementaciones;

import Dtos.AsignacionDTO;
import InterfacesFachada.IFachadaPrestamos;
import Servicios.ServicioPrestamos;
import interfacesServicios.IServicioPrestamos;
import java.util.List;

/**
 * Implementación de la fachada para operaciones de asignación y devolución de equipos.
 * <p>
 * Proporciona acceso simplificado a la lógica de negocio del servicio de asignaciones.
 * Gestiona el ciclo de vida de los préstamos de equipos a Usuarios.
 * Utiliza el patrón Facade para abstraer la complejidad de las operaciones.
 * </p>
 * @author JMorales
 */
public class FachadaPrestamos implements IFachadaPrestamos {

    private final IServicioPrestamos servicioPrestamos;

    public FachadaPrestamos(IServicioPrestamos servicioPrestamos) {
        this.servicioPrestamos = servicioPrestamos;
    }

    public FachadaPrestamos() {
        this(new ServicioPrestamos());
    }

    @Override
    public void asignarEquipo(Long idEquipo, Long idUsuario) {
        servicioPrestamos.asignarEquipo(idEquipo, idUsuario);
    }

    @Override
    public void devolverEquipo(Long idAsignacion) {
        servicioPrestamos.devolverEquipo(idAsignacion);
    }

    @Override
    public List<AsignacionDTO> obtenerEquiposDeUsuarios(Long idUsuario) {
        return servicioPrestamos.obtenerEquiposDeUsuarios(idUsuario);
    }
}
