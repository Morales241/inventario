/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.inventariofrontfx.inventario;

import Dtos.EquipoBaseDTO;
import Enums.CondicionFisica;
import Enums.EstadoEquipo;
import Enums.TipoEquipo;
import InterfacesFachada.IFachadaEquipos;
import com.mycompany.inventariofrontfx.BaseController;
import com.mycompany.inventariofrontfx.DashBoardController;
import fabricaFachadas.FabricaFachadas;
import java.io.IOException;
import java.net.URL;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tacot
 */
public class InventarioController implements Initializable, BaseController {

    private DashBoardController dbc;
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
    private TextField txtFiltro;
    @FXML
    private ComboBox<TipoEquipo> cbxTipo;
    @FXML
    private ComboBox<CondicionFisica> cbxCondicion;
    @FXML
    private ComboBox<EstadoEquipo> cbxEstado;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarColumnas();
        configurarAcciones();
        cargarDatos();

    }
    
    private void llenarComboBox(){}

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

    private void configurarAcciones() {

        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final Button btnEditar = new Button("✏");
            private final Button btnEliminar = new Button("🗑");
            private final HBox container
                    = new HBox(8, btnEditar, btnEliminar);

            {
                btnEditar.setOnAction(e -> {
                    EquipoBaseDTO equipo = getTableView().getItems().get(getIndex());
                    editarEquipo(equipo);
                });

                btnEliminar.setOnAction(e -> {
                    EquipoBaseDTO equipo
                            = getTableView().getItems().get(getIndex());
                    eliminarEquipo(equipo);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : container);
            }
        });
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

    private void editarEquipo(EquipoBaseDTO equipo) {

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("FormInventario.fxml"));

            Parent view = loader.load();

            FormInventarioController controller
                    = loader.getController();

            controller.cargarEquipoParaEditar(equipo);

            Stage stage = new Stage();
            stage.setScene(new Scene(view));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setDashBoard(DashBoardController dbc) {
        this.dbc = dbc;
    }
}
