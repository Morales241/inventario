package Mappers;

import Dtos.AsignacionDto;
import Entidades.EquipoAsignado;

/**
 * Mapeador para transformaciones entre la entidad EquipoAsignado y su DTO.
 * <p>
 * Convierte los registros de asignaciones/préstamos de equipos entre entidadesy DTO,
 * incluyendo información transformada del trabajador y el equipo para presentación.
 * </p>
 */
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
                String desc = "GRI: " + entity.getEquipoDeComputo().getGry();
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