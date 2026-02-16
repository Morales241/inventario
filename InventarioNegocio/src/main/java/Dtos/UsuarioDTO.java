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
public class UsuarioDTO {

    private Long id;
    private String username;
    private String rol;

    public UsuarioDTO() {
    }

    public UsuarioDTO(Long id, String username, String rol) {
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

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
