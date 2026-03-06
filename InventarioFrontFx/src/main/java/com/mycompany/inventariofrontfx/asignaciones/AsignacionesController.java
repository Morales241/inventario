package com.mycompany.inventariofrontfx.asignaciones;

import Dtos.AsignacionDTO;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class AsignacionesController implements Initializable, BaseController {

    private MenuController dbc;
    private final IFachadaPrestamos fachadaPrestamos = FabricaFachadas.getFachadaPrestamos();

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
    private TableColumn<AsignacionDTO, String> colEstado; // "Activa" o "Devuelta"

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarColumnas();
        cargarAsignaciones();
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getId()));
        colEquipo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getIdentificadorEquipo()));
        colUsuario.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreUsuario()));
        colFechaEntrega.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getFechaEntrega().toString()));
        
        colFechaDevolucion.setCellValueFactory(data
                -> new SimpleStringProperty(data.getValue().getFechaDevolucion().toString()));
        
        colEstado.setCellValueFactory(data -> {
            if (data.getValue().getFechaDevolucion() == null) {
                return new SimpleStringProperty("Activa");
            } else {
                return new SimpleStringProperty("Devuelta");
            }
        });
    }

    private void cargarAsignaciones() {
        List<AsignacionDTO> asignaciones = fachadaPrestamos.obtenerEquiposDeUsuarios(1L); 
        ObservableList<AsignacionDTO> data = FXCollections.observableArrayList(asignaciones);
        tablaAsignaciones.setItems(data);
    }

    @FXML
    private void irANuevaAsignacion() {
        // Este método se llamará desde un botón "Nueva Asignación"
        // Aquí se cambiará a la vista FormAsignacion.fxml
        cambiarPantalla("FormAsignacion.fxml");
    }

    private void cambiarPantalla(String rutaFXML) {
        // Similar a como se hace en InventarioController
        try {
            dbc.cambiarDePantalla("/com/mycompany/inventariofrontfx/asignaciones/" + rutaFXML);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }
}