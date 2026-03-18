package com.mycompany.inventariofrontfx.asignaciones;

import Dtos.EquipoBaseDTO;
import Dtos.UsuarioDTO;
import com.mycompany.inventariofrontfx.menu.MenuController;
import interfaces.BaseController;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.pdf.JRPdfExporter;

/**
 * Controlador para mostrar la responsiva de asignación
 */
public class ResponsivaAsignacionController implements Initializable, BaseController {

    private MenuController dbc;

    @FXML
    private BorderPane rootPane;
    @FXML
    private WebView webViewPDF;
    @FXML
    private StackPane progressContainer;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label lblTrabajador;
    @FXML
    private Label lblEquipo;
    @FXML
    private Label lblFecha;
    @FXML
    private Button btnImprimir;
    @FXML
    private Button btnGuardarPDF;
    @FXML
    private Button btnVolver;

    private UsuarioDTO usuario;
    private EquipoBaseDTO equipo;
    private LocalDate fechaAsignacion;
    private byte[] pdfBytes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarBotones();
        
        progressContainer.setVisible(false);
        progressContainer.setManaged(false);
        
        webViewPDF.setVisible(true);
        webViewPDF.setManaged(true);
        
        Platform.runLater(() -> {
            webViewPDF.prefWidthProperty().bind(rootPane.widthProperty());
            webViewPDF.prefHeightProperty().bind(rootPane.heightProperty().subtract(150)); 
            
            if (webViewPDF.getParent() instanceof StackPane) {
                StackPane parent = (StackPane) webViewPDF.getParent();
                parent.prefWidthProperty().bind(rootPane.widthProperty());
                parent.prefHeightProperty().bind(rootPane.heightProperty().subtract(150));
            }
            
            System.out.println("WebView dimensiones iniciales: " + webViewPDF.getWidth() + " x " + webViewPDF.getHeight());
        });
        
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(1));
//        pause.setOnFinished(e -> {
//            String testHtml = "<html><body style='background: #13507d; display: flex; justify-content: center; align-items: center;'><h1 style='color: white; font-family: Arial;'>WebView FUNCIONA</h1></body></html>";
//            webViewPDF.getEngine().loadContent(testHtml);
//            System.out.println("Test HTML cargado - WebView funciona");
//        });
        pause.play();
    }

    /**
     * Recibe los datos de la asignación para generar la responsiva
     *
     * @param usuario
     * @param equipo
     * @param fecha
     */
    public void setDatosAsignacion(UsuarioDTO usuario, EquipoBaseDTO equipo, LocalDate fecha) {
        this.usuario = usuario;
        this.equipo = equipo;
        this.fechaAsignacion = fecha;

        mostrarDatosEnPantalla();
        generarResponsivaAsync();
    }

    private void mostrarDatosEnPantalla() {
        lblTrabajador.setText(usuario.getNombre() + " - " + usuario.getNoNomina());
        lblEquipo.setText("GRY: " + equipo.getGry() + " - " + equipo.getNombreModelo()
                + " (" + equipo.getIdentificador() + ")");
        lblFecha.setText(fechaAsignacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
    }

    private void configurarBotones() {
        btnImprimir.setOnAction(e -> imprimirResponsiva());
        btnGuardarPDF.setOnAction(e -> guardarPDF());
        btnVolver.setOnAction(e -> volverAListaAsignaciones());
    }

    private void generarResponsivaAsync() {
        Task<byte[]> task = new Task<>() {
            @Override
            protected byte[] call() throws Exception {
                return generarResponsivaPDF();
            }

            @Override
            protected void succeeded() {
                pdfBytes = getValue();
                progressContainer.setVisible(false);
                progressContainer.setManaged(false); 
                webViewPDF.setVisible(true);
                webViewPDF.setManaged(true);
                mostrarPDF(pdfBytes);
            }

            @Override
            protected void failed() {
                progressContainer.setVisible(false);
                progressContainer.setManaged(false);
                webViewPDF.setVisible(true);
                webViewPDF.setManaged(true);
                mostrarError("Error al generar responsiva", getException().getMessage());
            }
        };

        progressContainer.setVisible(true);
        progressContainer.setManaged(true); 
        webViewPDF.setVisible(false);
        new Thread(task).start();
    }

    /**
     * Genera la responsiva en PDF usando JasperReports desde archivo JRXML
     */
    private byte[] generarResponsivaPDF() throws Exception {
        System.out.println("=== INICIANDO GENERACIÓN DE RESPONSIVA ===");

        String rutaJRXML = "/com/mycompany/inventariofrontfx/asignaciones/responsiva_asignacion.jrxml";
        System.out.println("Buscando JRXML en: " + rutaJRXML);

        InputStream jrxmlStream = getClass().getResourceAsStream(rutaJRXML);

        if (jrxmlStream == null) {
            System.out.println("¡ERROR! No se encontró el archivo JRXML");
            System.out.println("Rutas alternativas:");
            System.out.println("  - " + getClass().getResource("/reportes/responsiva_asignacion.jrxml"));
            System.out.println("  - " + getClass().getResource("/responsiva_asignacion.jrxml"));
            System.out.println("  - " + getClass().getResource("responsiva_asignacion.jrxml"));

            // Mostrar error en la interfaz
            Platform.runLater(() -> {
                mostrarError("Error de configuración",
                        "No se encontró el archivo de la responsiva.\n"
                        + "Verifique que el archivo .jrxml esté en:\n"
                        + "src/main/resources/com/mycompany/inventariofrontfx/asignaciones/");
            });

            return generarResponsivaSimulada();
        }

        System.out.println("✓ Archivo JRXML encontrado");

        try {
            System.out.println("Compilando JRXML...");
            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            System.out.println("✓ JRXML compilado correctamente");

            System.out.println("Preparando parámetros...");
            Map<String, Object> parametros = prepararParametros();
            System.out.println("Parámetros preparados: " + parametros.keySet());

            System.out.println("Llenando reporte...");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, new JREmptyDataSource());
            System.out.println("✓ Reporte llenado correctamente");
            System.out.println("Páginas generadas: " + jasperPrint.getPages().size());

            System.out.println("Exportando a PDF...");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            JRPdfExporter exporter = new JRPdfExporter();
            exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
            exporter.exportReport();

            byte[] pdfBytes = baos.toByteArray();
            System.out.println("✓ PDF generado, tamaño: " + pdfBytes.length + " bytes");

            return pdfBytes;

        } catch (Exception e) {
            System.err.println("¡ERROR en generación de PDF!");
            e.printStackTrace();

            // Mostrar error en la interfaz
            Platform.runLater(() -> {
                mostrarError("Error al generar PDF",
                        e.getMessage() + "\n\nUsando versión HTML de respaldo");
            });

            return generarResponsivaSimulada();
        }
    }

    /**
     * Prepara los parámetros para el reporte según tu JRXML
     */
    private Map<String, Object> prepararParametros() {
        Map<String, Object> parametros = new HashMap<>();

        parametros.put("fecha", fechaAsignacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        parametros.put("nombreEmpleado", usuario.getNombre());
        parametros.put("puesto", usuario.getNombrePuesto() != null ? usuario.getNombrePuesto() : "No asignado");
        parametros.put("gry", equipo.getGry());
        parametros.put("numeroCarta", "RESP-" + System.currentTimeMillis()); // Número único para cada responsiva

        parametros.put("marca", obtenerMarcaDelModelo(equipo.getNombreModelo()));
        parametros.put("tipoEquipo", equipo.getTipo() != null ? equipo.getTipo() : "No especificado");
        parametros.put("numeroSerie", equipo.getIdentificador() != null ? equipo.getIdentificador() : "N/A");
        parametros.put("modelo", equipo.getNombreModelo() != null ? equipo.getNombreModelo() : "N/A");

        parametros.put("memoriaRam", "16");
        parametros.put("procesador", "Intel Core i7");
        parametros.put("almacenamiento", "512");

        parametros.put("mouse", true);
        parametros.put("teclado", true);
        parametros.put("mochila", false);
        parametros.put("Adaptador", false);
        parametros.put("baseMonitor", false);
        parametros.put("otrosAccesorios", "Ninguno");

        parametros.put("sistemaOperativo", true);
        parametros.put("antivirus", true);
        parametros.put("paqueteriaOffice", true);
        parametros.put("lectorPDF", true);

        return parametros;
    }

    /**
     * Extrae la marca del nombre del modelo (primer palabra antes del espacio)
     */
    private String obtenerMarcaDelModelo(String nombreModelo) {
        if (nombreModelo == null || nombreModelo.isBlank()) {
            return "No especificada";
        }

        String[] partes = nombreModelo.split(" ");
        return partes.length > 0 ? partes[0] : nombreModelo;
    }

    /**
     * Exporta JasperPrint a PDF
     */
    private byte[] exportarAPDF(JasperPrint jasperPrint) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        exporter.exportReport();
        return baos.toByteArray();
    }

    /**
     * Genera una responsiva simulada en HTML (fallback si no hay reporte jasper)
     */
    private byte[] generarResponsivaSimulada() {
        String fechaFormateada = fechaAsignacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String nombrePuesto = usuario.getNombrePuesto() != null ? usuario.getNombrePuesto() : "No asignado";

        String html = "<!DOCTYPE html>\n"
                + "<html>\n"
                + "<head>\n"
                + "    <meta charset=\"UTF-8\">\n"
                + "    <title>Responsiva de Asignación</title>\n"
                + "    <style>\n"
                + "        body { font-family: Arial, sans-serif; margin: 40px; }\n"
                + "        h1 { color: #13507d; border-bottom: 2px solid #13507d; }\n"
                + "        .datos { margin: 20px 0; padding: 15px; background: #f5f5f5; border-radius: 5px; }\n"
                + "        .firma { margin-top: 50px; }\n"
                + "        table { width: 100%; border-collapse: collapse; }\n"
                + "        td { padding: 10px; border: 1px solid #ddd; }\n"
                + "        .label { font-weight: bold; width: 200px; }\n"
                + "    </style>\n"
                + "</head>\n"
                + "<body>\n"
                + "    <h1>RESPONSIVA DE ASIGNACIÓN DE EQUIPO</h1>\n"
                + "    \n"
                + "    <div class=\"datos\">\n"
                + "        <h3>Datos del Trabajador</h3>\n"
                + "        <table>\n"
                + "            <tr><td class=\"label\">Nombre:</td><td>" + usuario.getNombre() + "</td></tr>\n"
                + "            <tr><td class=\"label\">Número de Nómina:</td><td>" + usuario.getNoNomina() + "</td></tr>\n"
                + "            <tr><td class=\"label\">Puesto:</td><td>" + nombrePuesto + "</td></tr>\n"
                + "        </table>\n"
                + "    </div>\n"
                + "    \n"
                + "    <div class=\"datos\">\n"
                + "        <h3>Datos del Equipo</h3>\n"
                + "        <table>\n"
                + "            <tr><td class=\"label\">GRY:</td><td>" + equipo.getGry() + "</td></tr>\n"
                + "            <tr><td class=\"label\">Modelo:</td><td>" + equipo.getNombreModelo() + "</td></tr>\n"
                + "            <tr><td class=\"label\">Identificador:</td><td>" + equipo.getIdentificador() + "</td></tr>\n"
                + "            <tr><td class=\"label\">Condición:</td><td>" + equipo.getCondicion() + "</td></tr>\n"
                + "        </table>\n"
                + "    </div>\n"
                + "    \n"
                + "    <div class=\"datos\">\n"
                + "        <h3>Detalles de la Asignación</h3>\n"
                + "        <table>\n"
                + "            <tr><td class=\"label\">Fecha de Asignación:</td><td>" + fechaFormateada + "</td></tr>\n"
                + "            <tr><td class=\"label\">Asignado por:</td><td>Sistema de Inventario</td></tr>\n"
                + "        </table>\n"
                + "    </div>\n"
                + "    \n"
                + "    <p>El trabajador se compromete a cuidar el equipo y devolverlo en las mismas condiciones en que lo recibe.</p>\n"
                + "    \n"
                + "    <div class=\"firma\">\n"
                + "        <table style=\"border: none; margin-top: 50px;\">\n"
                + "            <tr>\n"
                + "                <td style=\"border: none; text-align: center;\">\n"
                + "                    ______________________________<br>\n"
                + "                    <strong>Firma del Trabajador</strong>\n"
                + "                </td>\n"
                + "                <td style=\"border: none; text-align: center;\">\n"
                + "                    ______________________________<br>\n"
                + "                    <strong>Firma del Responsable</strong>\n"
                + "                </td>\n"
                + "            </tr>\n"
                + "        </table>\n"
                + "    </div>\n"
                + "</body>\n"
                + "</html>";

        System.out.println("Generando HTML de respaldo, tamaño: " + html.length() + " caracteres");
        return html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Muestra el PDF en el WebView
     */
    private void mostrarPDF(byte[] pdfData) {
        try {
            System.out.println("Mostrando PDF, tamaño: " + pdfData.length + " bytes");

            webViewPDF.setVisible(true);
            webViewPDF.setManaged(true);

            boolean esPDF = pdfData.length > 4
                    && pdfData[0] == 0x25 && pdfData[1] == 0x50
                    && pdfData[2] == 0x44 && pdfData[3] == 0x46; 

            String contenido;

            if (esPDF) {
                System.out.println("Es un PDF válido");
                String base64Pdf = java.util.Base64.getEncoder().encodeToString(pdfData);
                contenido = "<!DOCTYPE html>\n"
                        + "<html>\n"
                        + "<head>\n"
                        + "    <style>\n"
                        + "        body, html { margin: 0; padding: 0; width: 100%; height: 100%; overflow: auto; }\n"
                        + "        embed { width: 100%; height: 100%; }\n"
                        + "    </style>\n"
                        + "</head>\n"
                        + "<body>\n"
                        + "    <embed src=\"data:application/pdf;base64," + base64Pdf + "\" type=\"application/pdf\" />\n"
                        + "</body>\n"
                        + "</html>";
            } else {
                System.out.println("Es HTML (fallback)");
                contenido = new String(pdfData, java.nio.charset.StandardCharsets.UTF_8);
            }

            Platform.runLater(() -> {
                webViewPDF.getEngine().loadContent(contenido);

                webViewPDF.requestLayout();
                webViewPDF.getEngine().reload(); 

                System.out.println("Contenido cargado en WebView");
            });

        } catch (Exception e) {
            System.err.println("Error al mostrar PDF: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al mostrar PDF", e.getMessage());
        }
    }

    private void imprimirResponsiva() {
        if (pdfBytes == null) {
            mostrarError("No hay PDF para imprimir", "La responsiva aún no está lista");
            return;
        }

        javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
        if (job != null && job.showPrintDialog(rootPane.getScene().getWindow())) {
            webViewPDF.getEngine().print(job);
            job.endJob();
        }
    }

    private void guardarPDF() {
        if (pdfBytes == null) {
            mostrarError("No hay PDF para guardar", "La responsiva aún no está lista");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Responsiva");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivos PDF", "*.pdf")
        );
        fileChooser.setInitialFileName("responsiva_" + usuario.getNoNomina() + "_"
                + equipo.getGry() + ".pdf");

        java.io.File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
        if (file != null) {
            try {
                java.nio.file.Files.write(file.toPath(), pdfBytes);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("Responsiva guardada exitosamente");
                alert.showAndWait();

            } catch (IOException e) {
                mostrarError("Error al guardar", e.getMessage());
            }
        }
    }

    private void volverAListaAsignaciones() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Asignaciones.fxml"));
            Parent vista = loader.load();

            Object controller = loader.getController();
            if (controller instanceof BaseController baseController) {
                baseController.setDashBoard(dbc);
            }

            dbc.getCenterContainer().setContent(vista);
            dbc.getCenterContainer().setVvalue(0);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al volver", e.getMessage());
        }
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }
}