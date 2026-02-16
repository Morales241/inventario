package mapper;

import Dtos.TrabajadorDTO;
import Entidades.Puesto;
import Entidades.Trabajador;

/**
 * Mapeador para transformaciones entre la entidad Trabajador y su DTO.
 * <p>
Maneja la conversión bidireccional de Trabajador ↔ TrabajadorDTO,
incluyendo la relación con la entidad Puesto.
</p>
 */
public class MapperTrabajador {

    public static final Mapper<Trabajador, TrabajadorDTO> converter =
            new Mapper<>(

                    (t) -> {
                        if (t == null) return null;

                        TrabajadorDTO dto = new TrabajadorDTO();
                        dto.setId(t.getIdTrabajador());
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

                        Trabajador t = new Trabajador();
                        t.setIdTrabajador(dto.getId());
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
