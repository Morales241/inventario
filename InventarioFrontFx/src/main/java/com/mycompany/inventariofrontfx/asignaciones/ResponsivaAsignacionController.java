package com.mycompany.inventariofrontfx.asignaciones;

import Dtos.EquipoBaseDTO;
import Dtos.EquipoEscritorioDTO;
import Dtos.ModeloDTO;
import Dtos.MovilDTO;
import Dtos.OtroEquipoDTO;
import Dtos.UsuarioDTO;
import InterfacesFachada.IFachadaEquipos;
import com.mycompany.inventariofrontfx.menu.MenuController;
import fabricaFachadas.FabricaFachadas;
import interfaces.BaseController;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
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
 *
 * ── BUGS CORREGIDOS ──────────────────────────────────────────────────────────
 */
public class ResponsivaAsignacionController implements Initializable, BaseController {

    private MenuController dbc;

    // FIX #3: necesitamos la fachada para buscar el Modelo y el equipo tipado
    private final IFachadaEquipos fachadaEquipos = FabricaFachadas.getFachadaEquipos();

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

    // FIX #2: pdfBytes debe estar en la clase externa para que succeeded() lo llene
    private byte[] pdfBytes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarBotones();
        configurarScrollPane();
        progressContainer.setVisible(false);
        progressContainer.setManaged(false);
    }

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
        lblEquipo.setText("GRY: " + equipo.getGryFormateado()
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
        mostrarSpinner();

        Task<byte[]> task = new Task<>() {
            @Override
            protected byte[] call() throws Exception {

                // FIX #3: buscar el equipo tipado y el modelo para obtener
                // RAM, procesador, almacenamiento y marca reales
                EquipoBaseDTO equipoTipado = fachadaEquipos.buscarPorId(equipo.getIdEquipo());
                ModeloDTO modelo = null;
                if (equipo.getIdModelo() != null) {
                    modelo = fachadaEquipos.buscarModeloPorId(equipo.getIdModelo());
                }

                // Seleccionar el jasper según el tipo real del equipo
                String rutaJasper;
                if (equipoTipado instanceof EquipoEscritorioDTO) {
                    rutaJasper = "/com/mycompany/inventariofrontfx/asignaciones/Responsiva_Computo.jasper";
                } else if (equipoTipado instanceof MovilDTO) {
                    rutaJasper = "/com/mycompany/inventariofrontfx/asignaciones/Responsiva_Movil.jasper";
                } else {
                    rutaJasper = "/com/mycompany/inventariofrontfx/asignaciones/Responsiva_Otros.jasper";
                }

                InputStream jasperStream = getClass().getResourceAsStream(rutaJasper);
                if (jasperStream == null) {
                    throw new RuntimeException(
                            "No se encontró el archivo JASPER en: " + rutaJasper
                            + "\n¿Está el archivo .jasper compilado en src/main/resources?");
                }

                Map<String, Object> parametros = prepararParametros(equipoTipado, modelo);

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

            // FIX #2: succeeded() estaba AUSENTE → pdfBytes nunca se llenaba
            @Override
            protected void succeeded() {
                pdfBytes = getValue();  // guardar para imprimir/guardar después
                ocultarSpinner();
                mostrarPDF(pdfBytes);
            }

            @Override
            protected void failed() {
                ocultarSpinner();
                Throwable ex = getException();
                mostrarError("Error al generar responsiva",
                        ex != null ? ex.getMessage() : "Error desconocido");
            }
        };

        new Thread(task).start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FIX #3 + #4 + #5: construcción de parámetros con datos del Modelo
    // ─────────────────────────────────────────────────────────────────────────
    private Map<String, Object> prepararParametros(EquipoBaseDTO equipoTipado, ModeloDTO modelo) {
        Map<String, Object> p = new HashMap<>();

        // Datos comunes
        p.put("fecha", fechaAsignacion.format(
                DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "MX"))));
        p.put("nombreEmpleado", safe(usuario.getNombre()));
        p.put("puesto", safe(usuario.getNombrePuesto()));
        p.put("numeroCarta", "RESP-" + equipo.getGryFormateado() + "-"
                + fechaAsignacion.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        p.put("gry", equipo.getGryFormateado());
        p.put("tipoEquipo", safe(equipo.getTipo()));
        p.put("numeroSerie", safe(equipo.getIdentificador()));

        // FIX #3: marca y nombre de modelo vienen de ModeloDTO, no del equipo
        if (modelo != null) {
            p.put("marca", safe(modelo.getMarca()));
            p.put("modelo", safe(modelo.getMarca() + " " + modelo.getNombre()));
            p.put("memoriaRam", modelo.getMemoriaRam() != null
                    ? String.valueOf(modelo.getMemoriaRam()) : "—");
            p.put("procesador", safe(modelo.getProcesador()));
            p.put("almacenamiento", modelo.getAlmacenamiento() != null
                    ? String.valueOf(modelo.getAlmacenamiento()) : "—");
        } else {
            // Fallback si el modelo no se encontró
            p.put("marca", safe(equipo.getNombreModelo()));
            p.put("modelo", safe(equipo.getNombreModelo()));
            p.put("memoriaRam", "—");
            p.put("procesador", "—");
            p.put("almacenamiento", "—");
        }

        // FIX #4: campos específicos de escritorio (accesorios físicos del equipo)
        if (equipoTipado instanceof EquipoEscritorioDTO escritorio) {
            p.put("mouse", bool(escritorio.getMouse()));
            p.put("Mochila", bool(escritorio.getMochila()));
            // Teclado, monitor y cable de corriente no están en EquipoEscritorioDTO →
            // se ponen false por defecto (el jasper los muestra como desmarcados)
            p.put("teclado", false);
            p.put("monitor", false);
            p.put("cableCorriente", false);
            // Sistema operativo: se infiere de si tiene SO guardado
            p.put("sistemaOperativo", escritorio.getSisOpertativo() != null
                    && !escritorio.getSisOpertativo().isBlank());
            p.put("antivirus", false);
            p.put("paqueteriaOffice", false);
            p.put("lectorPDF", false);
            p.put("otrosAccesorios", "Ninguno");

            // FIX #5: campos específicos de móvil
        } else if (equipoTipado instanceof MovilDTO movil) {
            p.put("cargador", bool(movil.getCargador()));
            p.put("funda", bool(movil.getFunda()));
            // manosLibres → parámetro "audifonos" en el jasper (el más cercano disponible)
            p.put("audifonos", bool(movil.getManosLibres()));
            p.put("sistemaOperativo", true);   // los móviles siempre tienen SO
            p.put("antivirus", false);
            p.put("paqueteriaOffice", false);
            p.put("lectorPDF", false);
            p.put("otrosAccesorios",
                    (movil.getNumCelular() != null && !movil.getNumCelular().isBlank())
                    ? "Núm. celular: " + movil.getNumCelular()
                    : "Ninguno");

            // Otros equipos (impresora, monitor, escáner, proyector)
        } else if (equipoTipado instanceof OtroEquipoDTO otro) {
            // El jasper de Otros solo tiene: gry, marca, tipoEquipo, numeroSerie,
            // modelo, otrosAccesorios — los campos de accesorios no aplican
            StringBuilder extras = new StringBuilder();
            if (otro.getTituloCampoExtra() != null && !otro.getTituloCampoExtra().isBlank()) {
                extras.append(otro.getTituloCampoExtra()).append(": ")
                        .append(safe(otro.getContenidoCampoExtra()));
            }
            if (otro.getTituloCampoExtra2() != null && !otro.getTituloCampoExtra2().isBlank()) {
                if (!extras.isEmpty()) {
                    extras.append(" | ");
                }
                extras.append(otro.getTituloCampoExtra2()).append(": ")
                        .append(safe(otro.getContenidoCampoExtra2()));
            }
            p.put("otrosAccesorios", extras.isEmpty() ? "Ninguno" : extras.toString());

        } else {
            // Fallback genérico
            p.put("mouse", false);
            p.put("Mochila", false);
            p.put("teclado", false);
            p.put("monitor", false);
            p.put("cableCorriente", false);
            p.put("sistemaOperativo", false);
            p.put("antivirus", false);
            p.put("paqueteriaOffice", false);
            p.put("lectorPDF", false);
            p.put("otrosAccesorios", "Ninguno");
        }

        return p;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Mostrar PDF renderizado como imágenes
    // ─────────────────────────────────────────────────────────────────────────
    private void mostrarPDF(byte[] pdfData) {
        Task<Void> renderTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                PDDocument document = Loader.loadPDF(pdfData);
                PDFRenderer renderer = new PDFRenderer(document);

                Platform.runLater(() -> pdfContainer.getChildren().clear());

                for (int i = 0; i < document.getNumberOfPages(); i++) {
                    BufferedImage bufferedImage = renderer.renderImageWithDPI(i, 150);
                    Image fxImage = SwingFXUtils.toFXImage(bufferedImage, null);

                    ImageView imageView = new ImageView(fxImage);
                    imageView.setPreserveRatio(true);

                    double viewportWidth = scrollPDF.getViewportBounds() != null
                            ? scrollPDF.getViewportBounds().getWidth() : 760;
                    double fitWidth = Math.min(viewportWidth - 40, 800);
                    imageView.setFitWidth(fitWidth > 0 ? fitWidth : 760);
                    imageView.setStyle(
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.25), 8, 0, 0, 2);");

                    Platform.runLater(() -> {
                        pdfContainer.getChildren().add(imageView);
                        scrollPDF.viewportBoundsProperty().addListener((obs, oldB, newB) -> {
                            if (newB != null && newB.getWidth() > 0) {
                                double w = Math.min(newB.getWidth() - 40, 800);
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

        new Thread(renderTask).start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // FIX #1: métodos de botones en la clase EXTERNA, no dentro del Task
    // ─────────────────────────────────────────────────────────────────────────
    private void imprimirResponsiva() {
        if (pdfBytes == null) {
            mostrarError("Sin PDF", "El PDF aún no ha sido generado. Espere un momento.");
            return;
        }
        try {
            File temp = File.createTempFile("responsiva_", ".pdf");
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
        fileChooser.setInitialFileName("Responsiva_GRY" + equipo.getGryFormateado() + ".pdf");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo PDF (*.pdf)", "*.pdf"));

        File file = fileChooser.showSaveDialog(rootPane.getScene().getWindow());
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
            if (controller instanceof BaseController bc) {
                bc.setDashBoard(dbc);
            }
            dbc.getCenterContainer().setContent(vista);
            dbc.getCenterContainer().setVvalue(0);
        } catch (Exception e) {
            mostrarError("Error al volver", e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Spinner y helpers
    // ─────────────────────────────────────────────────────────────────────────
    private void mostrarSpinner() {
        Platform.runLater(() -> {
            progressContainer.setVisible(true);
            progressContainer.setManaged(true);
        });
    }

    private void ocultarSpinner() {
        Platform.runLater(() -> {
            progressContainer.setVisible(false);
            progressContainer.setManaged(false);
        });
    }

    private String safe(String valor) {
        return (valor != null && !valor.isBlank()) ? valor : "—";
    }

    private Boolean bool(Boolean valor) {
        return valor != null && valor;
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
