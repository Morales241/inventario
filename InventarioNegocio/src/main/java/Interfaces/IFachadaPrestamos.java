package Interfaces;

import Dtos.AsignacionDto;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IFachadaPrestamos {
    
    public void asignarEquipo(Long idEquipo, Long idTrabajador) throws Exception;
    
    public void devolverEquipo(Long idAsignacion) throws Exception;
    
    public List<AsignacionDto> obtenerEquiposDeTrabajador(Long idTrabajador);
}
