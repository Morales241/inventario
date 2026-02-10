/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.inventariofrontfx.inventario;

import com.mycompany.inventariofrontfx.BaseController;
import com.mycompany.inventariofrontfx.DashBoardController;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author tacot
 */
public class InfoEspecificaEscritorioController implements Initializable, BaseController {

    private DashBoardController dbc;
    
    @FXML
    private TextField txtNombreEquipo;
    @FXML
    private TextField txtCuentaEquipo;
    @FXML
    private DatePicker fechaGarantia;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public TextField getTxtNombreEquipo() {
        return txtNombreEquipo;
    }

    public void setTxtNombreEquipo(TextField txtNombreEquipo) {
        this.txtNombreEquipo = txtNombreEquipo;
    }

    public TextField getTxtCuentaEquipo() {
        return txtCuentaEquipo;
    }

    public void setTxtCuentaEquipo(TextField txtCuentaEquipo) {
        this.txtCuentaEquipo = txtCuentaEquipo;
    }

    public DatePicker getFechaGarantia() {
        return fechaGarantia;
    }

    public void setFechaGarantia(DatePicker fechaGarantia) {
        this.fechaGarantia = fechaGarantia;
    }

    @Override
    public void setDashBoard(DashBoardController dbc) {
        this.dbc = dbc;
    }
}
