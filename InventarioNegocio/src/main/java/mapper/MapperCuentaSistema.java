package mapper;

import Dtos.CuentaSistemaDTO;
import Entidades.CuentaSistema;
import Enums.RolCuenta;

/**
 * Mapeador CuentaSistema ↔ CuentaSistemaDTO.
 */
public class MapperCuentaSistema {

    public static final Mapper<CuentaSistema, CuentaSistemaDTO> converter =
            new Mapper<>(

                    // ── entity → DTO ──────────────────────────────────────────
                    (entity) -> {
                        if (entity == null) return null;

                        CuentaSistemaDTO dto = new CuentaSistemaDTO();
                        dto.setId(entity.getId());
                        dto.setUsername(entity.getUsername());

                        if (entity.getRol() != null) {
                            dto.setRol(entity.getRol().name());
                        }

                        if (entity.getVersion() != null) {
                            dto.setVersion(entity.getVersion());
                        }

                        // Campos de auditoría (de AuditoriaBase)
                        dto.setCreadoPor(entity.getCreadoPor());
                        dto.setFechaCreacion(entity.getFechaCreacion());
                        dto.setModificadoPor(entity.getModificadoPor());
                        dto.setFechaModificacion(entity.getFechaModificacion());

                        return dto;
                    },

                    // ── DTO → entity ────────
                    (dto) -> {
                        if (dto == null) return null;

                        CuentaSistema u = new CuentaSistema();
                        
                        if (dto.getId() != null) {
                            u.setId(dto.getId());
                        }
                        
                        u.setUsername(dto.getUsername());
                        u.setPassword(dto.getPassword());
                        
                        if (dto.getRol() != null) {
                            u.setRol(RolCuenta.valueOf(dto.getRol()));
                        }

                        if (dto.getVersion() != null) {
                            u.setVersion(dto.getVersion());
                        }

                        return u;
                    }
            );
}
