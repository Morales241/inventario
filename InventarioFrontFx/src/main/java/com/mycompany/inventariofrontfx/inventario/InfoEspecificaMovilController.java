package com.mycompany.inventariofrontfx.inventario;

import Dtos.EquipoBaseDTO;
import Dtos.MovilDTO;
import interfaces.BaseController;
import com.mycompany.inventariofrontfx.menu.MenuController;
import util.ValidacionUtil;
import interfaces.IValidaciones;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import util.ValidacionUtil;

public class InfoEspecificaMovilController
        implements Initializable, BaseController, IValidaciones<MovilDTO> {

    private MenuController dbc;

    @FXML
    private RadioButton rbtCargador;
    @FXML
    private RadioButton rbtFunda;
    @FXML
    private RadioButton rbtManosLibres;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField txtCorreo;
    @FXML
    private TextField txtContra;
    @FXML
    private Label errTelefono;
    @FXML
    private Label errCorreo;
    @FXML
    private Label errContra;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Restringir a solo dígitos numéricos y máximo 10
        int maxLength = 10;
        phoneField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0," + maxLength + "}")) {
                return change;
            }
            return null;
        }));

        // Listeners en tiempo real
        phoneField.textProperty().addListener((obs, old, val) -> {
            if (val != null && val.trim().length() == 10) {
                ValidacionUtil.marcarOk(phoneField);
                ValidacionUtil.ocultarLabel(errTelefono);
            }
        });
        txtCorreo.textProperty().addListener((obs, old, val) -> {
            if (val != null && val.trim().matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
                ValidacionUtil.marcarOk(txtCorreo);
                ValidacionUtil.ocultarLabel(errCorreo);
            }
        });
        txtContra.textProperty().addListener((obs, old, val) -> {
            if (val != null && val.trim().length() >= 4) {
                ValidacionUtil.marcarOk(txtContra);
                ValidacionUtil.ocultarLabel(errContra);
            }
        });
    }

    @Override
    public boolean validarFormulario() {
        boolean valido = true;
        StringBuilder errores = new StringBuilder();

        // Número de teléfono: exactamente 10 dígitos
        if (!ValidacionUtil.esNDigitos(phoneField, 10)) {
            String msg = phoneField.getText().trim().isEmpty()
                    ? "El número de teléfono es obligatorio."
                    : "El número de teléfono debe tener exactamente 10 dígitos.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.mostrarLabelError(errTelefono, msg);
            valido = false;
        } else {
            ValidacionUtil.ocultarLabel(errTelefono);
        }

        // Correo electrónico: obligatorio y con formato válido
        if (!ValidacionUtil.esEmail(txtCorreo)) {
            String msg = txtCorreo.getText().trim().isEmpty()
                    ? "El correo electrónico es obligatorio."
                    : "El correo electrónico no tiene un formato válido.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.mostrarLabelError(errCorreo, msg);
            valido = false;
        } else {
            ValidacionUtil.ocultarLabel(errCorreo);
        }

        // Contraseña: obligatoria, mínimo 4 caracteres
        String contra = txtContra.getText() == null ? "" : txtContra.getText().trim();
        if (contra.isEmpty()) {
            String msg = "La contraseña de la cuenta del móvil es obligatoria.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.marcarError(txtContra);
            ValidacionUtil.mostrarLabelError(errContra, msg);
            valido = false;
        } else if (contra.length() < 4) {
            String msg = "La contraseña debe tener al menos 4 caracteres.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.marcarError(txtContra);
            ValidacionUtil.mostrarLabelError(errContra, msg);
            valido = false;
        } else {
            ValidacionUtil.marcarOk(txtContra);
            ValidacionUtil.ocultarLabel(errContra);
        }

        if (!valido) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datos del móvil");
            alert.setHeaderText("Corrige los siguientes errores en la sección del móvil:");
            alert.setContentText(errores.toString());
            alert.showAndWait();
        }

        return valido;
    }

    @Override
    public MovilDTO getDatosEntidad() {
        MovilDTO movil = new MovilDTO();
        movil.setCargador(rbtCargador.isSelected());
        movil.setFunda(rbtFunda.isSelected());
        movil.setManosLibres(rbtManosLibres.isSelected());
        movil.setNumCelular(phoneField.getText().trim());
        movil.setContrasenaCuenta(txtContra.getText().trim());
        movil.setCorreoCuenta(txtCorreo.getText().trim());
        return movil;
    }

    @Override
    public <T extends EquipoBaseDTO> void cargarEquipoParaEditar(T dto) {
        MovilDTO Mdto = (MovilDTO) dto;
        rbtCargador.setSelected(Mdto.getCargador());
        rbtFunda.setSelected(Mdto.getFunda());
        rbtManosLibres.setSelected(Mdto.getManosLibres());
        txtContra.setText(Mdto.getContrasenaCuenta());
        phoneField.setText(Mdto.getNumCelular());
        txtCorreo.setText(Mdto.getCorreoCuenta());

        ValidacionUtil.resetTodos(phoneField, txtCorreo, txtContra);
        ValidacionUtil.ocultarLabel(errTelefono);
        ValidacionUtil.ocultarLabel(errCorreo);
        ValidacionUtil.ocultarLabel(errContra);
    }

    public RadioButton getRbtCargador() {
        return rbtCargador;
    }

    public void setRbtCargador(RadioButton rbtCargador) {
        this.rbtCargador = rbtCargador;
    }

    public RadioButton getRbtFunda() {
        return rbtFunda;
    }

    public void setRbtFunda(RadioButton rbtFunda) {
        this.rbtFunda = rbtFunda;
    }

    public RadioButton getRbtManosLibres() {
        return rbtManosLibres;
    }

    public void setRbtManosLibres(RadioButton rbtManosLibres) {
        this.rbtManosLibres = rbtManosLibres;
    }

    public TextField getPhoneField() {
        return phoneField;
    }

    public void setPhoneField(TextField phoneField) {
        this.phoneField = phoneField;
    }

    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }
}
