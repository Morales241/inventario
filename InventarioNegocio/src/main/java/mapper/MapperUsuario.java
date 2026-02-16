package mapper;

import Dtos.UsuarioDTO;
import Entidades.UsuarioSistema;
import Enums.RolUsuario;
import mapper.Mapper;

/**
 * Mapeador para transformaciones entre la entidad UsuarioSistema y su DTO.
 * <p>
 * Convierte datos de autenticación y autorización entre la entidad y el DTO.
 * <b>Nota de seguridad:</b> La contraseña no se incluye en la conversión desde DTO a entidad.
 * </p>
 */
public class MapperUsuario {

    public static final Mapper<UsuarioSistema, UsuarioDTO> converter =
            new Mapper<>(

                    (entity) -> {
                        if (entity == null) return null;

                        UsuarioDTO dto = new UsuarioDTO();
                        dto.setId(entity.getId());
                        dto.setUsername(entity.getUsername());

                        if (entity.getRol() != null) {
                            dto.setRol(entity.getRol().name());
                        }

                        return dto;
                    },

                    (dto) -> {
                        if (dto == null) return null;

                        UsuarioSistema u = new UsuarioSistema();
                        u.setId(dto.getId());
                        u.setUsername(dto.getUsername());

                        if (dto.getRol() != null) {
                            u.setRol(RolUsuario.valueOf(dto.getRol()));
                        }
                        return u;
                    }
            );
}
