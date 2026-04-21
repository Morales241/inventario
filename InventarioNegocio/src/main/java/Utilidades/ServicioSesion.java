package Utilidades;

import Dtos.CuentaSistemaDTO;
import Enums.RolCuenta;
import mapper.MapperCuentaSistema;

/**
 * Servicio de sesión — puente entre el DTO de la capa de presentación
 */
public class ServicioSesion {

    private static CuentaSistemaDTO usuarioLogueado;
    private static SesionActual     sesion;
    private static ServicioSesion   servicio;

    private ServicioSesion() {
        sesion         = SesionActual.getInstance();
        usuarioLogueado = MapperCuentaSistema.converter.mapToDto(sesion.getUsuario());
    }

    public static ServicioSesion getInstance() {
        if (servicio == null) servicio = new ServicioSesion();
        return servicio;
    }

    public static CuentaSistemaDTO getUsuario() {
        return usuarioLogueado;
    }

    public static void setUsuario(CuentaSistemaDTO usuario) {
        usuarioLogueado = usuario;
        // mapToEntity devuelve null si dto es null; SesionActual lo admite
        sesion.setUsuario(MapperCuentaSistema.converter.mapToEntity(usuario));
    }

    /**
     * Limpia la sesión al hacer logout.
     * Resetea a la cuenta "SISTEMA" para que AuditoriaBase nunca reciba null.
     */
    public static void cerrarSesion() {
        if (sesion == null) sesion = SesionActual.getInstance();

        sesion.setUsuario(new Entidades.CuentaSistema("SISTEMA", "", RolCuenta.ADMIN));
        usuarioLogueado = null;   
    }
}