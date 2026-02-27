package Entidades;

import Enums.RolCuenta;
import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "CuentasDelSistema")
public class CuentaSistema extends AuditoriaBase implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id_Cuenta")
    private Long idCuenta;
    
    @Version
    private Long version;
    
    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password; 

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolCuenta rol; 

    public CuentaSistema() {
    }

    public CuentaSistema(String username, String password, RolCuenta rol) {
        this.username = username;
        this.password = password;
        this.rol = rol;
    }

    public Long getId() {
        return idCuenta;
    }

    public void setId(Long id) {
        this.idCuenta = id;
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

    public RolCuenta getRol() {
        return rol;
    }

    public void setRol(RolCuenta rol) {
        this.rol = rol;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(Long idCuenta) {
        this.idCuenta = idCuenta;
    }
}