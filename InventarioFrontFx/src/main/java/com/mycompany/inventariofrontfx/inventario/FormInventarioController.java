package com.mycompany.inventariofrontfx.inventario;

import com.mycompany.inventariofrontfx.BaseController;
import com.mycompany.inventariofrontfx.DashBoardController;
import java.io.IOException;
import java.net.URL;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author tacot
 */
public class FormInventarioController implements Initializable, BaseController {

    private DashBoardController dbc;

    @FXML
    private TextField txtGry; //ya
    @FXML
    private TextField txtIdentificador;
    @FXML
    private TextField txtAlmacenamiento;
    @FXML
    private TextField txtRam;
    @FXML
    private TextField txtProcesador;
    @FXML
    private TextField txtMarca;
    @FXML
    private TextField txtModelo;
    @FXML
    private DatePicker fechaCopmra; //ya
    @FXML
    private TextField txtFactura; //ya
    @FXML
    private TextField txtObservaciones; //ya
    
    @FXML
    private ComboBox<String> cbxTipoEquipo; //ya
    @FXML
    private ComboBox<String> cbxEmpresa; //ya
    @FXML
    private ComboBox<String> cbxSucursal; //ya
    @FXML
    private ComboBox<String> cbxCondicion; //ya
    @FXML
    private ComboBox<String> cbxModelo;
    
    @FXML
    private CheckBox ckbCrearNuevoModelo;
    @FXML
    private FlowPane containerEspecifico;
    @FXML
    private Button btnCancelar;
    @FXML
    private Button btnAgregar;

    private Object controladorActual;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarDatosFicticios();
        configurarListeners();
    }

    @Override
    public void setDashBoard(DashBoardController dbc) {
        this.dbc = dbc;
    }

    /**
     * Configura los eventos (qué pasa cuando seleccionas algo)
     */
    private void configurarListeners() {
        cbxTipoEquipo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                cargarVistaEspecifica(newVal);
            }
        });

        btnCancelar.setOnAction(e -> {
            if (dbc != null) {
                dbc.cambiarDePantalla("inventario/Inventario.fxml");
            }
        });

        btnAgregar.setOnAction(e -> guardarDatos());
    }

    /**
     * Carga dinámicamente el FXML hijo dentro del VBox containerEspecifico
     */
    private void cargarVistaEspecifica(String tipo) {
        String fxml = "";

        switch (tipo) {
            case "Movil":
                fxml = "InfoEspecidicaMovil.fxml";
                break;
            case "Escritorio": 
            case "Laptop":
                fxml = "InfoEspecificaEscritorio.fxml";
                break;
            default:
                fxml = "InfoEspecificaOtros.fxml";
                break;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent vista = loader.load();

            this.controladorActual = loader.getController();

            containerEspecifico.getChildren().clear();
            containerEspecifico.getChildren().add(vista);

        } catch (IOException ex) {
            System.err.println("Error cargando vista especifica: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void guardarDatos() {
        System.out.println("Guardando datos...");
        System.out.println("GRY: " + txtGry.getText());
        System.out.println("Tipo: " + cbxTipoEquipo.getValue());

    }

    private void cargarDatosFicticios() {
        List<String> itemsCbxTipos = asList("Laptop", "Escritorio", "Movil",  "Otros");

        List<String> itemsCbxCondicion = asList("Nuevo", "Bueno", "Regular", "Malo");

        cbxTipoEquipo.setItems(FXCollections.observableArrayList(itemsCbxTipos));
        cbxCondicion.setItems(FXCollections.observableArrayList(itemsCbxCondicion));

        cbxTipoEquipo.getSelectionModel().selectFirst();
        cbxCondicion.getSelectionModel().selectFirst();
        
        cargarVistaEspecifica("Laptop");
    }
}
