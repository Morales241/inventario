package Mappers;

import Dtos.ModeloDto;
import Entidades.Modelo;

public class MapperModelo {

    public static final Mapper<Modelo, ModeloDto> converter = new Mapper<>(
            (m) -> {
                ModeloDto dto = new ModeloDto();
                dto.setId(m.getId());
                dto.setNombre(m.getNombre());
                dto.setMarca(m.getMarca());
                dto.setEspecificaciones("RAM: " + m.getMemoriaRam()
                        + "GB / HDD: " + m.getAlmacenamiento()
                        + "GB / CPU: " + m.getPreocesador());
                return dto;
            },
            (dto) -> {
                Modelo m = new Modelo();
                m.setId(dto.getId());
                m.setNombre(dto.getNombre());
                m.setMarca(dto.getMarca());
                return m;
            }
    );
}
