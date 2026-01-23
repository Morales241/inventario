package Interfaces;

import Entidades.Sucursal;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IDaoSucursal extends IDaoGenerico<Sucursal, Long>{
    public Sucursal busquedaEspecifica(String nombre, String ubicacion);
    
    public List<Sucursal> busquedaConFiltros(String cadena, String cadenaUbicacion, Long idEmpresa);
}
