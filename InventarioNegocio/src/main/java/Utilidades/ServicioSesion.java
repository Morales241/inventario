package Utilidades;

import Dtos.CuentaSistemaDTO;
import Entidades.CuentaSistema;
import Enums.RolCuenta;
import mapper.Mapper;
import mapper.MapperCuentaSistema;
import mapper.MapperUsuario;

public class ServicioSesion {
    private static CuentaSistemaDTO usuarioLogueado;
    private static SesionActual sesion;
    private static ServicioSesion servicio;
    
    private ServicioSesion(){
        
        sesion = SesionActual.getInstance();
        usuarioLogueado = MapperCuentaSistema.converter.mapToDto(sesion.getUsuario());
        
    }
    
    public static ServicioSesion getInstance() {
    
        if (servicio == null) {
            servicio = new ServicioSesion();
        }
        
        return servicio;
    }
    
    public static CuentaSistemaDTO getUsuario() {
        return usuarioLogueado;
    }

    public static void setUsuario(CuentaSistemaDTO usuario) {
        usuarioLogueado = usuario;
        sesion.setUsuario(MapperCuentaSistema.converter.mapToEntity(usuario));
    }
}