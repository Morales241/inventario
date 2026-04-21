package com.mycompany.inventariofrontfx;

import Dtos.CuentaSistemaDTO;
import InterfacesFachada.IFachadaPersonas;
import Utilidades.ServicioSesion;
import Utilidades.SesionActual;
import com.mycompany.inventariofrontfx.menu.MenuController;
import fabricaFachadas.FabricaFachadas;
import interfaces.BaseController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Controlador del Login.
 */
public class LogInController implements Initializable, BaseController {

    private MenuController dbc;
    private Stage stage;

    private final IFachadaPersonas fachada = FabricaFachadas.getFachadaPersonas();

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword;
    @FXML
    private Button btnIngresar;
    @FXML
    private Button btnSalir;
    @FXML
    private Label lblError;
    @FXML
    private ProgressIndicator progressLogin;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ocultarError();
        ocultarSpinner();

        txtUsuario.setOnAction(e -> txtPassword.requestFocus());
        txtPassword.setOnAction(e -> procesarLogin(e));

        txtUsuario.textProperty().addListener((obs, old, v) -> ocultarError());
        txtPassword.textProperty().addListener((obs, old, v) -> ocultarError());
    }

    @FXML
    public void iniciarSesion(ActionEvent event) {
        this.stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        procesarLogin(event);
    }

    private void procesarLogin(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText();

        if (usuario.isEmpty() || password.isEmpty()) {
            mostrarError("Ingresa tu usuario y contraseña.");
            return;
        }

        setFormularioHabilitado(false);
        mostrarSpinner();

        Task<CuentaSistemaDTO> task = new Task<>() {
            @Override
            protected CuentaSistemaDTO call() {
                return fachada.login(usuario, password);
            }
        };

        task.setOnSucceeded(e -> {
            CuentaSistemaDTO cuenta = task.getValue();
            setFormularioHabilitado(true);
            ocultarSpinner();

            if (cuenta == null) {
                txtPassword.clear();
                txtPassword.requestFocus();
                mostrarError("Usuario o contraseña incorrectos.");
                return;
            }

            // Guardar la sesión
            ServicioSesion.setUsuario(cuenta);
            // Navegar al menú
            if (stage == null && event != null) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            }
            navegarAlMenu();
        });

        task.setOnFailed(e -> {
            setFormularioHabilitado(true);
            ocultarSpinner();
            txtPassword.clear();
            txtPassword.requestFocus();

            Throwable ex = task.getException();
            String msg = (ex != null && ex.getMessage() != null)
                    ? ex.getMessage()
                    : "Usuario o contraseña incorrectos.";
            mostrarError(msg);
        });

        new Thread(task).start();
    }

    private void navegarAlMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("menu/Menu.fxml"));
            Parent vista = loader.load();

            MenuController menuController = loader.getController();
            // Inyectar datos de sesión en el menú
            menuController.aplicarSesion(ServicioSesion.getUsuario());

            if (stage.getScene() == null) {
                stage.setScene(new Scene(vista));
            } else {
                stage.getScene().setRoot(vista);
            }
            stage.setResizable(true);
            if (!stage.isMaximized()) {
                stage.setMaximized(true);
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            mostrarError("Error al cargar la aplicación.");
        }
    }

    @FXML
    public void cerrar() {
        System.exit(0);
    }

    private void mostrarError(String mensaje) {
        if (lblError != null) {
            lblError.setText(mensaje);
            System.out.println(mensaje);
            lblError.setVisible(true);
            lblError.setManaged(true);
        }
    }

    private void ocultarError() {
        if (lblError != null) {
            lblError.setVisible(false);
            lblError.setManaged(false);
        }
    }

    private void mostrarSpinner() {
        if (progressLogin != null) {
            progressLogin.setVisible(true);
            progressLogin.setManaged(true);
        }
    }

    private void ocultarSpinner() {
        if (progressLogin != null) {
            progressLogin.setVisible(false);
            progressLogin.setManaged(false);
        }
    }

    private void setFormularioHabilitado(boolean habilitado) {
        txtUsuario.setDisable(!habilitado);
        txtPassword.setDisable(!habilitado);
        if (btnIngresar != null) {
            btnIngresar.setDisable(!habilitado);
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
