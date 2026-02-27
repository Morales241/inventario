package mapper;

import Dtos.UsuarioDTO;
import Entidades.Puesto;
import Entidades.Usuario;

/**
 * Mapeador para transformaciones entre la entidad Usuario y su DTO.
 * <p>
Maneja la conversión bidireccional de Usuario ↔ UsuarioDTO,
incluyendo la relación con la entidad Puesto.
</p>
 */
public class MapperUsuario {

    public static final Mapper<Usuario, UsuarioDTO> converter =
            new Mapper<>(

                    (u) -> {
                        if (u == null) return null;

                        UsuarioDTO dto = new UsuarioDTO();
                        dto.setId(u.getIdUsuario());
                        dto.setNombre(u.getNombre());
                        dto.setNoNomina(u.getNoNomina());
                        dto.setActivo(u.getActivo());

                        if (u.getPuesto() != null) {
                            dto.setIdPuesto(u.getPuesto().getIdPuesto());
                            dto.setNombrePuesto(u.getPuesto().getNombre());
                        }
                        
                        if (u.getVersion() != null) {
                            dto.setVersion(u.getVersion());
                            
                        }

                        return dto;
                    },

                    (dto) -> {
                        if (dto == null) return null;

                        Usuario u = new Usuario();
                        u.setIdUsuario(dto.getId());
                        u.setNombre(dto.getNombre());
                        u.setNoNomina(dto.getNoNomina());
                        u.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

                        /*
                         * NO resolver relaciones aquí.
                         * Solo setear ID proxy si viene.
                         */
                        if (dto.getIdPuesto() != null) {
                            Puesto p = new Puesto();
                            p.setIdPuesto(dto.getIdPuesto());
                            u.setPuesto(p);
                        }
                        
                        if (dto.getVersion() != null) {
                            u.setVersion(dto.getVersion());
                            
                        }

                        return u;
                    }
            );
}
