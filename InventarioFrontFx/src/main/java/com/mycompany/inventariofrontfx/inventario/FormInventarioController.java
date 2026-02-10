package com.mycompany.inventariofrontfx.inventario;

import Dtos.EquipoBaseDTO;
import com.mycompany.inventariofrontfx.BaseController;
import com.mycompany.inventariofrontfx.DashBoardController;
import com.mycompany.inventariofrontfx.IValidaciones;
import java.io.IOException;
import java.net.URL;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

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
    private DatePicker fechaCompra; //ya
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

    private IValidaciones<?> controladorHijoActual;

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

            this.controladorHijoActual = loader.getController();
            

            containerEspecifico.getChildren().clear();
            containerEspecifico.getChildren().add(vista);

        } catch (IOException ex) {
            System.err.println("Error cargando vista especifica: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
    
    @FXML
    private <T extends EquipoBaseDTO> void guardarDatos() {

        if (!validarFormulario()) {
            return;
        }
        
        if (!controladorHijoActual.validarFormulario()) {
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Guardado");
        alert.setHeaderText("¿Estás seguro de guardar este equipo?");

        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("imagenes/logo.png"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {

            System.out.println("Guardando datos...");
            System.out.println("GRY: " + txtGry.getText());
            System.out.println("Tipo: " + cbxTipoEquipo.getValue());
        }

    }

    private void cargarDatosFicticios() {
        List<String> itemsCbxTipos = asList("Laptop", "Escritorio", "Movil", "Otros");

        List<String> itemsCbxCondicion = asList("Nuevo", "Bueno", "Regular", "Malo");

        cbxTipoEquipo.setItems(FXCollections.observableArrayList(itemsCbxTipos));
        cbxCondicion.setItems(FXCollections.observableArrayList(itemsCbxCondicion));

        cbxTipoEquipo.getSelectionModel().selectFirst();
        cbxCondicion.getSelectionModel().selectFirst();

        cargarVistaEspecifica("Laptop");
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

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error Crítico");
        alert.setHeaderText(mensaje);
        System.out.println(mensaje);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("imagenes/logo.png"));
        alert.showAndWait();
    }

    public boolean validarFormulario() {

        if (cbxEmpresa.getSelectionModel().getSelectedItem() == null || cbxEmpresa.getSelectionModel().getSelectedIndex() == -1) {
            mostrarAdvertencia("Debes seleccionar la 'Empresa' propietaria del equipo.");
            cbxEmpresa.requestFocus();
            return false;
        }

        if (cbxSucursal.getSelectionModel().getSelectedItem() == null || cbxSucursal.getSelectionModel().getSelectedIndex() == -1) {
            mostrarAdvertencia("Debes seleccionar la 'Sucursal' donde estará el equipo.");
            cbxSucursal.requestFocus();
            return false;
        }

        if (cbxTipoEquipo.getSelectionModel().getSelectedItem() == null || cbxTipoEquipo.getSelectionModel().getSelectedIndex() == -1) {
            mostrarAdvertencia("Selecciona el 'Tipo de Equipo'.");
            cbxTipoEquipo.requestFocus();
            return false;
        }

        if (cbxCondicion.getSelectionModel().getSelectedItem() == null || cbxCondicion.getSelectionModel().getSelectedIndex() == -1) {
            mostrarAdvertencia("Selecciona la 'Condición' actual del equipo.");
            cbxCondicion.requestFocus();
            return false;
        }

        if (txtIdentificador.getText().trim().isEmpty()) {
            mostrarAdvertencia("El 'Número de Serie' es obligatorio.");
            txtIdentificador.requestFocus();
            return false;
        }
        if (txtIdentificador.getText().trim().length() < 10) {
            mostrarAdvertencia("El Número de Serie parece demasiado corto. Verifícalo.");
            txtIdentificador.requestFocus();
            return false;
        }

        if (txtGry.getText().trim().isEmpty()) {
            mostrarAdvertencia("El código 'GRI' (Etiqueta de inventario) es obligatorio.");
            txtGry.requestFocus();
            return false;
        }

        if (txtMarca.getText().trim().isEmpty()) {
            mostrarAdvertencia("Ingresa la 'Marca' del equipo.");
            txtMarca.requestFocus();
            return false;
        }

        if (txtModelo.getText().trim().isEmpty()) {
            mostrarAdvertencia("Ingresa el nombre del Modelo.");
            txtModelo.requestFocus();
            return false;
        }

        if (txtProcesador.getText().trim().isEmpty()) {
            mostrarAdvertencia("Ingresa el Procesador.");
            txtProcesador.requestFocus();
            return false;
        }

        String ram = txtRam.getText().trim();
        if (ram.isEmpty()) {
            mostrarAdvertencia("Ingresa la cantidad de 'RAM'.");
            txtRam.requestFocus();
            return false;
        }

        if (txtAlmacenamiento.getText().trim().isEmpty()) {
            mostrarAdvertencia("Ingresa el Almacenamiento (Ej: 512GB SSD).");
            txtAlmacenamiento.requestFocus();
            return false;
        }

        if (txtFactura.getText().trim().isEmpty()) {
            mostrarAdvertencia("El número de 'Factura' es obligatorio para auditoría.");
            txtFactura.requestFocus();
            return false;
        }

        java.time.LocalDate fecha = fechaCompra.getValue();

        if (fecha == null) {
            mostrarAdvertencia("Debes seleccionar la 'Fecha de Compra' en el calendario.");
            fechaCompra.requestFocus();
            return false;
        }

        if (fecha.isAfter(java.time.LocalDate.now())) {
            mostrarError("La fecha de compra no puede ser una fecha futura.");
            fechaCompra.requestFocus();
            return false;
        }

        if (txtObservaciones.getText().length() > 500) {
            mostrarAdvertencia("Las observaciones son demasiado largas (Máx 500 caracteres).");
            txtObservaciones.requestFocus();
            return false;
        }

        return true;
    }
}
