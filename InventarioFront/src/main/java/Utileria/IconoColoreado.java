package Utileria;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.Color;

/**
 *
 * @author tacot
 */
public class IconoColoreado {

    public IconoColoreado() {
    }
    
    public FlatSVGIcon crearIconoColoreado(String ruta, int ancho, int alto, Color colorDeseado) {
        FlatSVGIcon icono = new FlatSVGIcon(ruta, ancho, alto);
        icono.setColorFilter(new FlatSVGIcon.ColorFilter(c -> colorDeseado));
        return icono;
    }
}
