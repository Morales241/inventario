package Mappers;

import Dtos.TrabajadorDto;
import Entidades.Puesto;
import Entidades.Trabajador;

public class MapperTrabajador {

    public static final Mapper<Trabajador, TrabajadorDto> converter = new Mapper<>(
        (t) -> {
            TrabajadorDto dto = new TrabajadorDto();
            dto.setId(t.getIdTrabajador());
            dto.setNombre(t.getNombre());
            dto.setNoNomina(t.getNoNomina());
            dto.setActivo(t.getActivo());
            
            if (t.getPuesto() != null) {
                dto.setIdPuesto(t.getPuesto().getIdPuesto());
            }
            return dto;
        },
        (dto) -> {
            Trabajador t = new Trabajador();
            t.setIdTrabajador(dto.getId());
            t.setNombre(dto.getNombre());
            t.setNoNomina(dto.getNoNomina());
            t.setActivo(dto.getActivo());
            
            if (dto.getIdPuesto() != null) {
                Puesto p = new Puesto();
                p.setIdPuesto(dto.getIdPuesto());
                t.setPuesto(p);
            }
            return t;
        }
    );
}