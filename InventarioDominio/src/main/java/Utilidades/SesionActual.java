package Utilidades;

import Entidades.CuentaSistema;
import Enums.RolCuenta;

public class SesionActual {
    private static CuentaSistema usuarioLogueado;
    
    private static SesionActual sesionActual;

    private SesionActual(){
        usuarioLogueado = new CuentaSistema("SISTEMA", "", RolCuenta.ADMIN);
    }
    
    public static SesionActual getInstance() {
    
        if (sesionActual == null) {
            sesionActual = new SesionActual();
        }
        
        return sesionActual;
    }
    
    public static CuentaSistema getUsuario() {
        
        if (sesionActual == null) {
            sesionActual = new SesionActual();
        }
        
        return usuarioLogueado;
    }

    public static void setUsuario(CuentaSistema usuario) {
        usuarioLogueado = usuario;
    }
}