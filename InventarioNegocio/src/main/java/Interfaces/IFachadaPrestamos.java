package Interfaces;

/**
 *
 * @author JMorales
 */
public interface IFachadaPrestamos {
    
    public void asignarEquipo(Long idEquipo, Long idTrabajador) throws Exception;
    
    public void devolverEquipo(Long idAsignacion) throws Exception;
}
