package com.mycompany.inventariofrontfx.inventario;

import Dtos.EmpresaDto;
import Dtos.EquipoBaseDTO;
import Dtos.ModeloDto;
import Dtos.SucursalDto;
import Enums.CondicionFisica;
import Implementaciones.FachadaEquipos;
import Implementaciones.FachadaOrganizacion;
import Interfaces.IFachadaEquipos;
import Interfaces.IFachadaOrganizacion;
import com.mycompany.inventariofrontfx.BaseController;
import com.mycompany.inventariofrontfx.DashBoardController;
import com.mycompany.inventariofrontfx.IValidaciones;
import java.io.IOException;
import java.net.URL;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
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
    private IFachadaOrganizacion fachadaOrganizacion;
    private IFachadaEquipos fachadaEquipos;
    private EquipoBaseDTO equipoEdicion = null;

    @FXML private TextField txtGry, txtIdentificador, txtAlmacenamiento, txtRam, txtProcesador, txtMarca, txtModelo, txtFactura, txtObservaciones;
    @FXML private DatePicker fechaCompra;
    @FXML private ComboBox<String> cbxTipoEquipo;
    @FXML private ComboBox<EmpresaDto> cbxEmpresa;
    @FXML private ComboBox<SucursalDto> cbxSucursal;
    @FXML private ComboBox<CondicionFisica> cbxCondicion;
    @FXML private ComboBox<ModeloDto> cbxModelo;
    @FXML private FlowPane containerEspecifico;
    @FXML private Button btnCancelar, btnAgregar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        fachadaEquipos = new FachadaEquipos();
        fachadaOrganizacion = new FachadaOrganizacion();
        
        configurarComponentes();
        cargarDatosIniciales();
    }

    private void configurarComponentes() {
        cbxTipoEquipo.setItems(FXCollections.observableArrayList("Laptop", "Escritorio", "Movil", "Otros"));
        cbxCondicion.setItems(FXCollections.observableArrayList(CondicionFisica.values()));
        
        cbxModelo.setEditable(true);

        cbxTipoEquipo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) cargarVistaEspecifica(newVal);
        });

        cbxModelo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                txtModelo.setText(newVal.getNombre());
                txtMarca.setText(newVal.getMarca());
                txtProcesador.setText(newVal.getProcesador());
                txtRam.setText(String.valueOf(newVal.getMemoriaRam()));
                txtAlmacenamiento.setText(String.valueOf(newVal.getAlmacenamiento()));
                bloquearCamposModelo(true);
            } else {
                bloquearCamposModelo(false);
            }
        });

        btnCancelar.setOnAction(e -> regresarADashboard());
    }

    private void bloquearCamposModelo(boolean bloquear) {
        txtModelo.setDisable(bloquear);
        txtMarca.setDisable(bloquear);
        txtProcesador.setDisable(bloquear);
        txtRam.setDisable(bloquear);
        txtAlmacenamiento.setDisable(bloquear);
    }

    private void cargarDatosIniciales() {
        Task<Map<String, List<?>>> task = new Task<>() {
            @Override
            protected Map<String, List<?>> call() throws Exception {
                Map<String, List<?>> datos = new HashMap<>();
                datos.put("empresas", fachadaOrganizacion.listarEmpresas(null));
                datos.put("modelos", fachadaEquipos.listarModelos(null));
                return datos;
            }
        };

        task.setOnSucceeded(e -> {
            cbxEmpresa.setItems(FXCollections.observableArrayList((List<EmpresaDto>) task.getValue().get("empresas")));
            cbxModelo.setItems(FXCollections.observableArrayList((List<ModeloDto>) task.getValue().get("modelos")));
            
            // Valores por defecto
            cbxTipoEquipo.getSelectionModel().selectFirst();
            cbxCondicion.getSelectionModel().selectFirst();
        });

        new Thread(task).start();
    }

    @FXML
    private void cbxEmpresaAction() {
        EmpresaDto emp = cbxEmpresa.getSelectionModel().getSelectedItem();
        if (emp != null) {
            // Filtrar sucursales por empresa
            List<SucursalDto> sucursales = fachadaOrganizacion.listarSucursales(null, emp.getId());
            cbxSucursal.setItems(FXCollections.observableArrayList(sucursales));
        }
    }

    @FXML
    private void guardarDatos() {
        if (!validarFormulario()) return;

        ModeloDto modeloFinal = cbxModelo.getSelectionModel().getSelectedItem();
        if (modeloFinal == null) {
            modeloFinal = new ModeloDto();
            modeloFinal.setNombre(txtModelo.getText());
            modeloFinal.setMarca(txtMarca.getText());
            modeloFinal.setProcesador(txtProcesador.getText());
            modeloFinal.setMemoriaRam(Integer.parseInt(txtRam.getText()));
            modeloFinal.setAlmacenamiento(Integer.parseInt(txtAlmacenamiento.getText()));
        }

        EquipoBaseDTO equipo = (equipoEdicion != null) ? equipoEdicion : new EquipoBaseDTO();
        equipo.setGry(Integer.parseInt(txtGry.getText()));
        equipo.setIdentificador(txtIdentificador.getText());
        equipo.setFactura(txtFactura.getText());
        equipo.setFechaCompra(fechaCompra.getValue());
        equipo.setCondicion(cbxCondicion.getValue());
        equipo.setNombreModelo(modeloFinal.getNombre());
        equipo.setObservaciones(txtObservaciones.getText());

        Task<Void> saveTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (equipoEdicion == null) fachadaEquipos.guardarEquipo(equipo);
                else fachadaEquipos.actualizarEquipo(equipo);
                return null;
            }
        };

        saveTask.setOnSucceeded(e -> {
            mostrarMensaje("Equipo guardado con éxito.");
            regresarADashboard();
        });
        
        saveTask.setOnFailed(e -> mostrarError("Error: " + saveTask.getException().getMessage()));
        new Thread(saveTask).start();
    }

    public void setEquipoParaEditar(EquipoBaseDTO equipo) {
        this.equipoEdicion = equipo;
        if (equipo != null) {
            btnAgregar.setText("Actualizar Equipo");
            txtGry.setText(String.valueOf(equipo.getGry()));
            txtIdentificador.setText(equipo.getIdentificador());
//            cbxModelo.setValue(equipo.getModelo());
        }
    }

    private void regresarADashboard() {
        if (dbc != null) dbc.cambiarDePantalla("inventario/Inventario.fxml");
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
    
    private void mostrarMensaje(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Guardar equipo");
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
            mostrarAdvertencia("El Identificador es obligatorio.");
            txtIdentificador.requestFocus();
            return false;
        }
        if (txtIdentificador.getText().trim().length() < 10) {
            mostrarAdvertencia("El Identificador parece demasiado corto. Verifícalo.");
            txtIdentificador.requestFocus();
            return false;
        }

        if (txtGry.getText().trim().isEmpty()) {
            mostrarAdvertencia("El código 'GRY' (Etiqueta de inventario) es obligatorio.");
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

    @Override
    public void setDashBoard(DashBoardController dbc) {
        this.dbc = dbc;
    }
}
