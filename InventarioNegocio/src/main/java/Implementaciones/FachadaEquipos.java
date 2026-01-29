/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Implementaciones;

import Dtos.EquipoBaseDTO;
import Dtos.EquipoEscritorioDTO;
import Dtos.ModeloDto;
import Dtos.MovilDTO;
import Dtos.OtroEquipoDTO;
import Enums.EstadoEquipo;
import Interfaces.IFachadaEquipos;
import Servicios.ServicioEquipos;
import java.util.List;

/**
 *
 * @author JMorales
 */
public class FachadaEquipos implements IFachadaEquipos{

    private final ServicioEquipos servicioEquipos;
    
    public FachadaEquipos() {
        this.servicioEquipos = new ServicioEquipos();
    }
    
    @Override
    public List<EquipoBaseDTO> buscarEquipos(Integer gri, Long idSucursal, EstadoEquipo estado, String criterioBusqueda) {
        return servicioEquipos.buscarEquipos(gri, idSucursal, estado, criterioBusqueda);
    }

    @Override
    public EquipoBaseDTO obtenerEquipoPorId(Long id) throws Exception {
        return servicioEquipos.obtenerEquipoPorId(id);
    }

    @Override
    public void guardarEscritorio(EquipoEscritorioDTO dto) throws Exception {
        servicioEquipos.guardarEscritorio(dto);
    }

    @Override
    public void guardarMovil(MovilDTO dto) throws Exception {
        servicioEquipos.guardarMovil(dto);
    }

    @Override
    public void guardarOtro(OtroEquipoDTO dto) throws Exception {
        servicioEquipos.guardarOtro(dto);
    }

    @Override
    public void eliminarEquipo(Long id) throws Exception {
        servicioEquipos.eliminarEquipo(id);
    }

    @Override
    public List<ModeloDto> listarModelos() {
        return servicioEquipos.listarModelos();
    }

    @Override
    public void guardarModelo(ModeloDto dto) throws Exception {
        servicioEquipos.guardarModelo(dto);
    }    
}
