package Implementaciones;

import Dtos.EquipoBaseDTO;
import Dtos.EquipoEscritorioDTO;
import Dtos.ModeloDto;
import Dtos.MovilDTO;
import Dtos.OtroEquipoDTO;
import Enums.EstadoEquipo;
import Interfaces.IFachadaEquipos;
import Mappers.MapperEquipos;
import Mappers.MapperModelo;
import Servicios.ServicioEquipos;
import java.util.List;

/**
 * Implementación de la fachada para operaciones de gestión de equipos de cómputo.
 * <p>
 * Proporciona acceso simplificado a la lógica de negocio del servicio de equipos.
 * Utiliza el patrón Facade para abstraer la complejidad de las operaciones.
 * </p>
 * @author JMorales
 */
public class FachadaEquipos implements IFachadaEquipos{

    private final ServicioEquipos servicioEquipos;
    
    public FachadaEquipos() {
        this.servicioEquipos = new ServicioEquipos();
    }
    
    @Override
    public List<EquipoBaseDTO> buscarEquipos(Integer gri, EstadoEquipo estado, String criterioBusqueda) {
        return servicioEquipos.buscarEquipos(gri, estado, criterioBusqueda);
    }

    @Override
    public EquipoBaseDTO obtenerEquipoPorId(Long id) throws Exception {
        return servicioEquipos.obtenerEquipoPorId(id);
    }

    @Override
    public EquipoEscritorioDTO guardarEscritorio(EquipoEscritorioDTO dto) throws Exception {
        return servicioEquipos.guardarEscritorio(dto);
    }

    @Override
    public MovilDTO guardarMovil(MovilDTO dto) throws Exception {
        return servicioEquipos.guardarMovil(dto);
    }

    @Override
    public OtroEquipoDTO guardarOtro(OtroEquipoDTO dto) throws Exception {
        return servicioEquipos.guardarOtro(dto);
    }

    @Override
    public void eliminarEquipo(Long id) throws Exception {
        servicioEquipos.eliminarEquipo(id);
    }

    @Override
    public List<ModeloDto> listarModelos(String noSerie) {
        return servicioEquipos.listarModelos(noSerie);
    }

    @Override
    public ModeloDto guardarModelo(ModeloDto dto) throws Exception {
        return servicioEquipos.guardarModelo(dto);
    }    
    
    @Override
    public List<ModeloDto> busquedaConFiltros(String marca, String memoriaRam, String almacenamiento, String procesador){
        return servicioEquipos.busquedaConFiltros(marca, memoriaRam, almacenamiento, procesador);
    }

    @Override
    public ModeloDto busquedarModeloPorId(Long id) {
        return servicioEquipos.busquedarModeloPorId(id);
    }
}
