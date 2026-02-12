package Mappers;

import Dtos.AsignacionDTO;
import Entidades.EquipoAsignado;

/**
 * Mapeador para transformaciones entre la entidad EquipoAsignado y su DTO.
 * <p>
 * Convierte los registros de asignaciones/préstamos de equipos entre entidadesy
 * DTO, incluyendo información transformada del trabajador y el equipo para
 * presentación.
 * </p>
 */
public class MapperAsignacion {

    public static final Mapper<EquipoAsignado, AsignacionDTO> converter
            = new Mapper<>(
                    (entity) -> {
                        AsignacionDTO dto = new AsignacionDTO();

                        dto.setId(entity.getId());
                        dto.setFechaEntrega(entity.getFechaEntrega());
                        dto.setFechaDevolucion(entity.getFechaDevolucion());

                        if (entity.getTrabajador() != null) {
                            dto.setIdTrabajador(entity.getTrabajador().getIdTrabajador());
                            dto.setNombreTrabajador(entity.getTrabajador().getNombre());
                        }

                        if (entity.getEquipoDeComputo() != null) {
                            dto.setIdEquipo(entity.getEquipoDeComputo().getId());
                            dto.setIdentificadorEquipo(entity.getEquipoDeComputo().getIndetificador());
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
