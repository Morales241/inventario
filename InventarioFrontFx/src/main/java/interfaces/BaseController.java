/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfaces;

import com.mycompany.inventariofrontfx.menu.MenuController;
import com.mycompany.inventariofrontfx.util.AlertService;

/**
 * Interfaz base para todos los controladores de la aplicación
 * Proporciona métodos para mostrar notificaciones al usuario
 * 
 * @author tacot
 */
public interface BaseController {
    
    /**
     * Establece el controlador principal (MenuController) para navegación
     */
    public void setDashBoard(MenuController dbc);
    
    /**
     * Muestra un error al usuario con detalles técnicos
     */
    default void mostrarError(String titulo, String mensaje, Exception ex) {
        AlertService.mostrarError(titulo, mensaje, ex);
    }
    
    /**
     * Muestra un error al usuario
     */
    default void mostrarError(String titulo, String mensaje) {
        AlertService.mostrarError(titulo, mensaje);
    }
    
    /**
     * Muestra una advertencia al usuario
     */
    default void mostrarAdvertencia(String titulo, String mensaje) {
        AlertService.mostrarAdvertencia(titulo, mensaje);
    }
    
    /**
     * Muestra una información al usuario
     */
    default void mostrarInfo(String titulo, String mensaje) {
        AlertService.mostrarInfo(titulo, mensaje);
    }
    
    /**
     * Muestra un mensaje de éxito al usuario
     */
    default void mostrarExito(String titulo, String mensaje) {
        AlertService.mostrarExito(titulo, mensaje);
    }
}
