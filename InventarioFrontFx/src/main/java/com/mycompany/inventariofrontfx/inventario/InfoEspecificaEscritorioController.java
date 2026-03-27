package com.mycompany.inventariofrontfx.inventario;

import Dtos.EquipoBaseDTO;
import Dtos.EquipoEscritorioDTO;
import InterfacesFachada.IFachadaEquipos;
import interfaces.BaseController;
import com.mycompany.inventariofrontfx.menu.MenuController;
import util.ValidacionUtil;
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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import util.ValidacionUtil;

public class InfoEspecificaEscritorioController
        implements Initializable, BaseController, IValidaciones<EquipoEscritorioDTO> {

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

    // Labels de error — agregar en InfoEspecificaEscritorio.fxml:
    // <Label fx:id="errNombreEquipo" visible="false" managed="false"/>
    // <Label fx:id="errSistema"      visible="false" managed="false"/>
    @FXML
    private Label errNombreEquipo;
    @FXML
    private Label errSistema;

    private List<String> sistemasOperativos;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sistemasOperativos = Arrays.asList(
                "Windows 10 - LTSC", "Windows 10 - Home", "Windows 10 - Pro",
                "Windows 11 - LTSC", "Windows 11 - Home", "Windows 11 - Pro"
        );
        cbxSistema.getItems().setAll(sistemasOperativos);
        cbxSistema.getSelectionModel().selectFirst();

        // Listeners en tiempo real
        txtNombreEquipo.textProperty().addListener((obs, old, val) -> {
            if (val != null && !val.trim().isEmpty()) {
                ValidacionUtil.marcarOk(txtNombreEquipo);
                ValidacionUtil.ocultarLabel(errNombreEquipo);
            }
        });
        cbxSistema.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                ValidacionUtil.marcarOk(cbxSistema);
                ValidacionUtil.ocultarLabel(errSistema);
            }
        });
    }

    @Override
    public boolean validarFormulario() {
        boolean valido = true;
        StringBuilder errores = new StringBuilder();

        // Nombre del equipo: obligatorio
        if (!ValidacionUtil.requerido(txtNombreEquipo)) {
            String msg = "El nombre del equipo es obligatorio.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.mostrarLabelError(errNombreEquipo, msg);
            valido = false;
        } else {
            ValidacionUtil.ocultarLabel(errNombreEquipo);
        }

        // Sistema operativo: debe tener algo seleccionado
        if (!ValidacionUtil.seleccionado(cbxSistema)) {
            String msg = "Debes seleccionar un sistema operativo.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.mostrarLabelError(errSistema, msg);
            valido = false;
        } else {
            ValidacionUtil.ocultarLabel(errSistema);
        }

        if (!valido) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datos del equipo");
            alert.setHeaderText("Corrige los siguientes errores en la sección del equipo:");
            alert.setContentText(errores.toString());
            alert.showAndWait();
        }

        return valido;
    }

    @Override
    public EquipoEscritorioDTO getDatosEntidad() {
        EquipoEscritorioDTO equipoEscritorio = new EquipoEscritorioDTO();
        equipoEscritorio.setNombreEquipo(txtNombreEquipo.getText().trim());
        equipoEscritorio.setCuenta(txtUserRed.getText().trim());
        equipoEscritorio.setFinalGarantia(fechaGarantia.getValue());
        equipoEscritorio.setMochila(rbtMochila.isSelected());
        equipoEscritorio.setMouse(rbtMouse.isSelected());
        equipoEscritorio.setSisOpertativo(cbxSistema.getSelectionModel().getSelectedItem());
        equipoEscritorio.setUserRed(txtUserRed.getText().trim());
        return equipoEscritorio;
    }

    @Override
    public <T extends EquipoBaseDTO> void cargarEquipoParaEditar(T dto) {
        EquipoEscritorioDTO EEdto = (EquipoEscritorioDTO) dto;
        txtNombreEquipo.setText(EEdto.getNombreEquipo());
        txtUserRed.setText(EEdto.getUserRed());
        fechaGarantia.setValue(EEdto.getFinalGarantia());
        rbtMochila.setSelected(EEdto.getMochila());
        rbtMouse.setSelected(EEdto.getMouse());
        int index = sistemasOperativos.indexOf(EEdto.getSisOpertativo());
        cbxSistema.getSelectionModel().select(index);

        ValidacionUtil.resetTodos(txtNombreEquipo, cbxSistema);
        ValidacionUtil.ocultarLabel(errNombreEquipo);
        ValidacionUtil.ocultarLabel(errSistema);
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
}
