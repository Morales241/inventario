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
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author tacot
 */
public class InfoEspecificaOtrosController implements Initializable, BaseController {

    private DashBoardController dbc;
    
    @FXML
    private TextField txtTCE;
    @FXML
    private TextField txtCCE;
    @FXML
    private TextField txtTCE2;
    @FXML
    private TextField txtCCE2;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    public TextField getTxtTCE() {
        return txtTCE;
    }

    public void setTxtTCE(TextField txtTCE) {
        this.txtTCE = txtTCE;
    }

    public TextField getTxtCCE() {
        return txtCCE;
    }

    public void setTxtCCE(TextField txtCCE) {
        this.txtCCE = txtCCE;
    }

    public TextField getTxtTCE2() {
        return txtTCE2;
    }

    public void setTxtTCE2(TextField txtTCE2) {
        this.txtTCE2 = txtTCE2;
    }

    public TextField getTxtCCE2() {
        return txtCCE2;
    }

    public void setTxtCCE2(TextField txtCCE2) {
        this.txtCCE2 = txtCCE2;
    }

    @Override
    public void setDashBoard(DashBoardController dbc) {
        this.dbc = dbc;
    }
}
