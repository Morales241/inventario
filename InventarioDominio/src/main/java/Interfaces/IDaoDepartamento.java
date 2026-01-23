package Interfaces;

import Entidades.Departamento;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IDaoDepartamento extends IDaoGenerico<Departamento, Long>{
    
    public Departamento busquedaEspecifica(String nombre);
    
    public List<Departamento> busquedaConFiltros(String nombre, Long  idSucursal);
    
}
