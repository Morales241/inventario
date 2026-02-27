package Servicios;

import Dao.DaoPuesto;
import Dao.DaoUsuario;
import Dao.DaoCuentaSistema;
import Dtos.UsuarioDTO;
import Dtos.CuentaSistemaDTO;
import Entidades.Puesto;
import Entidades.Usuario;
import Entidades.CuentaSistema;
import mapper.MapperUsuario;
import mapper.MapperCuentaSistema;
import excepciones.RecursoNoEncontradoException;
import excepciones.ReglaNegocioException;
import interfacesServicios.IServicioPersonas;
import jakarta.persistence.EntityManager;
import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de personas en el sistema.
 * <p>
 * Coordina las operaciones relacionadas con autenticación de usuarios y
 * administración de Usuarioes. Incluye validaciones de integridad de datos y
 * cambios de estado para el control de acceso.
 * </p>
 */
public class ServicioPersonas extends ServicioBase implements IServicioPersonas {

    private final DaoCuentaSistema daoCuentaSistema;
    private final DaoUsuario daoUsuario;
    private final DaoPuesto daoPuesto;

    public ServicioPersonas() {
        this.daoCuentaSistema = new DaoCuentaSistema();
        this.daoUsuario = new DaoUsuario();
        this.daoPuesto = new DaoPuesto();
    }

    private void configurar(EntityManager em) {
        daoCuentaSistema.setEntityManager(em);
        daoUsuario.setEntityManager(em);
        daoPuesto.setEntityManager(em);
    }

    @Override
    public CuentaSistemaDTO login(String username, String password) {

        if (username == null || username.isBlank()
                || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        return ejecutarLectura(em -> {
            configurar(em);

            CuentaSistema usuario
                    = daoCuentaSistema.login(username.trim(), password);

            if (usuario == null) {
                throw new ReglaNegocioException(
                        "Usuario o contraseña incorrectos");
            }

            return MapperCuentaSistema.converter.mapToDto(usuario);
        });
    }

    @Override
    public List<UsuarioDTO> buscarUsuarios(String criterioGlobal) {

        String criterio
                = (criterioGlobal != null) ? criterioGlobal.trim() : "";

        return ejecutarLectura(em -> {
            configurar(em);

            return MapperUsuario.converter.mapToDtoList(
                    daoUsuario.busquedaConFiltros(
                            criterio, criterio, criterio));
        });
    }

    @Override
    public UsuarioDTO obtenerUsuario(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        return ejecutarLectura(em -> {
            configurar(em);

            Usuario Usuario = daoUsuario.buscarPorId(id);

            if (Usuario == null) {
                throw new RecursoNoEncontradoException(
                        "Usuario no encontrado");
            }

            return MapperUsuario.converter.mapToDto(Usuario);
        });
    }

    @Override
    public UsuarioDTO guardarUsuario(UsuarioDTO dto) {

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

            Usuario entidad
                    = MapperUsuario.converter.mapToEntity(dto);

            entidad.setPuesto(puesto);

            if (dto.getId() != null && dto.getId() > 0) {

                Usuario existente
                        = daoUsuario.buscarPorId(dto.getId());

                if (existente == null) {
                    throw new RecursoNoEncontradoException(
                            "Usuario no encontrado");
                }

                entidad = daoUsuario.actualizar(entidad);

            } else {

                entidad.setActivo(true);
                entidad = daoUsuario.guardar(entidad);
            }

            return MapperUsuario.converter.mapToDto(entidad);
        });
    }

    @Override
    public void cambiarEstadoUsuario(Long id, boolean activo) {

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        ejecutarTransaccion(em -> {
            configurar(em);

            Usuario Usuario
                    = daoUsuario.buscarPorId(id);

            if (Usuario == null) {
                throw new RecursoNoEncontradoException(
                        "Usuario no encontrado");
            }

            Usuario.setActivo(activo);

            daoUsuario.actualizar(Usuario);

            return null;
        });
    }

}
