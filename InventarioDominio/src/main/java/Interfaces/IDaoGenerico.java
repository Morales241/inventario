package Interfaces;

import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IDaoGenerico<T, ID> {
    void guardar(T entidad);
    void actualizar(T entidad);
    void eliminar(ID id);
    T buscarPorId(ID id);
    List<T> buscarTodos();
}