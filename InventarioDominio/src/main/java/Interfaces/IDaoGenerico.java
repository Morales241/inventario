package Interfaces;

import jakarta.persistence.EntityManager;
import java.util.List;

/**
 *
 * @author JMorales
 * @param <T>
 * @param <ID>
 */
public interface IDaoGenerico<T, ID> {
    T guardar(T entidad);
    T actualizar(T entidad);
    void eliminar(ID id);
    T buscarPorId(ID id);
    List<T> buscarTodos();
    void setEntityManager(EntityManager em);
}