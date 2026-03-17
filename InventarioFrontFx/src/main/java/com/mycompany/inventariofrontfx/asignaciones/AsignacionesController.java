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
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class AsignacionesController implements Initializable, BaseController {

    private MenuController dbc;
    private final IFachadaPrestamos fachadaPrestamos = FabricaFachadas.getFachadaPrestamos();
    private final IFachadaPersonas fachadapersonas = FabricaFachadas.getFachadaPersonas();
    private final IFachadaEquipos fachadaEquipos = FabricaFachadas.getFachadaEquipos();

    @FXML
    private TableView<AsignacionDTO> tablaAsignaciones;
    @FXML
    private TableColumn<AsignacionDTO, Long> colId;
    @FXML
    private TableColumn<AsignacionDTO, String> colEquipo;
    @FXML
    private TableColumn<AsignacionDTO, String> colUsuario;
    @FXML
    private TableColumn<AsignacionDTO, String> colFechaEntrega;
    @FXML
    private TableColumn<AsignacionDTO, String> colFechaDevolucion;
    @FXML
    private TableColumn<AsignacionDTO, String> colEstado;
    @FXML
    private TableColumn<AsignacionDTO, Void> colAcciones;
    @FXML
    private TextField txtFiltro;
    @FXML
    private Button btnNuevaAsignacion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        configurarAcciones();
        cargarDatos();

        txtFiltro.textProperty().addListener((obs, oldVal, newVal) -> {
            aplicarFiltro();
        });
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));

        colEquipo.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getIdentificadorEquipo() + " (" + data.getValue().getIdEquipo() + ")"));

        colUsuario.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getNombreUsuario()));

        colFechaEntrega.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getFechaEntrega().toString()));

        colFechaDevolucion.setCellValueFactory(data -> {
            if (data.getValue().getFechaDevolucion() != null) {
                return new SimpleStringProperty(data.getValue().getFechaDevolucion().toString());
            }
            return new SimpleStringProperty("");
        });

        colEstado.setCellValueFactory(data -> {
            if (data.getValue().getFechaDevolucion() == null) {
                return new SimpleStringProperty("Activa");
            } else {
                return new SimpleStringProperty("Devuelta");
            }
        });
    }

    // En la clase AsignacionesController, dentro del método configurarAcciones()
    private void configurarAcciones() {
        colAcciones.setCellFactory(col -> new TableCell<>() {
            private final FontIcon viewIcon = new FontIcon("fas-file-pdf");
            private final FontIcon returnIcon = new FontIcon("fas-undo-alt");
            private final Button btnVerResponsiva = new Button("", viewIcon);
            private final Button btnDevolver = new Button("", returnIcon);
            private final HBox container = new HBox(8, btnVerResponsiva, btnDevolver);

            {
                viewIcon.setIconSize(16);
                returnIcon.setIconSize(16);

                btnVerResponsiva.getStyleClass().add("btn-ver");
                btnDevolver.getStyleClass().add("btn-editar");

                btnVerResponsiva.setTooltip(new Tooltip("Ver responsiva"));
                btnDevolver.setTooltip(new Tooltip("Devolver equipo"));
                container.setAlignment(Pos.CENTER);

                btnVerResponsiva.setOnAction(e -> {
                    AsignacionDTO asignacion = getTableRow().getItem();
                    if (asignacion != null) {
                        verResponsiva(asignacion);
                    }
                });

                btnDevolver.setOnAction(e -> {
                    AsignacionDTO asignacion = getTableRow().getItem();
                    if (asignacion != null && asignacion.getFechaDevolucion() == null) {
                        confirmarDevolucion(asignacion);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    AsignacionDTO asignacion = getTableRow().getItem();
                    btnDevolver.setDisable(asignacion.getFechaDevolucion() != null);
                    setGraphic(container);
                }
            }
        });
    }

// Método para ver la responsiva de una asignación existente
    private void verResponsiva(AsignacionDTO asignacion) {
        try {
            // Obtener datos completos del usuario y equipo
            UsuarioDTO usuario = fachadapersonas.obtenerUsuario(asignacion.getIdUsuario());
            EquipoBaseDTO equipo = fachadaEquipos.obtenerEquipoPorId(asignacion.getIdEquipo());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ResponsivaAsignacion.fxml"));
            Parent vista = loader.load();

            ResponsivaAsignacionController controller = loader.getController();
            controller.setDashBoard(dbc);
            controller.setDatosAsignacion(usuario, equipo, asignacion.getFechaEntrega());

            dbc.getCenterContainer().setContent(vista);
            dbc.getCenterContainer().setVvalue(0);

        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al cargar responsiva");
        }
    }

    private void cargarDatos() {
        List<AsignacionDTO> asignaciones = fachadaPrestamos.obtenerTodasLasAsignacionesActivas();
        tablaAsignaciones.setItems(FXCollections.observableArrayList(asignaciones));
    }

    private void aplicarFiltro() {
        cargarDatosAsync();
    }

    private void cargarDatosAsync() {
        Task<List<AsignacionDTO>> task = new Task<>() {
            @Override
            protected List<AsignacionDTO> call() {
                String filtro = txtFiltro.getText();
                if (filtro == null || filtro.trim().isEmpty()) {
                    return fachadaPrestamos.obtenerTodasLasAsignacionesActivas();
                } else {
                    return fachadaPrestamos.buscarAsignaciones(filtro);
                }
            }
        };

        tablaAsignaciones.setDisable(true);

        task.setOnSucceeded(e -> {
            tablaAsignaciones.getItems().setAll(task.getValue());
            tablaAsignaciones.setDisable(false);
        });

        task.setOnFailed(e -> {
            tablaAsignaciones.setDisable(false);
            task.getException().printStackTrace();
            mostrarError("Error al cargar asignaciones");
        });

        new Thread(task).start();
    }

    private void confirmarDevolucion(AsignacionDTO asignacion) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar devolución");
        alert.setHeaderText("Devolver equipo");
        alert.setContentText("¿Confirmar devolución del equipo "
                + asignacion.getIdentificadorEquipo() + " asignado a " + asignacion.getNombreUsuario() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    fachadaPrestamos.devolverEquipo(asignacion.getId());
                    mostrarExito("Equipo devuelto correctamente");
                    cargarDatos();
                } catch (Exception e) {
                    mostrarError("Error al devolver: " + e.getMessage());
                }
            }
        });
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
            if (controller instanceof BaseController baseController) {
                baseController.setDashBoard(dbc);
            }

            dbc.getCenterContainer().setContent(vista);
            dbc.getCenterContainer().setVvalue(0);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("Error al cargar la pantalla");
        }
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }
}
