package Utilidades;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utilidad para manejar logging detallado de errores
 * Proporciona métodos para registrar errores con contexto completo
 */
public class LoggerUtil {
    
    /**
     * Registra un error con contexto completo
     */
    public static void registrarError(Logger logger, String operacion, String detalles, Exception ex) {
        logger.error("ERROR en operación: {} - Detalles: {}", operacion, detalles, ex);
    }
    
    /**
     * Registra un error sin excepción
     */
    public static void registrarError(Logger logger, String operacion, String detalles) {
        logger.error("ERROR en operación: {} - Detalles: {}", operacion, detalles);
    }
    
    /**
     * Registra una advertencia
     */
    public static void registrarAdvertencia(Logger logger, String operacion, String detalles) {
        logger.warn("ADVERTENCIA en operación: {} - Detalles: {}", operacion, detalles);
    }
    
    /**
     * Registra información de operación exitosa
     */
    public static void registrarExito(Logger logger, String operacion, String detalles) {
        logger.info("ÉXITO en operación: {} - Detalles: {}", operacion, detalles);
    }
    
    /**
     * Registra información general
     */
    public static void registrarInfo(Logger logger, String operacion, String detalles) {
        logger.info("INFO operación: {} - Detalles: {}", operacion, detalles);
    }
    
    /**
     * Registra información de depuración
     */
    public static void registrarDebug(Logger logger, String operacion, String detalles) {
        logger.debug("DEBUG operación: {} - Detalles: {}", operacion, detalles);
    }
}
