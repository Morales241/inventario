package com.mycompany.inventariofrontfx.asignaciones;

import Dtos.EquipoBaseDTO;
import Dtos.EquipoEscritorioDTO;
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
import javafx.geometry.Pos;
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
 * Controlador para mostrar la responsiva de asignación.
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
        configurarScrollPane();          // <-- CORRECCIÓN centrado

        progressContainer.setVisible(false);
        progressContainer.setManaged(false);
    }

    /**
     * CORRECCIÓN: configura el ScrollPane y el VBox para que el PDF siempre
     * aparezca centrado, sin importar el ancho de la ventana.
     */
    private void configurarScrollPane() {
        scrollPDF.setFitToWidth(true);

        pdfContainer.setAlignment(Pos.TOP_CENTER);

        scrollPDF.setStyle("-fx-background-color: #f0f0f0; -fx-background: #f0f0f0;");
    }

    public void setDatosAsignacion(UsuarioDTO usuario, EquipoBaseDTO equipo, LocalDate fecha) {
        this.usuario = usuario;
        this.equipo = equipo;
        this.fechaAsignacion = fecha;

        mostrarDatosEnPantalla();
        generarResponsivaAsync();
    }

    private void mostrarDatosEnPantalla() {
        lblTrabajador.setText(usuario.getNombre() + " — " + usuario.getNoNomina());
        lblEquipo.setText("GRY: " + equipo.getGry()
                + " — " + equipo.getNombreModelo()
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
                mostrarError("Error al generar responsiva",
                        getException() != null ? getException().getMessage() : "Error desconocido");
            }
        };

        progressContainer.setVisible(true);
        progressContainer.setManaged(true);
        new Thread(task).start();
    }

    /**
     * Compila el JRXML (desde el classpath), llena el reporte con los
     * parámetros y exporta el resultado a bytes PDF.
     */
    private byte[] generarResponsivaPDF() throws Exception {
        String rutaJasper = "/com/mycompany/inventariofrontfx/asignaciones/responsiva_asignacion.jasper";
        InputStream jasperStream = getClass().getResourceAsStream(rutaJasper);

        if (jasperStream == null) {
            throw new RuntimeException("No se encontró el archivo JASPER en: " + rutaJasper + " (¿Revisaste que esté en src/main/resources?)");
        }

        Map<String, Object> parametros = prepararParametros();

        JasperPrint jasperPrint = JasperFillManager.fillReport(
                jasperStream,
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

        p.put("fecha", fechaAsignacion.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy",
                new java.util.Locale("es", "MX"))));
        p.put("nombreEmpleado", safe(usuario.getNombre()));
        p.put("puesto", safe(usuario.getNombrePuesto()));
        p.put("numeroCarta", "RESP-" + equipo.getGry() + "-"
                + fechaAsignacion.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        p.put("gry", equipo.getGry());
        p.put("marca", safe(equipo.getNombreModelo()));
        p.put("tipoEquipo", safe(equipo.getTipo()));
        p.put("numeroSerie", safe(equipo.getIdentificador()));
        p.put("modelo", safe(equipo.getNombreModelo()));

        if (equipo instanceof EquipoEscritorioDTO escritorio) {
            p.put("memoriaRam", "-");
            p.put("procesador", safe(escritorio.getSisOpertativo()));
            p.put("almacenamiento", "—");
            p.put("mouse", bool(escritorio.getMouse()));
            p.put("teclado", false);
            p.put("monitor", bool(escritorio.getMochila()));
            p.put("cableCorriente", false);
            p.put("baseMonitor", false);
            p.put("sistemaOperativo", true);

            p.put("antivirus", false);
            p.put("paqueteriaOffice", false);
            p.put("lectorPDF", false);
        } else {
            p.put("memoriaRam", "—");
            p.put("procesador", "—");
            p.put("almacenamiento", "—");
            p.put("mouse", false);
            p.put("teclado", false);
            p.put("monitor", false);
            p.put("cableCorriente", true);
            p.put("Mochila", false);
            p.put("sistemaOperativo", true);
            p.put("antivirus", false);
            p.put("paqueteriaOffice", false);
            p.put("lectorPDF", false);
        }

        p.put("otrosAccesorios", "Ninguno");

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

                    double viewportWidth = scrollPDF.getViewportBounds() != null
                            ? scrollPDF.getViewportBounds().getWidth()
                            : 760;
                    double fitWidth = Math.min(viewportWidth - 40, 800);
                    imageView.setFitWidth(fitWidth > 0 ? fitWidth : 760);

                    imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 2);");

                    final int pagina = i;
                    Platform.runLater(() -> {
                        pdfContainer.getChildren().add(imageView);

                        scrollPDF.viewportBoundsProperty().addListener((obs, oldBounds, newBounds) -> {
                            if (newBounds != null && newBounds.getWidth() > 0) {
                                double w = Math.min(newBounds.getWidth() - 40, 800);
                                imageView.setFitWidth(w);
                            }
                        });
                    });
                }

                document.close();
                return null;
            }

            @Override
            protected void failed() {
                mostrarError("Error al renderizar PDF",
                        getException() != null ? getException().getMessage() : "Error desconocido");
            }
        };

        new Thread(task).start();
    }

    private void imprimirResponsiva() {
        if (pdfBytes == null) {
            mostrarError("Sin PDF", "El PDF aún no ha sido generado. Espere un momento.");
            return;
        }
        try {
            java.io.File temp = java.io.File.createTempFile("responsiva_", ".pdf");
            temp.deleteOnExit();
            java.nio.file.Files.write(temp.toPath(), pdfBytes);
            java.awt.Desktop.getDesktop().print(temp);
        } catch (Exception e) {
            mostrarError("Error al imprimir", e.getMessage());
        }
    }

    private void guardarPDF() {
        if (pdfBytes == null) {
            mostrarError("Sin PDF", "El PDF aún no ha sido generado. Espere un momento.");
            return;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Responsiva como PDF");
        fileChooser.setInitialFileName("Responsiva_GRY" + equipo.getGry() + ".pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf"));

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

    /**
     * Devuelve el String si no es null/vacío, o "—" como placeholder.
     */
    private String safe(String valor) {
        return (valor != null && !valor.isBlank()) ? valor : "—";
    }

    /**
     * Convierte un Integer a String seguro.
     */
    private String safeInt(Integer valor) {
        return valor != null ? String.valueOf(valor) : "—";
    }

    /**
     * Convierte Boolean de forma segura (null → false).
     */
    private Boolean bool(Boolean valor) {
        return valor != null && valor;
    }

    /**
     * Determina si el sistema operativo tiene un valor real.
     */
    private Boolean sistemaOperativoTieneValor(String so) {
        return so != null && !so.isBlank() && !so.equals("—");
    }

    @Override
    public void mostrarError(String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(titulo);
            alert.setContentText(mensaje != null ? mensaje : "Error desconocido");
            alert.showAndWait();
        });
    }

    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }
}
