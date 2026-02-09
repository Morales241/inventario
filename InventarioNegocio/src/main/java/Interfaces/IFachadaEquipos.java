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
    
    public List<EquipoBaseDTO> buscarEquipos(Integer gri, EstadoEquipo estado, String criterioBusqueda);
    
    public EquipoBaseDTO obtenerEquipoPorId(Long id) throws Exception;
    
    public EquipoEscritorioDTO guardarEscritorio(EquipoEscritorioDTO dto) throws Exception;
    
    public MovilDTO guardarMovil(MovilDTO dto) throws Exception;
    
    public OtroEquipoDTO guardarOtro(OtroEquipoDTO dto) throws Exception;
    
    public void eliminarEquipo(Long id) throws Exception;
    
    public List<ModeloDto> listarModelos(String nombreModelo);
    
    public ModeloDto guardarModelo(ModeloDto dto) throws Exception;
    
    public List<ModeloDto> busquedaConFiltros(String marca, String memoriaRam, String almacenamiento, String procesador);
    
    public ModeloDto busquedarModeloPorId(Long id);
}
