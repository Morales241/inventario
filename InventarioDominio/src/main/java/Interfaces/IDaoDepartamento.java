package Interfaces;

import Entidades.Departamento;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IDaoDepartamento{
    
    public Departamento busquedaEspecifica(String nombre);
    
    public List<Departamento> busquedaConFiltros(String nombre, String nombreSucursal);
    
}
