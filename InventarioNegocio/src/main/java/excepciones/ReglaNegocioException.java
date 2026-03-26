/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package excepciones;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author tacot
 */
public class ReglaNegocioException extends NegocioException {
    private static final Logger logger = LoggerFactory.getLogger(ReglaNegocioException.class);
    
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
        logger.warn("Regla de negocio violada: {}", mensaje);
    }
    
    public ReglaNegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        logger.warn("Regla de negocio violada: {}", mensaje, causa);
    }
}
