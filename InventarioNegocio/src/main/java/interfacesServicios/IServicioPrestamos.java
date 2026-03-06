// InventarioNegocio/src/main/java/interfacesServicios/IServicioPrestamos.java
package interfacesServicios;

import Dtos.AsignacionDTO;
import Entidades.Usuario;
import java.util.List;

/**
 * Interfaz para el servicio de gestión de asignaciones y préstamos de equipos.
 * Define operaciones para asignar, devolver y consultar préstamos de equipos
 * a usuarios.
 */
public interface IServicioPrestamos {

    // ========================= OPERACIONES PRINCIPALES =========================
    
    /**
     * Asigna un equipo a un usuario para su uso.
     * @param idEquipo Identificador del equipo
     * @param idUsuario Identificador del usuario
     */
    void asignarEquipo(Long idEquipo, Long idUsuario);
    
    /**
     * Procesa la devolución de un equipo, finalizando el préstamo.
     * @param idAsignacion Identificador del registro de asignación
     */
    void devolverEquipo(Long idAsignacion);
    
    /**
     * Obtiene los equipos actualmente asignados a un usuario.
     * @param idUsuario Identificador del usuario
     * @return Lista de AsignacionDTO con equipos en poder del usuario
     */
    List<AsignacionDTO> obtenerEquiposDeUsuarios(Long idUsuario);

    // ========================= CONSULTAS AVANZADAS =========================
    
    /**
     * Obtiene todas las asignaciones activas del sistema.
     * @return Lista de asignaciones activas
     */
    List<AsignacionDTO> obtenerTodasLasAsignacionesActivas();
    
    /**
     * Obtiene el historial completo de asignaciones de un usuario.
     * @param idUsuario Identificador del usuario
     * @return Lista con todo el historial de asignaciones del usuario
     */
    List<AsignacionDTO> obtenerHistorialUsuario(Long idUsuario);
    
    /**
     * Obtiene todas las asignaciones de un equipo específico (historial).
     * @param idEquipo Identificador del equipo
     * @return Lista con todo el historial de asignaciones del equipo
     */
    List<AsignacionDTO> obtenerHistorialEquipo(Long idEquipo);
    
    /**
     * Verifica si un equipo está actualmente asignado.
     * @param idEquipo Identificador del equipo
     * @return true si el equipo está asignado
     */
    boolean equipoEstaAsignado(Long idEquipo);
    
    /**
     * Obtiene el usuario actual de un equipo (si está asignado).
     * @param idEquipo Identificador del equipo
     * @return Usuario que tiene el equipo, o null si no está asignado
     */
    Usuario obtenerUsuarioActualDeEquipo(Long idEquipo);
    
    /**
     * Busca asignaciones por filtro (nombre de usuario, GRY, identificador).
     * @param filtro Criterio de búsqueda
     * @return Lista de asignaciones que coinciden
     */
    List<AsignacionDTO> buscarAsignaciones(String filtro);
    
    /**
     * Obtiene las asignaciones activas de un equipo específico.
     * @param idEquipo Identificador del equipo
     * @return Lista de asignaciones activas del equipo (normalmente 0 o 1)
     */
    List<AsignacionDTO> obtenerAsignacionesActivasPorEquipo(Long idEquipo);
    
    /**
     * Obtiene las asignaciones por rango de fechas.
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de asignaciones en el rango de fechas
     */
    List<AsignacionDTO> obtenerAsignacionesPorRangoFechas(String fechaInicio, String fechaFin);
    
    /**
     * Obtiene el conteo de equipos asignados a un usuario.
     * @param idUsuario Identificador del usuario
     * @return Número de equipos asignados activos
     */
    int contarEquiposAsignadosAUsuario(Long idUsuario);
    
    /**
     * Obtiene los usuarios con más equipos asignados.
     * @param limite Número máximo de resultados
     * @return Lista de usuarios con conteo de equipos
     */
//    List<Object[]> obtenerTopUsuariosConEquipos(int limite);
    
    /**
     * Obtiene estadísticas de asignaciones por estado.
     * @return Mapa con conteo de asignaciones activas vs devueltas
     */
    // Map<String, Long> obtenerEstadisticasAsignaciones();
    
    /**
     * Obtiene la última asignación de un equipo.
     * @param idEquipo Identificador del equipo
     * @return Última asignación del equipo (activa o devuelta)
     */
    AsignacionDTO obtenerUltimaAsignacionDeEquipo(Long idEquipo);
    
    /**
     * Verifica si un usuario puede recibir más equipos.
     * @param idUsuario Identificador del usuario
     * @param limiteMaximo Límite máximo de equipos por usuario
     * @return true si puede recibir más equipos
     */
    boolean usuarioPuedeRecibirMasEquipos(Long idUsuario, int limiteMaximo);
}