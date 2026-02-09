/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.inventariofrontfx;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

/**
 * FXML Controller class
 *
 * @author JMorales
 */
public class DashBoardController implements Initializable {

    @FXML
    private ToggleGroup menu;

    @FXML
    private ScrollPane centerContainer;
    
    @FXML private ToggleButton btnDashBoard;
    
    @FXML private ToggleButton btnInventario;
    
    @FXML private ToggleButton btnColaboradores;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Escuchamos los cambios en el ToggleGroup
        menu.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ToggleButton selectedBtn = (ToggleButton) newValue;
                String menuText = selectedBtn.getText();

                manejarNavegacion(menuText);
            } else {
                // Esto evita que el botón se deseleccione si haces clic de nuevo
                oldValue.setSelected(true);
            }
        });
    }

    private void manejarNavegacion(String opcion) {
        System.out.println("Navegando a: " + opcion);

        switch (opcion) {
            case "DashBoard":
                // logic para cargar vista dashboard
                break;
            case "Inventario":
                // logic para cargar vista inventario
                break;
            case "Usuarios":
                // logic para submenú usuarios
                break;
            default:
                break;
        }
    }
}
