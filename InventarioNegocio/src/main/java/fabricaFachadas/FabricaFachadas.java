/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
 *
 * @author tacot
 */
public final class FabricaFachadas {

    private static IFachadaEquipos fachadaEquipos;
    private static IFachadaOrganizacion fachadaOrganizacion;
    private static IFachadaPersonas fachadaPersonas;
    private static IFachadaPrestamos fachadaPrestamos;

    private FabricaFachadas() {
    }

    public static IFachadaEquipos getFachadaEquipos() {
        if (fachadaEquipos == null) {
            fachadaEquipos = new FachadaEquipos();
        }
        return fachadaEquipos;
    }

    public static IFachadaOrganizacion getFachadaOrganizacion() {
        if (fachadaOrganizacion == null) {
            fachadaOrganizacion = new FachadaOrganizacion();
        }
        return fachadaOrganizacion;
    }

    public static IFachadaPersonas getFachadaPersonas() {
        if (fachadaPersonas == null) {
            fachadaPersonas = new FachadaPersonas();
        }
        return fachadaPersonas;
    }

    public static IFachadaPrestamos getFachadaPrestamos() {
        if (fachadaPrestamos == null) {
            fachadaPrestamos = new FachadaPrestamos();
        }
        return fachadaPrestamos;
    }
}
