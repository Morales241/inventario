/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.inventariofrontfx.colaboradores;

import Dtos.TrabajadorDTO;
import InterfacesFachada.IFachadaPersonas;
import fabricaFachadas.FabricaFachadas;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tacot
 */
public class FormColaboradoresController implements Initializable {

    @FXML private TextField txtNombre;
    @FXML private TextField txtNomina;
    @FXML private ComboBox<String> cbxPuesto;

    @FXML private Button btnCancelar;
    @FXML private Button btnAgregar;

    private final IFachadaPersonas fachadaColaborador =
            FabricaFachadas.getFachadaPersonas();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarPuestos();
        validarNominaNumerica();
    }

    private void cargarPuestos() {
        cbxPuesto.getItems().addAll(
                "Desarrollador",
                "Soporte",
                "Administrador",
                "Consultor"
        );
    }

    private void validarNominaNumerica() {

        txtNomina.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtNomina.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    @FXML
    private void guardarDatos() throws Exception {

        if (!validarCampos()) return;

        TrabajadorDTO dto = new TrabajadorDTO();
        dto.setNombre(txtNombre.getText());
        dto.setNoNomina(txtNomina.getText());
        dto.setNombrePuesto(cbxPuesto.getValue());

        fachadaColaborador.guardarTrabajador(dto);

        cerrarVentana();
    }

    private boolean validarCampos() {

        if (txtNombre.getText().isEmpty()) return false;
        if (txtNomina.getText().isEmpty()) return false;
        if (cbxPuesto.getValue() == null) return false;

        return true;
    }

    @FXML
    private void btnCancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}