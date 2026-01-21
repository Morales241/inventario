package Utilidades;

public class SesionActual {
    private static String usuarioLogueado = "Sistema"; 

    public static String getUsuario() {
        return usuarioLogueado;
    }

    public static void setUsuario(String usuario) {
        usuarioLogueado = usuario;
    }
}