package com.mycompany.inventariofrontfx.inventario;

import Dtos.EquipoBaseDTO;
import Dtos.OtroEquipoDTO;
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
import javafx.scene.control.TextField;
import util.ValidacionUtil;

public class InfoEspecificaOtrosController
        implements Initializable, BaseController, IValidaciones<OtroEquipoDTO> {

    private MenuController dbc;

    @FXML
    private TextField txtTCE;
    @FXML
    private TextField txtCCE;
    @FXML
    private TextField txtTCE2;
    @FXML
    private TextField txtCCE2;
    @FXML
    private Label errTCE;
    @FXML
    private Label errCCE;
    @FXML
    private Label errTCE2;  // se usa cuando título 2 está lleno pero contenido 2 no

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Listeners en tiempo real
        txtTCE.textProperty().addListener((obs, old, val) -> {
            if (val != null && !val.trim().isEmpty()) {
                ValidacionUtil.marcarOk(txtTCE);
                ValidacionUtil.ocultarLabel(errTCE);
            }
        });
        txtCCE.textProperty().addListener((obs, old, val) -> {
            if (val != null && !val.trim().isEmpty()) {
                ValidacionUtil.marcarOk(txtCCE);
                ValidacionUtil.ocultarLabel(errCCE);
            }
        });
        // Campo 2: limpiar error cuando se completa el par
        txtCCE2.textProperty().addListener((obs, old, val) -> {
            if (val != null && !val.trim().isEmpty()) {
                ValidacionUtil.ocultarLabel(errTCE2);
            }
        });
        txtTCE2.textProperty().addListener((obs, old, val) -> {
            if (val != null && !val.trim().isEmpty()) {
                ValidacionUtil.ocultarLabel(errTCE2);
            }
        });
    }

    @Override
    public boolean validarFormulario() {
        boolean valido = true;
        StringBuilder errores = new StringBuilder();

        // Campo extra 1 - Título: obligatorio
        if (!ValidacionUtil.requerido(txtTCE)) {
            String msg = "El título del primer campo extra es obligatorio.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.mostrarLabelError(errTCE, msg);
            valido = false;
        } else {
            ValidacionUtil.ocultarLabel(errTCE);
        }

        // Campo extra 1 - Contenido: obligatorio
        if (!ValidacionUtil.requerido(txtCCE)) {
            String msg = "El contenido del primer campo extra es obligatorio.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.mostrarLabelError(errCCE, msg);
            valido = false;
        } else {
            ValidacionUtil.ocultarLabel(errCCE);
        }

        // Campo extra 2: si se llena uno de los dos, el otro también debe llenarse
        boolean tce2Lleno = txtTCE2 != null && !txtTCE2.getText().trim().isEmpty();
        boolean cce2Lleno = txtCCE2 != null && !txtCCE2.getText().trim().isEmpty();

        if (tce2Lleno && !cce2Lleno) {
            String msg = "Si ingresas un segundo título, debes ingresar también el contenido.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.marcarError(txtCCE2);
            ValidacionUtil.mostrarLabelError(errTCE2, msg);
            valido = false;
        } else if (!tce2Lleno && cce2Lleno) {
            String msg = "Si ingresas un segundo contenido, debes ingresar también el título.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.marcarError(txtTCE2);
            ValidacionUtil.mostrarLabelError(errTCE2, msg);
            valido = false;
        } else {
            ValidacionUtil.resetTodos(txtTCE2, txtCCE2);
            ValidacionUtil.ocultarLabel(errTCE2);
        }

        if (!valido) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Datos del equipo");
            alert.setHeaderText("Corrige los siguientes errores:");
            alert.setContentText(errores.toString());
            alert.showAndWait();
        }

        return valido;
    }

    @Override
    public OtroEquipoDTO getDatosEntidad() {
        OtroEquipoDTO dto = new OtroEquipoDTO();
        dto.setTituloCampoExtra(txtTCE.getText().trim());
        dto.setContenidoCampoExtra(txtCCE.getText().trim());
        dto.setTituloCampoExtra2(txtTCE2 != null ? txtTCE2.getText().trim() : "");
        dto.setContenidoCampoExtra2(txtCCE2 != null ? txtCCE2.getText().trim() : "");
        return dto;
    }

    @Override
    public <T extends EquipoBaseDTO> void cargarEquipoParaEditar(T dto) {
        OtroEquipoDTO Odto = (OtroEquipoDTO) dto;
        txtTCE.setText(Odto.getTituloCampoExtra());
        txtCCE.setText(Odto.getContenidoCampoExtra());
        if (txtTCE2 != null) {
            txtTCE2.setText(Odto.getTituloCampoExtra2());
        }
        if (txtCCE2 != null) {
            txtCCE2.setText(Odto.getContenidoCampoExtra2());
        }

        // Limpiar errores al cargar para editar
        ValidacionUtil.resetTodos(txtTCE, txtCCE, txtTCE2, txtCCE2);
        ValidacionUtil.ocultarLabel(errTCE);
        ValidacionUtil.ocultarLabel(errCCE);
        ValidacionUtil.ocultarLabel(errTCE2);
    }

    public TextField getTxtTCE() {
        return txtTCE;
    }

    public void setTxtTCE(TextField txtTCE) {
        this.txtTCE = txtTCE;
    }

    public TextField getTxtCCE() {
        return txtCCE;
    }

    public void setTxtCCE(TextField txtCCE) {
        this.txtCCE = txtCCE;
    }

    public TextField getTxtTCE2() {
        return txtTCE2;
    }

    public void setTxtTCE2(TextField txtTCE2) {
        this.txtTCE2 = txtTCE2;
    }

    public TextField getTxtCCE2() {
        return txtCCE2;
    }

    public void setTxtCCE2(TextField txtCCE2) {
        this.txtCCE2 = txtCCE2;
    }

    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }
}
