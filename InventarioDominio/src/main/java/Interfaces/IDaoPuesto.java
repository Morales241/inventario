package Interfaces;

import Entidades.Puesto;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IDaoPuesto extends IDaoGenerico<Puesto, Long>{
    public Puesto busquedaEspecifica(String nombre);
    
    public List<Puesto> busquedaPorDepartamento(Long idDepartamento);    
    
    public List<Puesto> busquedaPorEmpresa(Long idEmpresa);
}
