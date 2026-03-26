package Servicios;

import Interfaces.IDaoGenerico;
import excepciones.RecursoNoEncontradoException;
import interfacesServicios.IServicioGenerico;
import jakarta.persistence.EntityManager;
import java.util.List;
import mapper.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio genérico que implementa operaciones CRUD básicas.
 * @param <E> Tipo de la Entidad
 * @param <D> Tipo del DTO
 * @param <ID> Tipo del ID
 */
public abstract class ServicioGenerico<E, D, ID> extends ServicioBase implements IServicioGenerico<D, ID> {
    protected static final Logger logger = LoggerFactory.getLogger(ServicioGenerico.class);

    final IDaoGenerico<E, ID> dao;
    final Mapper<E, D> mapper;
    final Class<E> claseEntidad;

    public ServicioGenerico(IDaoGenerico<E, ID> dao, Mapper<E, D> mapper, Class<E> claseEntidad) {
        this.dao = dao;
        this.mapper = mapper;
        this.claseEntidad = claseEntidad;
    }

    protected abstract void configurarEntityManager(EntityManager em);

    protected abstract void validarNegocio(D dto, boolean esNuevo, EntityManager em);

    protected abstract ID extraerId(D dto);

    protected void validarEliminacion(E entidad) {
    }

    @Override
    public D guardar(D dto) {
        logger.info("Iniciando guardar: {}", claseEntidad.getSimpleName());
        return ejecutarTransaccion(em -> {
            try {
                configurarEntityManager(em);
                
                validarNegocio(dto, true, em);
                logger.debug("Validaciones de negocio pasadas para nuevo registro");

                E entidad = mapper.mapToEntity(dto);
                entidad = dao.guardar(entidad);
                logger.info("Registro guardado exitosamente: {}", claseEntidad.getSimpleName());

                return mapper.mapToDto(entidad);
            } catch (Exception e) {
                logger.error("Error al guardar {}: {}", claseEntidad.getSimpleName(), e.getMessage(), e);
                throw e;
            }
        });
    }

    @Override
    public D actualizar(D dto) {
        logger.info("Iniciando actualizar: {}", claseEntidad.getSimpleName());
        return ejecutarTransaccion(em -> {
            try {
                configurarEntityManager(em);
                
                validarNegocio(dto, false, em);
                logger.debug("Validaciones de negocio pasadas para actualización");

                ID id = extraerId(dto);
                E existente = dao.buscarPorId(id);
                if (existente == null) {
                    logger.warn("Intento de actualizar {} con ID {} no encontrado", claseEntidad.getSimpleName(), id);
                    throw new RecursoNoEncontradoException(
                        claseEntidad.getSimpleName() + " no encontrado para actualizar");
                }

                E entidad = mapper.mapToEntity(dto);
                entidad = dao.actualizar(entidad);
                logger.info("Registro actualizado exitosamente: {} - ID: {}", claseEntidad.getSimpleName(), id);

                return mapper.mapToDto(entidad);
            } catch (Exception e) {
                logger.error("Error al actualizar {}: {}", claseEntidad.getSimpleName(), e.getMessage(), e);
                throw e;
            }
        });
    }

    @Override
    public void eliminar(ID id) {
        logger.info("Iniciando eliminar: {} - ID: {}", claseEntidad.getSimpleName(), id);
        if (id == null) {
            logger.error("ID nulo proporcionado para eliminar {}", claseEntidad.getSimpleName());
            throw new IllegalArgumentException("ID inválido");
        }

        ejecutarTransaccion(em -> {
            try {
                configurarEntityManager(em);

                E entidad = dao.buscarPorId(id);
                if (entidad == null) {
                    logger.warn("Intento de eliminar {} con ID {} no encontrado", claseEntidad.getSimpleName(), id);
                    throw new RecursoNoEncontradoException(
                        claseEntidad.getSimpleName() + " no encontrado");
                }

                validarEliminacion(entidad);
                dao.eliminar(id);
                logger.info("Registro eliminado exitosamente: {} - ID: {}", claseEntidad.getSimpleName(), id);
                return null;
            } catch (Exception e) {
                logger.error("Error al eliminar {} - ID {}: {}", claseEntidad.getSimpleName(), id, e.getMessage(), e);
                throw e;
            }
        });
    }

    @Override
    public D buscarPorId(ID id) {
        logger.debug("Buscando {} por ID: {}", claseEntidad.getSimpleName(), id);
        if (id == null) {
            logger.error("ID nulo proporcionado para buscar {}", claseEntidad.getSimpleName());
            throw new IllegalArgumentException("ID inválido");
        }

        return ejecutarLectura(em -> {
            try {
                configurarEntityManager(em);

                E entidad = dao.buscarPorId(id);
                if (entidad == null) {
                    logger.debug("{} no encontrado con ID: {}", claseEntidad.getSimpleName(), id);
                    throw new RecursoNoEncontradoException(
                        claseEntidad.getSimpleName() + " no encontrado");
                }

                logger.debug("{} encontrado con ID: {}", claseEntidad.getSimpleName(), id);
                return mapper.mapToDto(entidad);
            } catch (Exception e) {
                logger.error("Error al buscar {}: {}", claseEntidad.getSimpleName(), e.getMessage(), e);
                throw e;
            }
        });
    }

    @Override
    public List<D> buscarTodos() {
        logger.debug("Buscando todos los registros de {}", claseEntidad.getSimpleName());
        return ejecutarLectura(em -> {
            try {
                configurarEntityManager(em);
                List<D> resultados = mapper.mapToDtoList(dao.buscarTodos());
                logger.debug("Se encontraron {} registros de {}", resultados.size(), claseEntidad.getSimpleName());
                return resultados;
            } catch (Exception e) {
                logger.error("Error al buscar todos los {}: {}", claseEntidad.getSimpleName(), e.getMessage(), e);
                throw e;
            }
        });
    }
    
    protected void validarNegocio(D dto, boolean esNuevo) {
        throw new UnsupportedOperationException("Use la versión con EntityManager");
    }
}