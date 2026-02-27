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

                    (t) -> {
                        if (t == null) return null;

                        UsuarioDTO dto = new UsuarioDTO();
                        dto.setId(t.getIdUsuario());
                        dto.setNombre(t.getNombre());
                        dto.setNoNomina(t.getNoNomina());
                        dto.setActivo(t.getActivo());

                        if (t.getPuesto() != null) {
                            dto.setIdPuesto(t.getPuesto().getIdPuesto());
                            dto.setNombrePuesto(t.getPuesto().getNombre());
                        }

                        return dto;
                    },

                    (dto) -> {
                        if (dto == null) return null;

                        Usuario t = new Usuario();
                        t.setIdUsuario(dto.getId());
                        t.setNombre(dto.getNombre());
                        t.setNoNomina(dto.getNoNomina());
                        t.setActivo(dto.getActivo() != null ? dto.getActivo() : true);

                        /*
                         * NO resolver relaciones aquí.
                         * Solo setear ID proxy si viene.
                         */
                        if (dto.getIdPuesto() != null) {
                            Puesto p = new Puesto();
                            p.setIdPuesto(dto.getIdPuesto());
                            t.setPuesto(p);
                        }

                        return t;
                    }
            );
}
