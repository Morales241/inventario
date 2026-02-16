package Implementaciones;

import Dtos.EquipoBaseDTO;
import Dtos.EquipoEscritorioDTO;
import Dtos.ModeloDTO;
import Dtos.MovilDTO;
import Dtos.OtroEquipoDTO;
import Enums.EstadoEquipo;
import InterfacesFachada.IFachadaEquipos;
import mapper.MapperModelo;
import Servicios.ServicioEquipos;
import interfacesServicios.IServicioEquipos;
import java.util.List;

/**
 * Implementación de la fachada para operaciones de gestión de equipos de
 * cómputo.
 * <p>
 * Proporciona acceso simplificado a la lógica de negocio del servicio de
 * equipos. Utiliza el patrón Facade para abstraer la complejidad de las
 * operaciones.
 * </p>
 *
 * @author JMorales
 */
public class FachadaEquipos implements IFachadaEquipos {

    private final IServicioEquipos servicioEquipos;

    public FachadaEquipos(IServicioEquipos servicioEquipos) {
        this.servicioEquipos = servicioEquipos;
    }

    public FachadaEquipos() {
        this(new ServicioEquipos());
    }

    @Override
    public List<EquipoBaseDTO> buscarEquipos(Integer gry, EstadoEquipo estado, String criterioBusqueda) {
        return servicioEquipos.buscarEquipos(gry, estado, criterioBusqueda);
    }

    @Override
    public EquipoBaseDTO obtenerEquipoPorId(Long id) {
        return servicioEquipos.obtenerPorId(id);
    }

    @Override
    public EquipoBaseDTO buscarPorGry(Integer gry) {
        return servicioEquipos.buscarPorGry(gry);
    }

    @Override
    public EquipoEscritorioDTO guardarEscritorio(EquipoEscritorioDTO dto) {
        return servicioEquipos.guardarEscritorio(dto);
    }

    @Override
    public MovilDTO guardarMovil(MovilDTO dto) {
        return servicioEquipos.guardarMovil(dto);
    }

    @Override
    public OtroEquipoDTO guardarOtro(OtroEquipoDTO dto) {
        return servicioEquipos.guardarOtro(dto);
    }

    @Override
    public void eliminarEquipo(Long id) {
        servicioEquipos.eliminarEquipo(id);
    }

    @Override
    public ModeloDTO guardarModelo(ModeloDTO dto) {
        return servicioEquipos.guardarModelo(dto);
    }

    @Override
    public List<ModeloDTO> listarModelos() {
        return servicioEquipos.listarModelos();
    }

    @Override
    public ModeloDTO buscarModeloPorId(Long id) {
        return servicioEquipos.buscarModeloPorId(id);
    }

    @Override
    public List<ModeloDTO> buscarModelosConFiltros(
            String nombre,
            String marca,
            Integer memoriaRam,
            Integer almacenamiento,
            String procesador) {
        
        return servicioEquipos.buscarModelosConFiltros(nombre, marca, memoriaRam, almacenamiento, procesador);
    }
}
