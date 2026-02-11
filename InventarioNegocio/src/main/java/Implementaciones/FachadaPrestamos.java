package Implementaciones;

import Dtos.AsignacionDto;
import Interfaces.IFachadaPrestamos;
import Servicios.ServicioPrestamos;
import java.util.List;

/**
 * Implementación de la fachada para operaciones de asignación y devolución de equipos.
 * <p>
 * Proporciona acceso simplificado a la lógica de negocio del servicio de asignaciones.
 * Gestiona el ciclo de vida de los préstamos de equipos a trabajadores.
 * Utiliza el patrón Facade para abstraer la complejidad de las operaciones.
 * </p>
 * @author JMorales
 */
public class FachadaPrestamos implements IFachadaPrestamos{

    private final ServicioPrestamos servicioPrestamos;
    
    public FachadaPrestamos() {
        this.servicioPrestamos = new ServicioPrestamos();
    }
    
    @Override
    public void asignarEquipo(Long idEquipo, Long idTrabajador) throws Exception {
        servicioPrestamos.asignarEquipo(idEquipo, idTrabajador);
    }

    @Override
    public void devolverEquipo(Long idAsignacion) throws Exception {
        servicioPrestamos.devolverEquipo(idAsignacion);
    }
    
    @Override
    public List<AsignacionDto> obtenerEquiposDeTrabajador(Long idTrabajador) {
        return servicioPrestamos.obtenerEquiposDeTrabajador(idTrabajador);
    }    
}
