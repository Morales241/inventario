package Servicios;

import Dao.DaoEquipoAsignado;
import Dao.DaoEquipoDeComputo;
import Dao.DaoTrabajador;
import Dtos.AsignacionDTO;
import Entidades.EquipoAsignado;
import Entidades.EquipoDeComputo;
import Entidades.Trabajador;
import Enums.EstadoEquipo;
import Mappers.MapperAsignacion;
import excepciones.RecursoNoEncontradoException;
import excepciones.ReglaNegocioException;
import interfacesServicios.IServicioPrestamos;
import java.time.LocalDate;
import java.util.List;

public class ServicioPrestamos implements IServicioPrestamos{

    private final DaoEquipoAsignado daoAsignacion;
    private final DaoEquipoDeComputo daoEquipo;
    private final DaoTrabajador daoTrabajador;

    public ServicioPrestamos() {
        this.daoAsignacion = new DaoEquipoAsignado();
        this.daoEquipo = new DaoEquipoDeComputo();
        this.daoTrabajador = new DaoTrabajador();
    }


    @Override
    public void asignarEquipo(Long idEquipo, Long idTrabajador) {

        if (idEquipo == null || idTrabajador == null) {
            throw new IllegalArgumentException("IDs inválidos");
        }

        EquipoDeComputo equipo = daoEquipo.buscarPorId(idEquipo);

        if (equipo == null) {
            throw new RecursoNoEncontradoException("Equipo no encontrado");
        }

        Trabajador trabajador = daoTrabajador.buscarPorId(idTrabajador);

        if (trabajador == null) {
            throw new RecursoNoEncontradoException("Trabajador no encontrado");
        }

        if (!Boolean.TRUE.equals(trabajador.getActivo())) {
            throw new ReglaNegocioException("El trabajador está inactivo");
        }

        if (equipo.getEstado() != EstadoEquipo.EN_STOCK) {
            throw new ReglaNegocioException("El equipo no está disponible");
        }

        boolean yaAsignado = daoAsignacion
                .buscarPorTrabajadorActivo(trabajador.getIdTrabajador())
                .stream()
                .anyMatch(a -> a.getEquipoDeComputo().getId().equals(idEquipo));

        if (yaAsignado) {
            throw new ReglaNegocioException("El equipo ya tiene una asignación activa");
        }

        EquipoAsignado asignacion = new EquipoAsignado();
        asignacion.setEquipoDeComputo(equipo);
        asignacion.setTrabajador(trabajador);
        asignacion.setFechaEntrega(LocalDate.now());

        equipo.setEstado(EstadoEquipo.ASIGNADO);

        daoEquipo.actualizar(equipo);
        daoAsignacion.guardar(asignacion);
    }

    @Override
    public void devolverEquipo(Long idAsignacion) {

        if (idAsignacion == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        EquipoAsignado asignacion = daoAsignacion.buscarPorId(idAsignacion);

        if (asignacion == null) {
            throw new RecursoNoEncontradoException("Asignación no encontrada");
        }

        if (asignacion.getFechaDevolucion() != null) {
            throw new ReglaNegocioException("El equipo ya fue devuelto");
        }

        EquipoDeComputo equipo = asignacion.getEquipoDeComputo();

        asignacion.setFechaDevolucion(LocalDate.now());
        equipo.setEstado(EstadoEquipo.EN_STOCK);

        daoAsignacion.actualizar(asignacion);
        daoEquipo.actualizar(equipo);
    }

    @Override
    public List<AsignacionDTO> obtenerEquiposDeTrabajador(Long idTrabajador) {

        if (idTrabajador == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        if (daoTrabajador.buscarPorId(idTrabajador) == null) {
            throw new RecursoNoEncontradoException("Trabajador no encontrado");
        }

        List<EquipoAsignado> asignaciones =
                daoAsignacion.buscarPorTrabajadorActivo(idTrabajador);

        return MapperAsignacion.converter.mapToDtoList(asignaciones);
    }
}
