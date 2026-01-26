package Interfaces;

import Dtos.EquipoDto;
import Dtos.ModeloDto;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IFachadaEquipos {
    
    public List<EquipoDto> buscarEquipos(String criterio);
    
    public List<ModeloDto> listarModelos();
    
    public void guardarEquipo(EquipoDto dto) throws Exception;
}
