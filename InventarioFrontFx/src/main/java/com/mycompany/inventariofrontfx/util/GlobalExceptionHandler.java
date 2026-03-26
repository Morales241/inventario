package com.mycompany.inventariofrontfx.util;

import excepciones.NegocioException;
import excepciones.RecursoNoEncontradoException;
import excepciones.ReglaNegocioException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manejador global de excepciones no capturadas
 * Registra los errores y los muestra al usuario de forma amigable
 */
public class GlobalExceptionHandler implements Thread.UncaughtExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        logger.error("EXCEPCIÓN NO CAPTURADA en thread: {}", thread.getName(), throwable);
        
        // Clasificar la excepción y mostrar mensaje apropiado
        if (throwable instanceof NegocioException) {
            manejarExcepcionNegocio((NegocioException) throwable);
        } else if (throwable instanceof RecursoNoEncontradoException) {
            manejarRecursoNoEncontrado((RecursoNoEncontradoException) throwable);
        } else if (throwable instanceof ReglaNegocioException) {
            manejarReglaNegocio((ReglaNegocioException) throwable);
        } else if (throwable instanceof IllegalArgumentException) {
            AlertService.mostrarError(
                "Parámetro inválido",
                "Se proporcionó un parámetro inválido: " + throwable.getMessage(),
                (Exception) throwable
            );
        } else if (throwable instanceof NullPointerException) {
            AlertService.mostrarError(
                "Error interno",
                "Se encontró una referencia nula. Por favor contacte al administrador.",
                (Exception) throwable
            );
        } else if (throwable instanceof ClassCastException) {
            AlertService.mostrarError(
                "Error de tipo",
                "Error al convertir tipos de datos: " + throwable.getMessage(),
                (Exception) throwable
            );
        } else {
            AlertService.mostrarError(
                "Error inesperado",
                "Ocurrió un error inesperado: " + throwable.getMessage(),
                (Exception) throwable
            );
        }
    }
    
    private void manejarExcepcionNegocio(NegocioException ex) {
        AlertService.mostrarError(
            "Error de negocio",
            ex.getMessage(),
            ex
        );
    }
    
    private void manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        AlertService.mostrarError(
            "Recurso no encontrado",
            ex.getMessage(),
            ex
        );
    }
    
    private void manejarReglaNegocio(ReglaNegocioException ex) {
        AlertService.mostrarAdvertencia(
            "Validación de negocio",
            ex.getMessage()
        );
    }
}
