package Interfaces;

import Dtos.AsignacionDto;
import Dtos.EquipoBaseDTO;
import Dtos.EquipoEscritorioDTO;
import Dtos.ModeloDto;
import Dtos.MovilDTO;
import Dtos.OtroEquipoDTO;
import Enums.EstadoEquipo;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IFachadaEquipos {
    
    public List<EquipoBaseDTO> buscarEquipos(Integer gri, Long idSucursal, EstadoEquipo estado, String criterioBusqueda);
    
    public EquipoBaseDTO obtenerEquipoPorId(Long id) throws Exception;
    
    public void guardarEscritorio(EquipoEscritorioDTO dto) throws Exception;
    
    public void guardarMovil(MovilDTO dto) throws Exception;
    
    public void guardarOtro(OtroEquipoDTO dto) throws Exception;
    
    public void eliminarEquipo(Long id) throws Exception;
    
    public List<ModeloDto> listarModelos();
    
    public void guardarModelo(ModeloDto dto) throws Exception;
    
}
