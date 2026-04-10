package Dtos;

import java.time.LocalDateTime;

/**
 * CuentaSistemaDTO ampliado con campos de auditoría.
 */
public class CuentaSistemaDTO {

    private Long          id;
    private String        username;
    private String        password;
    private Long          version;
    private String        rol;

    private String        creadoPor;
    private LocalDateTime fechaCreacion;
    private String        modificadoPor;
    private LocalDateTime fechaModificacion;

    public CuentaSistemaDTO() {}

    public CuentaSistemaDTO(Long id, String username, String rol) {
        this.id = id;
        this.username = username;
        this.rol = rol;
    }


    public Long getId()                   { return id; }
    public void setId(Long id)            { this.id = id; }

    public String getUsername()           { return username; }
    public void setUsername(String u)     { this.username = u; }

    public String getRol()                { return rol; }
    public void setRol(String rol)        { this.rol = rol; }

    public Long getVersion()              { return version; }
    public void setVersion(Long v)        { this.version = v; }

    public String getPassword()           { return password; }
    public void setPassword(String p)     { this.password = p; }

    // ── Getters / Setters de auditoría (nuevos) ───────────────────────────────

    public String getCreadoPor()                        { return creadoPor; }
    public void   setCreadoPor(String creadoPor)        { this.creadoPor = creadoPor; }

    public LocalDateTime getFechaCreacion()             { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime f)       { this.fechaCreacion = f; }

    public String getModificadoPor()                    { return modificadoPor; }
    public void   setModificadoPor(String modificadoPor){ this.modificadoPor = modificadoPor; }

    public LocalDateTime getFechaModificacion()         { return fechaModificacion; }
    public void setFechaModificacion(LocalDateTime f)   { this.fechaModificacion = f; }
}
