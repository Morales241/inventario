package Mappers;

import Dtos.ModeloDto;
import Entidades.Modelo;

/**
 * Mapeador para transformaciones entre la entidad Modelo de equipo y su DTO.
 * <p>
 * Convierte especificaciones técnicas del modelo (marca, RAM, almacenamiento, procesador)
 * entre la entidad y su representación en DTO para catálogos y búsquedas.
 * </p>
 */
public class MapperModelo {

    public static final Mapper<Modelo, ModeloDto> converter = new Mapper<>(
            
            (m) -> {
                ModeloDto dto = new ModeloDto();
                dto.setIdModelo(m.getId());
                dto.setNombre(m.getNombre());
                dto.setMarca(m.getMarca());
                dto.setMemoriaRam(m.getMemoriaRam());
                dto.setAlmacenamiento(m.getAlmacenamiento());
                dto.setProcesador(m.getPreocesador()); 
                return dto;
            },
            (dto) -> {
                Modelo m = new Modelo();
                m.setId(dto.getIdModelo());
                m.setNombre(dto.getNombre());
                m.setMarca(dto.getMarca());
                m.setMemoriaRam(dto.getMemoriaRam());
                m.setAlmacenamiento(dto.getAlmacenamiento());
                m.setPreocesador(dto.getProcesador());
                return m;
            }
    );
}