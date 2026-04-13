package mapper;

import Dtos.ModeloDTO;
import Entidades.Modelo;

/**
 * Mapeador para transformaciones entre la entidad Modelo de equipo y su DTO.
 * <p>
 * Convierte especificaciones técnicas del modelo (marca, RAM, almacenamiento, procesador)
 * entre la entidad y su representación en DTO para catálogos y búsquedas.
 * </p>
 */
public class MapperModelo {

    public static final Mapper<Modelo, ModeloDTO> converter =
            new Mapper<>(

                    (m) -> {
                        if (m == null) return null;

                        ModeloDTO dto = new ModeloDTO();
                        dto.setIdModelo(m.getId());
                        dto.setNombre(m.getNombre());
                        dto.setMarca(m.getMarca());
                        dto.setMemoriaRam(m.getMemoriaRam());
                        dto.setAlmacenamiento(m.getAlmacenamiento());
                        dto.setProcesador(m.getProcesador());

                        // Campos de auditoría
                        dto.setCreadoPor(m.getCreadoPor());
                        dto.setFechaCreacion(m.getFechaCreacion());
                        dto.setModificadoPor(m.getModificadoPor());
                        dto.setFechaModificacion(m.getFechaModificacion());

                        return dto;
                    },

                    (dto) -> {
                        if (dto == null) return null;

                        Modelo m = new Modelo();
                        m.setId(dto.getIdModelo());
                        m.setNombre(dto.getNombre());
                        m.setMarca(dto.getMarca());
                        m.setMemoriaRam(dto.getMemoriaRam());
                        m.setAlmacenamiento(dto.getAlmacenamiento());
                        m.setProcesador(dto.getProcesador());
                        return m;
                    }
            );
}
