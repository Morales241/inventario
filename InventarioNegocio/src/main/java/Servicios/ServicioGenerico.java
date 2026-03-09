package Servicios;

import Interfaces.IDaoGenerico;
import excepciones.RecursoNoEncontradoException;
import interfacesServicios.IServicioGenerico;
import jakarta.persistence.EntityManager;
import java.util.List;
import mapper.Mapper;

/**
 * Servicio genérico que implementa operaciones CRUD básicas.
 * @param <E> Tipo de la Entidad
 * @param <D> Tipo del DTO
 * @param <ID> Tipo del ID
 */
public abstract class ServicioGenerico<E, D, ID> extends ServicioBase implements IServicioGenerico<D, ID> {

    protected IDaoGenerico<E, ID> dao;
    final Mapper<E, D> mapper;
    final Class<E> claseEntidad;

    public ServicioGenerico(IDaoGenerico<E, ID> dao, Mapper<E, D> mapper, Class<E> claseEntidad) {
        this.dao = dao;
        this.mapper = mapper;
        this.claseEntidad = claseEntidad;
    }

    protected abstract void configurarEntityManager(EntityManager em);

    protected abstract void validarNegocio(D dto, boolean esNuevo);

    @Override
    public D guardar(D dto) {
        validarNegocio(dto, true);

        return ejecutarTransaccion(em -> {
            configurarEntityManager(em);

            E entidad = mapper.mapToEntity(dto);
            entidad = dao.guardar(entidad);

            return mapper.mapToDto(entidad);
        });
    }

    @Override
    public D actualizar(D dto) {
        validarNegocio(dto, false);

        return ejecutarTransaccion(em -> {
            configurarEntityManager(em);

            // Verificar que existe
            ID id = extraerId(dto);
            E existente = dao.buscarPorId(id);
            if (existente == null) {
                throw new RecursoNoEncontradoException(
                    claseEntidad.getSimpleName() + " no encontrado para actualizar");
            }

            E entidad = mapper.mapToEntity(dto);
            entidad = dao.actualizar(entidad);

            return mapper.mapToDto(entidad);
        });
    }

    @Override
    public void eliminar(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        ejecutarTransaccion(em -> {
            configurarEntityManager(em);

            E entidad = dao.buscarPorId(id);
            if (entidad == null) {
                throw new RecursoNoEncontradoException(
                    claseEntidad.getSimpleName() + " no encontrado");
            }

            validarEliminacion(entidad);
            dao.eliminar(id);
            return null;
        });
    }

    @Override
    public D buscarPorId(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        return ejecutarLectura(em -> {
            configurarEntityManager(em);

            E entidad = dao.buscarPorId(id);
            if (entidad == null) {
                throw new RecursoNoEncontradoException(
                    claseEntidad.getSimpleName() + " no encontrado");
            }

            return mapper.mapToDto(entidad);
        });
    }

    @Override
    public List<D> buscarTodos() {
        return ejecutarLectura(em -> {
            configurarEntityManager(em);
            return mapper.mapToDtoList(dao.buscarTodos());
        });
    }
    
    /**
     * Extrae el ID del DTO. Debe ser implementado por las subclases.
     */
    protected abstract ID extraerId(D dto);

    /**
     * Validación adicional antes de eliminar. Por defecto, no hace nada.
     */
    protected void validarEliminacion(E entidad) {
        // Hook method - las subclases pueden sobreescribir
    }
}