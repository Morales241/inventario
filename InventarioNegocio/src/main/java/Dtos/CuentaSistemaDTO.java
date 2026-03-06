package Dtos;

import Enums.RolCuenta;

/**
 * Transferencia de datos para usuarios del sistema.
 * <p>
 * Contiene información de autenticación y autorización del usuario.
 * El rol determina los permisos y acceso a diferentes funcionalidades del sistema.
 * La contraseña se maneja por separado por razones de seguridad.
 * </p>
 */
public class CuentaSistemaDTO {

    private Long id;
    private String username;
    private String password;
    private Long version;
    private String rol;

    public CuentaSistemaDTO() {
    }

    public CuentaSistemaDTO(Long id, String username, String rol) {
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

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
