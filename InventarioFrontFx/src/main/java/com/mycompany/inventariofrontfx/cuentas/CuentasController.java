package com.mycompany.inventariofrontfx.cuentas;

import Dtos.CuentaSistemaDTO;
import Enums.RolCuenta;
import InterfacesFachada.IFachadaPersonas;
import Utilidades.ServicioSesion;
import Utilidades.SesionActual;
import com.mycompany.inventariofrontfx.menu.MenuController;
import fabricaFachadas.FabricaFachadas;
import interfaces.BaseController;
import java.net.URL;
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
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Controlador del módulo Cuentas del Sistema.
 */
public class CuentasController implements Initializable, BaseController {

    private MenuController dbc;
    private final IFachadaPersonas fachada = FabricaFachadas.getFachadaPersonas();

    @FXML
    private TextField txtFiltro;
    @FXML
    private TableView<CuentaSistemaDTO> tablaCuentas;
    @FXML
    private TableColumn<CuentaSistemaDTO, Long> colId;
    @FXML
    private TableColumn<CuentaSistemaDTO, String> colUsername;
    @FXML
    private TableColumn<CuentaSistemaDTO, String> colRol;
    @FXML
    private TableColumn<CuentaSistemaDTO, Void> colAcciones;

    @FXML
    private Label lblTituloForm;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField pfPassword;
    @FXML
    private Label lblPasswordHint;
    @FXML
    private ComboBox<RolCuenta> cbxRol;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;

    @FXML
    private Label errUsername;
    @FXML
    private Label errPassword;
    @FXML
    private Label errRol;

    private Long idEditando = null;
    private Long version = 0L;
    private boolean modoEdicion = false;

    private final PauseTransition debounce = new PauseTransition(Duration.millis(350));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        configurarAcciones();
        configurarInteraccionTabla();
        configurarFormulario();
        configurarFiltro();
        cargarCuentas();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().getId()));

        colUsername.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getUsername()));

        colRol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String rol, boolean empty) {
                super.updateItem(rol, empty);
                if (empty || rol == null) {
                    setGraphic(null);
                    return;
                }
                Label badge = new Label(rol);
                badge.getStyleClass().add(badgeClass(rol));
                badge.setStyle(badgeStyle(rol));
                HBox box = new HBox(badge);
                box.setAlignment(Pos.CENTER);
                setGraphic(box);
            }
        });
        colRol.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getRol()));
    }

    private void configurarAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final FontIcon editIcon = new FontIcon("fas-edit");
            private final FontIcon deleteIcon = new FontIcon("fas-trash");
            private final Button btnEditar = new Button("", editIcon);
            private final Button btnEliminar = new Button("", deleteIcon);
            private final HBox box = new HBox(8, btnEditar, btnEliminar);

            {
                editIcon.setIconSize(16);
                deleteIcon.setIconSize(16);
                btnEditar.getStyleClass().add("btn-editar");
                btnEliminar.getStyleClass().add("btn-eliminar");
                Tooltip.install(btnEditar, new Tooltip("Editar cuenta"));
                Tooltip.install(btnEliminar, new Tooltip("Eliminar cuenta"));
                box.setAlignment(Pos.CENTER);

                btnEditar.setOnAction(e -> {
                    CuentaSistemaDTO c = getTableRow().getItem();
                    if (c != null) {
                        cargarParaEditar(c);
                    }
                });
                btnEliminar.setOnAction(e -> {
                    CuentaSistemaDTO c = getTableRow().getItem();
                    if (c != null) {
                        confirmarEliminacion(c);
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
                CuentaSistemaDTO cuenta = getTableRow().getItem();
                btnEliminar.setDisable(esMiCuenta(cuenta));
                setGraphic(box);
            }
        });
    }

    private void configurarInteraccionTabla() {
        tablaCuentas.setRowFactory(tv -> {
            TableRow<CuentaSistemaDTO> row = new TableRow<>();

            // Menú contextual estilo Excel / Desktop
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editarItem = new MenuItem("Editar Cuenta");
            editarItem.setGraphic(new FontIcon("fas-edit"));
            editarItem.setOnAction(event -> cargarParaEditar(row.getItem()));

            MenuItem eliminarItem = new MenuItem("Eliminar Cuenta");
            eliminarItem.setGraphic(new FontIcon("fas-trash"));
            eliminarItem.setOnAction(event -> confirmarEliminacion(row.getItem()));

            contextMenu.getItems().addAll(editarItem, new SeparatorMenuItem(), eliminarItem);

            row.contextMenuProperty().bind(
                Bindings.when(row.emptyProperty())
                    .then((ContextMenu) null)
                    .otherwise(contextMenu)
            );

            // Doble clic para editar
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                    cargarParaEditar(row.getItem());
                }
            });

            // Si es la cuenta propia, no dejar eliminar desde el menú contextual tampoco
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                if (newItem != null) {
                    eliminarItem.setDisable(esMiCuenta(newItem));
                }
            });

            return row;
        });

        // Atajo teclado: ENTER para editar en la tabla
        tablaCuentas.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                CuentaSistemaDTO selected = tablaCuentas.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    cargarParaEditar(selected);
                }
            }
        });
    }

    private void cargarCuentas() {
        String filtro = txtFiltro.getText();

        Task<List<CuentaSistemaDTO>> task = new Task<>() {
            @Override
            protected List<CuentaSistemaDTO> call() {
                return filtro == null || filtro.isBlank()
                        ? fachada.listarCuentasSistema()
                        : fachada.buscarCuentasSistema(filtro);
            }
        };

        tablaCuentas.setDisable(true);
        task.setOnSucceeded(e -> {
            tablaCuentas.setItems(FXCollections.observableArrayList(task.getValue()));
            tablaCuentas.setDisable(false);
        });
        task.setOnFailed(e -> {
            tablaCuentas.setDisable(false);
            mostrarError("Error al cargar", task.getException() != null
                    ? task.getException().getMessage() : "Error desconocido");
        });
        new Thread(task).start();
    }

    private void configurarFiltro() {
        debounce.setOnFinished(e -> cargarCuentas());
        txtFiltro.textProperty().addListener((obs, old, val) -> debounce.playFromStart());
    }

    private void configurarFormulario() {
        cbxRol.setItems(FXCollections.observableArrayList(
                RolCuenta.ADMIN, RolCuenta.OPERARIO, RolCuenta.INVITADO
        ));

        txtUsername.textProperty().addListener((obs, old, v) -> ocultarLabel(errUsername));
        pfPassword.textProperty().addListener((obs, old, v) -> ocultarLabel(errPassword));
        cbxRol.valueProperty().addListener((obs, old, v) -> ocultarLabel(errRol));

        // UX: Guardar con ENTER desde los campos
        txtUsername.setOnKeyPressed(e -> { if(e.getCode() == KeyCode.ENTER) guardarCuenta(); });
        pfPassword.setOnKeyPressed(e -> { if(e.getCode() == KeyCode.ENTER) guardarCuenta(); });
        cbxRol.setOnKeyPressed(e -> { if(e.getCode() == KeyCode.ENTER) guardarCuenta(); });

        ocultarLabel(lblPasswordHint);
        modoNuevo();
    }

    @FXML
    public void modoNuevo() {
        modoEdicion = false;
        idEditando = null;
        version = 0L;
        lblTituloForm.setText("Nueva Cuenta");
        txtUsername.clear();
        txtUsername.setDisable(false);
        pfPassword.clear();
        pfPassword.setPromptText("Contraseña (mín. 4 caracteres)");
        if (!cbxRol.getItems().isEmpty()) {
            cbxRol.getSelectionModel().selectFirst();
        }
        ocultarLabel(errUsername);
        ocultarLabel(errPassword);
        ocultarLabel(errRol);
        ocultarLabel(lblPasswordHint);
    }

    private void cargarParaEditar(CuentaSistemaDTO cuenta) {
        modoEdicion = true;
        idEditando = cuenta.getId();
        version = cuenta.getVersion() != null ? cuenta.getVersion() : 0L;

        lblTituloForm.setText("Editar Cuenta");
        txtUsername.setText(cuenta.getUsername());
        txtUsername.setDisable(true);
        pfPassword.clear();
        pfPassword.setPromptText("Dejar vacío para no cambiar la contraseña");
        cbxRol.setValue(RolCuenta.valueOf(cuenta.getRol()));

        mostrarLabel(lblPasswordHint, "Dejar vacío para no cambiar la contraseña");
        ocultarLabel(errUsername);
        ocultarLabel(errPassword);
        ocultarLabel(errRol);
    }

    @FXML
    private void guardarCuenta() {
        if (!validarFormulario()) {
            return;
        }

        CuentaSistemaDTO dto = new CuentaSistemaDTO();

        if (modoEdicion) {
            dto.setId(idEditando);
            dto.setVersion(version);
            dto.setUsername(txtUsername.getText().trim());
            // Solo actualizar contraseña si se ingresó una nueva
            if (!pfPassword.getText().isBlank()) {
                dto.setPassword(pfPassword.getText());
            }
        } else {
            dto.setUsername(txtUsername.getText().trim());
            dto.setPassword(pfPassword.getText());
        }

        dto.setRol(cbxRol.getValue().name());

        Task<CuentaSistemaDTO> task = new Task<>() {
            @Override
            protected CuentaSistemaDTO call() {
                return fachada.guardarCuentaSistema(dto);
            }
        };

        task.setOnSucceeded(e -> {
            mostrarExito(modoEdicion ? "Cuenta actualizada" : "Cuenta creada correctamente");
            modoNuevo();
            cargarCuentas();
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            String msg = ex != null && ex.getMessage() != null
                    ? ex.getMessage()
                    : "Error al guardar la cuenta";
            mostrarError("No se pudo guardar", msg);
        });

        new Thread(task).start();
    }

    @FXML
    private void cancelarFormulario() {
        modoNuevo();
        tablaCuentas.getSelectionModel().clearSelection();
    }

    private void confirmarEliminacion(CuentaSistemaDTO cuenta) {
        if (esMiCuenta(cuenta)) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setTitle("Operación no permitida");
            a.setHeaderText("No puedes eliminar tu propia cuenta");
            a.setContentText("Cierra sesión primero o pide a otro administrador que realice esta acción.");
            a.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmar eliminación");
        confirm.setHeaderText("¿Eliminar cuenta \"" + cuenta.getUsername() + "\"?");
        confirm.setContentText("Esta acción no se puede deshacer.");

        confirm.showAndWait().filter(btn -> btn == ButtonType.OK).ifPresent(b -> {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    fachada.eliminarCuentaSistema(cuenta.getId());
                    return null;
                }
            };
            task.setOnSucceeded(e -> {
                mostrarExito("Cuenta eliminada");
                cargarCuentas();
            });
            task.setOnFailed(e -> {
                String msg = task.getException() != null
                        ? task.getException().getMessage()
                        : "No se pudo eliminar la cuenta";
                mostrarError("Error al eliminar", msg);
            });
            new Thread(task).start();
        });
    }

    private boolean validarFormulario() {
        boolean ok = true;

        String user = txtUsername.getText() == null ? "" : txtUsername.getText().trim();
        if (!modoEdicion && user.isEmpty()) {
            mostrarLabel(errUsername, "El nombre de usuario es obligatorio.");
            ok = false;
        } else if (!modoEdicion && user.length() < 3) {
            mostrarLabel(errUsername, "El nombre de usuario debe tener al menos 3 caracteres.");
            ok = false;
        } else {
            ocultarLabel(errUsername);
        }

        String pass = pfPassword.getText();
        if (!modoEdicion && (pass == null || pass.isBlank())) {
            mostrarLabel(errPassword, "La contraseña es obligatoria.");
            ok = false;
        } else if (!modoEdicion && pass.length() < 4) {
            mostrarLabel(errPassword, "La contraseña debe tener al menos 4 caracteres.");
            ok = false;
        } else if (modoEdicion && !pass.isBlank() && pass.length() < 4) {
            mostrarLabel(errPassword, "La nueva contraseña debe tener al menos 4 caracteres.");
            ok = false;
        } else {
            ocultarLabel(errPassword);
        }

        if (cbxRol.getValue() == null) {
            mostrarLabel(errRol, "Debes seleccionar un rol.");
            ok = false;
        } else {
            ocultarLabel(errRol);
        }

        return ok;
    }

    /**
     * true si la cuenta dada corresponde al usuario actualmente logueado.
     */
    private boolean esMiCuenta(CuentaSistemaDTO cuenta) {
        CuentaSistemaDTO actual = ServicioSesion.getUsuario();
        return actual != null && actual.getId() != null
                && actual.getId().equals(cuenta.getId());
    }

    private String badgeStyle(String rol) {
        if (rol == null) {
            return "";
        }
        return switch (rol.toUpperCase()) {
            case "ADMIN" ->
                "-fx-background-color: #DBEAFE; -fx-text-fill: #1E40AF; "
                + "-fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold;";
            case "ALMACENISTA" ->
                "-fx-background-color: #D1FAE5; -fx-text-fill: #065F46; "
                + "-fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold;";
            case "INVITADO" ->
                "-fx-background-color: #F3F4F6; -fx-text-fill: #6B7280; "
                + "-fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold;";
            default ->
                "-fx-background-color: #FEF3C7; -fx-text-fill: #92400E; "
                + "-fx-background-radius: 20; -fx-padding: 3 10; -fx-font-size: 11px; -fx-font-weight: bold;";
        };
    }

    private String badgeClass(String rol) {
        return "";
    }

    private void mostrarLabel(Label lbl, String texto) {
        if (lbl == null) {
            return;
        }
        lbl.setText(texto);
        lbl.setVisible(true);
        lbl.setManaged(true);
    }

    private void ocultarLabel(Label lbl) {
        if (lbl == null) {
            return;
        }
        lbl.setVisible(false);
        lbl.setManaged(false);
    }

    private void mostrarExito(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Éxito");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @Override
    public void mostrarError(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(titulo);
        a.setContentText(msg);
        a.showAndWait();
    }

    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }
}
