package Dtos;

import Enums.RolUsuario;

public class UsuarioDto {

    private Long id;
    private String username;
    private RolUsuario rol;

    public UsuarioDto() {
    }

    public UsuarioDto(Long id, String username, RolUsuario rol) {
        this.id = id;
        this.username = username;
        this.rol = rol;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
}
