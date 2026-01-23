package Interfaces;

import Entidades.Empresa;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IDaoEmpresa extends IDaoGenerico<Empresa, Long>{
    public Empresa buscarPorNombre(String nombre);
    
    public List<Empresa> buscarPorCoincidencias(String cadena);
}
