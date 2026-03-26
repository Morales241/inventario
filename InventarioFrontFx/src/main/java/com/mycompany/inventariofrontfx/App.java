package com.mycompany.inventariofrontfx;

import conexion.Conexion;
import com.mycompany.inventariofrontfx.util.GlobalExceptionHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.image.Image;
import javafx.stage.StageStyle;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    static {
        // Registrar manejador global de excepciones
        Thread.setDefaultUncaughtExceptionHandler((Thread.UncaughtExceptionHandler) new GlobalExceptionHandler());
    }

    @Override
    public void start(Stage stage) throws IOException {
        Conexion.getEntityManager();
        scene = new Scene(loadFXML("LogIn"), 640, 480);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.setTitle("");
        stage.getIcons().add(
                new Image(getClass().getResourceAsStream("/imagenes/logo.png"))
        );
        stage.show();

    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public void main(String[] args) {
        launch();
    }

}
