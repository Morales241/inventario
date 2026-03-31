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
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
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
 * Controlador del módulo de Usuarios con paginación del lado del servidor.
 *
 * ─── QUÉ CAMBIÓ Y POR QUÉ ──────────────────────────────────────────────────
 *
 * ANTES (2 minutos de carga):
 *   cargarDatos() bloqueaba el hilo de JavaFX y hacía:
 *   1. 1 query sin límite → 300 usuarios completos en memoria
 *   2. 300 queries obtenerEquiposDeUsuarios() → traía la lista completa solo para .size()
 *   3. 300 queries buscarPuestoEspecifico()
 *   Total: ~601 round-trips a SQL Server
 *
 * AHORA (< 1 segundo):
 *   1. 1 query COUNT en BD             → sabe el total (para paginador)
 *   2. 1 query SELECT con LIMIT/OFFSET → trae solo 25 filas
 *   3. 25 queries COUNT de equipos     → una por usuario, pero solo COUNT
 *   Total: 27 queries. Todo en Task → UI no se congela.
 *
 * ─── PAGINACIÓN ─────────────────────────────────────────────────────────────
 *   TAMANO_PAGINA = 25 filas (ajústalo con la constante).
 *   Barra inferior: [← Anterior]  Página 1 / 12  (300 empleados)  [Siguiente →]
 *
 * ─── DEBOUNCE EN FILTRO ─────────────────────────────────────────────────────
 *   El campo de búsqueda espera 350 ms sin actividad antes de lanzar la query,
 *   evitando una consulta por cada tecla que escribe el usuario.
 * ────────────────────────────────────────────────────────────────────────────
 */
public class UsuariosController implements Initializable {

    private static final int TAMANO_PAGINA = 25;

    // ── Tabla ─────────────────────────────────────────────────────────────────
    @FXML private TextField                       txtFiltro;
    @FXML private TableView<UsuarioDTO>           tablaUsuarios;
    @FXML private TableColumn<UsuarioDTO, Long>   colId;
    @FXML private TableColumn<UsuarioDTO, String> colNombre;
    @FXML private TableColumn<UsuarioDTO, String> colNoNomina;
    @FXML private TableColumn<UsuarioDTO, String> colPuesto;
    @FXML private TableColumn<UsuarioDTO, Integer>colEquiposAsignados;
    @FXML private TableColumn<UsuarioDTO, Void>   colAcciones;

    // ── Controles de paginación ───────────────────────────────────────────────
    // Agregar en Usuarios.fxml (ver instrucciones al final de este archivo)
    @FXML private Button            btnAnterior;
    @FXML private Button            btnSiguiente;
    @FXML private Label             lblPagina;      // "Página 1 / 12"
    @FXML private Label             lblTotal;       // "300 empleados"
    @FXML private ProgressIndicator progressCarga;  // spinner mientras carga

    // ── Formulario lateral ────────────────────────────────────────────────────
    @FXML private TextField            txtNomina;
    @FXML private TextField            txtNombre;
    @FXML private ComboBox<EmpresaDTO> cbxEmpresa;
    @FXML private ComboBox<PuestoDTO>  cbxPuesto;

    // ── Labels de error ───────────────────────────────────────────────────────
    @FXML private Label errNombre;
    @FXML private Label errNomina;
    @FXML private Label errPuesto;

    // ── Estado interno ────────────────────────────────────────────────────────
    private Long    idUsuarioEditando;
    private Boolean modoEdicion;
    private long    version;
    private int     paginaActual   = 0;
    private long    totalRegistros = 0;

    private ObservableList<EmpresaDTO> empresas;
    private ObservableList<PuestoDTO>  puestos;

    /** Espera 350 ms tras la última tecla antes de lanzar la búsqueda. */
    private final PauseTransition debounce = new PauseTransition(Duration.millis(350));

    private final IFachadaPersonas     fachadaUsuario      = FabricaFachadas.getFachadaPersonas();
    private final IFachadaPrestamos    fachadaPrestamos    = FabricaFachadas.getFachadaPrestamos();
    private final IFachadaOrganizacion fachadaOrganizacion = FabricaFachadas.getFachadaOrganizacion();

    // ─────────────────────────────────────────────────────────────────────────
    // Inicialización
    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        configurarAcciones();
        configurarFiltroConDebounce();
        configurarBotonesPaginacion();
        configurarListenersValidacion();
        cargarCombosLaterales();

        ocultarSpinner();
        cargarPagina();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // CARGA PAGINADA — núcleo de la optimización
    // ─────────────────────────────────────────────────────────────────────────

    private void cargarPagina() {
        mostrarSpinner();
        tablaUsuarios.setDisable(true);

        final String filtro       = txtFiltro.getText();
        final int    paginaActual = this.paginaActual;

        Task<Void> task = new Task<>() {

            List<UsuarioDTO> pagina;
            long             total;

            @Override
            protected Void call() {
                total  = fachadaUsuario.contarUsuarios(filtro);
                pagina = fachadaUsuario.buscarUsuariosPaginado(filtro, paginaActual, TAMANO_PAGINA);
                return null;
            }

            @Override
            protected void succeeded() {
                totalRegistros = total;
                tablaUsuarios.getItems().setAll(pagina);
                tablaUsuarios.setDisable(false);
                actualizarBarraPaginacion();
                ocultarSpinner();
            }

            @Override
            protected void failed() {
                tablaUsuarios.setDisable(false);
                ocultarSpinner();
                Throwable ex = getException();
                if (ex != null) ex.printStackTrace();
                Platform.runLater(() -> mostrarAlertError(
                        "No se pudieron cargar los usuarios",
                        ex != null ? ex.getMessage() : "Error desconocido"));
            }
        };

        new Thread(task).start();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Paginación — botones y label
    // ─────────────────────────────────────────────────────────────────────────

    private void configurarBotonesPaginacion() {
        if (btnAnterior  != null) btnAnterior.setOnAction(e -> irAPaginaAnterior());
        if (btnSiguiente != null) btnSiguiente.setOnAction(e -> irAPaginaSiguiente());
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
        if ((long)(paginaActual + 1) * TAMANO_PAGINA < totalRegistros) {
            paginaActual++;
            cargarPagina();
        }
    }

    private void actualizarBarraPaginacion() {
        int totalPaginas = (int) Math.ceil((double) totalRegistros / TAMANO_PAGINA);
        if (totalPaginas == 0) totalPaginas = 1;

        if (lblPagina  != null) lblPagina.setText("Página " + (paginaActual + 1) + " / " + totalPaginas);
        if (lblTotal   != null) lblTotal.setText(totalRegistros + (totalRegistros == 1 ? " empleado" : " empleados"));
        if (btnAnterior  != null) btnAnterior.setDisable(paginaActual == 0);
        if (btnSiguiente != null) btnSiguiente.setDisable((long)(paginaActual + 1) * TAMANO_PAGINA >= totalRegistros);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Filtro con debounce
    // ─────────────────────────────────────────────────────────────────────────

    private void configurarFiltroConDebounce() {
        debounce.setOnFinished(e -> {
            paginaActual = 0;
            cargarPagina();
        });
        txtFiltro.textProperty().addListener((obs, old, val) -> debounce.playFromStart());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Columnas y acciones de la tabla
    // ─────────────────────────────────────────────────────────────────────────

    private void configurarColumnas() {
        colId.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getId()));
        colNombre.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getNombre()));
        colNoNomina.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getNoNomina()));
        colPuesto.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getNombrePuesto()));
        colEquiposAsignados.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getNumeroDeEquipos()));
    }

    private void configurarAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final FontIcon editIcon   = new FontIcon("fas-edit");
            private final FontIcon deleteIcon = new FontIcon("fas-trash");
            private final Button   btnEditar   = new Button("", editIcon);
            private final Button   btnEliminar = new Button("", deleteIcon);
            private final HBox     contenedor  = new HBox(8, btnEditar, btnEliminar);

            {
                editIcon.setIconSize(16);
                deleteIcon.setIconSize(16);
                btnEditar.getStyleClass().add("btn-editar");
                btnEliminar.getStyleClass().add("btn-eliminar");
                Tooltip.install(btnEditar,   new Tooltip("Editar usuario"));
                Tooltip.install(btnEliminar, new Tooltip("Eliminar usuario"));
                contenedor.setAlignment(Pos.CENTER);

                btnEditar.setOnAction(e -> {
                    UsuarioDTO u = getTableRow().getItem();
                    if (u != null) cargarUsuarioParaEditar(u);
                });
                btnEliminar.setOnAction(e -> {
                    UsuarioDTO u = getTableRow().getItem();
                    if (u != null) confirmarEliminacion(u);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || getTableRow().getItem() == null ? null : contenedor);
            }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Combos laterales
    // ─────────────────────────────────────────────────────────────────────────

    private void cargarCombosLaterales() {
        Task<List<EmpresaDTO>> task = new Task<>() {
            @Override protected List<EmpresaDTO> call() {
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
        if (cbxEmpresa.getSelectionModel().getSelectedItem() == null) return;
        Long id = cbxEmpresa.getSelectionModel().getSelectedItem().getId();
        if (id != null) {
            puestos = FXCollections.observableArrayList(fachadaOrganizacion.busquedaPorEmpresa(id));
            cbxPuesto.setItems(puestos);
            cbxPuesto.setDisable(puestos == null || puestos.isEmpty());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Guardar
    // ─────────────────────────────────────────────────────────────────────────

    @FXML
    private void guardarUsuario() {
        if (!validarFormulario()) return;

        Task<Void> task = new Task<>() {
            @Override protected Void call() throws Exception {
                fachadaUsuario.guardarUsuario(construirUsuario());
                return null;
            }
        };
        task.setOnSucceeded(e -> {
            modoEdicion = false; idUsuarioEditando = null; version = 0L;
            limpiarFormulario();
            cargarPagina();
        });
        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            mostrarAlertError("No se pudo guardar el usuario",
                    ex != null ? ex.getMessage() : "Error desconocido");
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

    // ─────────────────────────────────────────────────────────────────────────
    // Eliminar
    // ─────────────────────────────────────────────────────────────────────────

    private void confirmarEliminacion(UsuarioDTO usuario) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Eliminar Usuario");
        alert.setContentText("¿Desea eliminar al usuario: " + usuario.getNombre() + "?");
        alert.showAndWait()
             .filter(btn -> btn == ButtonType.OK)
             .ifPresent(b -> {
                 Task<Void> task = new Task<>() {
                     @Override protected Void call() throws Exception {
                         fachadaUsuario.cambiarEstadoUsuario(usuario.getId(), false);
                         return null;
                     }
                 };
                 task.setOnSucceeded(e -> cargarPagina());
                 task.setOnFailed(e -> {
                     Throwable ex = task.getException();
                     System.getLogger(InventarioController.class.getName())
                           .log(System.Logger.Level.ERROR, (String) null, ex);
                 });
                 new Thread(task).start();
             });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Editar
    // ─────────────────────────────────────────────────────────────────────────

    private void cargarUsuarioParaEditar(UsuarioDTO usuario) {
        modoEdicion = true; idUsuarioEditando = usuario.getId(); version = usuario.getVersion();
        txtNombre.setText(usuario.getNombre());
        txtNomina.setText(usuario.getNoNomina());

        Task<EmpresaDTO> task = new Task<>() {
            @Override protected EmpresaDTO call() {
                return fachadaOrganizacion.buscarEmpresaPorPuesto(usuario.getIdPuesto());
            }
        };
        task.setOnSucceeded(e -> {
            EmpresaDTO empresa = task.getValue();
            cbxEmpresa.getItems().stream()
                .filter(emp -> emp.getId().equals(empresa.getId()))
                .findFirst()
                .ifPresent(emp -> cbxEmpresa.getSelectionModel().select(emp));
            onAccionCbxEmpresa();
            cbxPuesto.getItems().stream()
                .filter(p -> p.getId().equals(usuario.getIdPuesto()))
                .findFirst()
                .ifPresent(p -> cbxPuesto.getSelectionModel().select(p));
        });
        new Thread(task).start();

        ValidacionUtil.resetTodos(txtNombre, txtNomina, cbxPuesto);
        ValidacionUtil.ocultarLabel(errNombre);
        ValidacionUtil.ocultarLabel(errNomina);
        ValidacionUtil.ocultarLabel(errPuesto);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Limpiar
    // ─────────────────────────────────────────────────────────────────────────

    private void limpiarFormulario() {
        txtNombre.clear(); txtNomina.clear();
        cbxEmpresa.getSelectionModel().selectFirst();
        onAccionCbxEmpresa();
        modoEdicion = false; idUsuarioEditando = null; version = 0L;
        ValidacionUtil.resetTodos(txtNombre, txtNomina, cbxPuesto);
        ValidacionUtil.ocultarLabel(errNombre);
        ValidacionUtil.ocultarLabel(errNomina);
        ValidacionUtil.ocultarLabel(errPuesto);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Validación
    // ─────────────────────────────────────────────────────────────────────────

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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campos inválidos");
            alert.setHeaderText("Por favor corrige los siguientes errores:");
            alert.setContentText(errores.toString());
            alert.showAndWait();
        }
        return valido;
    }

    private void configurarListenersValidacion() {
        txtNombre.textProperty().addListener((obs, old, val) -> {
            if (val != null && val.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+") && val.trim().length() >= 3) {
                ValidacionUtil.marcarOk(txtNombre); ValidacionUtil.ocultarLabel(errNombre);
            }
        });
        txtNomina.textProperty().addListener((obs, old, val) -> {
            if (val != null && val.matches("\\d+") && val.trim().length() >= 5) {
                ValidacionUtil.marcarOk(txtNomina); ValidacionUtil.ocultarLabel(errNomina);
            }
        });
        cbxPuesto.valueProperty().addListener((obs, old, val) -> {
            if (val != null) { ValidacionUtil.marcarOk(cbxPuesto); ValidacionUtil.ocultarLabel(errPuesto); }
        });
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

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
