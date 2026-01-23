package Dtos;

import Enums.RolUsuario;

public class UsuarioDto {

    private Integer id;
    private String username;
    private RolUsuario rol;

    public UsuarioDto() {
    }

    public UsuarioDto(Integer id, String username, RolUsuario rol) {
        this.id = id;
        this.username = username;
        this.rol = rol;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
