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
import java.util.stream.Collectors;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;
import util.ValidacionUtil;

public class UsuariosController implements Initializable {

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
    private TableColumn<UsuarioDTO, Integer> colEquiposAsignados;
    @FXML
    private TextField txtNomina;
    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<EmpresaDTO> cbxEmpresa;
    @FXML
    private ComboBox<PuestoDTO> cbxPuesto;
    @FXML
    private TableColumn<UsuarioDTO, Void> colAcciones;

    // Labels de error (agrégalos en el FXML junto a cada campo):
    // <Label fx:id="errNombre"  visible="false" managed="false"/>
    // <Label fx:id="errNomina"  visible="false" managed="false"/>
    // <Label fx:id="errPuesto"  visible="false" managed="false"/>
    @FXML
    private Label errNombre;
    @FXML
    private Label errNomina;
    @FXML
    private Label errPuesto;

    private Long idUsuarioEditando;
    private Boolean modoEdicion;
    private long version;

    private ObservableList<UsuarioDTO> listaUsuarios;
    private ObservableList<EmpresaDTO> empresas;
    private ObservableList<PuestoDTO> puestos;

    private final IFachadaPersonas fachadaUsuario = FabricaFachadas.getFachadaPersonas();
    private final IFachadaPrestamos fachadaPrestamos = FabricaFachadas.getFachadaPrestamos();
    private final IFachadaOrganizacion fachadaOrganizacion = FabricaFachadas.getFachadaOrganizacion();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        configurarFiltroReactivo();
        cargarDatos();
        configurarAcciones();
        configurarListenersValidacion();
    }

    /**
     * Listeners que limpian el borde rojo en cuanto el usuario corrige el
     * campo.
     */
    private void configurarListenersValidacion() {
        // Nombre: mínimo 3 caracteres, no vacío
        txtNombre.textProperty().addListener((obs, old, val) -> {
            if (val != null) {
                // Solo letras y espacios
                if (val.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                    if (val.trim().length() >= 3) {
                        ValidacionUtil.marcarOk(txtNombre);
                        ValidacionUtil.ocultarLabel(errNombre);
                    } else {
                        ValidacionUtil.marcarError(txtNombre);
                        ValidacionUtil.mostrarLabelError(errNombre, "Debe tener al menos 3 letras");
                    }
                } else {
                    ValidacionUtil.marcarError(txtNombre);
                    ValidacionUtil.mostrarLabelError(errNombre, "Solo se permiten letras");
                }
            }
        });

        txtNomina.textProperty().addListener((obs, old, val) -> {
            if (val != null) {
                if (val.matches("\\d+")) {
                    if (val.trim().length() >= 5) {
                        ValidacionUtil.marcarOk(txtNomina);
                        ValidacionUtil.ocultarLabel(errNomina);
                    } else {
                        ValidacionUtil.marcarError(txtNomina);
                        ValidacionUtil.mostrarLabelError(errNomina, "Debe tener al menos 5 dígitos");
                    }
                } else {
                    ValidacionUtil.marcarError(txtNomina);
                    ValidacionUtil.mostrarLabelError(errNomina, "Solo se permiten números");
                }
            }
        });

        cbxPuesto.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                ValidacionUtil.marcarOk(cbxPuesto);
                ValidacionUtil.ocultarLabel(errPuesto);
            }
        });
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
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Campos inválidos");
            alert.setHeaderText("Por favor corrige los siguientes errores:");
            alert.setContentText(errores.toString());
            alert.showAndWait();
        }

        return valido;
    }

    @FXML
    private void guardarUsuario() {
        if (!validarFormulario()) {
            return;
        }

        try {
            UsuarioDTO usuario = construirUsuario();
            fachadaUsuario.guardarUsuario(usuario);
            modoEdicion = false;
            idUsuarioEditando = null;
            version = 0L;
            limpiarFormulario();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo guardar el usuario");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    private UsuarioDTO construirUsuario() {
        UsuarioDTO usuario = new UsuarioDTO();
        if (modoEdicion != null && modoEdicion) {
            usuario.setId(idUsuarioEditando);
            usuario.setVersion(version);
        } else {
            usuario.setActivo(Boolean.TRUE);
        }
        usuario.setNombre(txtNombre.getText().trim());
        usuario.setNoNomina(txtNomina.getText().trim());
        usuario.setIdPuesto(cbxPuesto.getSelectionModel().getSelectedItem().getId());
        return usuario;
    }

    private void limpiarFormulario() {
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

        cargarDatos();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        colNombre.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getNombre()));
        colNoNomina.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getNoNomina()));
        colPuesto.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getNombrePuesto()));
        colEquiposAsignados.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getNumeroDeEquipos()));
    }

    private void configurarAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final FontIcon editIcon = new FontIcon("fas-edit");
            private final FontIcon deleteIcon = new FontIcon("fas-trash");
            private final Button btnEditar = new Button("", editIcon);
            private final Button btnEliminar = new Button("", deleteIcon);
            private final HBox container = new HBox(8, btnEditar, btnEliminar);

            {
                editIcon.setIconSize(16);
                deleteIcon.setIconSize(16);
                btnEditar.getStyleClass().add("btn-editar");
                btnEliminar.getStyleClass().add("btn-eliminar");
                Tooltip.install(btnEditar, new Tooltip("Editar usuario"));
                Tooltip.install(btnEliminar, new Tooltip("Eliminar usuario"));
                container.setAlignment(Pos.CENTER);

                btnEditar.setOnAction(e -> {
                    UsuarioDTO usuario = getTableRow().getItem();
                    if (usuario != null) {
                        cargarUsuarioParaEditar(usuario);
                    }
                });
                btnEliminar.setOnAction(e -> {
                    UsuarioDTO usuario = getTableRow().getItem();
                    if (usuario != null) {
                        confirmarEliminacion(usuario);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty || getTableRow().getItem() == null ? null : container);
            }
        });
    }

    private void cargarDatos() {
        listaUsuarios = FXCollections.observableArrayList(
                fachadaUsuario.buscarUsuarios(null)
                        .stream()
                        .filter(UsuarioDTO::getActivo)
                        .collect(Collectors.toList())
        );

        listaUsuarios.forEach(c -> {
            int numeroEquipos = fachadaPrestamos.obtenerEquiposDeUsuarios(c.getId()).size();
            String nombrePuesto = fachadaOrganizacion.buscarPuestoEspecifico(c.getIdPuesto()).getNombre();
            c.setNumeroDeEquipos(numeroEquipos);
            c.setNombrePuesto(nombrePuesto);
        });

        tablaUsuarios.setItems(listaUsuarios);
        empresas = FXCollections.observableArrayList(fachadaOrganizacion.listarEmpresas(null));
        cbxEmpresa.setItems(empresas);
        cbxEmpresa.getSelectionModel().selectFirst();
        onAccionCbxEmpresa();
        cbxEmpresa.setOnAction(e -> onAccionCbxEmpresa());
    }

    private void onAccionCbxEmpresa() {
        Long id = 0L;
        if (cbxEmpresa.getSelectionModel().getSelectedItem() != null) {
            id = cbxEmpresa.getSelectionModel().getSelectedItem().getId();
        }
        if (id != null) {
            puestos = FXCollections.observableArrayList(fachadaOrganizacion.busquedaPorEmpresa(id));
            if (puestos != null && !puestos.isEmpty()) {
                cbxPuesto.setItems(puestos);
                cbxPuesto.setDisable(false);
            } else {
                cbxPuesto.setDisable(true);
            }
        }
    }

    private void configurarFiltroReactivo() {
        txtFiltro.textProperty().addListener((obs, oldValue, newValue) -> aplicarFiltro());
    }

    private void aplicarFiltro() {
        cargarDatosAsync();
    }

    private void cargarDatosAsync() {
        Task<List<UsuarioDTO>> task = new Task<>() {
            @Override
            protected List<UsuarioDTO> call() {
                return fachadaUsuario.buscarUsuarios(txtFiltro.getText())
                        .stream()
                        .filter(UsuarioDTO::getActivo)
                        .collect(Collectors.toList());
            }
        };
        tablaUsuarios.setDisable(true);
        task.setOnSucceeded(e -> {
            tablaUsuarios.getItems().setAll(task.getValue());
            tablaUsuarios.setDisable(false);
        });
        task.setOnFailed(e -> {
            tablaUsuarios.setDisable(false);
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    private void confirmarEliminacion(UsuarioDTO usuario) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Eliminar Usuario");
        alert.setContentText("¿Desea eliminar al usuario: " + usuario.getNombre() + "?");
        alert.showAndWait()
                .filter(btn -> btn == ButtonType.OK)
                .ifPresent(b -> {
                    try {
                        fachadaUsuario.cambiarEstadoUsuario(usuario.getId(), false);
                    } catch (Exception ex) {
                        System.getLogger(InventarioController.class.getName())
                                .log(System.Logger.Level.ERROR, (String) null, ex);
                    }
                    cargarDatos();
                });
    }

    private void cargarUsuarioParaEditar(UsuarioDTO usuario) {
        modoEdicion = true;
        idUsuarioEditando = usuario.getId();
        version = usuario.getVersion();

        txtNombre.setText(usuario.getNombre());
        txtNomina.setText(usuario.getNoNomina());

        EmpresaDTO empresa = fachadaOrganizacion.buscarEmpresaPorPuesto(usuario.getIdPuesto());
        cbxEmpresa.getItems().stream()
                .filter(e -> e.getId().equals(empresa.getId()))
                .findFirst()
                .ifPresent(e -> cbxEmpresa.getSelectionModel().select(e));

        cbxEmpresa.getSelectionModel().selectFirst();
        onAccionCbxEmpresa();

        cbxPuesto.getItems().stream()
                .filter(p -> p.getId().equals(usuario.getIdPuesto()))
                .findFirst()
                .ifPresent(p -> cbxPuesto.getSelectionModel().select(p));

        // Al cargar para editar, los campos tienen datos: limpiar errores
        ValidacionUtil.resetTodos(txtNombre, txtNomina, cbxPuesto);
        ValidacionUtil.ocultarLabel(errNombre);
        ValidacionUtil.ocultarLabel(errNomina);
        ValidacionUtil.ocultarLabel(errPuesto);
    }
}
