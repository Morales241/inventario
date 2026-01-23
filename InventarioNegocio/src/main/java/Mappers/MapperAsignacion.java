package Mappers;

import Dtos.AsignacionDto;
import Entidades.EquipoAsignado;

public class MapperAsignacion {

    public static final Mapper<EquipoAsignado, AsignacionDto> converter = new Mapper<>(
        (entity) -> {
            AsignacionDto dto = new AsignacionDto();
            dto.setId(entity.getId());
            dto.setFechaEntrega(entity.getFechaEntrega());
            dto.setFechaDevolucion(entity.getFechaDevolucion());
            
            if (entity.getTrabajador() != null) {
                dto.setNombreTrabajador(entity.getTrabajador().getNombre());
            }
            if (entity.getEquipoDeComputo() != null) {
                String desc = "GRI: " + entity.getEquipoDeComputo().getGri();
                if (entity.getEquipoDeComputo().getModelo() != null) {
                    desc += " (" + entity.getEquipoDeComputo().getModelo().getNombre() + ")";
                }
                dto.setDescripcionEquipo(desc);
            }
            return dto;
        },
        (dto) -> {
            EquipoAsignado entity = new EquipoAsignado();
            entity.setId(dto.getId());
            entity.setFechaEntrega(dto.getFechaEntrega());
            entity.setFechaDevolucion(dto.getFechaDevolucion());
            return entity;
        }
    );
}