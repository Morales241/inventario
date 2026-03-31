package com.mycompany.inventariofrontfx.inventario;

import Dtos.EquipoBaseDTO;
import Enums.CondicionFisica;
import Enums.EstadoEquipo;
import Enums.TipoEquipo;
import InterfacesFachada.IFachadaEquipos;
import com.mycompany.inventariofrontfx.menu.MenuController;
import fabricaFachadas.FabricaFachadas;
import interfaces.ControllerInventario;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Controlador del módulo de Inventario con paginación del lado del servidor.
 */
public class InventarioController implements Initializable, ControllerInventario {

    private static final int TAMANO_PAGINA = 25;

    private MenuController       dbc;
    private final IFachadaEquipos fachadaEquipos = FabricaFachadas.getFachadaEquipos();

    @FXML private TableView<EquipoBaseDTO>          tablaEquipos;
    @FXML private TableColumn<EquipoBaseDTO, Long>   colId;
    @FXML private TableColumn<EquipoBaseDTO, Integer>colGry;
    @FXML private TableColumn<EquipoBaseDTO, String> colTipo;
    @FXML private TableColumn<EquipoBaseDTO, String> colFecha;
    @FXML private TableColumn<EquipoBaseDTO, String> colModelo;
    @FXML private TableColumn<EquipoBaseDTO, String> colCondicion;
    @FXML private TableColumn<EquipoBaseDTO, String> colEstado;
    @FXML private TableColumn<EquipoBaseDTO, Void>   colAcciones;

    @FXML private Button                 btnAgregar;
    @FXML private TextField              txtFiltro;
    @FXML private ComboBox<TipoEquipo>   cbxTipo;
    @FXML private ComboBox<CondicionFisica> cbxCondicion;
    @FXML private ComboBox<EstadoEquipo> cbxEstado;

    @FXML private Button            btnAnterior;
    @FXML private Button            btnSiguiente;
    @FXML private Label             lblPagina;      
    @FXML private Label             lblTotal;       
    @FXML private ProgressIndicator progressCarga;

    private int     paginaActual   = 0;
    private long    totalRegistros = 0;

    /**
     * Flag para evitar que llenarComboBox() dispare el filtro mientras
     * los combos se inicializan en initialize().
     */
    private boolean inicializando = false;

    /** Debounce: espera 350 ms tras la última tecla antes de hacer la query. */
    private final PauseTransition debounce = new PauseTransition(Duration.millis(350));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        configurarAcciones();
        configurarBotonesPaginacion();
        ocultarSpinner();

        inicializando = true;
        llenarComboBox();
        inicializando = false;

        configurarFiltros();

        cargarPagina();
    }

    private void cargarPagina() {
        mostrarSpinner();
        tablaEquipos.setDisable(true);

        final String         texto     = txtFiltro.getText();
        final TipoEquipo     tipo      = cbxTipo.getValue();
        final CondicionFisica condicion = cbxCondicion.getValue();
        final EstadoEquipo   estado    = cbxEstado.getValue();
        final int            pagina    = this.paginaActual;

        Task<Void> task = new Task<>() {

            List<EquipoBaseDTO> pagData;
            long                total;

            @Override
            protected Void call() {
                total   = fachadaEquipos.contarEquipos(texto, tipo, condicion, estado);
                pagData = fachadaEquipos.buscarConFiltrosPaginado(
                                texto, tipo, condicion, estado, pagina, TAMANO_PAGINA);
                return null;
            }

            @Override
            protected void succeeded() {
                totalRegistros = total;
                tablaEquipos.getItems().setAll(pagData);
                tablaEquipos.setDisable(false);
                actualizarBarraPaginacion();
                ocultarSpinner();
            }

            @Override
            protected void failed() {
                tablaEquipos.setDisable(false);
                ocultarSpinner();
                Throwable ex = getException();
                if (ex != null) ex.printStackTrace();
                Platform.runLater(() -> mostrarAlertError(
                        "No se pudieron cargar los equipos",
                        ex != null ? ex.getMessage() : "Error desconocido"));
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

        if (lblPagina  != null) lblPagina.setText("Página " + (paginaActual + 1) + " / " + totalPaginas);
        if (lblTotal   != null) lblTotal.setText(totalRegistros + (totalRegistros == 1 ? " equipo" : " equipos"));
        if (btnAnterior  != null) btnAnterior.setDisable(paginaActual == 0);
        if (btnSiguiente != null) btnSiguiente.setDisable((long)(paginaActual + 1) * TAMANO_PAGINA >= totalRegistros);
    }

    private void llenarComboBox() {
        cbxTipo.getItems().setAll(TipoEquipo.values());
        cbxCondicion.getItems().setAll(CondicionFisica.values());
        cbxEstado.getItems().setAll(EstadoEquipo.values());
    }

    private void configurarFiltros() {
        // Campo texto: debounce 350 ms
        debounce.setOnFinished(e -> { paginaActual = 0; cargarPagina(); });
        txtFiltro.textProperty().addListener((obs, old, val) -> debounce.playFromStart());

        // Combos: reactivo inmediato, pero ignorar si aún estamos inicializando
        cbxTipo.valueProperty().addListener((obs, old, val) -> {
            if (!inicializando) { paginaActual = 0; cargarPagina(); }
        });
        cbxCondicion.valueProperty().addListener((obs, old, val) -> {
            if (!inicializando) { paginaActual = 0; cargarPagina(); }
        });
        cbxEstado.valueProperty().addListener((obs, old, val) -> {
            if (!inicializando) { paginaActual = 0; cargarPagina(); }
        });
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(d      -> new SimpleObjectProperty<>(d.getValue().getIdEquipo()));
        colGry.setCellValueFactory(d     -> new SimpleObjectProperty<>(d.getValue().getGry()));
        colTipo.setCellValueFactory(d    -> new SimpleStringProperty(d.getValue().getTipo()));
        colModelo.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().getNombreModelo()));
        colFecha.setCellValueFactory(d   -> new SimpleStringProperty(
                d.getValue().getFechaCompra() != null ? d.getValue().getFechaCompra().toString() : ""));
        colCondicion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCondicion()));
        colEstado.setCellValueFactory(d  -> new SimpleStringProperty(d.getValue().getEstado()));
    }

    private void configurarAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final FontIcon viewIcon   = new FontIcon("fas-eye");
            private final FontIcon editIcon   = new FontIcon("fas-edit");
            private final FontIcon deleteIcon = new FontIcon("fas-trash");

            private final Button btnVer      = new Button("", viewIcon);
            private final Button btnEditar   = new Button("", editIcon);
            private final Button btnEliminar = new Button("", deleteIcon);
            private final HBox   container   = new HBox(8, btnVer, btnEditar, btnEliminar);

            {
                viewIcon.setIconSize(16);
                editIcon.setIconSize(16);
                deleteIcon.setIconSize(16);

                btnVer.getStyleClass().add("btn-ver");
                btnEditar.getStyleClass().add("btn-editar");
                btnEliminar.getStyleClass().add("btn-eliminar");

                Tooltip.install(btnVer,      new Tooltip("Ver detalles"));
                Tooltip.install(btnEditar,   new Tooltip("Editar equipo"));
                Tooltip.install(btnEliminar, new Tooltip("Eliminar equipo"));

                container.setAlignment(Pos.CENTER);

                btnVer.setOnAction(e -> {
                    EquipoBaseDTO eq = getTableRow().getItem();
                    if (eq != null) cargarEquipoParaVer(eq);
                });
                btnEditar.setOnAction(e -> {
                    EquipoBaseDTO eq = getTableRow().getItem();
                    if (eq != null) cargarEquipoParaEditar(eq);
                });
                btnEliminar.setOnAction(e -> {
                    EquipoBaseDTO eq = getTableRow().getItem();
                    if (eq != null) confirmarEliminacion(eq);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    EquipoBaseDTO eq = getTableRow().getItem();
                    btnEliminar.setDisable("ASIGNADO".equalsIgnoreCase(eq.getEstado()));
                    setGraphic(container);
                }
            }
        });
    }

    private void confirmarEliminacion(EquipoBaseDTO equipo) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Eliminar equipo");
        alert.setContentText("¿Desea eliminar el equipo GRY " + equipo.getGry() + "?");

        alert.showAndWait()
             .filter(btn -> btn == ButtonType.OK)
             .ifPresent(b -> {
                 Task<Void> task = new Task<>() {
                     @Override protected Void call() throws Exception {
                         fachadaEquipos.eliminarEquipo(equipo.getIdEquipo());
                         return null;
                     }
                 };
                 task.setOnSucceeded(e -> cargarPagina());
                 task.setOnFailed(e -> {
                     Throwable ex = task.getException();
                     mostrarAlertError("No se pudo eliminar el equipo",
                             ex != null ? ex.getMessage() : "Error desconocido");
                 });
                 new Thread(task).start();
             });
    }
 
    @FXML
    private void agregarEquipo() {
        cambiarPantalla("FormInventario.fxml");
    }

    @Override
    public <T extends EquipoBaseDTO> void cargarEquipoParaEditar(T equipo) {
        // buscarPorId() trae el objeto completo con todos sus campos específicos
        T equipoCompleto = fachadaEquipos.buscarPorId(equipo.getIdEquipo());
        ControllerInventario controller = cambiarPantalla("FormInventario.fxml");
        if (controller instanceof FormInventarioController form) {
            form.cargarEquipoParaEditar(equipoCompleto);
        }
    }

    public <T extends EquipoBaseDTO> void cargarEquipoParaVer(T equipo) {
        T equipoCompleto = fachadaEquipos.buscarPorId(equipo.getIdEquipo());
        ControllerInventario controller = cambiarPantalla("FormInventario.fxml");
        if (controller instanceof FormInventarioController form) {
            form.cargarEquipoParaVisualizar(equipoCompleto);
        }
    }


    @Override
    public void setDashBoard(MenuController dbc) { this.dbc = dbc; }

    @Override
    public ControllerInventario cambiarPantalla(String rutaFXML) {
        try {
            if (rutaFXML != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
                Parent vista = loader.load();
                Object controller = loader.getController();
                if (controller instanceof ControllerInventario ci) ci.setDashBoard(dbc);
                dbc.getCenterContainer().setContent(vista);
                dbc.getCenterContainer().setVvalue(0);
                return (ControllerInventario) controller;
            }
        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    @Override public void limpiarFormulario() { /* no aplica en la tabla */ }

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

    private void mostrarAlertError(String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(titulo);
            a.setContentText(mensaje);
            a.showAndWait();
        });
    }
}