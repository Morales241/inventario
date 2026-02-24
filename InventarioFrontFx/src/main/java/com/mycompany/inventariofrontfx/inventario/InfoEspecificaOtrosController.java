/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.inventariofrontfx.inventario;

import Dtos.EquipoBaseDTO;
import Dtos.OtroEquipoDTO;
import interfaces.BaseController;
import com.mycompany.inventariofrontfx.menu.MenuController;
import interfaces.IValidaciones;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tacot
 */
public class InfoEspecificaOtrosController implements Initializable, BaseController, IValidaciones<OtroEquipoDTO> {

    private MenuController dbc;
    
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
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }

    @Override
    public boolean validarFormulario() {
        if (txtTCE.getText().trim().isEmpty()) {
            mostrarAdvertencia("El primer titulo para campo extra es obligatorio.");
            txtTCE.requestFocus();
            return false;
        }
        
        if (txtCCE.getText().trim().isEmpty()) {
            mostrarAdvertencia("El primer contenido para el campo extra es obligatorio.");
            txtCCE.requestFocus();
            return false;
        }
        
        return true;
        
    }

    @Override
    public OtroEquipoDTO getDatosEntidad() {
        OtroEquipoDTO otroEquipoDTO = new OtroEquipoDTO();
        
        String tce2 = txtTCE2 == null ? "": txtTCE2.getText();
        
        String cce2 = txtCCE2 == null ? "": txtCCE2.getText();
        
        otroEquipoDTO.setTituloCampoExtra(txtTCE.getText());
        otroEquipoDTO.setTituloCampoExtra2(txtTCE2.getText());
        otroEquipoDTO.setContenidoCampoExtra(tce2);
        otroEquipoDTO.setContenidoCampoExtra2(cce2);

        return otroEquipoDTO;
    }
    
    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Faltan Datos");
        alert.setHeaderText(mensaje);
        System.out.println(mensaje);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("imagenes/logo.png"));
        alert.showAndWait();
    }

    @Override
    public <T extends EquipoBaseDTO>void cargarEquipoParaEditar(T dto) {
        OtroEquipoDTO Odto = (OtroEquipoDTO) dto;
        
        txtCCE.setText(Odto.getContenidoCampoExtra());
        txtCCE2.setText(Odto.getContenidoCampoExtra2());
        txtTCE.setText(Odto.getTituloCampoExtra());
        txtTCE2.setText(Odto.getTituloCampoExtra2());
    }
    
}
