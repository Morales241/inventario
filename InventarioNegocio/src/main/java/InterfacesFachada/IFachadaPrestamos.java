package InterfacesFachada;

import Dtos.AsignacionDTO;
import java.util.List;

/**
 * Contrato de fachada para operaciones de asignación y devolución de equipos a Usuarios.
 * <p>
 * Define métodos para gestionar el ciclo de vida de los préstamos de equipos.
 * Implementa el patrón Facade para simplificar la interacción con la capa de servicios de asignaciones.
 * </p>
 * @author JMorales
 */
public interface IFachadaPrestamos {
    
    /**
     * Asigna un equipo a un Usuario para su uso.
     * <p>Valida integridad: equipo disponible y Usuario activo.</p>
     * @param idEquipo Identificador del equipo.
     * @param idUsuario Identificador del Usuario.
     * @throws Exception Si el equipo está asignado, Usuario está inactivo, o no existen.
     */
    public void asignarEquipo(Long idEquipo, Long idUsuario) throws Exception;
    
    /**
     * Procesa la devolución de un equipo, finalizando el préstamo.
     * @param idAsignacion Identificador del registro de asignación/préstamo.
     * @throws Exception Si la asignación no existe o ya fue devuelta.
     */
    public void devolverEquipo(Long idAsignacion) throws Exception;
    
    /**
     * Obtiene los equipos actualmente asignados a un Usuario.
     * <p>Solo devuelve asignaciones activas (sin fecha de devolución).</p>
     * @param idUsuario Identificador del Usuario.
     * @return Lista de AsignacionDTO con equipos en poder del Usuario.
     */
    public List<AsignacionDTO> obtenerEquiposDeUsuarios(Long idUsuario);
}
