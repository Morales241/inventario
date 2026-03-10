package com.mycompany.inventariofrontfx.inventario;

import Dtos.EquipoBaseDTO;
import Dtos.EquipoEscritorioDTO;
import InterfacesFachada.IFachadaEquipos;
import interfaces.BaseController;
import com.mycompany.inventariofrontfx.menu.MenuController;
import interfaces.IValidaciones;
import fabricaFachadas.FabricaFachadas;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author tacot
 */
public class InfoEspecificaEscritorioController implements Initializable, BaseController, IValidaciones<EquipoEscritorioDTO> {
    
    private MenuController dbc;
    
    private final IFachadaEquipos fachadaEquipos = FabricaFachadas.getFachadaEquipos();
    
    @FXML
    private TextField txtNombreEquipo;
    @FXML
    private TextField txtUserRed;
    @FXML
    private DatePicker fechaGarantia;
    @FXML
    private RadioButton rbtMochila;
    @FXML
    private RadioButton rbtMouse;
    @FXML
    private ComboBox<String> cbxSistema;
    
    private List<String> sistemasOperativos;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        sistemasOperativos = Arrays.asList("Windows 10 - LTSC", "Windows 10 - Home", "Windows 10 - Pro",
                "Windows 11 - LTSC", "Windows 11 - Home", "Windows 11 - Pro");
        
        cbxSistema.getItems().setAll(sistemasOperativos);
        cbxSistema.getSelectionModel().selectFirst();
    }
    
    public TextField getTxtNombreEquipo() {
        return txtNombreEquipo;
    }
    
    public void setTxtNombreEquipo(TextField txtNombreEquipo) {
        this.txtNombreEquipo = txtNombreEquipo;
    }
    
    public DatePicker getFechaGarantia() {
        return fechaGarantia;
    }
    
    public void setFechaGarantia(DatePicker fechaGarantia) {
        this.fechaGarantia = fechaGarantia;
    }
    
    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
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
    
    @Override
    public EquipoEscritorioDTO getDatosEntidad() {
        EquipoEscritorioDTO equipoEscritorio = new EquipoEscritorioDTO();
        equipoEscritorio.setNombreEquipo(txtNombreEquipo.getText());
        equipoEscritorio.setCuenta(txtNombreEquipo.getText());
        equipoEscritorio.setFinalGarantia(fechaGarantia.getValue());
        equipoEscritorio.setMochila(rbtMochila.isSelected());
        equipoEscritorio.setMouse(rbtMouse.isSelected());
        equipoEscritorio.setSisOpertativo(cbxSistema.getSelectionModel().getSelectedItem());
        equipoEscritorio.setUserRed(txtUserRed.getText());
        
        return equipoEscritorio;
    }
    
    @Override
    public boolean validarFormulario() {
        
        if (txtNombreEquipo.getText().trim().isEmpty()) {
            mostrarAdvertencia("El Nombre del equipo es obligatorio.");
            txtNombreEquipo.requestFocus();
            return false;
        }
        
//        if (txtUserRed.getText().trim().isEmpty()) {
//            mostrarAdvertencia("La cuenta de equipo es obligatoria.");
//            txtUserRed.requestFocus();
//            return false;
//        }
        
        return true;
    }
    
    @Override
    public <T extends EquipoBaseDTO>void cargarEquipoParaEditar(T dto) {
        
        EquipoEscritorioDTO EEdto = (EquipoEscritorioDTO) dto;
        
        txtNombreEquipo.setText(EEdto.getNombreEquipo());
        txtUserRed.setText(EEdto.getUserRed());
        fechaGarantia.setValue(EEdto.getFinalGarantia());
        rbtMochila.setSelected(EEdto.getMochila());
        rbtMouse.setSelected(EEdto.getMouse());
        
        int index = sistemasOperativos.indexOf(EEdto.getSisOpertativo());
        
        cbxSistema.getSelectionModel().select(index);
        
    }
}
