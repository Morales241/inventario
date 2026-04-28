package com.mycompany.inventariofrontfx.asignaciones;

import Dtos.AsignacionDTO;
import Dtos.EquipoBaseDTO;
import Dtos.UsuarioDTO;
import InterfacesFachada.IFachadaEquipos;
import InterfacesFachada.IFachadaPersonas;
import InterfacesFachada.IFachadaPrestamos;
import com.mycompany.inventariofrontfx.menu.MenuController;
import fabricaFachadas.FabricaFachadas;
import interfaces.BaseController;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.control.TableRow;

/**
 * Controlador del módulo de Asignaciones con paginación del lado del servidor.
 */
public class AsignacionesController implements Initializable, BaseController {

    private static final int TAMANO_PAGINA = 25;

    private MenuController          dbc;
    private final IFachadaPrestamos fachadaPrestamos = FabricaFachadas.getFachadaPrestamos();
    private final IFachadaPersonas  fachadaPersonas  = FabricaFachadas.getFachadaPersonas();
    private final IFachadaEquipos   fachadaEquipos   = FabricaFachadas.getFachadaEquipos();

    // ── Tabla ──────────────────────────────────────────────────────────────────
    @FXML private TableView<AsignacionDTO>          tablaAsignaciones;
    @FXML private TableColumn<AsignacionDTO, Long>   colId;
    @FXML private TableColumn<AsignacionDTO, String> colEquipo;
    @FXML private TableColumn<AsignacionDTO, String> colUsuario;
    @FXML private TableColumn<AsignacionDTO, String> colFechaEntrega;
    @FXML private TableColumn<AsignacionDTO, String> colFechaDevolucion;
    @FXML private TableColumn<AsignacionDTO, String> colEstado;
    @FXML private TableColumn<AsignacionDTO, Void>   colAcciones;

    @FXML private TextField txtFiltro;
    @FXML private Button    btnNuevaAsignacion;

    @FXML private Button            btnAnterior;
    @FXML private Button            btnSiguiente;
    @FXML private Label             lblPagina;     
    @FXML private Label             lblTotal;       
    @FXML private ProgressIndicator progressCarga;

    private int  paginaActual   = 0;
    private long totalRegistros = 0;

    private final PauseTransition debounce = new PauseTransition(Duration.millis(350));

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        configurarAcciones();
        configurarInteraccionTabla();
        configurarBotonesPaginacion();
        ocultarSpinner();

        // Debounce: espera 350 ms sin actividad antes de lanzar la query
        debounce.setOnFinished(e -> { paginaActual = 0; cargarPagina(); });
        txtFiltro.textProperty().addListener((obs, old, val) -> debounce.playFromStart());

        cargarPagina();
    }

    private void cargarPagina() {
        mostrarSpinner();
        tablaAsignaciones.setDisable(true);

        final String filtro  = txtFiltro.getText();
        final int    pagina  = this.paginaActual;

        Task<Void> task = new Task<>() {

            List<AsignacionDTO> pagData;
            long                total;

            @Override
            protected Void call() {
                total   = fachadaPrestamos.contarAsignaciones(filtro);
                pagData = fachadaPrestamos.buscarAsignacionesPaginado(filtro, pagina, TAMANO_PAGINA);
                return null;
            }

            @Override
            protected void succeeded() {
                totalRegistros = total;
                tablaAsignaciones.getItems().setAll(pagData);
                tablaAsignaciones.setDisable(false);
                actualizarBarraPaginacion();
                ocultarSpinner();
            }

            @Override
            protected void failed() {
                tablaAsignaciones.setDisable(false);
                ocultarSpinner();
                Throwable ex = getException();
                if (ex != null) ex.printStackTrace();
                Platform.runLater(() -> mostrarError(
                        "Error al cargar asignaciones: " +
                        (ex != null ? ex.getMessage() : "Error desconocido")));
            }
        };

        new Thread(task).start();
    }

    private void configurarBotonesPaginacion() {
        if (btnAnterior  != null) btnAnterior.setOnAction(e -> irAPaginaAnterior());
        if (btnSiguiente != null) btnSiguiente.setOnAction(e -> irAPaginaSiguiente());
    }

    @FXML
    private void irAPaginaAnterior() {
        if (paginaActual > 0) { paginaActual--; cargarPagina(); }
    }

    @FXML
    private void irAPaginaSiguiente() {
        if ((long)(paginaActual + 1) * TAMANO_PAGINA < totalRegistros) {
            paginaActual++;
            cargarPagina();
        }
    }

    private void actualizarBarraPaginacion() {
        int totalPaginas = (int) Math.ceil((double) totalRegistros / TAMANO_PAGINA);
        if (totalPaginas == 0) totalPaginas = 1;
        if (lblPagina    != null) lblPagina.setText("Página " + (paginaActual + 1) + " / " + totalPaginas);
        if (lblTotal     != null) lblTotal.setText(totalRegistros + " asignaci" + (totalRegistros == 1 ? "ón" : "ones") + " activa" + (totalRegistros == 1 ? "" : "s"));
        if (btnAnterior  != null) btnAnterior.setDisable(paginaActual == 0);
        if (btnSiguiente != null) btnSiguiente.setDisable((long)(paginaActual + 1) * TAMANO_PAGINA >= totalRegistros);
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getId()));

        colEquipo.setCellValueFactory(d -> {
            AsignacionDTO a = d.getValue();
            String gry  = a.getGryFormateado() != null ? a.getGryFormateado() : "";
            String idEq = a.getIdEquipo() != null ? " (ID " + a.getIdEquipo() + ")" : "";
            return new SimpleStringProperty(gry);
        });

        colUsuario.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombreUsuario()));

        colFechaEntrega.setCellValueFactory(d -> {
            AsignacionDTO a = d.getValue();
            return new SimpleStringProperty(
                    a.getFechaEntrega() != null ? a.getFechaEntrega().format(FMT) : "");
        });

        colFechaDevolucion.setCellValueFactory(d -> {
            AsignacionDTO a = d.getValue();
            return new SimpleStringProperty(
                    a.getFechaDevolucion() != null ? a.getFechaDevolucion().format(FMT) : "—");
        });

        // Columna Estado: badge de color
        colEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) { setGraphic(null); return; }
                AsignacionDTO a = (AsignacionDTO) getTableRow().getItem();
                boolean activa = a.getFechaDevolucion() == null;
                Label badge = new Label(activa ? "Activa" : "Devuelta");
                badge.setStyle(activa
                        ? "-fx-background-color:#D1FAE5; -fx-text-fill:#065F46; " +
                          "-fx-background-radius:20; -fx-padding:3 10; -fx-font-size:11px; -fx-font-weight:bold;"
                        : "-fx-background-color:#F3F4F6; -fx-text-fill:#6B7280; " +
                          "-fx-background-radius:20; -fx-padding:3 10; -fx-font-size:11px; -fx-font-weight:bold;");
                HBox box = new HBox(badge);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            }
        });
        colEstado.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getFechaDevolucion() == null ? "Activa" : "Devuelta"));
    }

    private void configurarAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final FontIcon viewIcon   = new FontIcon("fas-file-pdf");
            private final FontIcon returnIcon = new FontIcon("fas-undo-alt");
            private final Button   btnVerResp = new Button("", viewIcon);
            private final Button   btnDev     = new Button("", returnIcon);
            private final HBox     container  = new HBox(8, btnVerResp, btnDev);

            {
                viewIcon.setIconSize(16);
                returnIcon.setIconSize(16);
                btnVerResp.getStyleClass().add("btn-ver");
                btnDev.getStyleClass().add("btn-editar");
                btnVerResp.setTooltip(new Tooltip("Ver responsiva"));
                btnDev.setTooltip(new Tooltip("Devolver equipo"));
                container.setAlignment(Pos.CENTER);

                btnVerResp.setOnAction(e -> {
                    AsignacionDTO a = getTableRow().getItem();
                    if (a != null) verResponsiva(a);
                });
                btnDev.setOnAction(e -> {
                    AsignacionDTO a = getTableRow().getItem();
                    if (a != null && a.getFechaDevolucion() == null) confirmarDevolucion(a);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) { setGraphic(null); return; }
                AsignacionDTO a = getTableRow().getItem();
                btnDev.setDisable(a.getFechaDevolucion() != null);
                setGraphic(container);
            }
        });
    }

    private void configurarInteraccionTabla() {
        tablaAsignaciones.setRowFactory(tv -> {
            TableRow<AsignacionDTO> row = new TableRow<>();

            // Menú contextual estilo Excel / Desktop
            ContextMenu contextMenu = new ContextMenu();

            MenuItem verRespItem = new MenuItem("Ver Responsiva");
            verRespItem.setGraphic(new FontIcon("fas-file-pdf"));
            verRespItem.setOnAction(event -> verResponsiva(row.getItem()));

            MenuItem devolverItem = new MenuItem("Devolver Equipo");
            devolverItem.setGraphic(new FontIcon("fas-undo-alt"));
            devolverItem.setOnAction(event -> {
                if (row.getItem().getFechaDevolucion() == null) {
                    confirmarDevolucion(row.getItem());
                }
            });

            contextMenu.getItems().addAll(verRespItem, new SeparatorMenuItem(), devolverItem);

            row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty())
                    .then((ContextMenu) null)
                    .otherwise(contextMenu)
            );

            // Doble clic para ver responsiva (la acción primaria)
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    verResponsiva(row.getItem());
                }
            });

            return row;
        });

        // Atajo teclado: ENTER para ver responsiva
        tablaAsignaciones.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                AsignacionDTO selected = tablaAsignaciones.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    verResponsiva(selected);
                }
            }
        });
    }

    private void confirmarDevolucion(AsignacionDTO asignacion) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar devolución");
        alert.setHeaderText("Devolver equipo");
        alert.setContentText("¿Confirmar devolución del equipo " +
                (asignacion.getGryFormateado() != null ? "GRY " + asignacion.getGryFormateado() : asignacion.getIdentificadorEquipo()) +
                " asignado a " + asignacion.getNombreUsuario() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                Task<Void> task = new Task<>() {
                    @Override protected Void call() {
                        fachadaPrestamos.devolverEquipo(asignacion.getId());
                        return null;
                    }
                };
                task.setOnSucceeded(e -> {
                    mostrarExito("Equipo devuelto correctamente");
                    cargarPagina();   // recarga solo la página actual
                });
                task.setOnFailed(e -> {
                    Throwable ex = task.getException();
                    mostrarError("Error al devolver: " + (ex != null ? ex.getMessage() : ""));
                });
                new Thread(task).start();
            }
        });
    }

    private void verResponsiva(AsignacionDTO asignacion) {
        try {
            UsuarioDTO    usuario = fachadaPersonas.obtenerUsuario(asignacion.getIdUsuario());
            EquipoBaseDTO equipo  = fachadaEquipos.obtenerEquipoPorId(asignacion.getIdEquipo());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ResponsivaAsignacion.fxml"));
            Parent vista = loader.load();

            ResponsivaAsignacionController controller = loader.getController();
            controller.setDashBoard(dbc);
            controller.setDatosAsignacion(usuario, equipo, asignacion.getFechaEntrega());

            dbc.getCenterContainer().setContent(vista);
            dbc.getCenterContainer().setVvalue(0);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cargar responsiva: " + e.getMessage());
        }
    }
    
    @FXML
    private void irANuevaAsignacion() {
        cambiarPantalla("FormAsignacion.fxml");
    }

    private void cambiarPantalla(String rutaFXML) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent vista = loader.load();
            Object controller = loader.getController();
            if (controller instanceof BaseController bc) bc.setDashBoard(dbc);
            dbc.getCenterContainer().setContent(vista);
            dbc.getCenterContainer().setVvalue(0);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar la pantalla");
        }
    }

    private void mostrarSpinner() {
        Platform.runLater(() -> {
            if (progressCarga != null) { progressCarga.setVisible(true); progressCarga.setManaged(true); }
        });
    }

    private void ocultarSpinner() {
        Platform.runLater(() -> {
            if (progressCarga != null) { progressCarga.setVisible(false); progressCarga.setManaged(false); }
        });
    }

    private void mostrarExito(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Éxito"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }

    private void mostrarError(String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error"); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
        });
    }

    @Override
    public void setDashBoard(MenuController dbc) { this.dbc = dbc; }
}