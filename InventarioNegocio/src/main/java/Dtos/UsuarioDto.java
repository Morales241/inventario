package Dtos;

import Enums.RolUsuario;

/**
 * Transferencia de datos para usuarios del sistema.
 * <p>
 * Contiene información de autenticación y autorización del usuario.
 * El rol determina los permisos y acceso a diferentes funcionalidades del sistema.
 * La contraseña se maneja por separado por razones de seguridad.
 * </p>
 */
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
