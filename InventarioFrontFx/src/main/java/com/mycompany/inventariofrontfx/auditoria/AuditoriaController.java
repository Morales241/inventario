package com.mycompany.inventariofrontfx.auditoria;

import Dtos.AsignacionDTO;
import Dtos.CuentaSistemaDTO;
import Dtos.EquipoBaseDTO;
import Dtos.EmpresaDTO;
import Dtos.ModeloDTO;
import Dtos.PuestoDTO;
import Dtos.SucursalDTO;
import Dtos.DepartamentoDTO;
import Dtos.UsuarioDTO;
import Enums.CondicionFisica;
import Enums.EstadoEquipo;
import InterfacesFachada.IFachadaEquipos;
import InterfacesFachada.IFachadaOrganizacion;
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
import javafx.beans.binding.Bindings;
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
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Controlador del módulo de Auditoría — dos secciones
 */
public class AuditoriaController implements Initializable, BaseController {

    private MenuController dbc;

    private final IFachadaPrestamos fachadaPrestamos = FabricaFachadas.getFachadaPrestamos();
    private final IFachadaEquipos   fachadaEquipos   = FabricaFachadas.getFachadaEquipos();
    private final IFachadaPersonas  fachadaPersonas  = FabricaFachadas.getFachadaPersonas();
    private final IFachadaOrganizacion fachadaOrganizacion = FabricaFachadas.getFachadaOrganizacion();

    private static final DateTimeFormatter FMT_FECHA  = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter FMT_HORA   = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // ── Tab pane ──────────────────────────────────────────────────────────────
    @FXML private TabPane tabPane;

    @FXML private TextField                          txtBuscarEquipo;
    @FXML private Button                             btnBuscarEquipo;
    @FXML private ProgressIndicator                  progressEquipo;

    // Card de info del equipo (visible tras buscar)
    @FXML private VBox                               cardEquipo;
    @FXML private VBox                               panelResultadoEquipo;
    @FXML private Label                              lblEquipoGry;
    @FXML private Label                              lblEquipoModelo;
    @FXML private Label                              lblEquipoTipo;
    @FXML private Label                              lblEquipoEstado;
    @FXML private Label                              lblEquipoIdentificador;
    @FXML private Label                              lblEquipoFactura;
    @FXML private Label                              lblEquipoCondicion;
    @FXML private Label                              lblEquipoSucursal;
    @FXML private Label                              lblEquipoFechaCompra;
    @FXML private Label                              lblEquipoPrecio;
    @FXML private Label                              lblEquipoVersion;
    @FXML private Label                              lblEquipoId;
    @FXML private Label                              lblEquipoCreadoPor;
    @FXML private Label                              lblEquipoFechaCreacion;
    @FXML private Label                              lblEquipoModificadoPor;
    @FXML private Label                              lblEquipoFechaModificacion;
    @FXML private Label                              lblEquipoObservaciones;

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

    @FXML private TextField                          txtFiltrosCuentas;
    @FXML private ProgressIndicator                  progressCuentas;

    @FXML private TableView<CuentaSistemaDTO>                   tablaCuentas;
    @FXML private TableColumn<CuentaSistemaDTO, String>         colCUsername;
    @FXML private TableColumn<CuentaSistemaDTO, String>         colCRol;
    @FXML private TableColumn<CuentaSistemaDTO, String>         colCCreadoPor;
    @FXML private TableColumn<CuentaSistemaDTO, String>         colCFechaCreacion;
    @FXML private TableColumn<CuentaSistemaDTO, String>         colCModificadoPor;
    @FXML private TableColumn<CuentaSistemaDTO, String>         colCFechaModificacion;

    @FXML private TableView<UsuarioDTO>                   tablaUsuarios;
    @FXML private TableColumn<UsuarioDTO, String>         colUUsername;
    @FXML private TableColumn<UsuarioDTO, String>         colUNoNomina;
    @FXML private TableColumn<UsuarioDTO, String>         colUCreadoPor;
    @FXML private TableColumn<UsuarioDTO, String>         colUFechaCreacion;
    @FXML private TableColumn<UsuarioDTO, String>         colUModificadoPor;
    @FXML private TableColumn<UsuarioDTO, String>         colUFechaModificacion;

    @FXML private TableView<EquipoBaseDTO>                tablaEquipos;
    @FXML private TableColumn<EquipoBaseDTO, String>      colEGry;
    @FXML private TableColumn<EquipoBaseDTO, String>      colENombreModelo;
    @FXML private TableColumn<EquipoBaseDTO, String>      colECreadoPor;
    @FXML private TableColumn<EquipoBaseDTO, String>      colEFechaCreacion;
    @FXML private TableColumn<EquipoBaseDTO, String>      colEModificadoPor;
    @FXML private TableColumn<EquipoBaseDTO, String>      colEFechaModificacion;

    @FXML private TableView<AsignacionDTO>                tablaAsignaciones;
    @FXML private TableColumn<AsignacionDTO, String>      colAUsuario;
    @FXML private TableColumn<AsignacionDTO, String>      colAEquipo;
    @FXML private TableColumn<AsignacionDTO, String>      colACreadoPor;
    @FXML private TableColumn<AsignacionDTO, String>      colAFechaCreacion;
    @FXML private TableColumn<AsignacionDTO, String>      colAModificadoPor;
    @FXML private TableColumn<AsignacionDTO, String>      colAFechaModificacion;

    @FXML private TableView<EmpresaDTO>                   tablaEmpresas;
    @FXML private TableColumn<EmpresaDTO, String>         colEmpNombre;
    @FXML private TableColumn<EmpresaDTO, String>         colEmpCreadoPor;
    @FXML private TableColumn<EmpresaDTO, String>         colEmpFechaCreacion;
    @FXML private TableColumn<EmpresaDTO, String>         colEmpModificadoPor;
    @FXML private TableColumn<EmpresaDTO, String>         colEmpFechaModificacion;

    @FXML private TableView<SucursalDTO>                  tablaSucursales;
    @FXML private TableColumn<SucursalDTO, String>        colSucNombre;
    @FXML private TableColumn<SucursalDTO, String>        colSucEmpresa;
    @FXML private TableColumn<SucursalDTO, String>        colSucCreadoPor;
    @FXML private TableColumn<SucursalDTO, String>        colSucFechaCreacion;
    @FXML private TableColumn<SucursalDTO, String>        colSucModificadoPor;
    @FXML private TableColumn<SucursalDTO, String>        colSucFechaModificacion;

    @FXML private TableView<DepartamentoDTO>              tablaDepartamentos;
    @FXML private TableColumn<DepartamentoDTO, String>    colDepNombre;
    @FXML private TableColumn<DepartamentoDTO, String>    colDepSucursal;
    @FXML private TableColumn<DepartamentoDTO, String>    colDepCreadoPor;
    @FXML private TableColumn<DepartamentoDTO, String>    colDepFechaCreacion;
    @FXML private TableColumn<DepartamentoDTO, String>    colDepModificadoPor;
    @FXML private TableColumn<DepartamentoDTO, String>    colDepFechaModificacion;

    @FXML private TableView<PuestoDTO>                    tablaPuestos;
    @FXML private TableColumn<PuestoDTO, String>          colPueNombre;
    @FXML private TableColumn<PuestoDTO, String>          colPueDepartamento;
    @FXML private TableColumn<PuestoDTO, String>          colPueCreadoPor;
    @FXML private TableColumn<PuestoDTO, String>          colPueFechaCreacion;
    @FXML private TableColumn<PuestoDTO, String>          colPueModificadoPor;
    @FXML private TableColumn<PuestoDTO, String>          colPueFechaModificacion;

    @FXML private TableView<ModeloDTO>                    tablaModelos;
    @FXML private TableColumn<ModeloDTO, String>          colModNombre;
    @FXML private TableColumn<ModeloDTO, String>          colModMarca;
    @FXML private TableColumn<ModeloDTO, String>          colModCreadoPor;
    @FXML private TableColumn<ModeloDTO, String>          colModFechaCreacion;
    @FXML private TableColumn<ModeloDTO, String>          colModModificadoPor;
    @FXML private TableColumn<ModeloDTO, String>          colModFechaModificacion;

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
        configurarColumnasUsuarios();
        configurarColumnasEquipos();
        configurarColumnasAsignaciones();
        configurarColumnasEmpresas();
        configurarColumnasSucursales();
        configurarColumnasDepartamentos();
        configurarColumnasPuestos();
        configurarColumnasModelos();
        configurarBusquedaEquipo();
        configurarFiltroCuentas();

        configurarInteraccionTablasAuditoria();

        // Ocultar card de equipo y tabla hasta que se busque
        ocultarCardEquipo();
        ocultarSpinner(progressEquipo);
        ocultarSpinner(progressCuentas);

        // Cargar datos de auditoría al abrir
        cargarCuentasAsync("");
        cargarUsuariosAsync("");
        cargarEquiposAsync("");
        cargarAsignacionesAsync("");
        cargarEmpresasAsync("");
        cargarSucursalesAsync("");
        cargarDepartamentosAsync("");
        cargarPuestosAsync("");
        cargarModelosAsync("");
    }

    private void configurarInteraccionTablasAuditoria() {
        aplicarUXLectura(tablaHistorialEquipo, () -> {
            if (equipoActual != null) cargarHistorialEquipoAsync(equipoActual.getIdEquipo());
        });
        aplicarUXLectura(tablaCuentas, () -> cargarCuentasAsync(txtFiltrosCuentas.getText()));
        aplicarUXLectura(tablaUsuarios, () -> cargarUsuariosAsync(txtFiltrosCuentas.getText()));
        aplicarUXLectura(tablaEquipos, () -> cargarEquiposAsync(txtFiltrosCuentas.getText()));
        aplicarUXLectura(tablaAsignaciones, () -> cargarAsignacionesAsync(txtFiltrosCuentas.getText()));
        aplicarUXLectura(tablaEmpresas, () -> cargarEmpresasAsync(txtFiltrosCuentas.getText()));
        aplicarUXLectura(tablaSucursales, () -> cargarSucursalesAsync(txtFiltrosCuentas.getText()));
        aplicarUXLectura(tablaDepartamentos, () -> cargarDepartamentosAsync(txtFiltrosCuentas.getText()));
        aplicarUXLectura(tablaPuestos, () -> cargarPuestosAsync(txtFiltrosCuentas.getText()));
        aplicarUXLectura(tablaModelos, () -> cargarModelosAsync(txtFiltrosCuentas.getText()));
    }

    private <T> void aplicarUXLectura(TableView<T> tabla, Runnable accionRecargar) {
        tabla.setRowFactory(tv -> {
            TableRow<T> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem refreshItem = new MenuItem("Actualizar Datos");
            refreshItem.setGraphic(new FontIcon("fas-sync"));
            refreshItem.setOnAction(event -> accionRecargar.run());

            contextMenu.getItems().add(refreshItem);

            // Permitir refrescar independientemente de si la fila está vacía
            row.setContextMenu(contextMenu);

            return row;
        });
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
        lblEquipoGry.setText("GRY: " + (equipo.getGryFormateado() != null ? equipo.getGryFormateado() : "—"));
        lblEquipoModelo.setText(equipo.getNombreModelo() != null ? equipo.getNombreModelo() : "—");
        lblEquipoTipo.setText(equipo.getTipo() != null ? equipo.getTipo() : "—");
        lblEquipoEstado.setText(equipo.getEstado() != null ? equipo.getEstado() : "—");
        lblEquipoIdentificador.setText(equipo.getIdentificador() != null ? equipo.getIdentificador() : "—");
        lblEquipoFactura.setText(equipo.getFactura() != null ? equipo.getFactura() : "—");
        lblEquipoCondicion.setText(equipo.getCondicion() != null ? equipo.getCondicion() : "—");
        lblEquipoSucursal.setText(equipo.getNombreSucursal() != null ? equipo.getNombreSucursal() : "—");
        lblEquipoFechaCompra.setText(equipo.getFechaCompra() != null ? equipo.getFechaCompra().format(FMT_FECHA) : "—");
        lblEquipoPrecio.setText(equipo.getPrecio() != null ? String.format("$%.2f", equipo.getPrecio()) : "—");
        lblEquipoVersion.setText(equipo.getVersion() != null ? equipo.getVersion().toString() : "—");
        lblEquipoId.setText(equipo.getIdEquipo() != null ? equipo.getIdEquipo().toString() : "—");
        lblEquipoCreadoPor.setText(equipo.getCreadoPor() != null ? equipo.getCreadoPor() : "—");
        lblEquipoFechaCreacion.setText(equipo.getFechaCreacion() != null ? equipo.getFechaCreacion().format(FMT_HORA) : "—");
        lblEquipoModificadoPor.setText(equipo.getModificadoPor() != null ? equipo.getModificadoPor() : "—");
        lblEquipoFechaModificacion.setText(equipo.getFechaModificacion() != null ? equipo.getFechaModificacion().format(FMT_HORA) : "—");
        lblEquipoObservaciones.setText(equipo.getObservaciones() != null ? equipo.getObservaciones() : "—");

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
        if (panelResultadoEquipo != null) {
            panelResultadoEquipo.setVisible(true);
            panelResultadoEquipo.setManaged(true);
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

    private void configurarColumnasUsuarios() {
        colUUsername.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre()));

        colUNoNomina.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNoNomina() != null ? d.getValue().getNoNomina() : "—"));

        colUCreadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCreadoPor() != null ? d.getValue().getCreadoPor() : "—"));

        colUFechaCreacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaCreacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "—");
        });

        colUModificadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getModificadoPor() != null ? d.getValue().getModificadoPor() : "—"));

        colUFechaModificacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaModificacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "Sin cambios");
        });
    }

    private void configurarColumnasEquipos() {
        colEGry.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getGryFormateado() != null ? d.getValue().getGryFormateado() : "—"));

        colENombreModelo.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombreModelo() != null ? d.getValue().getNombreModelo() : "—"));

        colECreadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCreadoPor() != null ? d.getValue().getCreadoPor() : "—"));

        colEFechaCreacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaCreacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "—");
        });

        colEModificadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getModificadoPor() != null ? d.getValue().getModificadoPor() : "—"));

        colEFechaModificacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaModificacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "Sin cambios");
        });
    }

    private void configurarColumnasAsignaciones() {
        colAUsuario.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombreUsuario() != null ? d.getValue().getNombreUsuario() : "—"));

        colAEquipo.setCellValueFactory(d -> {
            String gry = d.getValue().getGryFormateado();
            return new SimpleStringProperty(gry != null ? gry :
                    d.getValue().getIdentificadorEquipo() != null ? d.getValue().getIdentificadorEquipo() : "—");
        });

        colACreadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCreadoPor() != null ? d.getValue().getCreadoPor() : "—"));

        colAFechaCreacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaCreacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "—");
        });

        colAModificadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getModificadoPor() != null ? d.getValue().getModificadoPor() : "—"));

        colAFechaModificacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaModificacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "Sin cambios");
        });
    }

    private void configurarColumnasEmpresas() {
        colEmpNombre.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre()));

        colEmpCreadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCreadoPor() != null ? d.getValue().getCreadoPor() : "—"));

        colEmpFechaCreacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaCreacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "—");
        });

        colEmpModificadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getModificadoPor() != null ? d.getValue().getModificadoPor() : "—"));

        colEmpFechaModificacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaModificacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "Sin cambios");
        });
    }

    private void configurarColumnasSucursales() {
        colSucNombre.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre()));

        colSucEmpresa.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombreEmpresa() != null ? d.getValue().getNombreEmpresa() : "—"));

        colSucCreadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCreadoPor() != null ? d.getValue().getCreadoPor() : "—"));

        colSucFechaCreacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaCreacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "—");
        });

        colSucModificadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getModificadoPor() != null ? d.getValue().getModificadoPor() : "—"));

        colSucFechaModificacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaModificacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "Sin cambios");
        });
    }

    private void configurarColumnasDepartamentos() {
        colDepNombre.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre()));

        colDepSucursal.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombreSucursal() != null ? d.getValue().getNombreSucursal() : "—"));

        colDepCreadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCreadoPor() != null ? d.getValue().getCreadoPor() : "—"));

        colDepFechaCreacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaCreacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "—");
        });

        colDepModificadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getModificadoPor() != null ? d.getValue().getModificadoPor() : "—"));

        colDepFechaModificacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaModificacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "Sin cambios");
        });
    }

    private void configurarColumnasPuestos() {
        colPueNombre.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre()));

        colPueDepartamento.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombreDepartamento() != null ? d.getValue().getNombreDepartamento() : "—"));

        colPueCreadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCreadoPor() != null ? d.getValue().getCreadoPor() : "—"));

        colPueFechaCreacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaCreacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "—");
        });

        colPueModificadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getModificadoPor() != null ? d.getValue().getModificadoPor() : "—"));

        colPueFechaModificacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaModificacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "Sin cambios");
        });
    }

    private void configurarColumnasModelos() {
        colModNombre.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getNombre()));

        colModMarca.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getMarca() != null ? d.getValue().getMarca() : "—"));

        colModCreadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getCreadoPor() != null ? d.getValue().getCreadoPor() : "—"));

        colModFechaCreacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaCreacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "—");
        });

        colModModificadoPor.setCellValueFactory(d ->
                new SimpleStringProperty(d.getValue().getModificadoPor() != null ? d.getValue().getModificadoPor() : "—"));

        colModFechaModificacion.setCellValueFactory(d -> {
            java.time.LocalDateTime f = d.getValue().getFechaModificacion();
            return new SimpleStringProperty(f != null ? f.format(FMT_HORA) : "Sin cambios");
        });
    }
    
    private void cargarCuentasAsync(String filtro){
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

    private void cargarUsuariosAsync(String filtro) {
        Task<List<UsuarioDTO>> task = new Task<>() {
            @Override
            protected List<UsuarioDTO> call() {
                return (filtro == null || filtro.isBlank())
                        ? fachadaPersonas.listarUsuarios()
                        : fachadaPersonas.buscarUsuarios(filtro);
            }
        };
        task.setOnSucceeded(e -> {
            tablaUsuarios.getItems().setAll(task.getValue());
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar usuarios",
                    ex != null ? ex.getMessage() : "Error desconocido");
        });
        new Thread(task).start();
    }

    private void cargarEquiposAsync(String filtro) {
        Task<List<EquipoBaseDTO>> task = new Task<>() {
            @Override
            protected List<EquipoBaseDTO> call() {
                return (filtro == null || filtro.isBlank())
                        ? fachadaEquipos.listarEquipos()
                        : fachadaEquipos.buscarConFiltros(filtro, null, null, null);
            }
        };
        task.setOnSucceeded(e -> {
            tablaEquipos.getItems().setAll(task.getValue());
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar equipos",
                    ex != null ? ex.getMessage() : "Error desconocido");
        });
        new Thread(task).start();
    }

    private void cargarAsignacionesAsync(String filtro) {
        Task<List<AsignacionDTO>> task = new Task<>() {
            @Override
            protected List<AsignacionDTO> call() {
                return (filtro == null || filtro.isBlank())
                        ? fachadaPrestamos.listarAsignaciones()
                        : fachadaPrestamos.buscarAsignaciones(filtro);
            }
        };
        task.setOnSucceeded(e -> {
            tablaAsignaciones.getItems().setAll(task.getValue());
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar asignaciones",
                    ex != null ? ex.getMessage() : "Error desconocido");
        });
        new Thread(task).start();
    }

    private void cargarEmpresasAsync(String filtro) {
        Task<List<EmpresaDTO>> task = new Task<>() {
            @Override
            protected List<EmpresaDTO> call() {
                return (filtro == null || filtro.isBlank())
                        ? fachadaOrganizacion.listarTodasEmpresas()
                        : fachadaOrganizacion.listarEmpresas(filtro);
            }
        };
        task.setOnSucceeded(e -> {
            tablaEmpresas.getItems().setAll(task.getValue());
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar empresas",
                    ex != null ? ex.getMessage() : "Error desconocido");
        });
        new Thread(task).start();
    }

    private void cargarSucursalesAsync(String filtro) {
        Task<List<SucursalDTO>> task = new Task<>() {
            @Override
            protected List<SucursalDTO> call() {
                return fachadaOrganizacion.listarSucursales(filtro, null);
            }
        };
        task.setOnSucceeded(e -> {
            tablaSucursales.getItems().setAll(task.getValue());
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar sucursales",
                    ex != null ? ex.getMessage() : "Error desconocido");
        });
        new Thread(task).start();
    }

    private void cargarDepartamentosAsync(String filtro) {
        Task<List<DepartamentoDTO>> task = new Task<>() {
            @Override
            protected List<DepartamentoDTO> call() {
                return fachadaOrganizacion.listarDepartamentos(filtro, null);
            }
        };
        task.setOnSucceeded(e -> {
            tablaDepartamentos.getItems().setAll(task.getValue());
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar departamentos",
                    ex != null ? ex.getMessage() : "Error desconocido");
        });
        new Thread(task).start();
    }

    private void cargarPuestosAsync(String filtro) {
        Task<List<PuestoDTO>> task = new Task<>() {
            @Override
            protected List<PuestoDTO> call() {
                return fachadaOrganizacion.listarTodosPuestos();
            }
        };
        task.setOnSucceeded(e -> {
            tablaPuestos.getItems().setAll(task.getValue());
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar puestos",
                    ex != null ? ex.getMessage() : "Error desconocido");
        });
        new Thread(task).start();
    }

    private void cargarModelosAsync(String filtro) {
        Task<List<ModeloDTO>> task = new Task<>() {
            @Override
            protected List<ModeloDTO> call() {
                return (filtro == null || filtro.isBlank())
                        ? fachadaEquipos.listarModelos()
                        : fachadaEquipos.buscarModelosConFiltros(filtro, null, null, null, null);
            }
        };
        task.setOnSucceeded(e -> {
            tablaModelos.getItems().setAll(task.getValue());
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al cargar modelos",
                    ex != null ? ex.getMessage() : "Error desconocido");
        });
        new Thread(task).start();
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
