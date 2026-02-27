package mapper;

import Dtos.AsignacionDTO;
import Entidades.EquipoAsignado;

/**
 * Mapeador para transformaciones entre la entidad EquipoAsignado y su DTO.
 * <p>
 * Convierte los registros de asignaciones/préstamos de equipos entre entidadesy
 * DTO, incluyendo información transformada del Usuario y el equipo para
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

                        if (entity.getUsuario()!= null) {
                            dto.setIdUsuario(entity.getUsuario().getIdUsuario());
                            dto.setNombreUsuario(entity.getUsuario().getNombre());
                        }

                        if (entity.getEquipoDeComputo() != null) {
                            dto.setIdEquipo(entity.getEquipoDeComputo().getId());
                            dto.setIdentificadorEquipo(entity.getEquipoDeComputo().getIndetificador());
                        }
                        
                        if (entity.getVersion() != null) {
                            dto.setVersion(entity.getVersion());
                            
                        }

                        return dto;
                    },
                    (dto) -> {
                        EquipoAsignado entity = new EquipoAsignado();

                        entity.setId(dto.getId());
                        entity.setFechaEntrega(dto.getFechaEntrega());
                        entity.setFechaDevolucion(dto.getFechaDevolucion());
                        
                        if (dto.getVersion() != null) {
                            entity.setVersion(dto.getVersion());
                            
                        }
                        
                        return entity;
                    }
            );
}
