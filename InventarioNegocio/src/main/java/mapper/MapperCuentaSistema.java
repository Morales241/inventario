package mapper;

import Dtos.CuentaSistemaDTO;
import Entidades.CuentaSistema;
import Enums.RolUsuario;
import mapper.Mapper;

/**
 * Mapeador para transformaciones entre la entidad cuentaSistema y su DTO.
 * <p>
 * Convierte datos de autenticación y autorización entre la entidad y el DTO.
 * <b>Nota de seguridad:</b> La contraseña no se incluye en la conversión desde DTO a entidad.
 * </p>
 */
public class MapperCuentaSistema {

    public static final Mapper<CuentaSistema, CuentaSistemaDTO> converter =
            new Mapper<>(

                    (entity) -> {
                        if (entity == null) return null;

                        CuentaSistemaDTO dto = new CuentaSistemaDTO();
                        dto.setId(entity.getId());
                        dto.setUsername(entity.getUsername());

                        if (entity.getRol() != null) {
                            dto.setRol(entity.getRol().name());
                        }
                      
                        return dto;
                    },

                    (dto) -> {
                        if (dto == null) return null;

                        CuentaSistema u = new CuentaSistema();
                        u.setId(dto.getId());
                        u.setUsername(dto.getUsername());

                        if (dto.getRol() != null) {
                            u.setRol(RolUsuario.valueOf(dto.getRol()));
                        }
                        return u;
                    }
            );
}
