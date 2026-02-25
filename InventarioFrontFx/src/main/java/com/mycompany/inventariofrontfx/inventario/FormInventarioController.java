package com.mycompany.inventariofrontfx.inventario;

import Dtos.EquipoBaseDTO;
import Dtos.EquipoEscritorioDTO;
import Dtos.ModeloDTO;
import Dtos.MovilDTO;
import Dtos.OtroEquipoDTO;
import Enums.CondicionFisica;
import Enums.TipoEquipo;
import static Enums.TipoEquipo.DESKTOP;
import static Enums.TipoEquipo.IMPRESORA;
import static Enums.TipoEquipo.LAPTOP;
import static Enums.TipoEquipo.MONITOR;
import static Enums.TipoEquipo.MOVIL;
import static Enums.TipoEquipo.PROYECTOR;
import static Enums.TipoEquipo.SCANNER;
import InterfacesFachada.IFachadaEquipos;
import InterfacesFachada.IFachadaOrganizacion;
import interfaces.BaseController;
import com.mycompany.inventariofrontfx.menu.MenuController;
import fabricaFachadas.FabricaFachadas;
import interfaces.ControllerInventario;
import interfaces.IValidaciones;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

/**
 * FXML Controller class
 *
 * @author tacot
 */
public class FormInventarioController implements ControllerInventario, IValidaciones<EquipoBaseDTO> {
    
    private final IFachadaEquipos fachadaEquipos = FabricaFachadas.getFachadaEquipos();
    
    private final IFachadaOrganizacion fachadaOrganizacion = FabricaFachadas.getFachadaOrganizacion();
    
    private Parent panelEspecificoActual;
    private IValidaciones controllerEspecifico;
    private MenuController dbc;
    
    private boolean modoEdicion = false;
    private Long idEquipoEditando;
    
    private static final Long IdSucursal = 1L;
    
    @FXML
    private Button btnAgregar;
    @FXML
    private TextField txtGry;
    @FXML
    private ComboBox<CondicionFisica> cbxCondicion;
    @FXML
    private DatePicker fechaCompra;
    @FXML
    private ComboBox<TipoEquipo> cbxTipoEquipo;
    @FXML
    private TextField txtFactura;
    @FXML
    private TextField txtObservaciones;
    @FXML
    private TextField txtIdentificador;
    
    @FXML
    private FlowPane containerEspecifico;
    
    @FXML
    private ComboBox<ModeloDTO> cbxModelo;
    @FXML
    private CheckBox ckbCrearNuevoModelo;
    @FXML
    private TextField txtModelo;
    @FXML
    private TextField txtMarca;
    @FXML
    private TextField txtAlmacenamiento;
    @FXML
    private TextField txtRam;
    @FXML
    private TextField txtProcesador;
    
    @FXML
    public void initialize() {
        
        cbxCondicion.getItems().setAll(CondicionFisica.values());
        cbxTipoEquipo.getItems().setAll(TipoEquipo.values());
        
        cargarModelos();
        
        cbxTipoEquipo.setOnAction(e -> cambiarPanelEspecifico());
        
        if (modoEdicion) {
            this.btnAgregar.setText("+ Actualizar equipo");
        }
    }
    
    private void cambiarPanelEspecifico() {
        
        containerEspecifico.getChildren().clear();
        
        TipoEquipo tipo = cbxTipoEquipo.getValue();
        if (tipo == null) {
            return;
        }
        
        try {
            
            FXMLLoader loader = null;
            
            switch (tipo) {
                
                case LAPTOP, DESKTOP ->
                    loader = new FXMLLoader(getClass().getResource("InfoEspecificaEscritorio.fxml"));
                
                case MOVIL ->
                    loader = new FXMLLoader(getClass().getResource("InfoEspecificaMovil.fxml"));
                
                case IMPRESORA, MONITOR, SCANNER, PROYECTOR ->
                    loader = new FXMLLoader(getClass().getResource("InfoEspecificaOtros.fxml"));
            }
            
            panelEspecificoActual = loader.load();
            controllerEspecifico = (IValidaciones) loader.getController();
            
            containerEspecifico.getChildren().add(panelEspecificoActual);
            
        } catch (IOException ex) {
            mostrarError(ex.getMessage());
        }
    }
    
    private void cargarModelos() {
        
        List<ModeloDTO> modelos = fachadaEquipos.listarModelos();
        
        cbxModelo.getItems().setAll(modelos);
        
        cbxModelo.setOnAction(e -> llenarModeloSeleccionado());
    }
    
    private void llenarModeloSeleccionado() {
        try {
            ModeloDTO modelo = cbxModelo.getValue();
            if (modelo == null) {
                return;
            }
            
            txtModelo.setText(modelo.getNombre());
            txtMarca.setText(modelo.getMarca());
            txtAlmacenamiento.setText(String.valueOf(modelo.getAlmacenamiento()));
            txtRam.setText(String.valueOf(modelo.getMemoriaRam()));
            txtProcesador.setText(modelo.getProcesador());
        } catch (ClassCastException e) {
            System.out.println(e.getMessage());
        }
    }
    
    @FXML
    private void checkBoxAction() {
        
        boolean crear = ckbCrearNuevoModelo.isSelected();
        
        txtModelo.setDisable(!crear);
        txtMarca.setDisable(!crear);
        txtAlmacenamiento.setDisable(!crear);
        txtRam.setDisable(!crear);
        txtProcesador.setDisable(!crear);
        
        cbxModelo.setDisable(crear);
    }
    
    @FXML
    private void btnCancelar() {
        cambiarPantalla("/com/mycompany/inventariofrontfx/inventario/Inventario.fxml");
    }
    
    @FXML
    private void guardarDatos() {
        
        try {
            
            if (validarFormulario() && controllerEspecifico.validarFormulario()) { 
               
                ModeloDTO modelo = obtenerModelo();
                
                if (modoEdicion) {
                    actualizarEquipo(modelo);
                } else {
                    guardarEquipo(modelo);
                }
                
                volverAInventario();
                
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage()+ex.getStackTrace());
        }
    }
    
    private void volverAInventario() {
        cambiarPantalla("/com/mycompany/inventariofrontfx/inventario/Inventario.fxml");
    }
    
    private void actualizarEquipo(ModeloDTO modelo) throws Exception {
        
        EquipoBaseDTO dto = new EquipoBaseDTO();
        
        llenarBase(dto, modelo);
        
        dto.setIdEquipo(idEquipoEditando);
        
        guardarEquipo(modelo);
    }
    
    private void guardarEquipo(ModeloDTO modelo) throws Exception {
        switch (cbxTipoEquipo.getValue()) {
            case DESKTOP ->
                fachadaEquipos.guardarEscritorio(construirEscritorio(modelo));
            case MOVIL ->
                fachadaEquipos.guardarMovil(construirMovil(modelo));
            default ->
                fachadaEquipos.guardarOtro(construirOtro(modelo));
        }
    }
    
    private ModeloDTO obtenerModelo() throws Exception {
        
        if (ckbCrearNuevoModelo.isSelected()) {
            
            ModeloDTO nuevo = new ModeloDTO();
            nuevo.setNombre(txtModelo.getText());
            nuevo.setMarca(txtMarca.getText());
            nuevo.setAlmacenamiento(Integer.valueOf(txtAlmacenamiento.getText()));
            nuevo.setMemoriaRam(Integer.valueOf(txtRam.getText()));
            nuevo.setProcesador(txtProcesador.getText());
            
            return fachadaEquipos.guardarModelo(nuevo);
        }
        
        return cbxModelo.getValue();
    }
    
    private EquipoEscritorioDTO construirEscritorio(ModeloDTO modelo) {
        
        InfoEspecificaEscritorioController c = (InfoEspecificaEscritorioController) controllerEspecifico;
        
        EquipoEscritorioDTO dto = c.getDatosEntidad();
        
        llenarBase(dto, modelo);
        
        return dto;
    }
    
    private OtroEquipoDTO construirOtro(ModeloDTO modelo) {
        
        InfoEspecificaOtrosController c = (InfoEspecificaOtrosController) controllerEspecifico;
        
        OtroEquipoDTO dto = c.getDatosEntidad();
        
        llenarBase(dto, modelo);
        
        return dto;
    }
    
    private MovilDTO construirMovil(ModeloDTO modelo) {
        //ponle un generico a la interfaz y que regresen la entidad especifica sin tener que castear el controlador
        InfoEspecificaMovilController c = (InfoEspecificaMovilController) controllerEspecifico;
        
        MovilDTO dto = c.getDatosEntidad();
        
        llenarBase(dto, modelo);
        
        return dto;
    }
    
    private void llenarBase(EquipoBaseDTO dto, ModeloDTO modelo) {
        
        dto.setGry(Integer.valueOf(txtGry.getText()));
        dto.setCondicion(cbxCondicion.getValue().toString());
        dto.setFechaCompra(fechaCompra.getValue());
        dto.setFactura(txtFactura.getText());
        dto.setObservaciones(txtObservaciones.getText());
        dto.setIdentificador(txtIdentificador.getText());
        dto.setTipo(cbxTipoEquipo.getValue().toString());
        
        dto.setIdModelo(modelo.getIdModelo());
        dto.setIdSucursal(IdSucursal);
    }
    
    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText("Error");
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    @Override
    public void limpiarFormulario() {
        
        txtGry.clear();
        txtFactura.clear();
        txtObservaciones.clear();
        txtIdentificador.clear();
        txtModelo.clear();
        txtMarca.clear();
        txtAlmacenamiento.clear();
        txtRam.clear();
        
        cbxCondicion.getSelectionModel().clearSelection();
        cbxTipoEquipo.getSelectionModel().clearSelection();
        cbxModelo.getSelectionModel().clearSelection();
        
        fechaCompra.setValue(null);
        
        ckbCrearNuevoModelo.setSelected(false);

        // limpiar los datos del contenedor especifico
//        containerEspecifico.getChildren().clear();
    }
    
    public MenuController getDbc() {
        return dbc;
    }
    
    public void setDbc(MenuController dbc) {
        this.dbc = dbc;
    }
    
    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }
    
    @Override
    public <T extends EquipoBaseDTO>void cargarEquipoParaEditar(T equipo){
        
        txtGry.setText(String.valueOf(equipo.getGry()));
        txtFactura.setText(equipo.getFactura());
        txtObservaciones.setText(equipo.getObservaciones());
        txtIdentificador.setText(equipo.getIdentificador());
        
        cbxCondicion.setValue(
                CondicionFisica.valueOf(equipo.getCondicion()));
        
        cbxTipoEquipo.setValue(
                TipoEquipo.valueOf(equipo.getTipo()));
        
        fechaCompra.setValue(equipo.getFechaCompra());

        cbxModelo.getItems().stream()
                .filter(m -> m.getIdModelo().equals(equipo.getIdModelo()))
                .findFirst()
                .ifPresent(m -> cbxModelo.setValue(m));
        
        cbxModelo.getSelectionModel().selectFirst();

        cbxTipoEquipo.getItems().stream()
                .filter(t -> t.equals(TipoEquipo.valueOf(equipo.getTipo())))
                .findFirst()
                .ifPresent(t -> cbxTipoEquipo.setValue(t));
        
        cbxModelo.getSelectionModel().selectFirst();
        
        cambiarPanelEspecifico();
        
        controllerEspecifico.cargarEquipoParaEditar(equipo);
        
    }
    
    @Override
    public ControllerInventario cambiarPantalla(String rutaFXML) {
        try {
            
            if (rutaFXML != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
                Parent vista = loader.load();
                
                Object controller = loader.getController();
                if (controller instanceof BaseController baseController) {
                    baseController.setDashBoard(dbc);
                }
                
                this.dbc.cambiarDePantalla(rutaFXML);
                this.dbc.getCenterContainer().setVvalue(0);
                
            }
            
        } catch (IOException e) {
//            System.err.println("Error cargando la vista: " + rutaFXML);
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        
        return null;
    }
    
    @Override
    public boolean validarFormulario() {
        
        StringBuilder errores = new StringBuilder();
        
        if (txtGry.getText() == null || txtGry.getText().trim().isEmpty()) {
            errores.append("• El GRY es obligatorio.\n");
        }
        
        if (cbxTipoEquipo.getValue() == null) {
            errores.append("• Debes seleccionar un tipo de equipo.\n");
        }
        
        if (ckbCrearNuevoModelo.isSelected()) {
            
            if (txtMarca.getText() == null || txtMarca.getText().trim().isEmpty()) {
                errores.append("• La marca del modelo es obligatoria.\n");
            }
            
            if (txtModelo.getText() == null || txtModelo.getText().trim().isEmpty()) {
                errores.append("• El nombre del modelo es obligatorio.\n");
            }
            
            if (txtRam.getText() == null || txtRam.getText().trim().isEmpty()) {
                errores.append("• La RAM es obligatoria.\n");
            }
            
            if (txtAlmacenamiento.getText() == null || txtAlmacenamiento.getText().trim().isEmpty()) {
                errores.append("• El almacenamiento es obligatorio.\n");
            }
            
            if (txtProcesador.getText() == null || txtProcesador.getText().trim().isEmpty()) {
                errores.append("• El procesador es obligatorio.\n");
            }
            
        } else {
            
            if (cbxModelo.getValue() == null) {
                errores.append("• Debes seleccionar un modelo existente.\n");
            }
        }
        
        if (errores.length() > 0) {
            mostrarError(errores.toString());
            return false;
        }
        
        return true;
    }
    
    @Override
    public EquipoBaseDTO getDatosEntidad() {
        
        EquipoBaseDTO dto = new EquipoBaseDTO();
        
        dto.setGry(Integer.valueOf(txtGry.getText()));
        dto.setCondicion(cbxCondicion.getValue().toString());
        dto.setFechaCompra(fechaCompra.getValue());
        dto.setFactura(txtFactura.getText());
        dto.setObservaciones(txtObservaciones.getText());
        dto.setIdentificador(txtIdentificador.getText());
        dto.setTipo(cbxTipoEquipo.getValue().toString());
        
        return dto;
    }
}
