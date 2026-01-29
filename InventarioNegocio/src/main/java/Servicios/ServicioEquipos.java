package Servicios;

import Dao.*;
import Dtos.*;
import Entidades.*;
import Mappers.MapperEquipos;
import Mappers.MapperModelo;
import Enums.EstadoEquipo;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioEquipos {

    private final DaoEquipoDeComputo daoGeneral = new DaoEquipoDeComputo();
    private final DaoEquipoDeEscritorio daoEscritorio = new DaoEquipoDeEscritorio();
    private final DaoMovil daoMovil = new DaoMovil();
    private final DaoOtroEquipo daoOtro = new DaoOtroEquipo();
    private final DaoModelo daoModelo = new DaoModelo();

    /**
     * Busca equipos aplicando filtros dinámicos.
     * Retorna una lista base para llenar la tabla principal.
     * @param gri
     * @param idSucursal
     * @param estado
     * @param criterioBusqueda
     * @return 
     */
    public List<EquipoBaseDTO> buscarEquipos(Integer gri, Long idSucursal, EstadoEquipo estado, String criterioBusqueda) {
        List<EquipoDeComputo> lista = daoGeneral.buscarConFiltros(gri, idSucursal, estado, criterioBusqueda);
        
        return lista.stream().map(equipo -> {
            if (equipo instanceof EquipoDeEscritorio) {
                return MapperEquipos.escritorio.mapToDto((EquipoDeEscritorio) equipo);
            } else if (equipo instanceof Movil) {
                return MapperEquipos.movil.mapToDto((Movil) equipo);
            } else {
                return MapperEquipos.otro.mapToDto((OtroEquipo) equipo);
            }
        }).collect(Collectors.toList());
    }

    /**
     * Obtiene un equipo específico por ID para RELLENAR EL FORMULARIO de edición.
     * Devuelve un Object que deberás castear en la vista (instanceof) 
     * o puedes hacer 3 métodos separados.
     */
    public EquipoBaseDTO obtenerEquipoPorId(Long id) throws Exception {
        EquipoDeComputo e = daoGeneral.buscarPorId(id);
        if (e == null) throw new Exception("Equipo no encontrado");

        if (e instanceof EquipoDeEscritorio) {
            return MapperEquipos.escritorio.mapToDto((EquipoDeEscritorio) e);
        } else if (e instanceof Movil) {
            return MapperEquipos.movil.mapToDto((Movil) e);
        } else {
            return MapperEquipos.otro.mapToDto((OtroEquipo) e);
        }
    }

    public void guardarEscritorio(EquipoEscritorioDTO dto) throws Exception {
        validarDatosComunes(dto);
        EquipoDeEscritorio entidad = MapperEquipos.escritorio.mapToEntity(dto);
        
        if (dto.getIdEquipo() != null && dto.getIdEquipo() > 0) {
            daoEscritorio.actualizar(entidad);
        } else {
            daoEscritorio.guardar(entidad);
        }
    }

    public void guardarMovil(MovilDTO dto) throws Exception {
        validarDatosComunes(dto);
        if (dto.getImei() == null || dto.getImei().isEmpty()) throw new Exception("El IMEI es obligatorio");
        
        Movil entidad = MapperEquipos.movil.mapToEntity(dto);
        
        if (dto.getIdEquipo() != null && dto.getIdEquipo() > 0) {
            daoMovil.actualizar(entidad);
        } else {
            daoMovil.guardar(entidad);
        }
    }

    public void guardarOtro(OtroEquipoDTO dto) throws Exception {
        validarDatosComunes(dto);
        OtroEquipo entidad = MapperEquipos.otro.mapToEntity(dto);
        
        if (dto.getIdEquipo() != null && dto.getIdEquipo() > 0) {
            daoOtro.actualizar(entidad);
        } else {
            daoOtro.guardar(entidad);
        }
    }

    public void eliminarEquipo(Long id) throws Exception {
        EquipoDeComputo e = daoGeneral.buscarPorId(id);
        if (e.getEstado() == EstadoEquipo.ASIGNADO) {
            throw new Exception("No se puede eliminar un equipo que está actualmente asignado a un empleado.");
        }
        daoGeneral.eliminar(id);
    }

    public List<ModeloDto> listarModelos() {
        return MapperModelo.converter.mapToDtoList(daoModelo.buscarTodos());
    }
    
    public void guardarModelo(ModeloDto dto) throws Exception {
        if(dto.getNombre().isEmpty()) throw new Exception("El nombre del modelo es requerido");
        daoModelo.guardar(MapperModelo.converter.mapToEntity(dto));
    }

    private void validarDatosComunes(EquipoBaseDTO dto) throws Exception {
        if (dto.getGri() == null || dto.getGri() <= 0) throw new Exception("El GRI es obligatorio y válido");
        if (dto.getIdSucursal() == null) throw new Exception("Debe seleccionar una sucursal");
    }
}