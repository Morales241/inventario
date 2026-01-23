package Interfaces;

import Entidades.Puesto;

/**
 *
 * @author JMorales
 */
public interface IDaoPuesto extends IDaoGenerico<Puesto, Long>{
    public Puesto busquedaEspecifica(String nombre);
}
