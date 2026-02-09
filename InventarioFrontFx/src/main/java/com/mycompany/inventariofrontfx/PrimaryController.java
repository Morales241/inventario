package com.mycompany.inventariofrontfx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

public class PrimaryController {

    // --- CUADRANTE 1 ---
    @FXML private TextField txtNoSerie;
    @FXML private TextField txtGRI;
    @FXML private ComboBox<String> cbxCondicion;
    @FXML private TextField txtFactura;
    @FXML private DatePicker fechaCompra;

    // --- CUADRANTE 2 (MODELO) ---
    @FXML private CheckBox chkNuevoModelo;
    @FXML private ComboBox<String> cbxModelosExistentes;
    @FXML private TextField txtMarca;
    @FXML private TextField txtNombreModelo;

    // --- CUADRANTE 3 (SPECS) ---
    @FXML private ComboBox<String> cbxTipoEquipo;
    @FXML private GridPane panelSpecsPC; // Para ocultarlo si es necesario
    @FXML private TextField txtProcesador;
    @FXML private TextField txtRam;
    @FXML private TextField txtAlmacenamiento;

    // --- CUADRANTE 4 ---
    @FXML private ComboBox<String> cbxEmpresa;
    @FXML private ComboBox<String> cbxSucursal;
    @FXML private TextArea txtObservaciones;

    @FXML
    public void initialize() {
        // Cargar datos dummy
        cargarDatosPrueba();
        
        // Lógica del Cuadrante 2: Checkbox "Crear Nuevo"
        chkNuevoModelo.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                // Modo CREAR: Habilitar textos, deshabilitar combo selección
                txtMarca.setDisable(false);
                txtNombreModelo.setDisable(false);
                cbxModelosExistentes.setDisable(true);
                cbxModelosExistentes.setValue(null);
                txtMarca.clear();
                txtNombreModelo.clear();
            } else {
                // Modo SELECCIONAR: Bloquear textos, habilitar combo
                txtMarca.setDisable(true);
                txtNombreModelo.setDisable(true);
                cbxModelosExistentes.setDisable(false);
            }
        });

        // Lógica Cuadrante 3: Cambiar specs según tipo
        cbxTipoEquipo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("Monitor".equals(newVal) || "Impresora".equals(newVal)) {
                panelSpecsPC.setVisible(false); // Ocultar specs de PC
            } else {
                panelSpecsPC.setVisible(true);
            }
        });
    }

    @FXML
    public void guardarInventario() {
        // Tu lógica de validación aquí
        System.out.println("Guardando...");
        // Validar campos...
        // Llamar al DAO...
        mostrarAlerta("Éxito", "Equipo guardado correctamente.");
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cargarDatosPrueba() {
        cbxCondicion.getItems().addAll("Nuevo", "Bueno", "Regular", "Malo");
        cbxTipoEquipo.getItems().addAll("Computadora Escritorio", "Laptop", "Monitor", "Impresora");
        cbxEmpresa.getItems().addAll("Empresa A", "Empresa B");
        cbxSucursal.getItems().addAll("Sucursal Centro", "Sucursal Norte");
        cbxModelosExistentes.getItems().addAll("Dell Latitude 5420", "HP ProDesk 400", "Lenovo ThinkPad");
    }
}