package com.mycompany.inventariofrontfx;

import com.mycompany.inventariofrontfx.menu.MenuController;
import interfaces.BaseController;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author JesusM
 */
public class LogInController implements Initializable, BaseController {

    private MenuController dbc;

    private Stage stage;
    
    
    @FXML
    private TextField txtPassword;
    @FXML
    private TextField txtUsuario;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dbc = new MenuController();
    }

    @FXML
    public void cerrar() {
        System.exit(0);
    }

    @FXML
    public void iniciarSesion(ActionEvent event) {
        this.stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        
        cambiarDePantalla("menu/Menu.fxml");
    }

    public void cambiarDePantalla(String rutaFXML) {
        try {
            if (rutaFXML != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
                Parent vista = loader.load();

                Object controller = loader.getController();
                if (controller instanceof BaseController baseController) {
                    baseController.setDashBoard(dbc);
                }

                Scene nuevaEscena = new Scene(vista);
                stage.setScene(nuevaEscena);
                
                stage.setResizable(true);
                
                stage.setMaximized(false);
                stage.setMaximized(true);
                
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }
}
