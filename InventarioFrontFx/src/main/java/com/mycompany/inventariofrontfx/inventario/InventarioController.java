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
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * FXML Controller class
 *
 * @author tacot
 */
public class InventarioController implements Initializable, ControllerInventario {

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
    private TableColumn<EquipoBaseDTO, String> colMarca;
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
    private ComboBox<TipoEquipo> cbxTipo;
    @FXML
    private ComboBox<CondicionFisica> cbxCondicion;
    @FXML
    private ComboBox<EstadoEquipo> cbxEstado;

    private PauseTransition pause
            = new PauseTransition(Duration.millis(400));

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        configurarColumnas();
        configurarAcciones();
        cargarDatos();
        llenarComboBox();

        txtFiltro.textProperty().addListener((obs, oldVal, newVal) -> {

            pause.setOnFinished(e -> aplicarFiltro());
            pause.playFromStart();
        });

        configurarFiltros();
    }

    private void llenarComboBox() {
        cbxTipo.getItems().setAll(TipoEquipo.values());
        cbxCondicion.getItems().setAll(CondicionFisica.values());
        cbxEstado.getItems().setAll(EstadoEquipo.values());
    }

    private void configurarColumnas() {

        colId.setCellValueFactory(data
                -> new SimpleObjectProperty<>(data.getValue().getIdEquipo()));

        colGry.setCellValueFactory(data
                -> new SimpleObjectProperty<>(data.getValue().getGry()));

        colTipo.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getTipo()));

        colMarca.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getNombreModelo()));

        colModelo.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getFechaCompra().toString()));

        colCondicion.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getCondicion()));

        colEstado.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getEstado()));
    }

    private void cargarDatos() {

        List<EquipoBaseDTO> lista
                = fachadaEquipos.buscarEquipos(null, null, null);

        tablaEquipos.getItems().setAll(lista);
    }

    private void eliminarEquipo(EquipoBaseDTO equipo) {

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Eliminar equipo");
        confirm.setContentText("¿Seguro que desea eliminar este equipo?");

        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.OK) {
                try {
                    fachadaEquipos.eliminarEquipo(equipo.getIdEquipo());
                } catch (Exception ex) {
                    System.getLogger(InventarioController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                }
                cargarDatos();
            }
        });
    }

    private void configurarAcciones() {

        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final FontIcon editIcon
                    = new FontIcon("fas-edit");

            private final FontIcon deleteIcon
                    = new FontIcon("fas-trash");

            private final Button btnEditar
                    = new Button("", editIcon);

            private final Button btnEliminar
                    = new Button("", deleteIcon);

            private final HBox container
                    = new HBox(10, btnEditar, btnEliminar);

            {
                editIcon.setIconSize(16);
                deleteIcon.setIconSize(16);

                btnEditar.getStyleClass().add("btn-icon");
                btnEliminar.getStyleClass().add("btn-icon");

                Tooltip.install(btnEditar,
                        new Tooltip("Editar equipo"));

                Tooltip.install(btnEliminar,
                        new Tooltip("Eliminar equipo"));

                btnEditar.setOnAction(e -> {
                    EquipoBaseDTO equipo
                            = getTableView().getItems().get(getIndex());
                    editarEquipo(equipo);
                });

                btnEliminar.setOnAction(e -> {
                    EquipoBaseDTO equipo
                            = getTableView().getItems().get(getIndex());
                    confirmarEliminacion(equipo);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {

                    EquipoBaseDTO equipo
                            = getTableView().getItems().get(getIndex());

                    btnEliminar.setDisable(
                            "ASIGNADO".equalsIgnoreCase(
                                    equipo.getEstado())
                    );

                    setGraphic(container);
                }
            }
        });
    }

    private void aplicarFiltro() {
        cargarDatosAsync();
    }

    private void configurarFiltros() {

        txtFiltro.textProperty().addListener((obs, oldVal, newVal)
                -> aplicarFiltro());

        cbxTipo.valueProperty().addListener((obs, oldVal, newVal)
                -> aplicarFiltro());

        cbxCondicion.valueProperty().addListener((obs, oldVal, newVal)
                -> aplicarFiltro());
        
        cbxEstado.valueProperty().addListener((obs, oldVal, newVal)
                -> aplicarFiltro());
    }

    private void cargarDatosAsync() {

        Task<List<EquipoBaseDTO>> task = new Task<>() {
            @Override
            protected List<EquipoBaseDTO> call() {

                return fachadaEquipos.buscarConFiltros(txtFiltro.getText(), cbxTipo.getSelectionModel().getSelectedItem(), 
                        cbxCondicion.getSelectionModel().getSelectedItem(), cbxEstado.getSelectionModel().getSelectedItem());
            }
        };

        tablaEquipos.setDisable(true);

        task.setOnSucceeded(e -> {
            tablaEquipos.getItems().setAll(task.getValue());
            tablaEquipos.setDisable(false);
        });

        task.setOnFailed(e -> {
            tablaEquipos.setDisable(false);
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void confirmarEliminacion(EquipoBaseDTO equipo) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Eliminar equipo");
        alert.setContentText(
                "¿Desea eliminar el equipo GRY "
                + equipo.getGry() + "?");

        alert.showAndWait()
                .filter(btn -> btn == ButtonType.OK)
                .ifPresent(b -> {

                    try {
                        fachadaEquipos.eliminarEquipo(equipo.getIdEquipo());
                    } catch (Exception ex) {
                        System.getLogger(InventarioController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                    }
                    cargarDatos();
                });
    }

    private void editarEquipo(EquipoBaseDTO equipo) {

        ControllerInventario controller
                = cambiarPantalla("FormInventario.fxml");

        if (controller instanceof FormInventarioController form) {
            form.cargarEquipoParaEditar(equipo);
        }
    }

    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }

    @Override
    public ControllerInventario cambiarPantalla(String rutaFXML) {
        try {
            if (rutaFXML != null) {

                FXMLLoader loader
                        = new FXMLLoader(getClass().getResource(rutaFXML));

                Parent vista = loader.load();
                Object controller = loader.getController();

                if (controller instanceof ControllerInventario ci) {
                    ci.setDashBoard(dbc);
                }

                dbc.getCenterContainer().setContent(vista);
                dbc.getCenterContainer().setVvalue(0);

                return (ControllerInventario) controller;
            }

        } catch (IOException e) {
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    @Override
    public void limpiarFormulario() {

    }

    @Override
    public <T extends EquipoBaseDTO> void cargarEquipoParaEditar(T equipo) {
        return;
    }

    @FXML
    private void agregarEquipo() {
        cambiarPantalla("FormInventario.fxml");
    }
}
