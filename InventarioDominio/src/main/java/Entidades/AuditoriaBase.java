package Entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import Utilidades.SesionActual;

@MappedSuperclass
public abstract class AuditoriaBase {

    @Column(name = "FechaCreacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "CreadoPor", nullable = false, updatable = false)
    private String creadoPor;

    @Column(name = "FechaModificacion")
    private LocalDateTime fechaModificacion;

    @Column(name = "ModificadoPor")
    private String modificadoPor;

    @PrePersist
    public void alCrear() {
        this.fechaCreacion = LocalDateTime.now();
        this.creadoPor = SesionActual.getUsuario().getUsername();
    }

    @PreUpdate
    public void alModificar() {
        this.fechaModificacion = LocalDateTime.now();
        this.modificadoPor = SesionActual.getUsuario().getUsername();
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getCreadoPor() {
        return creadoPor;
    }

    public void setCreadoPor(String creadoPor) {
        this.creadoPor = creadoPor;
    }

    public LocalDateTime getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(LocalDateTime fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public String getModificadoPor() {
        return modificadoPor;
    }

    public void setModificadoPor(String modificadoPor) {
        this.modificadoPor = modificadoPor;
    }
}
