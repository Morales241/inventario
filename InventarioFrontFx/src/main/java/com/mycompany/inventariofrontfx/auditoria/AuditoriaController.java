package com.mycompany.inventariofrontfx.auditoria;

import Dtos.AsignacionDTO;
import Dtos.CuentaSistemaDTO;
import Dtos.EquipoBaseDTO;
import InterfacesFachada.IFachadaEquipos;
import InterfacesFachada.IFachadaPersonas;
import InterfacesFachada.IFachadaPrestamos;
import com.mycompany.inventariofrontfx.menu.MenuController;
import fabricaFachadas.FabricaFachadas;
import interfaces.BaseController;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controlador del módulo de Auditoría — dos secciones:
 *
 * ── SECCIÓN 1: HISTORIAL DE EQUIPO ──────────────────────────────────────────
 * Busca un equipo por GRY o identificador y muestra una línea de tiempo de
 * todas sus asignaciones: quién lo tuvo, cuándo lo recibió, cuándo lo devolvió.
 * Fuente de datos: obtenerHistorialEquipo(idEquipo) — ya existe en la fachada.
 *
 * ── SECCIÓN 2: HISTORIAL DE CUENTAS DEL SISTEMA ─────────────────────────────
 * Muestra todas las cuentas del sistema con sus datos de auditoría:
 * quién la creó, cuándo, quién la modificó por última vez y cuándo.
 * Fuente de datos: AuditoriaBase (creadoPor, fechaCreacion, modificadoPor,
 * fechaModificacion) ya está en CuentaSistema a través de la herencia.
 *
 * NOTA SOBRE DATOS DE AUDITORÍA:
 * AuditoriaBase graba creadoPor/modificadoPor usando SesionActual.getUsuario().
 * Esos campos se exponen a través de CuentaSistemaDTO — necesitas agregar
 * los 4 campos de auditoría al DTO (instrucciones en CuentaSistemaDTO_AMPLIADO.java).
 */
public class AuditoriaController implements Initializable, BaseController {

    private MenuController dbc;

    private final IFachadaPrestamos fachadaPrestamos = FabricaFachadas.getFachadaPrestamos();
    private final IFachadaEquipos   fachadaEquipos   = FabricaFachadas.getFachadaEquipos();
    private final IFachadaPersonas  fachadaPersonas  = FabricaFachadas.getFachadaPersonas();

    private static final DateTimeFormatter FMT_FECHA  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_HORA   = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Tab pane ──────────────────────────────────────────────────────────────
    @FXML private TabPane tabPane;

    // ══════════════════════════════════════════════════════════════════════════
    // SECCIÓN 1 — HISTORIAL DE EQUIPO
    // ══════════════════════════════════════════════════════════════════════════
    @FXML private TextField                          txtBuscarEquipo;
    @FXML private Button                             btnBuscarEquipo;
    @FXML private ProgressIndicator                  progressEquipo;

    // Card de info del equipo (visible tras buscar)
    @FXML private VBox                               cardEquipo;
    @FXML private Label                              lblEquipoGry;
    @FXML private Label                              lblEquipoModelo;
    @FXML private Label                              lblEquipoTipo;
    @FXML private Label                              lblEquipoEstado;
    @FXML private Label                              lblEquipoIdentificador;

    // Tabla de historial de asignaciones
    @FXML private TableView<AsignacionDTO>                      tablaHistorialEquipo;
    @FXML private TableColumn<AsignacionDTO, Long>              colHEId;
    @FXML private TableColumn<AsignacionDTO, String>            colHEUsuario;
    @FXML private TableColumn<AsignacionDTO, String>            colHEFechaEntrega;
    @FXML private TableColumn<AsignacionDTO, String>            colHEFechaDevolucion;
    @FXML private TableColumn<AsignacionDTO, String>            colHEDuracion;
    @FXML private TableColumn<AsignacionDTO, String>            colHEEstado;

    // Panel placeholder sección 1
    @FXML private VBox panelPlaceholderEquipo;

    // ══════════════════════════════════════════════════════════════════════════
    // SECCIÓN 2 — HISTORIAL DE CUENTAS
    // ══════════════════════════════════════════════════════════════════════════
    @FXML private TextField                          txtFiltrosCuentas;
    @FXML private ProgressIndicator                  progressCuentas;

    @FXML private TableView<CuentaSistemaDTO>                   tablaCuentas;
    @FXML private TableColumn<CuentaSistemaDTO, String>         colCUsername;
    @FXML private TableColumn<CuentaSistemaDTO, String>         colCRol;
    @FXML private TableColumn<CuentaSistemaDTO, String>         colCCreadoPor;
    @FXML private TableColumn<CuentaSistemaDTO, String>         colCFechaCreacion;
    @FXML private TableColumn<CuentaSistemaDTO, String>         colCModificadoPor;
    @FXML private TableColumn<CuentaSistemaDTO, String>         colCFechaModificacion;

    // Debounce para filtro de cuentas
    private final PauseTransition debounce = new PauseTransition(Duration.millis(350));

    // Equipo actualmente mostrado
    private EquipoBaseDTO equipoActual = null;

    // ─────────────────────────────────────────────────────────────────────────
    // Inicialización
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnaHistorialEquipo();
        configurarColumnasCuentas();
        configurarBusquedaEquipo();
        configurarFiltroCuentas();

        // Ocultar card de equipo y tabla hasta que se busque
        ocultarCardEquipo();
        ocultarSpinner(progressEquipo);
        ocultarSpinner(progressCuentas);

        // Cargar cuentas al abrir (tab 2 ya tiene datos listos)
        cargarCuentasAsync("");
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SECCIÓN 1 — HISTORIAL DE EQUIPO
    // ══════════════════════════════════════════════════════════════════════════

    private void configurarBusquedaEquipo() {
        btnBuscarEquipo.setOnAction(e -> buscarEquipo());
        txtBuscarEquipo.setOnAction(e -> buscarEquipo());
    }

    @FXML
    private void buscarEquipo() {
        String texto = txtBuscarEquipo.getText().trim();
        if (texto.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo vacío",
                    "Ingresa un GRY o identificador para buscar.");
            return;
        }

        mostrarSpinner(progressEquipo);
        ocultarCardEquipo();

        Task<EquipoBaseDTO> task = new Task<>() {
            @Override
            protected EquipoBaseDTO call() {
                // Buscar por GRY si es numérico, sino por texto (modelo/identificador)
                if (texto.matches("\\d+")) {
                    return fachadaEquipos.buscarPorGry(Integer.parseInt(texto));
                } else {
                    // Buscar por filtro de texto (nombre de modelo)
                    List<EquipoBaseDTO> resultados =
                            fachadaEquipos.buscarConFiltros(texto, null, null, null);
                    return resultados.isEmpty() ? null : resultados.get(0);
                }
            }
        };

        task.setOnSucceeded(e -> {
            ocultarSpinner(progressEquipo);
            EquipoBaseDTO equipo = task.getValue();
            if (equipo == null) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Sin resultados",
                        "No se encontró ningún equipo con ese GRY o modelo.");
                return;
            }
            equipoActual = equipo;
            mostrarCardEquipo(equipo);
            cargarHistorialEquipoAsync(equipo.getIdEquipo());
        });

        task.setOnFailed(e -> {
            ocultarSpinner(progressEquipo);
            Throwable ex = task.getException();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al buscar",
                    ex != null ? ex.getMessage() : "Error desconocido");
        });

        new Thread(task).start();
    }

    private void mostrarCardEquipo(EquipoBaseDTO equipo) {
        lblEquipoGry.setText("GRY: " + (equipo.getGry() != null ? equipo.getGry() : "—"));
        lblEquipoModelo.setText(equipo.getNombreModelo() != null ? equipo.getNombreModelo() : "—");
        lblEquipoTipo.setText(equipo.getTipo() != null ? equipo.getTipo() : "—");
        lblEquipoEstado.setText(equipo.getEstado() != null ? equipo.getEstado() : "—");
        lblEquipoIdentificador.setText(equipo.getIdentificador() != null ? equipo.getIdentificador() : "—");

        // Color del badge de estado
        String estadoColor = switch (equipo.getEstado() != null ? equipo.getEstado().toUpperCase() : "") {
            case "EN_STOCK"       -> "#D1FAE5;-fx-text-fill:#065F46";
            case "ASIGNADO"       -> "#DBEAFE;-fx-text-fill:#1E40AF";
            case "EN_ESPERA_BAJA" -> "#FEF3C7;-fx-text-fill:#92400E";
            default               -> "#F3F4F6;-fx-text-fill:#6B7280";
        };
        lblEquipoEstado.setStyle("-fx-background-color:" + estadoColor +
                ";-fx-background-radius:20;-fx-padding:2 8;-fx-font-size:11px;-fx-font-weight:bold;");

        if (panelPlaceholderEquipo != null) {
            panelPlaceholderEquipo.setVisible(false);
            panelPlaceholderEquipo.setManaged(false);
        }
        if (cardEquipo != null) {
            cardEquipo.setVisible(true);
            cardEquipo.setManaged(true);
        }
        tablaHistorialEquipo.setVisible(true);
        tablaHistorialEquipo.setManaged(true);
    }

    private void cargarHistorialEquipoAsync(Long idEquipo) {
        mostrarSpinner(progressEquipo);
        tablaHistorialEquipo.setDisable(true);

        Task<List<AsignacionDTO>> task = new Task<>() {
            @Override
            protected List<AsignacionDTO> call() {
                // obtenerHistorialEquipo devuelve TODAS las asignaciones (activas + devueltas)
                return fachadaPrestamos.obtenerHistorialEquipo(idEquipo);
            }
        };

        task.setOnSucceeded(e -> {
            tablaHistorialEquipo.getItems().setAll(task.getValue());
            tablaHistorialEquipo.setDisable(false);
            ocultarSpinner(progressEquipo);
        });

        task.setOnFailed(e -> {
            tablaHistorialEquipo.setDisable(false);
            ocultarSpinner(progressEquipo);
            Throwable ex = task.getException();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar historial",
                    ex != null ? ex.getMessage() : "Error desconocido");
        });

        new Thread(task).start();
    }

    private void configurarColumnaHistorialEquipo() {
        colHEId.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getId()));

        colHEUsuario.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombreUsuario() != null
                        ? d.getValue().getNombreUsuario() : "—"));

        colHEFechaEntrega.setCellValueFactory(d -> {
            AsignacionDTO a = d.getValue();
            return new SimpleStringProperty(
                    a.getFechaEntrega() != null ? a.getFechaEntrega().format(FMT_FECHA) : "—");
        });

        colHEFechaDevolucion.setCellValueFactory(d -> {
            AsignacionDTO a = d.getValue();
            return new SimpleStringProperty(
                    a.getFechaDevolucion() != null ? a.getFechaDevolucion().format(FMT_FECHA) : "En posesión");
        });

        // Duración: días que tuvo el equipo
        colHEDuracion.setCellValueFactory(d -> {
            AsignacionDTO a = d.getValue();
            if (a.getFechaEntrega() == null) return new SimpleStringProperty("—");
            java.time.LocalDate fin = a.getFechaDevolucion() != null
                    ? a.getFechaDevolucion()
                    : java.time.LocalDate.now();
            long dias = java.time.temporal.ChronoUnit.DAYS.between(a.getFechaEntrega(), fin);
            return new SimpleStringProperty(dias + " día" + (dias == 1 ? "" : "s"));
        });

        // Estado: badge de color
        colHEEstado.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) { setGraphic(null); return; }
                AsignacionDTO a = (AsignacionDTO) getTableRow().getItem();
                boolean activa = a.getFechaDevolucion() == null;
                Label badge = new Label(activa ? "● Activa" : "✓ Devuelta");
                badge.setStyle(activa
                        ? "-fx-background-color:#D1FAE5;-fx-text-fill:#065F46;" +
                          "-fx-background-radius:20;-fx-padding:3 10;-fx-font-size:11px;-fx-font-weight:bold;"
                        : "-fx-background-color:#F3F4F6;-fx-text-fill:#6B7280;" +
                          "-fx-background-radius:20;-fx-padding:3 10;-fx-font-size:11px;");
                HBox box = new HBox(badge);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            }
        });
        colHEEstado.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getFechaDevolucion() == null ? "Activa" : "Devuelta"));
    }

    private void ocultarCardEquipo() {
        if (cardEquipo != null) { cardEquipo.setVisible(false); cardEquipo.setManaged(false); }
        if (tablaHistorialEquipo != null) {
            tablaHistorialEquipo.setVisible(false);
            tablaHistorialEquipo.setManaged(false);
            tablaHistorialEquipo.getItems().clear();
        }
        if (panelPlaceholderEquipo != null) {
            panelPlaceholderEquipo.setVisible(true);
            panelPlaceholderEquipo.setManaged(true);
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SECCIÓN 2 — HISTORIAL DE CUENTAS DEL SISTEMA
    // ══════════════════════════════════════════════════════════════════════════

    private void configurarFiltroCuentas() {
        debounce.setOnFinished(e -> cargarCuentasAsync(txtFiltrosCuentas.getText()));
        txtFiltrosCuentas.textProperty().addListener((obs, old, val) -> debounce.playFromStart());
    }

    private void cargarCuentasAsync(String filtro) {
        mostrarSpinner(progressCuentas);
        tablaCuentas.setDisable(true);

        Task<List<CuentaSistemaDTO>> task = new Task<>() {
            @Override
            protected List<CuentaSistemaDTO> call() {
                return (filtro == null || filtro.isBlank())
                        ? fachadaPersonas.listarCuentasSistema()
                        : fachadaPersonas.buscarCuentasSistema(filtro);
            }
        };

        task.setOnSucceeded(e -> {
            tablaCuentas.getItems().setAll(task.getValue());
            tablaCuentas.setDisable(false);
            ocultarSpinner(progressCuentas);
        });

        task.setOnFailed(e -> {
            tablaCuentas.setDisable(false);
            ocultarSpinner(progressCuentas);
            Throwable ex = task.getException();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar cuentas",
                    ex != null ? ex.getMessage() : "Error desconocido");
        });

        new Thread(task).start();
    }

    private void configurarColumnasCuentas() {
        colCUsername.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getUsername()));

        // Rol con badge
        colCRol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setGraphic(null); return; }
                Label badge = new Label(item);
                badge.setStyle(badgeRolStyle(item));
                HBox box = new HBox(badge);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            }
        });
        colCRol.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getRol() != null ? d.getValue().getRol() : "—"));

        // Creado por
        colCCreadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getCreadoPor() != null ? d.getValue().getCreadoPor() : "—"));

        // Fecha de creación
        colCFechaCreacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaCreacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "—");
        });

        // Modificado por
        colCModificadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(
                        d.getValue().getModificadoPor() != null ? d.getValue().getModificadoPor() : "—"));

        // Fecha de última modificación
        colCFechaModificacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaModificacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "Sin cambios");
        });
    }

    private String badgeRolStyle(String rol) {
        if (rol == null) return "";
        return switch (rol.toUpperCase()) {
            case "ADMIN"    -> "-fx-background-color:#DBEAFE;-fx-text-fill:#1E40AF;" +
                               "-fx-background-radius:20;-fx-padding:3 10;-fx-font-size:11px;-fx-font-weight:bold;";
            case "OPERARIO" -> "-fx-background-color:#D1FAE5;-fx-text-fill:#065F46;" +
                               "-fx-background-radius:20;-fx-padding:3 10;-fx-font-size:11px;-fx-font-weight:bold;";
            case "INVITADO" -> "-fx-background-color:#F3F4F6;-fx-text-fill:#6B7280;" +
                               "-fx-background-radius:20;-fx-padding:3 10;-fx-font-size:11px;-fx-font-weight:bold;";
            default         -> "-fx-background-color:#FEF3C7;-fx-text-fill:#92400E;" +
                               "-fx-background-radius:20;-fx-padding:3 10;-fx-font-size:11px;-fx-font-weight:bold;";
        };
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private void mostrarSpinner(ProgressIndicator p) {
        Platform.runLater(() -> { if (p != null) { p.setVisible(true); p.setManaged(true); } });
    }

    private void ocultarSpinner(ProgressIndicator p) {
        Platform.runLater(() -> { if (p != null) { p.setVisible(false); p.setManaged(false); } });
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert a = new Alert(tipo);
            a.setTitle(tipo == Alert.AlertType.ERROR ? "Error" : titulo);
            a.setHeaderText(titulo);
            a.setContentText(mensaje);
            a.showAndWait();
        });
    }

    @Override
    public void mostrarError(String titulo, String mensaje) {
        mostrarAlerta(Alert.AlertType.ERROR, titulo, mensaje);
    }

    @Override
    public void setDashBoard(MenuController dbc) { this.dbc = dbc; }
}
