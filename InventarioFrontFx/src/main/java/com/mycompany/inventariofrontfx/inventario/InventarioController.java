/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.inventariofrontfx.inventario;

import com.mycompany.inventariofrontfx.BaseController;
import com.mycompany.inventariofrontfx.DashBoardController;
import java.net.URL;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author tacot
 */
public class InventarioController implements Initializable, BaseController {

    private DashBoardController dbc;

    @FXML
    private Button btnAgregar;
    @FXML
    private ComboBox<String> cbxTipo;
    @FXML
    private ComboBox<String> cbxCondicion;
    @FXML
    private ComboBox<String> cbxStatus;
    @FXML
    private TextField txtFiltro;
    @FXML
    private TableView tablaEquipos;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        cargarDatosComboBox();

        cbxTipo.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
//                logica
            }
        });

        cbxCondicion.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
//                logica
            }
        });

        cbxStatus.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
//                logica
            }
        });

        btnAgregar.setOnAction(evento -> {
            actionListenerBtnAgregar(evento);
        });

    }

    private void actionListenerBtnAgregar(Event evento) {
        if (this.dbc != null) {
            this.dbc.cambiarDePantalla("inventario/FormInventario.fxml");
        } else {
            System.out.println("ERROR CRÍTICO: 'dbc' es null. Revisa el DashBoardController.");
        }
    }

    private void cargarDatosComboBox() {
        List<String> itemsCbxTipos = asList("Todos los tipos", "Movil", "Laptop", "Escritorio", "Otros");

        List<String> itemsCbxCondicion = asList("Todas las condiciones", "Nuevo", "Bueno", "Regular", "Malo");

        List<String> itemsCbxStatus = asList("Todos los estados", "Disponible", "Asignado", "En mantenimiento", "Baja");

        cbxTipo.setItems(FXCollections.observableArrayList(itemsCbxTipos));
        cbxCondicion.setItems(FXCollections.observableArrayList(itemsCbxCondicion));
        cbxStatus.setItems(FXCollections.observableArrayList(itemsCbxStatus));

        cbxTipo.getSelectionModel().selectFirst();
        cbxCondicion.getSelectionModel().selectFirst();
        cbxStatus.getSelectionModel().selectFirst();
    }

    public Button getBtnAgregar() {
        return btnAgregar;
    }

    public void setBtnAgregar(Button btnAgregar) {
        this.btnAgregar = btnAgregar;
    }

    public ComboBox<String> getCbxTipo() {
        return cbxTipo;
    }

    public void setCbxTipo(ComboBox<String> cbxTipo) {
        this.cbxTipo = cbxTipo;
    }

    public ComboBox<String> getCbxCondicion() {
        return cbxCondicion;
    }

    public void setCbxCondicion(ComboBox<String> cbxCondicion) {
        this.cbxCondicion = cbxCondicion;
    }

    public ComboBox<String> getCbxStatus() {
        return cbxStatus;
    }

    public void setCbxStatus(ComboBox<String> cbxStatus) {
        this.cbxStatus = cbxStatus;
    }

    public TextField getTxtFiltro() {
        return txtFiltro;
    }

    public void setTxtFiltro(TextField txtFiltro) {
        this.txtFiltro = txtFiltro;
    }

    public TableView getTablaEquipos() {
        return tablaEquipos;
    }

    public void setTablaEquipos(TableView tablaEquipos) {
        this.tablaEquipos = tablaEquipos;
    }

    @Override
    public void setDashBoard(DashBoardController dbc) {
        this.dbc = dbc;
    }
}
