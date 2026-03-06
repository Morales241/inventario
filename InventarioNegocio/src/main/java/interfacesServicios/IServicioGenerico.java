// InventarioNegocio/src/main/java/interfacesServicios/IServicioGenerico.java
package interfacesServicios;

import java.util.List;

/**
 * Interfaz genérica para servicios CRUD básicos.
 * @param <D> Tipo del DTO
 * @param <ID> Tipo del ID (Long, Integer, etc.)
 */
public interface IServicioGenerico<D, ID> {
    
    /**
     * Guarda una nueva entidad.
     * @param dto Datos de la entidad a guardar
     * @return DTO de la entidad guardada con ID asignado
     */
    D guardar(D dto);
    
    /**
     * Actualiza una entidad existente.
     * @param dto Datos actualizados de la entidad
     * @return DTO de la entidad actualizada
     */
    D actualizar(D dto);
    
    /**
     * Elimina una entidad por su ID.
     * @param id Identificador de la entidad a eliminar
     */
    void eliminar(ID id);
    
    /**
     * Busca una entidad por su ID.
     * @param id Identificador de la entidad
     * @return DTO de la entidad encontrada
     */
    D buscarPorId(ID id);
    
    /**
     * Lista todas las entidades.
     * @return Lista de DTOs de todas las entidades
     */
    List<D> buscarTodos();
    
    /**
     * Busca entidades que coincidan con un filtro.
     * @param filtro Criterio de búsqueda
     * @return Lista de DTOs que coinciden con el filtro
     */
    List<D> buscarConFiltro(String filtro);
}