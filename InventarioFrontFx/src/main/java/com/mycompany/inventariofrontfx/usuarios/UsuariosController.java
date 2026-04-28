package com.mycompany.inventariofrontfx.usuarios;

import Dtos.EmpresaDTO;
import Dtos.PuestoDTO;
import Dtos.UsuarioDTO;
import InterfacesFachada.IFachadaOrganizacion;
import InterfacesFachada.IFachadaPersonas;
import InterfacesFachada.IFachadaPrestamos;
import com.mycompany.inventariofrontfx.inventario.InventarioController;
import util.ValidacionUtil;
import fabricaFachadas.FabricaFachadas;
import java.net.URL;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import util.ColumnFilterPanel;
import util.ColumnFilterPanel;
import util.ValidacionUtil;

/**
 * Controlador del módulo de Usuarios.
 */
public class UsuariosController implements Initializable {

    private static final int TAMANO_PAGINA = 25;

    @FXML
    private TextField txtFiltro;
    @FXML
    private TableView<UsuarioDTO> tablaUsuarios;
    @FXML
    private TableColumn<UsuarioDTO, Long> colId;
    @FXML
    private TableColumn<UsuarioDTO, String> colNombre;
    @FXML
    private TableColumn<UsuarioDTO, String> colNoNomina;
    @FXML
    private TableColumn<UsuarioDTO, String> colPuesto;
    @FXML
    private TableColumn<UsuarioDTO, String> colEquiposAsignados;
    @FXML
    private TableColumn<UsuarioDTO, Void> colAcciones;

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

    @FXML
    private TextField txtNomina;
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<EmpresaDTO> cbxEmpresa;
    @FXML
    private ComboBox<PuestoDTO> cbxPuesto;
    @FXML
    private Label errNombre;
    @FXML
    private Label errNomina;
    @FXML
    private Label errPuesto;

    private Long idUsuarioEditando;
    private Boolean modoEdicion;
    private long version;
    private int paginaActual = 0;
    private long totalRegistros = 0;
    private ObservableList<EmpresaDTO> empresas;
    private ObservableList<PuestoDTO> puestos;
    private final PauseTransition debounce = new PauseTransition(Duration.millis(350));

    /**
     * Copia de la última página cargada para filtrar localmente.
     */
    private List<UsuarioDTO> paginaActualData = new ArrayList<>();

    /**
     * Filtros activos: key = nombre columna, value = valores seleccionados.
     */
    private final Map<String, Set<String>> filtrosColumna = new ConcurrentHashMap<>();

    private ColumnFilterPanel<UsuarioDTO> filtroPuesto;
    private ColumnFilterPanel<UsuarioDTO> filtroEquipos;

    private final IFachadaPersonas fachadaUsuario = FabricaFachadas.getFachadaPersonas();
    private final IFachadaPrestamos fachadaPrestamos = FabricaFachadas.getFachadaPrestamos();
    private final IFachadaOrganizacion fachadaOrganizacion = FabricaFachadas.getFachadaOrganizacion();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        configurarAcciones();
        configurarInteraccionTabla();
        configurarFiltroConDebounce();
        configurarBotonesPaginacion();
        configurarListenersValidacion();
        cargarCombosLaterales();
        ocultarSpinner();

        Platform.runLater(this::instalarFiltrosColumna);

        cargarPagina();
    }
    
    private void instalarFiltrosColumna() {
        filtroPuesto = new ColumnFilterPanel<>(
                colPuesto,
                u -> u.getNombrePuesto() != null ? u.getNombrePuesto() : "",
                seleccion -> {
                    if (seleccion.isEmpty()) {
                        filtrosColumna.remove("puesto");
                    } else {
                        filtrosColumna.put("puesto", seleccion);
                    }
                    aplicarFiltrosLocales();
                }
        );

        filtroEquipos = new ColumnFilterPanel<>(
                colEquiposAsignados,
                u -> u.getNumeroDeEquipos() == 0
                ? "Sin equipos"
                : u.getNumeroDeEquipos() + (u.getNumeroDeEquipos() == 1 ? " equipo" : " equipos"),
                seleccion -> {
                    if (seleccion.isEmpty()) {
                        filtrosColumna.remove("equipos");
                    } else {
                        filtrosColumna.put("equipos", seleccion);
                    }
                    aplicarFiltrosLocales();
                }
        );
    }

    private void aplicarFiltrosLocales() {
        List<UsuarioDTO> resultado = paginaActualData.stream()
                .filter(u -> filtroPuesto == null || filtroPuesto.acepta(
                u.getNombrePuesto() != null ? u.getNombrePuesto() : ""))
                .filter(u -> filtroEquipos == null || filtroEquipos.acepta(
                u.getNumeroDeEquipos() == 0
                ? "Sin equipos"
                : u.getNumeroDeEquipos() + (u.getNumeroDeEquipos() == 1 ? " equipo" : " equipos")))
                .collect(Collectors.toList());

        tablaUsuarios.getItems().setAll(resultado);

        if (lblTotal != null) {
            String sufijo = filtrosColumna.isEmpty() ? "" : " (filtrados)";
            lblTotal.setText(resultado.size() + (resultado.size() == 1 ? " empleado" : " empleados") + sufijo);
        }
    }

    private void cargarPagina() {
        mostrarSpinner();
        tablaUsuarios.setDisable(true);
        filtrosColumna.clear();

        final String filtro = txtFiltro.getText();
        final int pagina = this.paginaActual;

        Task<Void> task = new Task<>() {

            List<UsuarioDTO> pagData;
            long total;

            @Override
            protected Void call() {
                total = fachadaUsuario.contarUsuarios(filtro);
                pagData = fachadaUsuario.buscarUsuariosPaginado(filtro, pagina, TAMANO_PAGINA);
                return null;
            }

            @Override
            protected void succeeded() {
                totalRegistros = total;
                paginaActualData = new ArrayList<>(pagData);

                tablaUsuarios.getItems().setAll(pagData);
                tablaUsuarios.setDisable(false);

                // Notificar los nuevos valores a cada filtro de columna
                if (filtroPuesto != null) {
                    filtroPuesto.actualizarValores(pagData);
                }
                if (filtroEquipos != null) {
                    filtroEquipos.actualizarValores(pagData);
                }

                actualizarBarraPaginacion();
                ocultarSpinner();
            }

            @Override
            protected void failed() {
                tablaUsuarios.setDisable(false);
                ocultarSpinner();
                Throwable ex = getException();
                if (ex != null) {
                    ex.printStackTrace();
                }
                Platform.runLater(() -> mostrarAlertError(
                        "No se pudieron cargar los usuarios",
                        ex != null ? ex.getMessage() : "Error desconocido"));
            }
        };

        new Thread(task).start();
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
        int totalPaginas = (int) Math.ceil((double) totalRegistros / TAMANO_PAGINA);
        if (totalPaginas == 0) {
            totalPaginas = 1;
        }
        if (lblPagina != null) {
            lblPagina.setText("Página " + (paginaActual + 1) + " / " + totalPaginas);
        }
        if (lblTotal != null) {
            lblTotal.setText(totalRegistros + (totalRegistros == 1 ? " empleado" : " empleados"));
        }
        if (btnAnterior != null) {
            btnAnterior.setDisable(paginaActual == 0);
        }
        if (btnSiguiente != null) {
            btnSiguiente.setDisable((long) (paginaActual + 1) * TAMANO_PAGINA >= totalRegistros);
        }
    }

    private void configurarFiltroConDebounce() {
        debounce.setOnFinished(e -> {
            paginaActual = 0;
            cargarPagina();
        });
        txtFiltro.textProperty().addListener((obs, old, val) -> debounce.playFromStart());
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getId()));
        colNombre.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getNombre()));
        colNoNomina.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getNoNomina()));
        colPuesto.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getNombrePuesto()));
        colEquiposAsignados.setCellValueFactory(d -> new SimpleObjectProperty<>(String.valueOf(d.getValue().getNumeroDeEquipos())));

        colNombre.setStyle("-fx-alignment: CENTER-LEFT;");
        colNoNomina.setStyle("-fx-alignment: CENTER-LEFT;");
        colPuesto.setStyle("-fx-alignment: CENTER-LEFT;");

        alinearEncabezadoIzquierda(colNombre);
        alinearEncabezadoIzquierda(colPuesto);
        alinearEncabezadoIzquierda(colNoNomina);
    }

    private void alinearEncabezadoIzquierda(TableColumn<?, ?> columna) {
        String texto = columna.getText();
        if (texto == null || texto.isBlank()) {
            return;
        }
        columna.setText("");
        Label label = new Label(texto);
        label.setAlignment(Pos.CENTER_LEFT);
        label.setMaxWidth(Double.MAX_VALUE);
        HBox box = new HBox(label);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setMaxWidth(Double.MAX_VALUE);
        columna.setGraphic(box);
    }

    private void configurarAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final FontIcon editIcon = new FontIcon("fas-edit");
            private final FontIcon deleteIcon = new FontIcon("fas-trash");
            private final Button btnEditar = new Button("", editIcon);
            private final Button btnEliminar = new Button("", deleteIcon);
            private final HBox contenedor = new HBox(8, btnEditar, btnEliminar);

            {
                editIcon.setIconSize(16);
                deleteIcon.setIconSize(16);
                btnEditar.getStyleClass().add("btn-editar");
                btnEliminar.getStyleClass().add("btn-eliminar");
                Tooltip.install(btnEditar, new Tooltip("Editar usuario"));
                Tooltip.install(btnEliminar, new Tooltip("Eliminar usuario"));
                contenedor.setAlignment(Pos.CENTER);
                btnEditar.setOnAction(e -> {
                    UsuarioDTO u = getTableRow().getItem();
                    if (u != null) {
                        cargarUsuarioParaEditar(u);
                    }
                });
                btnEliminar.setOnAction(e -> {
                    UsuarioDTO u = getTableRow().getItem();
                    if (u != null) {
                        confirmarEliminacion(u);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(contenedor);
                }
            }
        });
    }

    private void configurarInteraccionTabla() {
        tablaUsuarios.setRowFactory(tv -> {
            TableRow<UsuarioDTO> row = new TableRow<>();

            // Menú contextual estilo Excel / Desktop
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editarItem = new MenuItem("Editar Usuario");
            editarItem.setGraphic(new FontIcon("fas-edit"));
            editarItem.setOnAction(event -> cargarUsuarioParaEditar(row.getItem()));

            MenuItem eliminarItem = new MenuItem("Eliminar Usuario");
            eliminarItem.setGraphic(new FontIcon("fas-trash"));
            eliminarItem.setOnAction(event -> confirmarEliminacion(row.getItem()));

            contextMenu.getItems().addAll(editarItem, new SeparatorMenuItem(), eliminarItem);

            // Mostrar el menú solo si la fila no está vacía
            row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty())
                    .then((ContextMenu) null)
                    .otherwise(contextMenu)
            );

            // Doble clic para editar
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    cargarUsuarioParaEditar(row.getItem());
                }
            });

            return row;
        });

        // Atajo teclado: ENTER para editar, como en muchas tablas
        tablaUsuarios.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                UsuarioDTO selected = tablaUsuarios.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    cargarUsuarioParaEditar(selected);
                }
            }
        });
    }

    private void cargarCombosLaterales() {
        Task<List<EmpresaDTO>> task = new Task<>() {
            @Override
            protected List<EmpresaDTO> call() {
                return fachadaOrganizacion.listarEmpresas(null);
            }
        };
        task.setOnSucceeded(e -> {
            empresas = FXCollections.observableArrayList(task.getValue());
            cbxEmpresa.setItems(empresas);
            cbxEmpresa.getSelectionModel().selectFirst();
            onAccionCbxEmpresa();
            cbxEmpresa.setOnAction(ev -> onAccionCbxEmpresa());
        });
        new Thread(task).start();
    }

    private void onAccionCbxEmpresa() {
        if (cbxEmpresa.getSelectionModel().getSelectedItem() == null) {
            return;
        }
        Long id = cbxEmpresa.getSelectionModel().getSelectedItem().getId();
        if (id != null) {
            puestos = FXCollections.observableArrayList(fachadaOrganizacion.busquedaPorEmpresa(id));
            cbxPuesto.setItems(puestos);
            cbxPuesto.setDisable(puestos == null || puestos.isEmpty());
        }
    }

    @FXML
    private void guardarUsuario() {
        if (!validarFormulario()) {
            return;
        }
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                fachadaUsuario.guardarUsuario(construirUsuario());
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            modoEdicion = false;
            idUsuarioEditando = null;
            version = 0L;
            limpiarFormulario();
            cargarPagina();
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            mostrarAlertError("No se pudo guardar el usuario", ex != null ? ex.getMessage() : "Error");
        });
        new Thread(task).start();
    }

    private UsuarioDTO construirUsuario() {
        UsuarioDTO u = new UsuarioDTO();
        if (modoEdicion != null && modoEdicion) {
            u.setId(idUsuarioEditando);
            u.setVersion(version);
        } else {
            u.setActivo(Boolean.TRUE);
        }
        u.setNombre(txtNombre.getText().trim());
        u.setNoNomina(txtNomina.getText().trim());
        u.setIdPuesto(cbxPuesto.getSelectionModel().getSelectedItem().getId());
        return u;
    }

    private void confirmarEliminacion(UsuarioDTO usuario) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Eliminar Usuario");
        alert.setContentText("¿Desea eliminar al usuario: " + usuario.getNombre() + "?");
        alert.showAndWait().filter(btn -> btn == ButtonType.OK).ifPresent(b -> {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    fachadaUsuario.cambiarEstadoUsuario(usuario.getId(), false);
                    return null;
                }
            };
            task.setOnSucceeded(e -> cargarPagina());
            task.setOnFailed(e -> System.getLogger(InventarioController.class.getName()).log(System.Logger.Level.ERROR, (String) null, task.getException()));
            new Thread(task).start();
        });
    }

    private void cargarUsuarioParaEditar(UsuarioDTO usuario) {
        modoEdicion = true;
        idUsuarioEditando = usuario.getId();
        version = usuario.getVersion();
        txtNombre.setText(usuario.getNombre());
        txtNomina.setText(usuario.getNoNomina());
        Task<EmpresaDTO> task = new Task<>() {
            @Override
            protected EmpresaDTO call() {
                return fachadaOrganizacion.buscarEmpresaPorPuesto(usuario.getIdPuesto());
            }
        };
        task.setOnSucceeded(e -> {
            EmpresaDTO empresa = task.getValue();
            cbxEmpresa.getItems().stream().filter(emp -> emp.getId().equals(empresa.getId())).findFirst().ifPresent(emp -> cbxEmpresa.getSelectionModel().select(emp));
            onAccionCbxEmpresa();
            cbxPuesto.getItems().stream().filter(p -> p.getId().equals(usuario.getIdPuesto())).findFirst().ifPresent(p -> cbxPuesto.getSelectionModel().select(p));
        });
        new Thread(task).start();
        ValidacionUtil.resetTodos(txtNombre, txtNomina, cbxPuesto);
        ValidacionUtil.ocultarLabel(errNombre);
        ValidacionUtil.ocultarLabel(errNomina);
        ValidacionUtil.ocultarLabel(errPuesto);
    }

    @FXML
    public void limpiarFormulario() {
        txtNombre.clear();
        txtNomina.clear();
        cbxEmpresa.getSelectionModel().selectFirst();
        onAccionCbxEmpresa();
        modoEdicion = false;
        idUsuarioEditando = null;
        version = 0L;
        ValidacionUtil.resetTodos(txtNombre, txtNomina, cbxPuesto);
        ValidacionUtil.ocultarLabel(errNombre);
        ValidacionUtil.ocultarLabel(errNomina);
        ValidacionUtil.ocultarLabel(errPuesto);
        
        // UX: Dejar seleccionado el primer valor por defecto en vez de perder foco
        if (cbxPuesto != null && !cbxPuesto.getItems().isEmpty()) {
             cbxPuesto.getSelectionModel().selectFirst();
        }
    }

    private boolean validarFormulario() {
        boolean valido = true;
        StringBuilder errores = new StringBuilder();
        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            String msg = "El nombre del empleado es obligatorio.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.marcarError(txtNombre);
            ValidacionUtil.mostrarLabelError(errNombre, msg);
            valido = false;
        } else if (nombre.length() < 3) {
            String msg = "El nombre debe tener al menos 3 caracteres.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.marcarError(txtNombre);
            ValidacionUtil.mostrarLabelError(errNombre, msg);
            valido = false;
        } else {
            ValidacionUtil.marcarOk(txtNombre);
            ValidacionUtil.ocultarLabel(errNombre);
        }
        if (!ValidacionUtil.requerido(txtNomina)) {
            String msg = "El número de nómina es obligatorio.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.mostrarLabelError(errNomina, msg);
            valido = false;
        } else {
            ValidacionUtil.ocultarLabel(errNomina);
        }
        if (!ValidacionUtil.seleccionado(cbxPuesto)) {
            String msg = "Debes seleccionar un puesto.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.mostrarLabelError(errPuesto, msg);
            valido = false;
        } else {
            ValidacionUtil.ocultarLabel(errPuesto);
        }
        if (!valido) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Campos inválidos");
            a.setHeaderText("Por favor corrige:");
            a.setContentText(errores.toString());
            a.showAndWait();
        }
        return valido;
    }

    private void configurarListenersValidacion() {
        txtNombre.textProperty().addListener((obs, old, val) -> {
            if (val != null && val.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+") && val.trim().length() >= 3) {
                ValidacionUtil.marcarOk(txtNombre);
                ValidacionUtil.ocultarLabel(errNombre);
            }
        });
        txtNomina.textProperty().addListener((obs, old, val) -> {
            if (val != null && val.matches("\\d+") && val.trim().length() >= 5) {
                ValidacionUtil.marcarOk(txtNomina);
                ValidacionUtil.ocultarLabel(errNomina);
            }
        });
        cbxPuesto.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                ValidacionUtil.marcarOk(cbxPuesto);
                ValidacionUtil.ocultarLabel(errPuesto);
            }
        });
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

    private void mostrarAlertError(String titulo, String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(titulo);
            a.setContentText(msg);
            a.showAndWait();
        });
    }
}
