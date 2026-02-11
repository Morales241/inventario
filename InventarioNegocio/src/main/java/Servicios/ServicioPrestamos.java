package Servicios;

import Dao.DaoEquipoAsignado;
import Dao.DaoEquipoDeComputo;
import Dao.DaoTrabajador;
import Dtos.AsignacionDto;
import Entidades.EquipoAsignado;
import Entidades.EquipoDeComputo;
import Entidades.Trabajador;
import Enums.EstadoEquipo;
import Mappers.MapperAsignacion;
import java.time.LocalDate;
import java.util.List;

public class ServicioPrestamos {

    private final DaoEquipoAsignado daoAsignacion;
    private final DaoEquipoDeComputo daoEquipo;
    private final DaoTrabajador daoTrabajador;

    public ServicioPrestamos() {
        daoAsignacion = new DaoEquipoAsignado();
        daoEquipo = new DaoEquipoDeComputo();
        daoTrabajador = new DaoTrabajador();
    }
    
    /**
     * Realiza la entrega formal de un equipo a un trabajador.
     * <p>
     * <b>Reglas de Negocio:</b>
     * <ul>
     * <li>El equipo debe existir en la base de datos.</li>
     * <li>El trabajador debe estar en estado <b>Activo</b>.</li>
     * <li>Tras la asignación, el estado del equipo cambia automáticamente a {@code EstadoEquipo.ASIGNADO}.</li>
     * </ul>
     * </p>
     * @param idEquipo ID del equipo a entregar.
     * @param idTrabajador ID del beneficiario.
     * @throws Exception Si el equipo/trabajador no existen o si el trabajador está inactivo.
     */
    public void asignarEquipo(Long idEquipo, Long idTrabajador) throws Exception {
        EquipoDeComputo equipo = daoEquipo.buscarPorId(idEquipo);
        Trabajador trabajador = daoTrabajador.buscarPorId(idTrabajador);

        if (equipo == null) throw new Exception("Equipo no encontrado");
        if (trabajador == null) throw new Exception("Trabajador no encontrado");
        
        if (!trabajador.getActivo()) {
            throw new Exception("El trabajador está dado de baja, no se le pueden asignar equipos.");
        }
        
        if (equipo.getEstado() != EstadoEquipo.DISPONIBLE) {
            throw new Exception("El equipo no está disponible (Estado actual: " + equipo.getEstado() + ")");
        }

        EquipoAsignado asignacion = new EquipoAsignado();
        asignacion.setEquipoDeComputo(equipo);
        asignacion.setTrabajador(trabajador);
        asignacion.setFechaEntrega(LocalDate.now());
        
        equipo.setEstado(EstadoEquipo.ASIGNADO);
        
        daoEquipo.actualizar(equipo);
        daoAsignacion.guardar(asignacion);
    }

    /**
     * Procesa la devolución de un equipo, finalizando el préstamo actual.
     * <p>
     * Establece la fecha de devolución en el registro de asignación y libera el equipo 
     * cambiando su estado a {@code EstadoEquipo.DISPONIBLE}.
     * </p>
     * @param idAsignacion ID del registro de préstamo a cerrar.
     * @throws Exception Si la asignación no existe o si el equipo ya había sido devuelto.
     */
    public void devolverEquipo(Long idAsignacion) throws Exception {
        EquipoAsignado asignacion = daoAsignacion.buscarPorId(idAsignacion);
        if (asignacion == null) throw new Exception("Asignación no encontrada");
        
        if (asignacion.getFechaDevolucion() != null) {
             throw new Exception("Este equipo ya fue devuelto anteriormente.");
        }

        asignacion.setFechaDevolucion(LocalDate.now());
        
        EquipoDeComputo equipo = asignacion.getEquipoDeComputo();
        equipo.setEstado(EstadoEquipo.DISPONIBLE);
        
        daoEquipo.actualizar(equipo);
        daoAsignacion.actualizar(asignacion);
    }
    
    /**
     * Obtiene los equipos que tiene actualmente asignados un trabajador.
     * <p>
     * Solo devuelve asignaciones <b>activas</b> (sin fecha de devolución).
     * Permite visualizar el inventario en poder de cada empleado.
     * </p>
     * @param idTrabajador ID del trabajador del cual obtener sus equipos.
     * @return Lista de AsignacionDto con los equipos asignados sin devolver.
     */
    public List<AsignacionDto> obtenerEquiposDeTrabajador(Long idTrabajador) {
        List<EquipoAsignado> lista = daoAsignacion.buscarPorTrabajadorActivo(idTrabajador);
        return MapperAsignacion.converter.mapToDtoList(lista);
    }
}