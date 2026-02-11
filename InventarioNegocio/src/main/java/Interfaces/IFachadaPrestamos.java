package Interfaces;

import Dtos.AsignacionDto;
import java.util.List;

/**
 * Contrato de fachada para operaciones de asignación y devolución de equipos a trabajadores.
 * <p>
 * Define métodos para gestionar el ciclo de vida de los préstamos de equipos.
 * Implementa el patrón Facade para simplificar la interacción con la capa de servicios de asignaciones.
 * </p>
 * @author JMorales
 */
public interface IFachadaPrestamos {
    
    /**
     * Asigna un equipo a un trabajador para su uso.
     * <p>Valida integridad: equipo disponible y trabajador activo.</p>
     * @param idEquipo Identificador del equipo.
     * @param idTrabajador Identificador del trabajador.
     * @throws Exception Si el equipo está asignado, trabajador está inactivo, o no existen.
     */
    public void asignarEquipo(Long idEquipo, Long idTrabajador) throws Exception;
    
    /**
     * Procesa la devolución de un equipo, finalizando el préstamo.
     * @param idAsignacion Identificador del registro de asignación/préstamo.
     * @throws Exception Si la asignación no existe o ya fue devuelta.
     */
    public void devolverEquipo(Long idAsignacion) throws Exception;
    
    /**
     * Obtiene los equipos actualmente asignados a un trabajador.
     * <p>Solo devuelve asignaciones activas (sin fecha de devolución).</p>
     * @param idTrabajador Identificador del trabajador.
     * @return Lista de AsignacionDto con equipos en poder del trabajador.
     */
    public List<AsignacionDto> obtenerEquiposDeTrabajador(Long idTrabajador);
}
