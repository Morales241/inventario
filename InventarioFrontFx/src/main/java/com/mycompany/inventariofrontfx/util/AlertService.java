package com.mycompany.inventariofrontfx.util;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servicio centralizado para mostrar alertas y notificaciones al usuario
 * Maneja errores, advertencias e información general
 */
public class AlertService {
    
    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);
    
    /**
     * Muestra un diálogo de error con detalles técnicos expandibles
     * @param titulo Título del error
     * @param mensaje Mensaje para el usuario
     * @param exception Excepción (puede ser null)
     */
    public static void mostrarError(String titulo, String mensaje, Exception exception) {
        logger.error("Error mostrado al usuario - {}: {}", titulo, mensaje, exception);
        
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(titulo);
            alert.setContentText(mensaje);
            
            // Si hay excepción, mostrar detalles técnicos en área expandible
            if (exception != null) {
                crearDetallesTecnicos(alert, exception);
            }
            
            alert.showAndWait();
        });
    }
    
    /**
     * Muestra un diálogo de error simple
     */
    public static void mostrarError(String titulo, String mensaje) {
        mostrarError(titulo, mensaje, null);
    }
    
    /**
     * Muestra un diálogo de advertencia
     */
    public static void mostrarAdvertencia(String titulo, String mensaje) {
        logger.warn("Advertencia mostrada al usuario - {}: {}", titulo, mensaje);
        
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(titulo);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }
    
    /**
     * Muestra un diálogo de información
     */
    public static void mostrarInfo(String titulo, String mensaje) {
        logger.info("Información mostrada al usuario - {}: {}", titulo, mensaje);
        
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Información");
            alert.setHeaderText(titulo);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }
    
    /**
     * Muestra un diálogo de éxito
     */
    public static void mostrarExito(String titulo, String mensaje) {
        logger.info("Éxito mostrado al usuario - {}: {}", titulo, mensaje);
        
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Éxito");
            alert.setHeaderText(titulo);
            alert.setContentText(mensaje);
            alert.showAndWait();
        });
    }
    
    /**
     * Crea un área expandible con detalles técnicos de la excepción
     */
    private static void crearDetallesTecnicos(Alert alert, Exception exception) {
        try {
            TextArea textArea = new TextArea();
            textArea.setText(obtenerStackTrace(exception));
            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefHeight(200);
            textArea.setPrefWidth(400);
            
            VBox expandableContent = new VBox();
            expandableContent.setMaxHeight(Double.MAX_VALUE);
            VBox.setVgrow(textArea, Priority.ALWAYS);
            expandableContent.getChildren().add(textArea);
            
            alert.getDialogPane().setExpandableContent(expandableContent);
            alert.getDialogPane().setExpanded(false);
        } catch (Exception e) {
            logger.error("Error al crear detalles técnicos del error", e);
        }
    }
    
    /**
     * Obtiene el stack trace completo de una excepción
     */
    private static String obtenerStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.getClass().getName()).append(": ")
          .append(throwable.getMessage()).append("\n\n");
        
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("  at ")
              .append(element.getClassName()).append(".")
              .append(element.getMethodName()).append("(")
              .append(element.getFileName()).append(":")
              .append(element.getLineNumber()).append(")\n");
        }
        
        // Mostrar causa raíz si la hay
        if (throwable.getCause() != null) {
            sb.append("\nCausa:\n").append(obtenerStackTrace(throwable.getCause()));
        }
        
        return sb.toString();
    }
}
