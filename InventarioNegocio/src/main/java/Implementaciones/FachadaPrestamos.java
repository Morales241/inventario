package Implementaciones;

import Dtos.AsignacionDto;
import Interfaces.IFachadaPrestamos;
import Servicios.ServicioPrestamos;
import java.util.List;

/**
 *
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
