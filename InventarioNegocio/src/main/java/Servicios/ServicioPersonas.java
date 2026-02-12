package Servicios;

import Dao.DaoPuesto;
import Dao.DaoTrabajador;
import Dao.DaoUsuario;
import Dtos.TrabajadorDTO;
import Dtos.UsuarioDTO;
import Entidades.Puesto;
import Entidades.Trabajador;
import Entidades.UsuarioSistema;
import Mappers.MapperTrabajador;
import Mappers.MapperUsuario;
import excepciones.RecursoNoEncontradoException;
import excepciones.ReglaNegocioException;
import interfacesServicios.IServicioPersonas;
import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de personas en el sistema.
 * <p>
 * Coordina las operaciones relacionadas con autenticación de usuarios y
 * administración de trabajadores. Incluye validaciones de integridad de datos y
 * cambios de estado para el control de acceso.
 * </p>
 */
public class ServicioPersonas extends ServicioBase implements IServicioPersonas {

    private final DaoUsuario daoUsuario;
    private final DaoTrabajador daoTrabajador;
    private final DaoPuesto daoPuesto;

    public ServicioPersonas() {
        this.daoUsuario = new DaoUsuario();
        this.daoTrabajador = new DaoTrabajador();
        this.daoPuesto = new DaoPuesto();
    }

    private void configurar(EntityManager em) {
        daoUsuario.setEntityManager(em);
        daoTrabajador.setEntityManager(em);
        daoPuesto.setEntityManager(em);
    }

    @Override
    public UsuarioDTO login(String username, String password) {

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        return ejecutarLectura(em -> {
            configurar(em);

            UsuarioSistema usuario
                    = daoUsuario.login(username.trim(), password);

            if (usuario == null) {
                throw new ReglaNegocioException(
                        "Usuario o contraseña incorrectos");
            }

            return MapperUsuario.converter.mapToDto(usuario);
        });
    }

    @Override
    public List<TrabajadorDTO> buscarTrabajadores(String criterioGlobal) {

        String criterio
                = (criterioGlobal != null) ? criterioGlobal.trim() : "";

        return ejecutarLectura(em -> {
            configurar(em);

            return MapperTrabajador.converter.mapToDtoList(
                    daoTrabajador.busquedaConFiltros(
                            criterio, criterio, criterio));
        });
    }

    @Override
    public TrabajadorDTO obtenerTrabajador(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        return ejecutarLectura(em -> {
            configurar(em);

            Trabajador trabajador = daoTrabajador.buscarPorId(id);

            if (trabajador == null) {
                throw new RecursoNoEncontradoException(
                        "Trabajador no encontrado");
            }

            return MapperTrabajador.converter.mapToDto(trabajador);
        });
    }

    @Override
    public TrabajadorDTO guardarTrabajador(TrabajadorDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Datos inválidos");
        }

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new ReglaNegocioException("El nombre es obligatorio");
        }

        if (dto.getNoNomina() == null || dto.getNoNomina().isBlank()) {
            throw new ReglaNegocioException(
                    "El número de nómina es obligatorio");
        }

        if (dto.getIdPuesto() == null) {
            throw new ReglaNegocioException(
                    "Debe asignar un puesto");
        }

        return ejecutarTransaccion(em -> {

            configurar(em);

            Puesto puesto = daoPuesto.buscarPorId(dto.getIdPuesto());

            if (puesto == null) {
                throw new RecursoNoEncontradoException("Puesto no encontrado");
            }

            Trabajador entidad
                    = MapperTrabajador.converter.mapToEntity(dto);

            entidad.setPuesto(puesto);

            if (dto.getId() != null && dto.getId() > 0) {

                Trabajador existente
                        = daoTrabajador.buscarPorId(dto.getId());

                if (existente == null) {
                    throw new RecursoNoEncontradoException(
                            "Trabajador no encontrado");
                }

                entidad = daoTrabajador.actualizar(entidad);

            } else {

                entidad.setActivo(true);
                entidad = daoTrabajador.guardar(entidad);
            }

            return MapperTrabajador.converter.mapToDto(entidad);
        });
    }

    @Override
    public void cambiarEstadoTrabajador(Long id, boolean activo) {

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        ejecutarTransaccion(em -> {
            configurar(em);

            Trabajador trabajador
                    = daoTrabajador.buscarPorId(id);

            if (trabajador == null) {
                throw new RecursoNoEncontradoException(
                        "Trabajador no encontrado");
            }

            trabajador.setActivo(activo);

            daoTrabajador.actualizar(trabajador);

            return null;
        });
    }

}
