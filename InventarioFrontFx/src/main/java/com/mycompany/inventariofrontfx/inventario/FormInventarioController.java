package com.mycompany.inventariofrontfx.inventario;

import Dtos.EquipoBaseDTO;
import Dtos.EquipoEscritorioDTO;
import Dtos.ModeloDTO;
import Dtos.MovilDTO;
import Dtos.OtroEquipoDTO;
import Enums.CondicionFisica;
import Enums.TipoEquipo;
import static Enums.TipoEquipo.*;
import InterfacesFachada.IFachadaEquipos;
import InterfacesFachada.IFachadaOrganizacion;
import interfaces.BaseController;
import com.mycompany.inventariofrontfx.menu.MenuController;
import util.ValidacionUtil;
import fabricaFachadas.FabricaFachadas;
import interfaces.ControllerInventario;
import interfaces.IValidaciones;
import java.io.IOException;
import java.util.List;
import javafx.animation.PauseTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Controlador del formulario de inventario.
 */
public class FormInventarioController implements ControllerInventario, IValidaciones<EquipoBaseDTO> {

    private final IFachadaEquipos fachadaEquipos = FabricaFachadas.getFachadaEquipos();
    private final IFachadaOrganizacion fachadaOrganizacion = FabricaFachadas.getFachadaOrganizacion();

    private Parent panelEspecificoActual;
    private IValidaciones controllerEspecifico;
    private MenuController dbc;

    private boolean modoEdicion = false;
    private boolean modoVisualizacion = false;
    private Long idEquipoEditando;
    private Long versionEquipo;

    private static final Long ID_SUCURSAL_DEFAULT = 1L;

    @FXML
    private TextField txtFiltroMarca;
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
    private TextField txtPrecio;
    @FXML
    private AnchorPane containerEspecifico;
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
    private Label errGry;
    @FXML
    private Label errTipo;
    @FXML
    private Label errCondicion;
    @FXML
    private Label errIdentificador;
    @FXML
    private Label errMarca;
    @FXML
    private Label errModelo;
    @FXML
    private Label errRam;
    @FXML
    private Label errAlmacenamiento;
    @FXML
    private Label errProcesador;
    @FXML
    private Label errPrecio;

    private final PauseTransition pause = new PauseTransition(Duration.millis(400));

    @FXML
    public void initialize() {
        cbxCondicion.getItems().setAll(CondicionFisica.values());
        cbxTipoEquipo.getItems().setAll(TipoEquipo.values());
        cbxTipoEquipo.getItems().remove(TipoEquipo.TODOS);
        cbxCondicion.getItems().remove(CondicionFisica.TODAS);

        // Valores por defecto
        fechaCompra.setValue(java.time.LocalDate.now());
        if (!cbxCondicion.getItems().isEmpty()) {
            cbxCondicion.getSelectionModel().selectFirst();
        }
        if (!cbxTipoEquipo.getItems().isEmpty()) {
            cbxTipoEquipo.getSelectionModel().selectFirst();
        }

        cambiarPanelEspecifico();

        cargarModelos();

        txtFiltroMarca.textProperty().addListener((obs, old, val) -> {
            pause.setOnFinished(e -> aplicarFiltro());
            pause.playFromStart();
        });

        // Configurado en listeners en lugar de action para que se ejecute al setear programáticamente
        // cbxTipoEquipo.setOnAction(e -> cambiarPanelEspecifico());

        configurarListenersValidacion();
    }

    private void cambiarPanelEspecifico() {
        containerEspecifico.getChildren().clear();
        TipoEquipo tipo = cbxTipoEquipo.getValue();
        if (tipo == null) {
            return;
        }

        try {
            FXMLLoader loader = switch (tipo) {
                case LAPTOP, DESKTOP, SERVIDOR, AIO ->
                    new FXMLLoader(getClass().getResource("InfoEspecificaEscritorio.fxml"));
                case MOVIL ->
                    new FXMLLoader(getClass().getResource("InfoEspecificaMovil.fxml"));
                case IMPRESORA, MONITOR, SCANNER, PROYECTOR ->
                    new FXMLLoader(getClass().getResource("InfoEspecificaOtros.fxml"));
                default ->
                    null;
            };

            if (loader == null) {
                return;
            }

            panelEspecificoActual = loader.load();
            controllerEspecifico = (IValidaciones) loader.getController();

            // FIX #4: un solo setAll, sin doble-add ni remove intermedio
            AnchorPane.setTopAnchor(panelEspecificoActual, 0.0);
            AnchorPane.setBottomAnchor(panelEspecificoActual, 0.0);
            AnchorPane.setLeftAnchor(panelEspecificoActual, 0.0);
            AnchorPane.setRightAnchor(panelEspecificoActual, 0.0);
            containerEspecifico.getChildren().setAll(panelEspecificoActual);

            if (panelEspecificoActual instanceof FlowPane flow) {
                flow.prefWrapLengthProperty().bind(containerEspecifico.widthProperty());
            }

        } catch (IOException ex) {
            mostrarError("Error al cargar el panel específico: " + ex.getMessage());
        }
    }

    private void configurarListenersValidacion() {
        txtGry.textProperty().addListener((obs, old, val) -> {
            if (val != null && !val.trim().isEmpty() && val.trim().matches("\\d+")) {
                ValidacionUtil.marcarOk(txtGry);
                ValidacionUtil.ocultarLabel(errGry);
            }
        });
        cbxTipoEquipo.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                ValidacionUtil.marcarOk(cbxTipoEquipo);
                ValidacionUtil.ocultarLabel(errTipo);
                cambiarPanelEspecifico();
            }
        });
        cbxCondicion.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                ValidacionUtil.marcarOk(cbxCondicion);
                ValidacionUtil.ocultarLabel(errCondicion);
            }
        });
        txtIdentificador.textProperty().addListener((obs, old, val) -> {
            if (val != null && !val.trim().isEmpty()) {
                ValidacionUtil.marcarOk(txtIdentificador);
                ValidacionUtil.ocultarLabel(errIdentificador);
            }
        });
        txtMarca.textProperty().addListener((obs, old, val) -> {
            if (val != null && !val.trim().isEmpty()) {
                ValidacionUtil.marcarOk(txtMarca);
                ValidacionUtil.ocultarLabel(errMarca);
            }
        });
        txtModelo.textProperty().addListener((obs, old, val) -> {
            if (val != null && !val.trim().isEmpty()) {
                ValidacionUtil.marcarOk(txtModelo);
                ValidacionUtil.ocultarLabel(errModelo);
            }
        });
        txtRam.textProperty().addListener((obs, old, val) -> {
            if (val != null && val.trim().matches("\\d+")) {
                try {
                    if (Integer.parseInt(val.trim()) > 0) {
                        ValidacionUtil.marcarOk(txtRam);
                        ValidacionUtil.ocultarLabel(errRam);
                    }
                } catch(NumberFormatException ignored) {}
            }
        });
        txtAlmacenamiento.textProperty().addListener((obs, old, val) -> {
            if (val != null && val.trim().matches("\\d+")) {
                try {
                    if (Integer.parseInt(val.trim()) > 0) {
                        ValidacionUtil.marcarOk(txtAlmacenamiento);
                        ValidacionUtil.ocultarLabel(errAlmacenamiento);
                    }
                } catch(NumberFormatException ignored) {}
            }
        });
        txtProcesador.textProperty().addListener((obs, old, val) -> {
            if (val != null && !val.trim().isEmpty()) {
                ValidacionUtil.marcarOk(txtProcesador);
                ValidacionUtil.ocultarLabel(errProcesador);
            }
        });
        txtPrecio.textProperty().addListener((obs, old, val) -> {
            if (val == null || val.trim().isEmpty()) {
                ValidacionUtil.marcarOk(txtPrecio);
                ValidacionUtil.ocultarLabel(errPrecio);
                return;
            }
            try {
                double d = Double.parseDouble(val.trim().replace(",", "."));
                if (d >= 0) {
                    ValidacionUtil.marcarOk(txtPrecio);
                    ValidacionUtil.ocultarLabel(errPrecio);
                }
            } catch (NumberFormatException ignored) {
            }
        });
        cbxModelo.valueProperty().addListener((obs, old, val) -> {
            if (val != null) {
                ValidacionUtil.marcarOk(cbxModelo);
            }
        });
    }

    @Override
    public boolean validarFormulario() {
        boolean valido = true;
        StringBuilder errores = new StringBuilder();

        if (!ValidacionUtil.esEnteroPositivo(txtGry)) {
            String msg = txtGry.getText().trim().isEmpty() ? "El GRY es obligatorio." : "El GRY debe ser un número entero positivo.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.mostrarLabelError(errGry, msg);
            valido = false;
        } else {
            ValidacionUtil.ocultarLabel(errGry);
        }

        if (!ValidacionUtil.seleccionado(cbxTipoEquipo)) {
            String msg = "Debes seleccionar un tipo de equipo.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.mostrarLabelError(errTipo, msg);
            valido = false;
        } else {
            ValidacionUtil.ocultarLabel(errTipo);
        }

        if (!ValidacionUtil.seleccionado(cbxCondicion)) {
            String msg = "Debes seleccionar la condición del equipo.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.mostrarLabelError(errCondicion, msg);
            valido = false;
        } else {
            ValidacionUtil.ocultarLabel(errCondicion);
        }

        if (!ValidacionUtil.requerido(txtIdentificador)) {
            String msg = "El identificador (serie/etiqueta) es obligatorio.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.mostrarLabelError(errIdentificador, msg);
            valido = false;
        } else {
            ValidacionUtil.ocultarLabel(errIdentificador);
        }

        if (!ValidacionUtil.esDecimalOpcional(txtPrecio)) {
            String msg = "El precio debe ser un número válido mayor o igual a cero.";
            errores.append("• ").append(msg).append("\n");
            ValidacionUtil.mostrarLabelError(errPrecio, msg);
            valido = false;
        } else {
            ValidacionUtil.ocultarLabel(errPrecio);
        }

        if (ckbCrearNuevoModelo.isSelected()) {
            if (!ValidacionUtil.requerido(txtMarca)) {
                String m = "La marca del modelo es obligatoria.";
                errores.append("• ").append(m).append("\n");
                ValidacionUtil.mostrarLabelError(errMarca, m);
                valido = false;
            } else {
                ValidacionUtil.ocultarLabel(errMarca);
            }
            if (!ValidacionUtil.requerido(txtModelo)) {
                String m = "El nombre del modelo es obligatorio.";
                errores.append("• ").append(m).append("\n");
                ValidacionUtil.mostrarLabelError(errModelo, m);
                valido = false;
            } else {
                ValidacionUtil.ocultarLabel(errModelo);
            }
            if (!ValidacionUtil.esEnteroPositivo(txtRam)) {
                String m = "La RAM debe ser un número entero positivo (en GB).";
                errores.append("• ").append(m).append("\n");
                ValidacionUtil.mostrarLabelError(errRam, m);
                valido = false;
            } else {
                ValidacionUtil.ocultarLabel(errRam);
            }
            if (!ValidacionUtil.esEnteroPositivo(txtAlmacenamiento)) {
                String m = "El almacenamiento debe ser un número entero positivo (en GB).";
                errores.append("• ").append(m).append("\n");
                ValidacionUtil.mostrarLabelError(errAlmacenamiento, m);
                valido = false;
            } else {
                ValidacionUtil.ocultarLabel(errAlmacenamiento);
            }
            if (!ValidacionUtil.requerido(txtProcesador)) {
                String m = "El procesador del modelo es obligatorio.";
                errores.append("• ").append(m).append("\n");
                ValidacionUtil.mostrarLabelError(errProcesador, m);
                valido = false;
            } else {
                ValidacionUtil.ocultarLabel(errProcesador);
            }
        } else {
            if (!ValidacionUtil.seleccionado(cbxModelo)) {
                errores.append("• Debes seleccionar un modelo existente.\n");
                valido = false;
            }
            ValidacionUtil.resetTodos(txtMarca, txtModelo, txtRam, txtAlmacenamiento, txtProcesador);
            ValidacionUtil.ocultarLabel(errMarca);
            ValidacionUtil.ocultarLabel(errModelo);
            ValidacionUtil.ocultarLabel(errRam);
            ValidacionUtil.ocultarLabel(errAlmacenamiento);
            ValidacionUtil.ocultarLabel(errProcesador);
        }

        if (!valido) {
            mostrarError(errores.toString());
        }
        return valido;
    }

    @FXML
    private void guardarDatos() {
        if (validarFormulario() && controllerEspecifico != null && controllerEspecifico.validarFormulario()) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    ModeloDTO modelo = obtenerModelo();
                    if (modoEdicion) {
                        actualizarEquipo(modelo);
                    } else {
                        guardarEquipo(modelo);
                    }
                    return null;
                }
            };
            task.setOnSucceeded(e -> volverAInventario());
            task.setOnFailed(e -> {
                Throwable ex = task.getException();
                mostrarError("Error al guardar: " + (ex != null ? ex.getMessage() : "Error desconocido"));
            });
            new Thread(task).start();
        }
    }


    private void actualizarEquipo(ModeloDTO modelo) throws Exception {
        switch (cbxTipoEquipo.getValue()) {
            case LAPTOP, DESKTOP, SERVIDOR, AIO -> {
                EquipoEscritorioDTO dto = construirEscritorio(modelo);
                dto.setIdEquipo(idEquipoEditando);
                fachadaEquipos.guardarEscritorio(dto);
            }
            case IMPRESORA, MONITOR, SCANNER, PROYECTOR -> {
                OtroEquipoDTO dto = construirOtro(modelo);
                dto.setIdEquipo(idEquipoEditando);
                fachadaEquipos.guardarOtro(dto);
            }
            case MOVIL -> {
                MovilDTO dto = construirMovil(modelo);
                dto.setIdEquipo(idEquipoEditando);
                fachadaEquipos.guardarMovil(dto);
            }
            default -> {
                // Si hay algún tipo no contemplado, no hace nada o puedes lanzar excepción
            }
        }
    }

    private void guardarEquipo(ModeloDTO modelo) throws Exception {
        switch (cbxTipoEquipo.getValue()) {
            case LAPTOP, DESKTOP, SERVIDOR, AIO ->
                fachadaEquipos.guardarEscritorio(construirEscritorio(modelo));
            case IMPRESORA, MONITOR, SCANNER, PROYECTOR ->
                fachadaEquipos.guardarOtro(construirOtro(modelo));
            case MOVIL ->
                fachadaEquipos.guardarMovil(construirMovil(modelo));
            default -> {
                // Si hay algún tipo no contemplado, no hace nada o puedes lanzar excepción
            }
        }
    }

    private ModeloDTO obtenerModelo() throws Exception {
        if (ckbCrearNuevoModelo.isSelected()) {
            ModeloDTO nuevo = new ModeloDTO();
            nuevo.setNombre(txtModelo.getText().trim());
            nuevo.setMarca(txtMarca.getText().trim());
            nuevo.setAlmacenamiento(Integer.parseInt(txtAlmacenamiento.getText().trim()));
            nuevo.setMemoriaRam(Integer.parseInt(txtRam.getText().trim()));
            nuevo.setProcesador(txtProcesador.getText().trim());
            return fachadaEquipos.guardarModelo(nuevo);
        }
        return cbxModelo.getValue();
    }

    private EquipoEscritorioDTO construirEscritorio(ModeloDTO modelo) {
        EquipoEscritorioDTO dto = (EquipoEscritorioDTO) controllerEspecifico.getDatosEntidad();
        llenarBase(dto, modelo);
        return dto;
    }

    private MovilDTO construirMovil(ModeloDTO modelo) {
        MovilDTO dto = (MovilDTO) controllerEspecifico.getDatosEntidad();
        llenarBase(dto, modelo);
        return dto;
    }

    private OtroEquipoDTO construirOtro(ModeloDTO modelo) {
        OtroEquipoDTO dto = (OtroEquipoDTO) controllerEspecifico.getDatosEntidad();
        llenarBase(dto, modelo);
        return dto;
    }

    private void llenarBase(EquipoBaseDTO dto, ModeloDTO modelo) {
        dto.setGry(Integer.parseInt(txtGry.getText().trim()));
        dto.setCondicion(cbxCondicion.getValue().toString());
        dto.setFechaCompra(fechaCompra.getValue());
        dto.setFactura(txtFactura.getText().trim());
        dto.setObservaciones(txtObservaciones.getText().trim());
        dto.setIdentificador(txtIdentificador.getText().trim());
        dto.setTipo(cbxTipoEquipo.getValue().toString());
        dto.setVersion(versionEquipo);
        dto.setIdModelo(modelo.getIdModelo());
        dto.setIdSucursal(ID_SUCURSAL_DEFAULT);
        dto.setPrecio(traerPrecio());
    }

    private Double traerPrecio() {
        if (txtPrecio == null) {
            return 0.0;
        }
        String texto = txtPrecio.getText();
        if (texto == null || texto.trim().isEmpty()) {
            return 0.0;
        }
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
    
    @Override
    public <T extends EquipoBaseDTO> void cargarEquipoParaEditar(T equipo) {
        limpiarFormulario();

        ModeloDTO modeloDto = fachadaEquipos.buscarModeloPorId(equipo.getIdModelo());
        idEquipoEditando = equipo.getIdEquipo();
        versionEquipo = equipo.getVersion();

        txtGry.setText(equipo.getGryFormateado() != null ? equipo.getGryFormateado() : "");
        txtFactura.setText(equipo.getFactura() != null ? equipo.getFactura() : "");
        txtObservaciones.setText(equipo.getObservaciones() != null ? equipo.getObservaciones() : "");
        txtIdentificador.setText(equipo.getIdentificador() != null ? equipo.getIdentificador() : "");
        txtPrecio.setText(equipo.getPrecio() != null ? String.valueOf(equipo.getPrecio()) : "");

        cbxCondicion.setValue(CondicionFisica.valueOf(equipo.getCondicion()));

        cbxTipoEquipo.setValue(TipoEquipo.valueOf(equipo.getTipo()));
        
        fechaCompra.setValue(equipo.getFechaCompra());
        cbxModelo.getSelectionModel().select(modeloDto);
        llenarModeloSeleccionado();

        btnAgregar.setText("+ Actualizar equipo");
        modoEdicion = true;
        modoVisualizacion = false;
        
        if (controllerEspecifico != null) {
            controllerEspecifico.cargarEquipoParaEditar(equipo);
        }

        aplicarModoVisualizacion(false);
    }

    public <T extends EquipoBaseDTO> void cargarEquipoParaVisualizar(T equipo) {
        cargarEquipoParaEditar(equipo);
        aplicarModoVisualizacion(true);
        btnAgregar.setVisible(false);
        btnAgregar.setManaged(false);
    }

    private void cargarModelos() {
        List<ModeloDTO> modelos = fachadaEquipos.listarModelos();
        cbxModelo.getItems().setAll(modelos);
        cbxModelo.setOnAction(e -> llenarModeloSeleccionado());
        if (!modelos.isEmpty()) {
            cbxModelo.getSelectionModel().selectFirst();
        }
    }

    private void aplicarFiltro() {
        cargarDatosAsync();
    }

    private void cargarDatosAsync() {
        Task<List<ModeloDTO>> task = new Task<>() {
            @Override
            protected List<ModeloDTO> call() {
                return fachadaEquipos.buscarModelosConFiltros(null, txtFiltroMarca.getText(), null, null, null);
            }
        };
        cbxModelo.setDisable(true);
        task.setOnSucceeded(e -> {
            cbxModelo.getItems().setAll(task.getValue());
            cbxModelo.setDisable(false);
            if (!task.getValue().isEmpty()) {
                cbxModelo.getSelectionModel().selectFirst();
            }
        });
        task.setOnFailed(e -> {
            cbxModelo.setDisable(false);
            task.getException().printStackTrace();
        });
        new Thread(task).start();
    }

    private void llenarModeloSeleccionado() {
        ModeloDTO modelo = cbxModelo.getValue();
        if (modelo == null) {
            return;
        }
        txtModelo.setText(modelo.getNombre());
        txtMarca.setText(modelo.getMarca());
        txtAlmacenamiento.setText(modelo.getAlmacenamiento() != null ? String.valueOf(modelo.getAlmacenamiento()) : "");
        txtRam.setText(modelo.getMemoriaRam() != null ? String.valueOf(modelo.getMemoriaRam()) : "");
        txtProcesador.setText(modelo.getProcesador());
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
        if (crear) {
            ValidacionUtil.resetCampo(cbxModelo);
        } else {
            ValidacionUtil.resetTodos(txtMarca, txtModelo, txtRam, txtAlmacenamiento, txtProcesador);
            ValidacionUtil.ocultarLabel(errMarca);
            ValidacionUtil.ocultarLabel(errModelo);
            ValidacionUtil.ocultarLabel(errRam);
            ValidacionUtil.ocultarLabel(errAlmacenamiento);
            ValidacionUtil.ocultarLabel(errProcesador);
        }
    }

    private void aplicarModoVisualizacion(boolean visualizar) {
        this.modoVisualizacion = visualizar;
        boolean editable = !visualizar;
        txtGry.setDisable(!editable);
        cbxCondicion.setDisable(!editable);
        fechaCompra.setDisable(!editable);
        cbxTipoEquipo.setDisable(!editable);
        txtFactura.setDisable(!editable);
        txtObservaciones.setDisable(!editable);
        txtIdentificador.setDisable(!editable);
        txtPrecio.setDisable(!editable);
        ckbCrearNuevoModelo.setDisable(!editable);
        cbxModelo.setDisable(!editable || ckbCrearNuevoModelo.isSelected());
        txtModelo.setDisable(!editable || !ckbCrearNuevoModelo.isSelected());
        txtMarca.setDisable(!editable || !ckbCrearNuevoModelo.isSelected());
        txtAlmacenamiento.setDisable(!editable || !ckbCrearNuevoModelo.isSelected());
        txtRam.setDisable(!editable || !ckbCrearNuevoModelo.isSelected());
        txtProcesador.setDisable(!editable || !ckbCrearNuevoModelo.isSelected());
        containerEspecifico.setDisable(!editable);
        txtFiltroMarca.setEditable(!editable);
        btnAgregar.setVisible(editable);
        btnAgregar.setManaged(editable);
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
        txtProcesador.clear();
        if (txtPrecio != null) {
            txtPrecio.clear();
        }

        if (!cbxCondicion.getItems().isEmpty()) {
            cbxCondicion.getSelectionModel().selectFirst();
        }
        if (!cbxTipoEquipo.getItems().isEmpty()) {
            cbxTipoEquipo.getSelectionModel().selectFirst();
        }
        if (!cbxModelo.getItems().isEmpty()) {
            cbxModelo.getSelectionModel().selectFirst();
        }

        fechaCompra.setValue(java.time.LocalDate.now());
        ckbCrearNuevoModelo.setSelected(false);
        modoEdicion = false;
        modoVisualizacion = false;

        ValidacionUtil.resetTodos(txtGry, cbxCondicion, cbxTipoEquipo, txtIdentificador,
                txtPrecio, cbxModelo, txtMarca, txtModelo, txtRam, txtAlmacenamiento, txtProcesador);
        ValidacionUtil.ocultarLabel(errGry);
        ValidacionUtil.ocultarLabel(errTipo);
        ValidacionUtil.ocultarLabel(errCondicion);
        ValidacionUtil.ocultarLabel(errIdentificador);
        ValidacionUtil.ocultarLabel(errMarca);
        ValidacionUtil.ocultarLabel(errModelo);
        ValidacionUtil.ocultarLabel(errRam);
        ValidacionUtil.ocultarLabel(errAlmacenamiento);
        ValidacionUtil.ocultarLabel(errProcesador);
        ValidacionUtil.ocultarLabel(errPrecio);
    }

    private void volverAInventario() {
        cambiarPantalla("/com/mycompany/inventariofrontfx/inventario/Inventario.fxml");
    }

    @FXML
    private void btnCancelar() {
        cambiarPantalla("/com/mycompany/inventariofrontfx/inventario/Inventario.fxml");
    }

    @Override
    public ControllerInventario cambiarPantalla(String rutaFXML) {
        if (rutaFXML == null || dbc == null) {
            return null;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent vista = loader.load();
            Object controller = loader.getController();
            if (controller instanceof BaseController bc) {
                bc.setDashBoard(dbc);
            }
            
            dbc.getCenterContainer().setContent(vista);
            dbc.getCenterContainer().setVvalue(0);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarError("No se pudo cargar la pantalla: " + e.getMessage());
        }
        return null;
    }
    
    @Override
    public EquipoBaseDTO getDatosEntidad() {
        EquipoBaseDTO dto = new EquipoBaseDTO();
        dto.setGry(Integer.parseInt(txtGry.getText().trim()));
        dto.setCondicion(cbxCondicion.getValue().toString());
        dto.setFechaCompra(fechaCompra.getValue());
        dto.setFactura(txtFactura.getText().trim());
        dto.setObservaciones(txtObservaciones.getText().trim());
        dto.setIdentificador(txtIdentificador.getText().trim());
        dto.setTipo(cbxTipoEquipo.getValue().toString());
        return dto;
    }

    private void mostrarError(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Corrige los siguientes errores:");
        System.out.println(msg);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }

    public MenuController getDbc() {
        return dbc;
    }

    public void setDbc(MenuController dbc) {
        this.dbc = dbc;
    }
}
