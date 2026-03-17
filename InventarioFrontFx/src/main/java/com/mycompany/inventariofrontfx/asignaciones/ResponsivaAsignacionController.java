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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
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
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
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
    }

    /**
     * Recibe los datos de la asignación para generar la responsiva
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
                mostrarPDF(pdfBytes);
            }

            @Override
            protected void failed() {
                progressContainer.setVisible(false);
                mostrarError("Error al generar responsiva", getException().getMessage());
            }
        };

        progressContainer.setVisible(true);
        new Thread(task).start();
    }

    /**
     * Genera la responsiva en PDF usando JasperReports
     */
    private byte[] generarResponsivaPDF() throws Exception {
        // Opción 1: Usar un reporte .jasper precompilado
        InputStream reportStream = getClass().getResourceAsStream("/reportes/responsiva_asignacion.jasper");

        // Si no existe, usar datos en memoria (simulado)
        if (reportStream == null) {
            return generarResponsivaSimulada();
        }

        // Cargar parámetros
        Map<String, Object> parametros = new HashMap<>();
        parametros.put("NOMBRE_USUARIO", usuario.getNombre());
        parametros.put("NOMINA_USUARIO", usuario.getNoNomina());
        parametros.put("PUESTO_USUARIO", usuario.getNombrePuesto());
        parametros.put("GRY_EQUIPO", equipo.getGry());
        parametros.put("MODELO_EQUIPO", equipo.getNombreModelo());
        parametros.put("IDENTIFICADOR", equipo.getIdentificador());
        parametros.put("FECHA_ASIGNACION", fechaAsignacion);
        parametros.put("FECHA_FORMATEADA", fechaAsignacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        // Cargar reporte
        JasperReport jasperReport = (JasperReport) net.sf.jasperreports.engine.util.JRLoader.loadObject(reportStream);

        // Llenar reporte (usando datasource vacío o con datos si es necesario)
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, new JRBeanCollectionDataSource(List.of()));

        // Exportar a PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        exporter.exportReport();

        return baos.toByteArray();
    }

    /**
     * Genera una responsiva simulada en HTML (fallback si no hay reporte
     * Jasper)
     */
    private byte[] generarResponsivaSimulada() {
        String html = String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <title>Responsiva de Asignación</title>
            <style>
                body { font-family: Arial, sans-serif; margin: 40px; }
                h1 { color: #13507d; border-bottom: 2px solid #13507d; }
                .datos { margin: 20px 0; padding: 15px; background: #f5f5f5; border-radius: 5px; }
                .firma { margin-top: 50px; }
                table { width: 100%%; border-collapse: collapse; }
                td { padding: 10px; border: 1px solid #ddd; }
                .label { font-weight: bold; width: 200px; }
            </style>
        </head>
        <body>
            <h1>RESPONSIVA DE ASIGNACIÓN DE EQUIPO</h1>
            
            <div class="datos">
                <h3>Datos del Trabajador</h3>
                <table>
                    <tr><td class="label">Nombre:</td><td>%s</td></tr>
                    <tr><td class="label">Número de Nómina:</td><td>%s</td></tr>
                    <tr><td class="label">Puesto:</td><td>%s</td></tr>
                </table>
            </div>
            
            <div class="datos">
                <h3>Datos del Equipo</h3>
                <table>
                    <tr><td class="label">GRY:</td><td>%d</td></tr>
                    <tr><td class="label">Modelo:</td><td>%s</td></tr>
                    <tr><td class="label">Identificador:</td><td>%s</td></tr>
                    <tr><td class="label">Condición:</td><td>%s</td></tr>
                </table>
            </div>
            
            <div class="datos">
                <h3>Detalles de la Asignación</h3>
                <table>
                    <tr><td class="label">Fecha de Asignación:</td><td>%s</td></tr>
                    <tr><td class="label">Asignado por:</td><td>Sistema de Inventario</td></tr>
                </table>
            </div>
            
            <p>El trabajador se compromete a cuidar el equipo y devolverlo en las mismas condiciones en que lo recibe.</p>
            
            <div class="firma">
                <table style="border: none; margin-top: 50px;">
                    <tr>
                        <td style="border: none; text-align: center;">
                            ______________________________<br>
                            <strong>Firma del Trabajador</strong>
                        </td>
                        <td style="border: none; text-align: center;">
                            ______________________________<br>
                            <strong>Firma del Responsable</strong>
                        </td>
                    </tr>
                </table>
            </div>
        </body>
        </html>
        """,
                usuario.getNombre(),
                usuario.getNoNomina(),
                usuario.getNombrePuesto() != null ? usuario.getNombrePuesto() : "No asignado",
                equipo.getGry(),
                equipo.getNombreModelo(),
                equipo.getIdentificador(),
                equipo.getCondicion(),
                fechaAsignacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );

        return html.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    /**
     * Muestra el PDF en el WebView
     */
    private void mostrarPDF(byte[] pdfData) {
        try {
            String base64Pdf = java.util.Base64.getEncoder().encodeToString(pdfData);
            String html = "<!DOCTYPE html>\n"
                    + "<html>\n"
                    + "<head>\n"
                    + "    <style>\n"
                    + "        body, html { margin: 0; padding: 0; width: 100%; height: 100%; }\n"
                    + "        embed { width: 100%; height: 100%; }\n"
                    + "    </style>\n"
                    + "</head>\n"
                    + "<body>\n"
                    + "    <embed src=\"data:application/pdf;base64," + base64Pdf + "\" type=\"application/pdf\" />\n"
                    + "</body>\n"
                    + "</html>";

            webViewPDF.getEngine().loadContent(html);

        } catch (Exception e) {
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
