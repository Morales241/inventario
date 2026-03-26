package com.mycompany.inventariofrontfx.asignaciones;

import Dtos.EquipoBaseDTO;
import Dtos.UsuarioDTO;
import com.mycompany.inventariofrontfx.menu.MenuController;
import interfaces.BaseController;

import java.awt.image.BufferedImage;
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
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

import javafx.stage.FileChooser;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.export.*;
import net.sf.jasperreports.pdf.JRPdfExporter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.Loader;

/**
 * Controlador para mostrar la responsiva de asignación (VERSIÓN CORREGIDA)
 */
public class ResponsivaAsignacionController implements Initializable, BaseController {

    private MenuController dbc;

    @FXML
    private BorderPane rootPane;
    @FXML
    private StackPane progressContainer;
    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    private ScrollPane scrollPDF;
    @FXML
    private VBox pdfContainer;

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
    }

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

                mostrarPDF(pdfBytes);
            }

            @Override
            protected void failed() {
                progressContainer.setVisible(false);
                progressContainer.setManaged(false);

                mostrarError("Error al generar responsiva", getException().getMessage());
            }
        };

        progressContainer.setVisible(true);
        progressContainer.setManaged(true);

        new Thread(task).start();
    }

    private byte[] generarResponsivaPDF() throws Exception {

        String rutaJRXML = "/com/mycompany/inventariofrontfx/asignaciones/responsiva_asignacion.jrxml";
        InputStream jrxmlStream = getClass().getResourceAsStream(rutaJRXML);

        if (jrxmlStream == null) {
            throw new RuntimeException("No se encontró el JRXML");
        }

        JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);

        Map<String, Object> parametros = prepararParametros();

        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperReport,
                parametros,
                new JREmptyDataSource()
        );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        JRPdfExporter exporter = new JRPdfExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(baos));
        exporter.exportReport();

        return baos.toByteArray();
    }

    private Map<String, Object> prepararParametros() {

        Map<String, Object> p = new HashMap<>();

        p.put("fecha", fechaAsignacion.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        p.put("nombreEmpleado", usuario.getNombre());
        p.put("puesto", usuario.getNombrePuesto());
        p.put("numeroCarta", "RESP-" + System.currentTimeMillis());

        p.put("gry", equipo.getGry());
//        p.put("marca", equipo.get()); 
        p.put("tipoEquipo", equipo.getTipo());
        p.put("numeroSerie", equipo.getIdentificador());
        p.put("modelo", equipo.getNombreModelo());

//        p.put("memoriaRam", safe(equipo.get()));           // AJUSTA según tu DTO
//        p.put("procesador", safe(equipo.getProcesador()));
//        p.put("almacenamiento", safe(equipo.getAlmacenamiento()));
//
//        p.put("mouse", bool(equipo.isTieneMouse()));
//        p.put("teclado", bool(equipo.isTieneTeclado()));
//
//        p.put("otrosAccesorios", safe(equipo.getOtrosAccesorios()));
//
//        p.put("sistemaOperativo", bool(equipo.isTieneSO()));
//        p.put("antivirus", bool(equipo.isTieneAntivirus()));
//        p.put("paqueteriaOffice", bool(equipo.isTieneOffice()));
//        p.put("lectorPDF", bool(equipo.isTienePdf()));

        return p;
    }

    private void mostrarPDF(byte[] pdfBytes) {

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {

                PDDocument document = Loader.loadPDF(pdfBytes);
                PDFRenderer renderer = new PDFRenderer(document);

                Platform.runLater(() -> pdfContainer.getChildren().clear());

                for (int i = 0; i < document.getNumberOfPages(); i++) {

                    BufferedImage bufferedImage = renderer.renderImageWithDPI(i, 150);

                    Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);
                    ImageView imageView = new ImageView(fxImage);

                    imageView.setPreserveRatio(true);
                    imageView.setFitWidth(800);

                    Platform.runLater(() -> pdfContainer.getChildren().add(imageView));
                }

                document.close();
                return null;
            }

            @Override
            protected void failed() {
                mostrarError("Error al renderizar PDF", getException().getMessage());
            }
        };

        new Thread(task).start();
    }

    private void imprimirResponsiva() {
        if (pdfBytes == null) {
            mostrarError("No hay PDF", "Aún no se genera");
            return;
        }

        try {
            java.io.File temp = java.io.File.createTempFile("responsiva", ".pdf");
            java.nio.file.Files.write(temp.toPath(), pdfBytes);

            java.awt.Desktop.getDesktop().print(temp);

        } catch (Exception e) {
            mostrarError("Error al imprimir", e.getMessage());
        }
    }

    private void guardarPDF() {
        if (pdfBytes == null) {
            mostrarError("No hay PDF", "Aún no se genera");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF", "*.pdf")
        );

        java.io.File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());

        if (file != null) {
            try {
                java.nio.file.Files.write(file.toPath(), pdfBytes);
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

        } catch (Exception e) {
            mostrarError("Error al volver", e.getMessage());
        }
    }

    @Override
    public void mostrarError(String titulo, String mensaje) {
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
