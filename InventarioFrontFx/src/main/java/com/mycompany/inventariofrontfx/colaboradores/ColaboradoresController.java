package com.mycompany.inventariofrontfx.colaboradores;

import Dtos.UsuarioDTO;
import InterfacesFachada.IFachadaPersonas;
import fabricaFachadas.FabricaFachadas;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tacot
 */
public class ColaboradoresController implements Initializable {

    @FXML private TextField txtFiltro;
    @FXML private TableView<UsuarioDTO> tablaColaboradores;
    @FXML private TableColumn<UsuarioDTO, Long> colId;
    @FXML private TableColumn<UsuarioDTO, String> colNombre;
    @FXML private TableColumn<UsuarioDTO, String> colPuesto;
    @FXML private TableColumn<UsuarioDTO, Integer> colEquiposAsignados;

    private ObservableList<UsuarioDTO> listaColaboradores;

    private final IFachadaPersonas fachadaColaborador =
            FabricaFachadas.getFachadaPersonas();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarColumnas();
        configurarFiltroReactivo();
        cargarDatos();
    }

    private void configurarColumnas() {

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPuesto.setCellValueFactory(new PropertyValueFactory<>("puesto"));
        colEquiposAsignados.setCellValueFactory(
                new PropertyValueFactory<>("totalEquiposAsignados"));
    }

    private void cargarDatos() {
        listaColaboradores = FXCollections.observableArrayList(
                fachadaColaborador.buscarUsuarios(null)
        );
        tablaColaboradores.setItems(listaColaboradores);
    }

    private void configurarFiltroReactivo() {
        txtFiltro.textProperty().addListener((obs, oldValue, newValue) -> {
            aplicarFiltro(newValue);
        });
    }

    private void aplicarFiltro(String texto) {

        List<UsuarioDTO> resultado =
                fachadaColaborador.buscarUsuarios(texto);

        tablaColaboradores.setItems(
                FXCollections.observableArrayList(resultado)
        );
    }

    @FXML
    private void agregarEquipo() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("FormColaboradores.fxml")
        );

        Parent root = loader.load();

        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Nuevo Colaborador");
        stage.showAndWait();

        cargarDatos();
    }
}
