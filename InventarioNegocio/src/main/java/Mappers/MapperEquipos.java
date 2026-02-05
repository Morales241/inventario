package Mappers;

import Dtos.*;
import Entidades.*;

public class MapperEquipos {

    public static final Mapper<EquipoDeEscritorio, EquipoEscritorioDTO> escritorio = new Mapper<>(
       
        (e) -> {
            EquipoEscritorioDTO dto = new EquipoEscritorioDTO();
            mapCommonToDto(e, dto);
            dto.setNombreEquipo(e.getNombreEquipo());
            dto.setCuenta(e.getCuenta());
            dto.setFinalGarantia(e.getFinalGarantia());
            return dto;
        },
        (d) -> {
            EquipoDeEscritorio e = new EquipoDeEscritorio();
            mapCommonToEntity(d, e); 
           
            e.setNombreEquipo(d.getNombreEquipo());
            e.setCuenta(d.getCuenta());
            e.setFinalGarantia(d.getFinalGarantia());
            return e;
        }
    );

    public static final Mapper<Movil, MovilDTO> movil = new Mapper<>(
        (e) -> {
            MovilDTO dto = new MovilDTO();
            mapCommonToDto(e, dto);
            
            dto.setNumCelular(e.getNumCelular());
            dto.setCargador(e.getCargador());
            dto.setFunda(e.getFunda());
            dto.setManosLibres(e.getManosLibres());
            return dto;
        },
        (d) -> {
            Movil e = new Movil();
            mapCommonToEntity(d, e);
            
            e.setNumCelular(d.getNumCelular());
            e.setCargador(d.getCargador());
            e.setFunda(d.getFunda());
            e.setManosLibres(d.getManosLibres());
            return e;
        }
    );

    public static final Mapper<OtroEquipo, OtroEquipoDTO> otro = new Mapper<>(
        (e) -> {
            OtroEquipoDTO dto = new OtroEquipoDTO();
            mapCommonToDto(e, dto);
            
            // Específico de Otro
            dto.setTipo(e.getTipo());
            dto.setTituloCampoExtra(e.getTituloCampoExtra());
            dto.setContenidoCampoExtra(e.getContenidoCampoExtra());
            dto.setTituloCampoExtra2(e.getTituloCampoExtra2());
            dto.setContenidoCampoExtra2(e.getContenidoCampoExtra2());
            return dto;
        },
        (d) -> {
            OtroEquipo e = new OtroEquipo();
            mapCommonToEntity(d, e);
            
            e.setTipo(d.getTipo());
            e.setTituloCampoExtra(d.getTituloCampoExtra());
            e.setContenidoCampoExtra(d.getContenidoCampoExtra());
            e.setTituloCampoExtra2(d.getTituloCampoExtra2());
            e.setContenidoCampoExtra2(d.getContenidoCampoExtra2());
            return e;
        }
    );

    private static void mapCommonToDto(EquipoDeComputo e, EquipoBaseDTO dto) {
        dto.setIdEquipo(e.getIdEquipo());
        dto.setGri(e.getGri());
        dto.setFactura(e.getFactura());
        dto.setEstado(e.getEstado());
        dto.setCondicion(e.getCondicion());
        dto.setObservaciones(e.getObservaciones());
        dto.setFechaCompra(e.getFechaCompra());
        
        if (e.getSucursal() != null) {
            dto.setIdSucursal(e.getSucursal().getIdSucursal());
            dto.setNombreSucursal(e.getSucursal().getNombre());
        }
        if (e.getModelo() != null) {
            dto.setIdModelo(e.getModelo().getId());
            dto.setNombreModelo(e.getModelo().getMarca() + " " + e.getModelo().getNombre());
        }
    }

    private static void mapCommonToEntity(EquipoBaseDTO dto, EquipoDeComputo e) {
        e.setIdEquipo(dto.getIdEquipo());
        e.setGri(dto.getGri());
        e.setFactura(dto.getFactura());
        e.setEstado(dto.getEstado());
        e.setCondicion(dto.getCondicion());
        e.setObservaciones(dto.getObservaciones());
        e.setFechaCompra(dto.getFechaCompra());
        
        if (dto.getIdSucursal() != null) {
            Sucursal s = new Sucursal();
            s.setIdSucursal(dto.getIdSucursal());
            e.setSucursal(s);
        }
        
        if (dto.getIdModelo() != null) {
            Modelo m = new Modelo();
            m.setId(dto.getIdModelo());
            e.setModelo(m);
        }
    }
}