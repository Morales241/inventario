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
public class NegocioException extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(NegocioException.class);
    
    public NegocioException(String mensaje) {
        super(mensaje);
        logger.error("NegocioException: {}", mensaje);
    }
    
    public NegocioException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        logger.error("NegocioException: {}", mensaje, causa);
    }
}
