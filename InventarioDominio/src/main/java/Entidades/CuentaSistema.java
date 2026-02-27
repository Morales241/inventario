package Entidades;

import Enums.RolUsuario;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "UsuariosDelSistema")
public class CuentaSistema extends AuditoriaBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Usuario")
    private Long idUsuario;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol; 

    public CuentaSistema() {
    }

    public CuentaSistema(String username, String password, RolUsuario rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public Long getId() {
        return idUsuario;
    }

    public void setId(Long id) {
        this.idUsuario = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
 
}