package com.mycompany.inventariofrontfx.reportes;

import Implementaciones.FachadaReportes;
import InterfacesFachada.IFachadaReportes;
import fabricaFachadas.FabricaFachadas;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.pdf.JRPdfExporter;

/**
 * Controlador para la gestión de reportes en la interfaz
 */
public class ReporteController {
    
    private static ReporteController instancia;
    private Stage stageVisor;
    private final IFachadaReportes facahdaReportes;
    private ReportesController visorController;
    private ProgressIndicator progressIndicator;
    
    private ReporteController() {
        inicializarVisor();
        facahdaReportes = FabricaFachadas.getFachadaReportes();
    }
    
    public static ReporteController getInstancia() {
        if (instancia == null) {
            instancia = new ReporteController();
        }
        return instancia;
    }
    
    private void inicializarVisor() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/fxml/ReporteViewer.fxml")
            );
            
            javafx.scene.Parent root = loader.load();
            visorController = loader.getController();
            
            Scene scene = new Scene(root);
            stageVisor = new Stage();
            stageVisor.initModality(Modality.APPLICATION_MODAL);
            stageVisor.setTitle("Visor de Reportes");
            stageVisor.setScene(scene);
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al inicializar visor", e.getMessage());
        }
    }
    
    /**
     * Muestra un reporte generado en un Task separado
     */
    private void mostrarReporteEnTask(Task<JasperPrint> task) {
        Stage progressStage = new Stage();
        progressStage.initModality(Modality.APPLICATION_MODAL);
        progressStage.setTitle("Generando reporte...");
        
        StackPane stackPane = new StackPane();
        progressIndicator = new ProgressIndicator();
        stackPane.setPrefSize(200, 150);
        stackPane.getChildren().add(progressIndicator);
        
        Scene scene = new Scene(stackPane);
        progressStage.setScene(scene);
        
        task.setOnSucceeded(event -> {
            progressStage.close();
            try {
                JasperPrint jasperPrint = task.getValue();
                byte[] pdfBytes = exportarJasperPrintAPDF(jasperPrint);
                visorController.cargarPDF(pdfBytes);
                stageVisor.showAndWait();
            } catch (Exception e) {
                mostrarError("Error al procesar reporte", e.getMessage());
            }
        });
        
        task.setOnFailed(event -> {
            progressStage.close();
            mostrarError("Error al generar reporte", task.getException().getMessage());
        });
        
        progressStage.show();
        new Thread(task).start();
    }
    
    /**
     * Exporta JasperPrint a PDF
     */
    private byte[] exportarJasperPrintAPDF(JasperPrint jasperPrint) throws JRException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        exporter.exportReport();
        return baos.toByteArray();
    }
    
    
    /**
     * Genera y muestra reporte de equipos
     */
    public void generarReporteEquipos(String titulo, String usuario) {
        Task<JasperPrint> task = new Task<>() {
            @Override
            protected JasperPrint call() throws Exception {
                return facahdaReportes.generarReporteEquipos(titulo, usuario);
            }
        };
        mostrarReporteEnTask(task);
    }
    
    /**
     * Genera y muestra reporte de equipos por sucursal
     */
    public void generarReporteEquiposPorSucursal(Long idSucursal, String nombreSucursal) {
        Task<JasperPrint> task = new Task<>() {
            @Override
            protected JasperPrint call() throws Exception {
                return facahdaReportes.generarReporteEquiposPorSucursal(idSucursal, nombreSucursal);
            }
        };
        mostrarReporteEnTask(task);
    }
    
    /**
     * Genera y muestra reporte de asignaciones por usuario
     */
    public void generarReporteAsignacionesUsuario(Long idUsuario, String nombreUsuario) {
        Task<JasperPrint> task = new Task<>() {
            @Override
            protected JasperPrint call() throws Exception {
                return facahdaReportes.generarReporteAsignacionesUsuario(idUsuario, nombreUsuario);
            }
        };
        mostrarReporteEnTask(task);
    }
    
    /**
     * Genera y muestra reporte de inventario general
     */
    public void generarReporteInventarioGeneral() {
        Task<JasperPrint> task = new Task<>() {
            @Override
            protected JasperPrint call() throws Exception {
                return facahdaReportes.generarReporteInventarioGeneral();
            }
        };
        mostrarReporteEnTask(task);
    }
    
    /**
     * Genera un reporte personalizado con parámetros
     */
    public void generarReportePersonalizado(String rutaReporte, Map<String, Object> parametros) {
        Task<JasperPrint> task = new Task<>() {
            @Override
            protected JasperPrint call() throws Exception {
                return facahdaReportes.generarReporte(rutaReporte, parametros);
            }
        };
        mostrarReporteEnTask(task);
    }
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}