
package com.mycompany.inventariofrontfx.reportes;

import java.io.ByteArrayInputStream;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
//import javafx.scene.web.WebView;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

/**
 * Controlador para el visor de PDF
 */
public class ReportesController {
    
    @FXML
    private BorderPane rootPane;
    
//    @FXML
//    private WebView webView;
    
    @FXML
    private ProgressIndicator progressIndicator;
    
    @FXML
    private Button btnCerrar;
    
    @FXML
    private Button btnImprimir;
    
    @FXML
    private Button btnExportar;
    
    @FXML
    private Button btnZoomIn;
    
    @FXML
    private Button btnZoomOut;
    
    private Stage stage;
    private JasperPrint jasperPrint;
    private byte[] pdfBytes;
    private double zoomLevel = 1.0;
    
    @FXML
    public void initialize() {
        // Configurar el WebView
//        webView.getEngine().setJavaScriptEnabled(true);
        
        // Configurar botones
        btnCerrar.setOnAction(e -> cerrar());
        btnImprimir.setOnAction(e -> imprimir());
        btnExportar.setOnAction(e -> exportar());
        btnZoomIn.setOnAction(e -> zoomIn());
        btnZoomOut.setOnAction(e -> zoomOut());
    }
    
    /**
     * Carga un JasperPrint para visualizar
     */
    public void cargarReporte(JasperPrint jasperPrint) {
        this.jasperPrint = jasperPrint;
        
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Exportar JasperPrint a PDF
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                    JRPdfExporter exporter = new JRPdfExporter();
//                    exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
//                    exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
//                    exporter.exportReport();
//                    
                    pdfBytes = baos.toByteArray();
                    
                } catch (Exception e) {
                    throw new RuntimeException("Error al procesar el PDF", e);
                }
                return null;
            }
            
            @Override
            protected void succeeded() {
                progressIndicator.setVisible(false);
                mostrarPDF(pdfBytes);
            }
            
            @Override
            protected void failed() {
                progressIndicator.setVisible(false);
                mostrarError("Error al cargar el PDF", getException().getMessage());
            }
        };
        
        progressIndicator.setVisible(true);
        new Thread(task).start();
    }
    
    /**
     * Carga un PDF desde un array de bytes
     */
    public void cargarPDF(byte[] pdfBytes) {
        this.pdfBytes = pdfBytes;
        progressIndicator.setVisible(false);
        mostrarPDF(pdfBytes);
    }
    
    /**
     * Muestra el PDF en el WebView
     */
    private void mostrarPDF(byte[] pdfBytes) {
        try {
            // Codificar el PDF a Base64
            String base64Pdf = Base64.getEncoder().encodeToString(pdfBytes);
            
            // Crear HTML con visor de PDF
            String html = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <style>\n" +
                "        body, html {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            width: 100%;\n" +
                "            height: 100%;\n" +
                "            overflow: auto;\n" +
                "        }\n" +
                "        embed {\n" +
                "            width: 100%;\n" +
                "            min-height: 100%;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <embed src=\"data:application/pdf;base64," + base64Pdf + "\" \n" +
                "           type=\"application/pdf\" width=\"100%\" height=\"100%\" />\n" +
                "</body>\n" +
                "</html>";
            
            // Cargar en el WebView
//            webView.getEngine().loadContent(html);
            
        } catch (Exception e) {
            mostrarError("Error al mostrar PDF", e.getMessage());
        }
    }
    
    @FXML
    private void imprimir() {
        try {
            // Crear diálogo de impresión
            javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(stage)) {
//                webView.getEngine().print(job);
                job.endJob();
            }
        } catch (Exception e) {
            mostrarError("Error al imprimir", e.getMessage());
        }
    }
    
    @FXML
    private void exportar() {
        if (pdfBytes == null) return;
        
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Guardar PDF");
        fileChooser.getExtensionFilters().add(
            new javafx.stage.FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );
        
        java.io.File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                java.nio.file.Files.write(file.toPath(), pdfBytes);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("PDF guardado exitosamente en:\n" + file.getAbsolutePath());
                alert.showAndWait();
                
            } catch (Exception e) {
                mostrarError("Error al guardar", e.getMessage());
            }
        }
    }
    
    @FXML
    private void zoomIn() {
        zoomLevel += 0.25;
//        webView.setZoom(zoomLevel);
    }
    
    @FXML
    private void zoomOut() {
        if (zoomLevel > 0.5) {
            zoomLevel -= 0.25;
//            webView.setZoom(zoomLevel);
        }
    }
    
    @FXML
    private void cerrar() {
        if (stage != null) {
            stage.close();
        }
    }
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    public void setStage(Stage stage) {
        this.stage = stage;
    }
}
