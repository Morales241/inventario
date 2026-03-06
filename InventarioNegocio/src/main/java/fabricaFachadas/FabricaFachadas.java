package fabricaFachadas;

import Implementaciones.FachadaEquipos;
import Implementaciones.FachadaOrganizacion;
import Implementaciones.FachadaPersonas;
import Implementaciones.FachadaPrestamos;
import InterfacesFachada.IFachadaEquipos;
import InterfacesFachada.IFachadaOrganizacion;
import InterfacesFachada.IFachadaPersonas;
import InterfacesFachada.IFachadaPrestamos;

/**
 * Fábrica para obtener instancias de las fachadas del sistema.
 * <p>
 * Implementa el patrón Singleton para asegurar una única instancia de cada fachada.
 * Proporciona punto de acceso centralizado a todas las operaciones de negocio.
 * </p>
 * @author JMorales
 */
public final class FabricaFachadas {

    private static IFachadaEquipos fachadaEquipos;
    private static IFachadaOrganizacion fachadaOrganizacion;
    private static IFachadaPersonas fachadaPersonas;
    private static IFachadaPrestamos fachadaPrestamos;

    private FabricaFachadas() {
    }

    /**
     * Obtiene la instancia única de la fachada de equipos.
     * @return Implementación de IFachadaEquipos
     */
    public static IFachadaEquipos getFachadaEquipos() {
        if (fachadaEquipos == null) {
            fachadaEquipos = new FachadaEquipos();
        }
        return fachadaEquipos;
    }

    /**
     * Obtiene la instancia única de la fachada de organización.
     * @return Implementación de IFachadaOrganizacion
     */
    public static IFachadaOrganizacion getFachadaOrganizacion() {
        if (fachadaOrganizacion == null) {
            fachadaOrganizacion = new FachadaOrganizacion();
        }
        return fachadaOrganizacion;
    }

    /**
     * Obtiene la instancia única de la fachada de personas.
     * @return Implementación de IFachadaPersonas
     */
    public static IFachadaPersonas getFachadaPersonas() {
        if (fachadaPersonas == null) {
            fachadaPersonas = new FachadaPersonas();
        }
        return fachadaPersonas;
    }

    /**
     * Obtiene la instancia única de la fachada de préstamos.
     * @return Implementación de IFachadaPrestamos
     */
    public static IFachadaPrestamos getFachadaPrestamos() {
        if (fachadaPrestamos == null) {
            fachadaPrestamos = new FachadaPrestamos();
        }
        return fachadaPrestamos;
    }
    
    /**
     * Reinicia todas las instancias (útil para pruebas).
     */
    public static void reset() {
        fachadaEquipos = null;
        fachadaOrganizacion = null;
        fachadaPersonas = null;
        fachadaPrestamos = null;
    }
}