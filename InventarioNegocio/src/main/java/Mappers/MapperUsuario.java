package Mappers;

import Dtos.UsuarioDto;
import Entidades.UsuarioSistema;
import Mappers.Mapper;

public class MapperUsuario {
    
    public static final Mapper<UsuarioSistema, UsuarioDto> converter = new Mapper<>(
        (entity) -> new UsuarioDto(entity.getId(), entity.getUsername(), entity.getRol()),
        (dto) -> {
            UsuarioSistema u = new UsuarioSistema();
            u.setId(dto.getId());
            u.setUsername(dto.getUsername());
            u.setRol(dto.getRol());
            // La contraseña se debe manejar aparte por seguridad
            return u;
        }
    );
}