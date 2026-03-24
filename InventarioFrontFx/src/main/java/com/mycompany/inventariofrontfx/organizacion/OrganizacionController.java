package com.mycompany.inventariofrontfx.organizacion;

import Dtos.*;
import InterfacesFachada.IFachadaOrganizacion;
import com.mycompany.inventariofrontfx.menu.MenuController;
import fabricaFachadas.FabricaFachadas;
import interfaces.BaseController;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class OrganizacionController implements Initializable, BaseController {

    private MenuController dbc;
    private final IFachadaOrganizacion fachada = FabricaFachadas.getFachadaOrganizacion();

    @FXML private TreeView<String> treeOrganizacion;
    @FXML private Label lblTituloDetalle;
    
    @FXML private VBox panelDetalle;
    
    @FXML private TextField txtNombre;
    @FXML private TextField txtUbicacion;
    @FXML private ComboBox<EmpresaDTO> cbxEmpresa;
    @FXML private ComboBox<SucursalDTO> cbxSucursal;
    @FXML private ComboBox<DepartamentoDTO> cbxDepartamento;
    
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Button btnNuevo;
    @FXML private Button btnCancelar;
    
    private TipoEntidad tipoActual = TipoEntidad.EMPRESA;
    private Object entidadActual = null;
    private TreeItem<String> itemSeleccionado = null;
    
    private enum TipoEntidad {
        EMPRESA, SUCURSAL, DEPARTAMENTO, PUESTO
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTreeView();
        configurarComboBoxes();
        configurarBotones();
        cargarDatosIniciales();
    }

    private void configurarTreeView() {
        treeOrganizacion.setRoot(new TreeItem<>("Organización"));
        treeOrganizacion.setShowRoot(false);
        
        treeOrganizacion.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    itemSeleccionado = newVal;
                    determinarTipoSeleccion(newVal);
                }
            }
        );
    }

    private void determinarTipoSeleccion(TreeItem<String> item) {
        if (item.getParent() == null) {
            
            tipoActual = TipoEntidad.EMPRESA;
            cargarDetalleEmpresa(null);
        } else if (item.getParent().getParent() == null) {
            
            tipoActual = TipoEntidad.EMPRESA;
            cargarDetalleEmpresa(item.getValue());
        } else if (item.getParent().getParent().getParent() == null) {
            
            tipoActual = TipoEntidad.SUCURSAL;
            cargarDetalleSucursal(item.getValue());
        } else if (item.getParent().getParent().getParent().getParent() == null) {
            
            tipoActual = TipoEntidad.DEPARTAMENTO;
            cargarDetalleDepartamento(item.getValue());
        } else {
            
            tipoActual = TipoEntidad.PUESTO;
            cargarDetallePuesto(item.getValue());
        }
    }

    private void cargarDetalleEmpresa(String nombreEmpresa) {
        lblTituloDetalle.setText(nombreEmpresa != null ? "Empresa: " + nombreEmpresa : "Nueva Empresa");
        
        
        panelDetalle.getChildren().clear();
        
        
        txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de la empresa");
        if (nombreEmpresa != null) txtNombre.setText(nombreEmpresa);
        
        btnGuardar = new Button("Guardar Empresa");
        btnGuardar.getStyleClass().add("btn-agregar");
        btnGuardar.setOnAction(e -> guardarEmpresa());
        
        btnEliminar = new Button("Eliminar Empresa");
        btnEliminar.getStyleClass().add("btn-eliminar");
        btnEliminar.setDisable(nombreEmpresa == null);
        btnEliminar.setOnAction(e -> eliminarEmpresa());
        
        panelDetalle.getChildren().addAll(
            new Label("Nombre:"), txtNombre, btnGuardar, btnEliminar
        );
    }

    private void cargarDetalleSucursal(String nombreSucursal) {
        lblTituloDetalle.setText("Sucursal: " + (nombreSucursal != null ? nombreSucursal : "Nueva"));
        
        panelDetalle.getChildren().clear();
        
        EmpresaDTO empresaSel = obtenerEmpresaSeleccionada();
        
        txtNombre = new TextField();
        txtNombre.setPromptText("Nombre de la sucursal");
        if (nombreSucursal != null) txtNombre.setText(nombreSucursal);
        
        txtUbicacion = new TextField();
        txtUbicacion.setPromptText("Ubicación");
        
        ComboBox<EmpresaDTO> cbxEmpresaSel = new ComboBox<>();
        cbxEmpresaSel.setItems(FXCollections.observableArrayList(fachada.listarEmpresas(null)));
        if (empresaSel != null) cbxEmpresaSel.setValue(empresaSel);
        
        btnGuardar = new Button("Guardar Sucursal");
        btnGuardar.getStyleClass().add("btn-agregar");
        btnGuardar.setOnAction(e -> guardarSucursal(cbxEmpresaSel.getValue()));
        
        btnEliminar = new Button("Eliminar Sucursal");
        btnEliminar.getStyleClass().add("btn-eliminar");
        btnEliminar.setDisable(nombreSucursal == null);
        btnEliminar.setOnAction(e -> eliminarSucursal());
        
        panelDetalle.getChildren().addAll(
            new Label("Nombre:"), txtNombre,
            new Label("Ubicación:"), txtUbicacion,
            new Label("Empresa:"), cbxEmpresaSel,
            btnGuardar, btnEliminar
        );
    }

    private void cargarDetalleDepartamento(String nombreDepartamento) {
        lblTituloDetalle.setText("Departamento: " + (nombreDepartamento != null ? nombreDepartamento : "Nuevo"));
        
        panelDetalle.getChildren().clear();
        
        SucursalDTO sucursalSel = obtenerSucursalSeleccionada();
        
        txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del departamento");
        if (nombreDepartamento != null) txtNombre.setText(nombreDepartamento);
        
        ComboBox<SucursalDTO> cbxSucursalSel = new ComboBox<>();
        if (sucursalSel != null) {
            cbxSucursalSel.setItems(FXCollections.observableArrayList(
                fachada.listarSucursales(null, sucursalSel.getIdEmpresa())
            ));
            cbxSucursalSel.setValue(sucursalSel);
        }
        
        btnGuardar = new Button("Guardar Departamento");
        btnGuardar.getStyleClass().add("btn-agregar");
//        btnGuardar.setOnAction(e -> guardarDepartamento(cbxSucursalSel.getSelectionModel().getSelectedItem()));
        
        btnEliminar = new Button("Eliminar Departamento");
        btnEliminar.getStyleClass().add("btn-eliminar");
        btnEliminar.setDisable(nombreDepartamento == null);
        btnEliminar.setOnAction(e -> eliminarDepartamento());
        
        panelDetalle.getChildren().addAll(
            new Label("Nombre:"), txtNombre,
            new Label("Sucursal:"), cbxSucursalSel,
            btnGuardar, btnEliminar
        );
    }

    private void cargarDetallePuesto(String nombrePuesto) {
        lblTituloDetalle.setText("Puesto: " + (nombrePuesto != null ? nombrePuesto : "Nuevo"));
        
        panelDetalle.getChildren().clear();
        
        // Obtener departamento seleccionado
        DepartamentoDTO deptoSel = obtenerDepartamentoSeleccionado();
        
        txtNombre = new TextField();
        txtNombre.setPromptText("Nombre del puesto");
        if (nombrePuesto != null) txtNombre.setText(nombrePuesto);
        
        ComboBox<DepartamentoDTO> cbxDeptoSel = new ComboBox<>();
        if (deptoSel != null) {
            cbxDeptoSel.setItems(FXCollections.observableArrayList(
                fachada.listarDepartamentos(null, deptoSel.getIdSucursal())
            ));
            cbxDeptoSel.setValue(deptoSel);
        }
        
        btnGuardar = new Button("Guardar Puesto");
        btnGuardar.getStyleClass().add("btn-agregar");
//        btnGuardar.setOnAction(e -> guardarPuesto(cbxDeptoSel.getValue()));
        
        btnEliminar = new Button("Eliminar Puesto");
        btnEliminar.getStyleClass().add("btn-eliminar");
        btnEliminar.setDisable(nombrePuesto == null);
        btnEliminar.setOnAction(e -> eliminarPuesto());
        
        panelDetalle.getChildren().addAll(
            new Label("Nombre:"), txtNombre,
            new Label("Departamento:"), cbxDeptoSel,
            btnGuardar, btnEliminar
        );
    }

    private void configurarComboBoxes() {
        // Inicializar ComboBoxes si es necesario
    }

    private void configurarBotones() {
        btnNuevo.setOnAction(e -> crearNuevo());
        btnCancelar.setOnAction(e -> limpiarSeleccion());
    }

    private void cargarDatosIniciales() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                cargarEmpresasEnArbol();
                return null;
            }
        };
        new Thread(task).start();
    }

    private void cargarEmpresasEnArbol() {
        List<EmpresaDTO> empresas = fachada.listarEmpresas(null);
        TreeItem<String> root = treeOrganizacion.getRoot();
        
        Platform.runLater(() -> root.getChildren().clear());
        
        for (EmpresaDTO empresa : empresas) {
            EmpresaItem empresaItem = new EmpresaItem(empresa);
            
            // Cargar sucursales
            List<SucursalDTO> sucursales = fachada.listarSucursales(null, empresa.getId());
            for (SucursalDTO sucursal : sucursales) {
                SucursalItem sucursalItem = new SucursalItem(sucursal);
                
                // Cargar departamentos
                List<DepartamentoDTO> departamentos = fachada.listarDepartamentos(null, sucursal.getId());
                for (DepartamentoDTO depto : departamentos) {
                    DepartamentoItem deptoItem = new DepartamentoItem(depto);
                    
                    // Cargar puestos
                    List<PuestoDTO> puestos = fachada.listarPuestos(depto.getId());
                    for (PuestoDTO puesto : puestos) {
                        deptoItem.addPuesto(new PuestoItem(puesto));
                    }
                    
                    sucursalItem.addDepartamento(deptoItem);
                }
                
                empresaItem.addSucursal(sucursalItem);
            }
            
            Platform.runLater(() -> root.getChildren().add(empresaItem.getTreeItem()));
        }
    }

    private void guardarEmpresa() {
        EmpresaDTO dto = new EmpresaDTO();
        dto.setNombre(txtNombre.getText());
        
        try {
            fachada.guardarEmpresa(dto);
            mostrarExito("Empresa guardada correctamente");
            recargarArbol();
        } catch (Exception e) {
            mostrarError("Error al guardar empresa", e.getMessage());
        }
    }

    private void eliminarEmpresa() {
        EmpresaDTO empresa = obtenerEmpresaSeleccionada();
        if (empresa == null) return;
        
        if (confirmarEliminacion("empresa", empresa.getNombre())) {
            try {
                fachada.eliminarEmpresa(empresa.getId());
                mostrarExito("Empresa eliminada");
                recargarArbol();
                limpiarSeleccion();
            } catch (Exception e) {
                mostrarError("No se puede eliminar", "La empresa tiene sucursales asociadas");
            }
        }
    }

    private void guardarSucursal(EmpresaDTO empresa) {
        if (empresa == null) {
            mostrarAdvertencia("Debe seleccionar una empresa");
            return;
        }
        
        SucursalDTO dto = new SucursalDTO();
        dto.setNombre(txtNombre.getText());
        dto.setUbicacion(txtUbicacion.getText());
        dto.setIdEmpresa(empresa.getId());
        
        try {
            fachada.guardarSucursal(dto);
            mostrarExito("Sucursal guardada");
            recargarArbol();
        } catch (Exception e) {
            mostrarError("Error", e.getMessage());
        }
    }

    private void eliminarSucursal() {
        SucursalDTO sucursal = obtenerSucursalSeleccionada();
        if (sucursal == null) return;
        
        if (confirmarEliminacion("sucursal", sucursal.getNombre())) {
            try {
                fachada.eliminarSucursal(sucursal.getId());
                mostrarExito("Sucursal eliminada");
                recargarArbol();
            } catch (Exception e) {
                mostrarError("No se puede eliminar", "La sucursal tiene departamentos asociados");
            }
        }
    }

    private void guardarDepartamento(DepartamentoDTO depto) {
        if (depto == null) {
            mostrarAdvertencia("Debe seleccionar un departamento");
            return;
        }
        
        DepartamentoDTO dto = new DepartamentoDTO();
        dto.setNombre(txtNombre.getText());
        dto.setIdSucursal(depto.getIdSucursal());
        
        try {
            fachada.guardarDepartamento(dto);
            mostrarExito("Departamento guardado");
            recargarArbol();
        } catch (Exception e) {
            mostrarError("Error", e.getMessage());
        }
    }

    private void eliminarDepartamento() {
        DepartamentoDTO depto = obtenerDepartamentoSeleccionado();
        if (depto == null) return;
        
        if (confirmarEliminacion("departamento", depto.getNombre())) {
            try {
                fachada.eliminarDepartamento(depto.getId());
                mostrarExito("Departamento eliminado");
                recargarArbol();
            } catch (Exception e) {
                mostrarError("No se puede eliminar", "El departamento tiene puestos asociados");
            }
        }
    }

    private void guardarPuesto(PuestoDTO puesto) {
        if (puesto == null) {
            mostrarAdvertencia("Debe seleccionar un puesto");
            return;
        }
        
        PuestoDTO dto = new PuestoDTO();
        dto.setNombre(txtNombre.getText());
        dto.setIdDepartamento(puesto.getIdDepartamento());
        
        try {
            fachada.guardarPuesto(dto);
            mostrarExito("Puesto guardado");
            recargarArbol();
        } catch (Exception e) {
            mostrarError("Error", e.getMessage());
        }
    }

    private void eliminarPuesto() {
        PuestoDTO puesto = obtenerPuestoSeleccionado();
        if (puesto == null) return;
        
        if (confirmarEliminacion("puesto", puesto.getNombre())) {
            try {
                fachada.eliminarPuesto(puesto.getId());
                mostrarExito("Puesto eliminado");
                recargarArbol();
            } catch (Exception e) {
                mostrarError("No se puede eliminar", "El puesto tiene trabajadores asignados");
            }
        }
    }

    private EmpresaDTO obtenerEmpresaSeleccionada() {
        if (itemSeleccionado == null || itemSeleccionado.getParent() == null) return null;
        
        String nombreEmpresa = itemSeleccionado.getValue();
        List<EmpresaDTO> empresas = fachada.listarEmpresas(nombreEmpresa);
        return empresas.isEmpty() ? null : empresas.get(0);
    }

    private SucursalDTO obtenerSucursalSeleccionada() {
        if (itemSeleccionado == null || itemSeleccionado.getParent() == null) return null;
        
        String nombreSucursal = itemSeleccionado.getValue().split(" \\(")[0];
        Long idEmpresa = obtenerEmpresaSeleccionada().getId();
        
        List<SucursalDTO> sucursales = fachada.listarSucursales(nombreSucursal, idEmpresa);
        return sucursales.isEmpty() ? null : sucursales.get(0);
    }

    private DepartamentoDTO obtenerDepartamentoSeleccionado() {
        if (itemSeleccionado == null || itemSeleccionado.getParent() == null) return null;
        
        String nombreDepto = itemSeleccionado.getValue();
        Long idSucursal = obtenerSucursalSeleccionada().getId();
        
        List<DepartamentoDTO> deptos = fachada.listarDepartamentos(nombreDepto, idSucursal);
        return deptos.isEmpty() ? null : deptos.get(0);
    }

    private PuestoDTO obtenerPuestoSeleccionado() {
        if (itemSeleccionado == null) return null;
        
        String nombrePuesto = itemSeleccionado.getValue();
        List<PuestoDTO> puestos = fachada.listarPuestos(null).stream()
            .filter(p -> p.getNombre().equals(nombrePuesto))
            .toList();
        return puestos.isEmpty() ? null : puestos.get(0);
    }

    private void crearNuevo() {
        if (itemSeleccionado == null) {
            cargarDetalleEmpresa(null);
        } else {
            if (itemSeleccionado.getParent() == null) {
                cargarDetalleEmpresa(null);
            } else if (itemSeleccionado.getParent().getParent() == null) {
                cargarDetalleSucursal(null);
            } else if (itemSeleccionado.getParent().getParent().getParent() == null) {
                cargarDetalleDepartamento(null);
            } else {
                cargarDetallePuesto(null);
            }
        }
    }

    private void limpiarSeleccion() {
        treeOrganizacion.getSelectionModel().clearSelection();
        panelDetalle.getChildren().clear();
        lblTituloDetalle.setText("Seleccione un elemento");
    }

    private void recargarArbol() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                cargarEmpresasEnArbol();
                return null;
            }
        };
        new Thread(task).start();
    }

    private boolean confirmarEliminacion(String tipo, String nombre) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Eliminar " + tipo + "?");
        alert.setContentText("¿Está seguro de eliminar " + tipo + " '" + nombre + "'?");
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Advertencia");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }
}