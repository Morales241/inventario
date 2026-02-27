package Servicios;

import Dao.DaoEquipoAsignado;
import Dao.DaoEquipoDeComputo;
import Dao.DaoUsuario;
import Dtos.AsignacionDTO;
import Entidades.EquipoAsignado;
import Entidades.EquipoDeComputo;
import Entidades.Usuario;
import Enums.EstadoEquipo;
import mapper.MapperAsignacion;
import excepciones.RecursoNoEncontradoException;
import excepciones.ReglaNegocioException;
import interfacesServicios.IServicioPrestamos;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

public class ServicioPrestamos extends ServicioBase implements IServicioPrestamos {

    private final DaoEquipoAsignado daoAsignacion;
    private final DaoEquipoDeComputo daoEquipo;
    private final DaoUsuario daoUsuario;

    public ServicioPrestamos() {
        this.daoAsignacion = new DaoEquipoAsignado();
        this.daoEquipo = new DaoEquipoDeComputo();
        this.daoUsuario = new DaoUsuario();
    }

    private void configurar(EntityManager em) {
        daoAsignacion.setEntityManager(em);
        daoEquipo.setEntityManager(em);
        daoUsuario.setEntityManager(em);
    }

    @Override
    public void asignarEquipo(Long idEquipo, Long idUsuario) {

        if (idEquipo == null || idUsuario == null) {
            throw new IllegalArgumentException("IDs inválidos");
        }

        ejecutarTransaccion(em -> {

            configurar(em);

            EquipoDeComputo equipo = daoEquipo.buscarPorId(idEquipo);
            if (equipo == null) {
                throw new RecursoNoEncontradoException("Equipo no encontrado");
            }

            Usuario Usuario = daoUsuario.buscarPorId(idUsuario);
            if (Usuario == null) {
                throw new RecursoNoEncontradoException("Usuario no encontrado");
            }

            if (!Boolean.TRUE.equals(Usuario.getActivo())) {
                throw new ReglaNegocioException("El Usuario está inactivo");
            }

            if (equipo.getEstado() != EstadoEquipo.EN_STOCK) {
                throw new ReglaNegocioException("El equipo no está disponible");
            }

            boolean yaAsignado = daoAsignacion
                    .buscarPorUsuarioActivo(Usuario.getIdUsuario())
                    .stream()
                    .anyMatch(a
                            -> a.getEquipoDeComputo()
                            .getId()
                            .equals(idEquipo));

            if (yaAsignado) {
                throw new ReglaNegocioException(
                        "El equipo ya tiene una asignación activa");
            }

            EquipoAsignado asignacion = new EquipoAsignado();
            asignacion.setEquipoDeComputo(equipo);
            asignacion.setUsuario(Usuario);
            asignacion.setFechaEntrega(LocalDate.now());

            equipo.setEstado(EstadoEquipo.ASIGNADO);

            daoEquipo.actualizar(equipo);
            daoAsignacion.guardar(asignacion);

            return null;
        });
    }

    @Override
    public void devolverEquipo(Long idAsignacion) {

        if (idAsignacion == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        ejecutarTransaccion(em -> {

            configurar(em);

            EquipoAsignado asignacion
                    = daoAsignacion.buscarPorId(idAsignacion);

            if (asignacion == null) {
                throw new RecursoNoEncontradoException(
                        "Asignación no encontrada");
            }

            if (asignacion.getFechaDevolucion() != null) {
                throw new ReglaNegocioException(
                        "El equipo ya fue devuelto");
            }

            EquipoDeComputo equipo
                    = asignacion.getEquipoDeComputo();

            asignacion.setFechaDevolucion(LocalDate.now());
            equipo.setEstado(EstadoEquipo.EN_STOCK);

            daoAsignacion.actualizar(asignacion);
            daoEquipo.actualizar(equipo);

            return null;
        });
    }

    @Override
    public List<AsignacionDTO> obtenerEquiposDeUsuarios(Long idUsuario) {

        if (idUsuario == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        return ejecutarLectura(em -> {

            configurar(em);

            if (daoUsuario.buscarPorId(idUsuario) == null) {
                throw new RecursoNoEncontradoException(
                        "Usuario no encontrado");
            }

            List<EquipoAsignado> asignaciones
                    = daoAsignacion.buscarPorUsuarioActivo(idUsuario);

            return MapperAsignacion.converter
                    .mapToDtoList(asignaciones);
        });
    }
}
