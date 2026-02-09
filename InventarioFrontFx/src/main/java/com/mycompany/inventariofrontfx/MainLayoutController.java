/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.inventariofrontfx;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author JMorales
 */
public class MainLayoutController implements Initializable {

    @FXML
    private BorderPane mainContainer;

    @FXML
    public void initialize() {
        // Al abrir la app, cargamos el Dashboard por defecto
        mostrarDashboard();
    }

    @FXML
    public void mostrarDashboard() {
        cargarVista("DashboardView");
    }

    @FXML
    public void mostrarInventario() {
        // Aquí cargas el archivo que hicimos en la respuesta anterior
        cargarVista("InventarioView"); 
    }

    // MÉTODO REUTILIZABLE PARA CAMBIAR PANTALLAS
    private void cargarVista(String nombreFXML) {
        try {
            // Carga el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(nombreFXML + ".fxml"));
            Parent vista = loader.load();
            
            // ¡LA MAGIA! Reemplaza el centro del BorderPane
            mainContainer.setCenter(vista);
            
        } catch (IOException ex) {
//            Logger.getLogger(MainLayoutController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
}
