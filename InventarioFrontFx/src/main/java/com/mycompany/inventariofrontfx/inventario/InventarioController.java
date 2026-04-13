package com.mycompany.inventariofrontfx.inventario;

import Dtos.EquipoBaseDTO;
import InterfacesFachada.IFachadaEquipos;
import com.mycompany.inventariofrontfx.menu.MenuController;
import util.ColumnFilterPanel;
import fabricaFachadas.FabricaFachadas;
import interfaces.ControllerInventario;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.ProgressIndicator;
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

/**
 * Controlador del módulo de Inventario.
 */
public class InventarioController implements Initializable, ControllerInventario {

    private static final int TAMANO_PAGINA = 25;
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private MenuController dbc;
    private final IFachadaEquipos fachadaEquipos = FabricaFachadas.getFachadaEquipos();

    @FXML
    private TableView<EquipoBaseDTO> tablaEquipos;
    @FXML
    private TableColumn<EquipoBaseDTO, Long> colId;
    @FXML
    private TableColumn<EquipoBaseDTO, Integer> colGry;
    @FXML
    private TableColumn<EquipoBaseDTO, String> colTipo;
    @FXML
    private TableColumn<EquipoBaseDTO, String> colFecha;
    @FXML
    private TableColumn<EquipoBaseDTO, String> colModelo;
    @FXML
    private TableColumn<EquipoBaseDTO, String> colCondicion;
    @FXML
    private TableColumn<EquipoBaseDTO, String> colEstado;
    @FXML
    private TableColumn<EquipoBaseDTO, Void> colAcciones;
    @FXML
    private Button btnAgregar;
    @FXML
    private TextField txtFiltro;
    @FXML
    private Button btnAnterior;
    @FXML
    private Button btnSiguiente;
    @FXML
    private Label lblPagina;
    @FXML
    private Label lblTotal;
    @FXML
    private ProgressIndicator progressCarga;

    private int paginaActual = 0;
    private long totalRegistros = 0;

    private List<EquipoBaseDTO> paginaActualData = new ArrayList<>();
    private final Map<String, Set<String>> filtrosColumna = new ConcurrentHashMap<>();
    private ColumnFilterPanel<EquipoBaseDTO> filtroTipo;
    private ColumnFilterPanel<EquipoBaseDTO> filtroCondicion;
    private ColumnFilterPanel<EquipoBaseDTO> filtroEstado;

    private final PauseTransition debounce = new PauseTransition(Duration.millis(350));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        configurarAcciones();
        configurarInteraccionTabla();
        configurarBotonesPaginacion();
        configurarDebounce();
        ocultarSpinner();
        Platform.runLater(this::instalarFiltrosColumna);
        cargarPagina();
    }
    
    private void cargarPagina() {
        mostrarSpinner();
        tablaEquipos.setDisable(true);
        filtrosColumna.clear();

        final String texto = txtFiltro != null ? txtFiltro.getText() : "";
        final int pagina = this.paginaActual;

        Task<Void> task = new Task<>() {

            List<EquipoBaseDTO> pagData;
            long total;

            @Override
            protected Void call() {
                // FIX #1 — estas líneas estaban comentadas, la tabla siempre salía vacía
                total = fachadaEquipos.contarEquipos(texto, null, null, null);
                pagData = fachadaEquipos.buscarConFiltrosPaginado(
                        texto, null, null, null, pagina, TAMANO_PAGINA);
                return null;
            }

            @Override
            protected void succeeded() {
                totalRegistros = total;
                paginaActualData = new ArrayList<>(pagData);
                tablaEquipos.getItems().setAll(pagData);
                tablaEquipos.setDisable(false);
                if (filtroTipo != null) {
                    filtroTipo.actualizarValores(pagData);
                }
                if (filtroCondicion != null) {
                    filtroCondicion.actualizarValores(pagData);
                }
                if (filtroEstado != null) {
                    filtroEstado.actualizarValores(pagData);
                }
                actualizarBarraPaginacion();
                ocultarSpinner();
            }

            @Override
            protected void failed() {
                tablaEquipos.setDisable(false);
                ocultarSpinner();
                Throwable ex = getException();
                if (ex != null) {
                    ex.printStackTrace();
                }
                mostrarAlertError("No se pudieron cargar los equipos",
                        ex != null ? ex.getMessage() : "Error desconocido");
            }
        };

        new Thread(task).start();
    }

    private void instalarFiltrosColumna() {
        filtroTipo = new ColumnFilterPanel<>(colTipo,
                e -> e.getTipo() != null ? e.getTipo() : "",
                sel -> {
                    if (sel.isEmpty()) {
                        filtrosColumna.remove("tipo");
                    } else {
                        filtrosColumna.put("tipo", sel);
                    }
                    aplicarFiltrosLocales();
                });

        filtroCondicion = new ColumnFilterPanel<>(colCondicion,
                e -> e.getCondicion() != null ? e.getCondicion() : "",
                sel -> {
                    if (sel.isEmpty()) {
                        filtrosColumna.remove("condicion");
                    } else {
                        filtrosColumna.put("condicion", sel);
                    }
                    aplicarFiltrosLocales();
                });

        filtroEstado = new ColumnFilterPanel<>(colEstado,
                e -> e.getEstado() != null ? e.getEstado() : "",
                sel -> {
                    if (sel.isEmpty()) {
                        filtrosColumna.remove("estado");
                    } else {
                        filtrosColumna.put("estado", sel);
                    }
                    aplicarFiltrosLocales();
                });
    }

    private void aplicarFiltrosLocales() {
        List<EquipoBaseDTO> resultado = paginaActualData.stream()
                .filter(e -> filtroTipo == null || filtroTipo.acepta(e.getTipo()))
                .filter(e -> filtroCondicion == null || filtroCondicion.acepta(e.getCondicion()))
                .filter(e -> filtroEstado == null || filtroEstado.acepta(e.getEstado()))
                .collect(Collectors.toList());
        tablaEquipos.getItems().setAll(resultado);
        if (lblTotal != null) {
            long n = resultado.size();
            lblTotal.setText(n + (n == 1 ? " equipo" : " equipos") + (filtrosColumna.isEmpty() ? "" : " (filtrados)"));
        }
    }

    @FXML
    private void limpiarFiltrosColumna() {
        filtrosColumna.clear();
        if (filtroTipo != null) {
            filtroTipo.limpiar();
        }
        if (filtroCondicion != null) {
            filtroCondicion.limpiar();
        }
        if (filtroEstado != null) {
            filtroEstado.limpiar();
        }
        tablaEquipos.getItems().setAll(paginaActualData);
        actualizarBarraPaginacion();
    }

    private void configurarDebounce() {
        debounce.setOnFinished(e -> {
            paginaActual = 0;
            cargarPagina();
        });
        if (txtFiltro != null) {
            txtFiltro.textProperty().addListener((obs, old, v) -> debounce.playFromStart());
        }
    }

    private void configurarBotonesPaginacion() {
        if (btnAnterior != null) {
            btnAnterior.setOnAction(e -> irAPaginaAnterior());
        }
        if (btnSiguiente != null) {
            btnSiguiente.setOnAction(e -> irAPaginaSiguiente());
        }
    }

    @FXML
    private void irAPaginaAnterior() {
        if (paginaActual > 0) {
            paginaActual--;
            cargarPagina();
        }
    }

    @FXML
    private void irAPaginaSiguiente() {
        if ((long) (paginaActual + 1) * TAMANO_PAGINA < totalRegistros) {
            paginaActual++;
            cargarPagina();
        }
    }

    private void actualizarBarraPaginacion() {
        int tp = (int) Math.ceil((double) totalRegistros / TAMANO_PAGINA);
        if (tp == 0) {
            tp = 1;
        }
        if (lblPagina != null) {
            lblPagina.setText("Página " + (paginaActual + 1) + " / " + tp);
        }
        if (lblTotal != null) {
            lblTotal.setText(totalRegistros + (totalRegistros == 1 ? " equipo" : " equipos"));
        }
        if (btnAnterior != null) {
            btnAnterior.setDisable(paginaActual == 0);
        }
        if (btnSiguiente != null) {
            btnSiguiente.setDisable((long) (paginaActual + 1) * TAMANO_PAGINA >= totalRegistros);
        }
    }
    
    private void configurarColumnas() {
        colId.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getIdEquipo()));
        colGry.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getGry()));
        colTipo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getTipo()));
        colModelo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombreModelo()));
        colFecha.setCellValueFactory(d -> {
            var f = d.getValue().getFechaCompra();
            return new SimpleStringProperty(f != null ? f.format(FMT) : "—");  // FIX #6
        });
        colCondicion.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCondicion()));
        colEstado.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getEstado()));
    }

    private void configurarAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final FontIcon viewIcon = new FontIcon("fas-eye");
            private final FontIcon editIcon = new FontIcon("fas-edit");
            private final FontIcon deleteIcon = new FontIcon("fas-trash");
            private final Button btnVer = new Button("", viewIcon);
            private final Button btnEditar = new Button("", editIcon);
            private final Button btnEliminar = new Button("", deleteIcon);
            private final HBox container = new HBox(8, btnVer, btnEditar, btnEliminar);

            {
                viewIcon.setIconSize(16);
                editIcon.setIconSize(16);
                deleteIcon.setIconSize(16);
                btnVer.getStyleClass().add("btn-ver");
                btnEditar.getStyleClass().add("btn-editar");
                btnEliminar.getStyleClass().add("btn-eliminar");
                Tooltip.install(btnVer, new Tooltip("Ver detalles"));
                Tooltip.install(btnEditar, new Tooltip("Editar equipo"));
                Tooltip.install(btnEliminar, new Tooltip("Eliminar equipo"));
                container.setAlignment(Pos.CENTER);

                btnVer.setOnAction(e -> {
                    var eq = getTableRow().getItem();
                    if (eq != null) {
                        cargarEquipoParaVer(eq);
                    }
                });
                btnEditar.setOnAction(e -> {
                    var eq = getTableRow().getItem();
                    if (eq != null) {
                        cargarEquipoParaEditar(eq);
                    }
                });
                btnEliminar.setOnAction(e -> {
                    var eq = getTableRow().getItem();
                    if (eq != null) {
                        confirmarEliminacion(eq);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                btnEliminar.setDisable("ASIGNADO".equalsIgnoreCase(getTableRow().getItem().getEstado()));
                setGraphic(container);
            }
        });
    }

    private void configurarInteraccionTabla() {
        tablaEquipos.setRowFactory(tv -> {
            TableRow<EquipoBaseDTO> row = new TableRow<>();

            // Menú contextual estilo Excel / Desktop
            ContextMenu contextMenu = new ContextMenu();
            MenuItem verItem = new MenuItem("Ver Detalles");
            verItem.setGraphic(new FontIcon("fas-eye"));
            verItem.setOnAction(event -> cargarEquipoParaVer(row.getItem()));

            MenuItem editarItem = new MenuItem("Editar Equipo");
            editarItem.setGraphic(new FontIcon("fas-edit"));
            editarItem.setOnAction(event -> cargarEquipoParaEditar(row.getItem()));

            MenuItem eliminarItem = new MenuItem("Eliminar Equipo");
            eliminarItem.setGraphic(new FontIcon("fas-trash"));
            // Deshabilitar opción si está asignado
            eliminarItem.setOnAction(event -> confirmarEliminacion(row.getItem()));

            contextMenu.getItems().addAll(verItem, editarItem, new SeparatorMenuItem(), eliminarItem);

            // Mostrar el menú solo si la fila no está vacía
            row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty())
                    .then((ContextMenu) null)
                    .otherwise(contextMenu)
            );

            // Doble clic para editar
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    cargarEquipoParaEditar(row.getItem());
                }
            });

            return row;
        });

        // Atajo teclado: ENTER para editar, como en muchas tablas tipo grid
        tablaEquipos.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                EquipoBaseDTO selected = tablaEquipos.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    cargarEquipoParaEditar(selected);
                }
            }
        });
    }

    private void confirmarEliminacion(EquipoBaseDTO equipo) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Eliminar equipo");
        alert.setContentText("¿Desea eliminar el equipo GRY " + equipo.getGry() + "?");
        alert.showAndWait().filter(btn -> btn == ButtonType.OK).ifPresent(b -> {
            Task<Void> t = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    fachadaEquipos.eliminarEquipo(equipo.getIdEquipo());
                    return null;
                }
            };
            t.setOnSucceeded(e -> cargarPagina());
            t.setOnFailed(e -> {
                var ex = t.getException();
                mostrarAlertError("No se pudo eliminar", ex != null ? ex.getMessage() : "Error");
            });
            new Thread(t).start();
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Navegación — FIX #5: buscarPorId en Task
    // ─────────────────────────────────────────────────────────────────────────
    @FXML
    private void agregarEquipo() {
        cambiarPantalla("FormInventario.fxml");
    }

    @Override
    public <T extends EquipoBaseDTO> void cargarEquipoParaEditar(T equipo) {
        mostrarSpinner();
        Task<T> t = new Task<>() {
            @Override
            protected T call() {
                return fachadaEquipos.buscarPorId(equipo.getIdEquipo());
            }
        };
        t.setOnSucceeded(e -> {
            ocultarSpinner();
            var c = cambiarPantalla("FormInventario.fxml");
            if (c instanceof FormInventarioController f) {
                f.cargarEquipoParaEditar(t.getValue());
            }
        });
        t.setOnFailed(e -> {
            ocultarSpinner();
            mostrarAlertError("Error", "No se pudo cargar el equipo para editar.");
        });
        new Thread(t).start();
    }

    public <T extends EquipoBaseDTO> void cargarEquipoParaVer(T equipo) {
        mostrarSpinner();
        Task<T> t = new Task<>() {
            @Override
            protected T call() {
                return fachadaEquipos.buscarPorId(equipo.getIdEquipo());
            }
        };
        t.setOnSucceeded(e -> {
            ocultarSpinner();
            var c = cambiarPantalla("FormInventario.fxml");
            if (c instanceof FormInventarioController f) {
                f.cargarEquipoParaVisualizar(t.getValue());
            }
        });
        t.setOnFailed(e -> {
            ocultarSpinner();
            mostrarAlertError("Error", "No se pudo cargar el equipo.");
        });
        new Thread(t).start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ControllerInventario + helpers
    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }

    @Override
    public void limpiarFormulario() {
    }

    @Override
    public ControllerInventario cambiarPantalla(String rutaFXML) {
        try {
            if (rutaFXML == null) {
                return null;
            }
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent vista = loader.load();
            Object controller = loader.getController();
            if (controller instanceof ControllerInventario ci) {
                ci.setDashBoard(dbc);
            }
            dbc.getCenterContainer().setContent(vista);
            dbc.getCenterContainer().setVvalue(0);
            return (ControllerInventario) controller;
        } catch (IOException e) {
            // FIX #4 — antes solo imprimía en consola
            e.printStackTrace();
            mostrarAlertError("Error de navegación", "No se pudo cargar '" + rutaFXML + "': " + e.getMessage());
            return null;
        }
    }

    private void mostrarSpinner() {
        Platform.runLater(() -> {
            if (progressCarga != null) {
                progressCarga.setVisible(true);
                progressCarga.setManaged(true);
            }
        });
    }

    private void ocultarSpinner() {
        Platform.runLater(() -> {
            if (progressCarga != null) {
                progressCarga.setVisible(false);
                progressCarga.setManaged(false);
            }
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
