package Servicios;

import Dao.DaoEquipoAsignado;
import Dao.DaoEquipoDeComputo;
import Dao.DaoTrabajador;
import Entidades.EquipoAsignado;
import Entidades.EquipoDeComputo;
import Entidades.Trabajador;
import Enums.EstadoEquipo;
import java.time.LocalDate;

public class ServicioPrestamos {

    private final DaoEquipoAsignado daoAsignacion = new DaoEquipoAsignado();
    private final DaoEquipoDeComputo daoEquipo = new DaoEquipoDeComputo();
    private final DaoTrabajador daoTrabajador = new DaoTrabajador();

    public void asignarEquipo(Long idEquipo, Long idTrabajador) throws Exception {
        EquipoDeComputo equipo = daoEquipo.buscarPorId(idEquipo);
        Trabajador trabajador = daoTrabajador.buscarPorId(idTrabajador);

        if (equipo == null) throw new Exception("Equipo no encontrado");
        if (trabajador == null) throw new Exception("Trabajador no encontrado");
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

    public void devolverEquipo(Long idAsignacion) throws Exception {
        EquipoAsignado asignacion = daoAsignacion.buscarPorId(idAsignacion);
        if (asignacion == null) throw new Exception("Asignación no encontrada");

        asignacion.setFechaDevolucion(LocalDate.now());
        
        EquipoDeComputo equipo = asignacion.getEquipoDeComputo();
        equipo.setEstado(EstadoEquipo.DISPONIBLE);
        
        daoEquipo.actualizar(equipo);
        daoAsignacion.actualizar(asignacion);
    }
}