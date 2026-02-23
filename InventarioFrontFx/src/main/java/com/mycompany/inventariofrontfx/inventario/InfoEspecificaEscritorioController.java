package com.mycompany.inventariofrontfx.inventario;

import Dtos.EquipoEscritorioDTO;
import interfaces.BaseController;
import com.mycompany.inventariofrontfx.menu.MenuController;
import com.mycompany.inventariofrontfx.IValidaciones;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tacot
 */
public class InfoEspecificaEscritorioController implements Initializable, BaseController, IValidaciones<EquipoEscritorioDTO>{

    private MenuController dbc;
    
    @FXML
    private TextField txtNombreEquipo;
    @FXML
    private TextField txtCuentaEquipo;
    @FXML
    private DatePicker fechaGarantia;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
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
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
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
    public EquipoEscritorioDTO getDatosEntidad() {
        EquipoEscritorioDTO equipoEscritorio = new EquipoEscritorioDTO();
        equipoEscritorio.setNombreEquipo(txtNombreEquipo.getText());
        equipoEscritorio.setCuenta(txtCuentaEquipo.getText());
        equipoEscritorio.setFinalGarantia(fechaGarantia.getValue());
        
        return equipoEscritorio;
    }
    
    @Override
    public boolean validarFormulario() {

        if (txtNombreEquipo.getText().trim().isEmpty()) {
            mostrarAdvertencia("El Nombre del equipo es obligatorio.");
            txtNombreEquipo.requestFocus();
            return false;
        }
        
        if (txtCuentaEquipo.getText().trim().isEmpty()) {
            mostrarAdvertencia("La cuenta de equipo es obligatoria.");
            txtCuentaEquipo.requestFocus();
            return false;
        }
        
        return true;
    }
}
