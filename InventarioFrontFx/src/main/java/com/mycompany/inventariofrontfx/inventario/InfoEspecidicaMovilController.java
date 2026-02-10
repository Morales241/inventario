/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.inventariofrontfx.inventario;

import Dtos.EquipoEscritorioDTO;
import Dtos.MovilDTO;
import com.mycompany.inventariofrontfx.BaseController;
import com.mycompany.inventariofrontfx.DashBoardController;
import com.mycompany.inventariofrontfx.IValidaciones;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tacot
 */
public class InfoEspecidicaMovilController implements Initializable, BaseController, IValidaciones<MovilDTO> {

    private DashBoardController dbc;
    
    @FXML
    private RadioButton rbtCargador;
    @FXML
    private RadioButton rbtFunda;
    @FXML
    private RadioButton rbtManosLibres;
    @FXML
    private TextField phoneField;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        int maxLength = 10;
        phoneField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0," + maxLength + "}")) {
                return change;
            }
            return null;
        }));
    }

    public RadioButton getRbtCargador() {
        return rbtCargador;
    }

    public void setRbtCargador(RadioButton rbtCargador) {
        this.rbtCargador = rbtCargador;
    }

    public RadioButton getRbtFunda() {
        return rbtFunda;
    }

    public void setRbtFunda(RadioButton rbtFunda) {
        this.rbtFunda = rbtFunda;
    }

    public RadioButton getRbtManosLibres() {
        return rbtManosLibres;
    }

    public void setRbtManosLibres(RadioButton rbtManosLibres) {
        this.rbtManosLibres = rbtManosLibres;
    }

    public TextField getPhoneField() {
        return phoneField;
    }

    public void setPhoneField(TextField phoneField) {
        this.phoneField = phoneField;
    }

    @Override
    public void setDashBoard(DashBoardController dbc) {
        this.dbc = dbc;
    }

    @Override
    public boolean validarFormulario() {
        if (phoneField.getText().trim().isEmpty()) {
            mostrarAdvertencia("El Numero de telefono es obligatorio.");
            phoneField.requestFocus();
            return false;
        }
        
        return true;
    }

    @Override
    public MovilDTO getDatosEntidad() {
        MovilDTO movil = new MovilDTO();

        Boolean cargador = false;
        Boolean manosLibres = false;
        Boolean funda = false;

        cargador = rbtCargador.isSelected();

        funda = rbtFunda.isSelected();
        
        manosLibres = rbtManosLibres.isSelected();

        movil.setCargador(cargador);
        movil.setFunda(funda);
        movil.setManosLibres(manosLibres);
        movil.setNumCelular(phoneField.getText());

        return movil;
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
    
}
