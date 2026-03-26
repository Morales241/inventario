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
public class RecursoNoEncontradoException extends NegocioException {
    private static final Logger logger = LoggerFactory.getLogger(RecursoNoEncontradoException.class);
    
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
        logger.warn("Recurso no encontrado: {}", mensaje);
    }
    
    public RecursoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        logger.warn("Recurso no encontrado: {}", mensaje, causa);
    }
}