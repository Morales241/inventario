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

    private final DaoEquipoAsignado daoAsignacion = new DaoEquipoAsignado();
    private final DaoEquipoDeComputo daoEquipo = new DaoEquipoDeComputo();
    private final DaoTrabajador daoTrabajador = new DaoTrabajador();

    /**
     * Asigna un equipo a un trabajador.
     * Cambia el estado del equipo a ASIGNADO y crea el registro en historial.
     * @param idEquipo
     * @param idTrabajador
     * @throws java.lang.Exception
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
     * Devuelve un equipo (Termina la asignación).
     * Pone fecha de devolución y libera el equipo a DISPONIBLE.
     * @param idAsignacion
     * @throws java.lang.Exception
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
     * Obtiene los equipos que tiene asignados un trabajador actualmente.
     * @param idTrabajador
     * @return 
     */
    public List<AsignacionDto> obtenerEquiposDeTrabajador(Long idTrabajador) {
        List<EquipoAsignado> lista = daoAsignacion.buscarPorTrabajadorActivo(idTrabajador);
        return MapperAsignacion.converter.mapToDtoList(lista);
    }
}