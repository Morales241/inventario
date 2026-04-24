package com.mycompany.inventariofrontfx.organizacion;

import Dtos.*;
import InterfacesFachada.IFachadaOrganizacion;
import com.mycompany.inventariofrontfx.menu.MenuController;
import fabricaFachadas.FabricaFachadas;
import interfaces.BaseController;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ContentDisplay;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Controlador del módulo de Organización.
 */
public class OrganizacionController implements Initializable, BaseController {

    public enum TipoNodo { EMPRESA, SUCURSAL, DEPARTAMENTO, PUESTO }

    private record NodoOrg(TipoNodo tipo, Long id, String nombre, Long idPadre, Long version) {
        @Override public String toString() { return nombre; }
    }

    private final IFachadaOrganizacion fachada = FabricaFachadas.getFachadaOrganizacion();
    private MenuController dbc;

    private NodoOrg nodoSeleccionado  = null;
    private boolean modoNuevo         = false;
    private TipoNodo tipoFormulario   = null;
    private Long    idPadreFormulario = null;

    @FXML private TreeView<NodoOrg>   treeOrganizacion;
    @FXML private TextField           txtBuscarArbol;
    @FXML private ProgressIndicator   progressArbol;
    @FXML private Button              btnNuevaEmpresa;
    @FXML private Button              btnLimpiar;
    @FXML private Label               lblEncabezadoIcono;

    @FXML private VBox                panelPlaceholder;
    @FXML private Label               lblPlaceholderIcon;
    @FXML private VBox                panelDetalle;
    
    @FXML private HBox                breadcrumbBox;
    @FXML private Label               lblIconoTipo;
    @FXML private Label               lblTituloDetalle;
    @FXML private Label               lblBadgeTipo;
    @FXML private Button              btnEliminar;
    @FXML private HBox                statsBox;

    @FXML private TextField           txtNombre;
    @FXML private Label               errNombre;
    @FXML private VBox                seccionUbicacion;
    @FXML private TextField           txtUbicacion;
    @FXML private VBox                seccionPadre;
    @FXML private Label               lblPadre;
    @FXML private ComboBox<Object>    cbxPadre;  
    @FXML private Label               errPadre;
    @FXML private Button              btnGuardar;
    @FXML private Button              btnCancelarForm;

    @FXML private VBox                seccionHijos;
    @FXML private Label               lblTituloHijos;
    @FXML private VBox                listaHijos;
    @FXML private Button              btnAgregarHijo;

    private final PauseTransition debounce = new PauseTransition(Duration.millis(300));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarArbol();
        configurarBusqueda();
        configurarIconos();
        configurarAtajosTecladoFormulario();
        mostrarPlaceholder();
        cargarArbolAsync();
    }

    private void configurarAtajosTecladoFormulario() {
        txtNombre.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                guardarSeleccionado();
            }
        });
        txtUbicacion.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                guardarSeleccionado();
            }
        });
    }

    private void configurarArbol() {
        TreeItem<NodoOrg> root = new TreeItem<>();
        treeOrganizacion.setRoot(root);
        treeOrganizacion.setShowRoot(false);

        treeOrganizacion.setCellFactory(tv -> {
            TreeCell<NodoOrg> cell = new TreeCell<>() {
                @Override
                protected void updateItem(NodoOrg nodo, boolean empty) {
                    super.updateItem(nodo, empty);
                    if (empty || nodo == null) {
                        setText(null); setGraphic(null);
                        getStyleClass().removeAll("nodo-empresa","nodo-sucursal","nodo-depto","nodo-puesto");
                    } else {
                        getStyleClass().removeAll("nodo-empresa","nodo-sucursal","nodo-depto","nodo-puesto");
                        FontIcon icono = crearIconoTipo(nodo.tipo(), 14);
                        String estilo = switch (nodo.tipo()) {
                            case EMPRESA      -> "nodo-empresa";
                            case SUCURSAL     -> "nodo-sucursal";
                            case DEPARTAMENTO -> "nodo-depto";
                            case PUESTO       -> "nodo-puesto";
                        };
                        setText(nodo.nombre());
                        setGraphic(icono);
                        getStyleClass().add(estilo);
                    }
                }
            };

            ContextMenu contextMenu = new ContextMenu();
            MenuItem agregarItem = new MenuItem("Agregar Subnivel");
            agregarItem.setGraphic(new FontIcon("fas-plus"));
            agregarItem.setOnAction(e -> {
                treeOrganizacion.getSelectionModel().select(cell.getTreeItem());
                agregarHijo();
            });

            MenuItem eliminarItem = new MenuItem("Eliminar");
            eliminarItem.setGraphic(new FontIcon("fas-trash"));
            eliminarItem.setOnAction(e -> {
                treeOrganizacion.getSelectionModel().select(cell.getTreeItem());
                eliminarSeleccionado();
            });

            contextMenu.getItems().addAll(agregarItem, eliminarItem);

            cell.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    cell.setContextMenu(null);
                } else {
                    NodoOrg nodo = cell.getItem();
                    if (nodo != null) {
                        agregarItem.setDisable(nodo.tipo() == TipoNodo.PUESTO);
                        cell.setContextMenu(contextMenu);
                    }
                }
            });

            return cell;
        });

        treeOrganizacion.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, newItem) -> {
                if (newItem != null && newItem.getValue() != null) {
                    nodoSeleccionado = newItem.getValue();
                    modoNuevo        = false;
                    mostrarDetalle(newItem);
                }
            }
        );
    }

    private void configurarBusqueda() {
        debounce.setOnFinished(e -> filtrarArbol(txtBuscarArbol.getText().trim().toLowerCase()));
        txtBuscarArbol.textProperty().addListener((obs, old, val) -> debounce.playFromStart());
    }

    private void filtrarArbol(String filtro) {
        TreeItem<NodoOrg> root = treeOrganizacion.getRoot();
        if (root == null) return;
        // Expandir o colapsar según haya filtro
        for (TreeItem<NodoOrg> empresa : root.getChildren()) {
            boolean coincideEmpresa = filtro.isEmpty() || empresa.getValue().nombre().toLowerCase().contains(filtro);
            empresa.setExpanded(!filtro.isEmpty());
            for (TreeItem<NodoOrg> suc : empresa.getChildren()) {
                boolean coincideSuc = filtro.isEmpty() || suc.getValue().nombre().toLowerCase().contains(filtro);
                suc.setExpanded(!filtro.isEmpty());
            }
        }
    }

    private void cargarArbolAsync() {
        mostrarSpinner(true);

        Task<TreeItem<NodoOrg>> task = new Task<>() {
            @Override
            protected TreeItem<NodoOrg> call() {
                TreeItem<NodoOrg> root = new TreeItem<>();

                List<EmpresaDTO> empresas = fachada.listarEmpresas(null);
                for (EmpresaDTO emp : empresas) {
                    NodoOrg nEmp = new NodoOrg(TipoNodo.EMPRESA, emp.getId(), emp.getNombre(), null, emp.getVersion());
                    TreeItem<NodoOrg> itemEmp = new TreeItem<>(nEmp);
                    itemEmp.setExpanded(true);

                    List<SucursalDTO> sucursales = fachada.listarSucursales(null, emp.getId());
                    for (SucursalDTO suc : sucursales) {
                        NodoOrg nSuc = new NodoOrg(TipoNodo.SUCURSAL, suc.getId(), suc.getNombre(), emp.getId(), suc.getVersion());
                        TreeItem<NodoOrg> itemSuc = new TreeItem<>(nSuc);
                        itemSuc.setExpanded(false);

                        List<DepartamentoDTO> deptos = fachada.listarDepartamentos(null, suc.getId());
                        for (DepartamentoDTO dep : deptos) {
                            NodoOrg nDep = new NodoOrg(TipoNodo.DEPARTAMENTO, dep.getId(), dep.getNombre(), suc.getId(), dep.getVersion());
                            TreeItem<NodoOrg> itemDep = new TreeItem<>(nDep);
                            itemDep.setExpanded(false);

                            List<PuestoDTO> puestos = fachada.listarPuestos(dep.getId());
                            for (PuestoDTO p : puestos) {
                                NodoOrg nP = new NodoOrg(TipoNodo.PUESTO, p.getId(), p.getNombre(), dep.getId(), p.getVersion());
                                itemDep.getChildren().add(new TreeItem<>(nP));
                            }
                            itemSuc.getChildren().add(itemDep);
                        }
                        itemEmp.getChildren().add(itemSuc);
                    }
                    root.getChildren().add(itemEmp);
                }
                return root;
            }
        };

        task.setOnSucceeded(e -> {
            TreeItem<NodoOrg> root = task.getValue();
            treeOrganizacion.setRoot(root);
            mostrarSpinner(false);
        });

        task.setOnFailed(e -> {
            mostrarSpinner(false);
            mostrarError("Error al cargar", task.getException() != null
                    ? task.getException().getMessage() : "Error desconocido");
        });

        new Thread(task).start();
    }

    private void mostrarDetalle(TreeItem<NodoOrg> item) {
        NodoOrg nodo = item.getValue();
        mostrarPanelDetalle();

        actualizarBreadcrumb(item);
        configurarCabeceraParaTipo(nodo);
        btnEliminar.setVisible(true);
        btnEliminar.setManaged(true);

        txtNombre.setText(nodo.nombre());
        ocultarError(errNombre);

        switch (nodo.tipo()) {
            case EMPRESA -> {
                seccionUbicacion.setVisible(false);    seccionUbicacion.setManaged(false);
                seccionPadre.setVisible(false);        seccionPadre.setManaged(false);
                mostrarStatsEmpresa(nodo);
                mostrarHijosEmpresa(nodo);
            }
            case SUCURSAL -> {
                seccionUbicacion.setVisible(true);     seccionUbicacion.setManaged(true);
                SucursalDTO suc = buscarSucursalPorId(nodo.id());
                txtUbicacion.setText(suc != null ? suc.getUbicacion() : "");
                seccionPadre.setVisible(false);        seccionPadre.setManaged(false);
                mostrarStatsSucursal(nodo);
                mostrarHijosSucursal(nodo);
            }
            case DEPARTAMENTO -> {
                seccionUbicacion.setVisible(false);    seccionUbicacion.setManaged(false);
                seccionPadre.setVisible(false);        seccionPadre.setManaged(false);
                mostrarStatsDepartamento(nodo);
                mostrarHijosDepartamento(nodo);
            }
            case PUESTO -> {
                seccionUbicacion.setVisible(false);    seccionUbicacion.setManaged(false);
                seccionPadre.setVisible(false);        seccionPadre.setManaged(false);
                statsBox.setVisible(false);            statsBox.setManaged(false);
                seccionHijos.setVisible(false);        seccionHijos.setManaged(false);
            }
        }
    }
    
    @FXML
    private void nuevaEmpresa() {
        modoNuevo         = true;
        tipoFormulario    = TipoNodo.EMPRESA;
        idPadreFormulario = null;
        prepararFormularioNuevo(TipoNodo.EMPRESA, null);
    }

    @FXML
    private void agregarHijo() {
        if (nodoSeleccionado == null) return;
        TipoNodo tipoHijo = switch (nodoSeleccionado.tipo()) {
            case EMPRESA      -> TipoNodo.SUCURSAL;
            case SUCURSAL     -> TipoNodo.DEPARTAMENTO;
            case DEPARTAMENTO -> TipoNodo.PUESTO;
            default           -> null;
        };
        if (tipoHijo == null) return;
        modoNuevo         = true;
        tipoFormulario    = tipoHijo;
        idPadreFormulario = nodoSeleccionado.id();
        prepararFormularioNuevo(tipoHijo, nodoSeleccionado.id());
    }

    @FXML
    private void guardarSeleccionado() {
        if (!validarFormulario()) return;

        try {
            if (modoNuevo) {
                guardarNuevo();
            } else {
                guardarEdicion();
            }
            cargarArbolAsync();
            mostrarExito(modoNuevo ? "Creado correctamente" : "Actualizado correctamente");
            mostrarPlaceholder();
        } catch (Exception ex) {
            mostrarError("Error al guardar", ex.getMessage());
        }
    }

    @FXML
    private void eliminarSeleccionado() {
        if (nodoSeleccionado == null) return;
        if (!confirmarEliminacion(nodoSeleccionado.tipo().name().toLowerCase(), nodoSeleccionado.nombre())) return;

        try {
            switch (nodoSeleccionado.tipo()) {
                case EMPRESA      -> fachada.eliminarEmpresa(nodoSeleccionado.id());
                case SUCURSAL     -> fachada.eliminarSucursal(nodoSeleccionado.id());
                case DEPARTAMENTO -> fachada.eliminarDepartamento(nodoSeleccionado.id());
                case PUESTO       -> fachada.eliminarPuesto(nodoSeleccionado.id());
            }
            mostrarExito("Eliminado correctamente");
            cargarArbolAsync();
            mostrarPlaceholder();
        } catch (Exception ex) {
            mostrarError("No se puede eliminar", ex.getMessage());
        }
    }

    @FXML
    private void cancelarFormulario() {
        modoNuevo = false;
        if (nodoSeleccionado != null && treeOrganizacion.getSelectionModel().getSelectedItem() != null) {
            mostrarDetalle(treeOrganizacion.getSelectionModel().getSelectedItem());
        } else {
            mostrarPlaceholder();
        }
    }

    @FXML
    private void limpiarSeleccion() {
        treeOrganizacion.getSelectionModel().clearSelection();
        nodoSeleccionado = null;
        modoNuevo        = false;
        mostrarPlaceholder();
    }

    private void guardarNuevo() {
        switch (tipoFormulario) {
            case EMPRESA -> {
                EmpresaDTO dto = new EmpresaDTO();
                dto.setNombre(txtNombre.getText().trim());
                fachada.guardarEmpresa(dto);
            }
            case SUCURSAL -> {
                SucursalDTO dto = new SucursalDTO();
                dto.setNombre(txtNombre.getText().trim());
                dto.setUbicacion(txtUbicacion.getText().trim());
                dto.setIdEmpresa(obtenerIdPadreDelCombo());
                fachada.guardarSucursal(dto);
            }
            case DEPARTAMENTO -> {
                DepartamentoDTO dto = new DepartamentoDTO();
                dto.setNombre(txtNombre.getText().trim());
                dto.setIdSucursal(obtenerIdPadreDelCombo());
                fachada.guardarDepartamento(dto);
            }
            case PUESTO -> {
                PuestoDTO dto = new PuestoDTO();
                dto.setNombre(txtNombre.getText().trim());
                dto.setIdDepartamento(obtenerIdPadreDelCombo());
                fachada.guardarPuesto(dto);
            }
        }
    }

    private void guardarEdicion() {
        if (nodoSeleccionado == null) return;
        switch (nodoSeleccionado.tipo()) {
            case EMPRESA -> {
                EmpresaDTO dto = new EmpresaDTO();
                dto.setId(nodoSeleccionado.id());
                dto.setVersion(nodoSeleccionado.version());
                dto.setNombre(txtNombre.getText().trim());
                fachada.guardarEmpresa(dto);
            }
            case SUCURSAL -> {
                SucursalDTO dto = new SucursalDTO();
                dto.setId(nodoSeleccionado.id());
                dto.setVersion(nodoSeleccionado.version());
                dto.setNombre(txtNombre.getText().trim());
                dto.setUbicacion(txtUbicacion.getText().trim());
                dto.setIdEmpresa(nodoSeleccionado.idPadre());
                fachada.guardarSucursal(dto);
            }
            case DEPARTAMENTO -> {
                DepartamentoDTO dto = new DepartamentoDTO();
                dto.setId(nodoSeleccionado.id());
                dto.setVersion(nodoSeleccionado.version());
                dto.setNombre(txtNombre.getText().trim());
                dto.setIdSucursal(nodoSeleccionado.idPadre());
                fachada.guardarDepartamento(dto);
            }
            case PUESTO -> {
                PuestoDTO dto = new PuestoDTO();
                dto.setId(nodoSeleccionado.id());
                dto.setVersion(nodoSeleccionado.version());
                dto.setNombre(txtNombre.getText().trim());
                dto.setIdDepartamento(nodoSeleccionado.idPadre());
                fachada.guardarPuesto(dto);
            }
        }
    }

    private void prepararFormularioNuevo(TipoNodo tipo, Long idPadre) {
        mostrarPanelDetalle();
        breadcrumbBox.getChildren().clear();
        configurarCabeceraParaTipo(new NodoOrg(tipo, null, "Nuevo " + nombreTipo(tipo), idPadre, null));
        btnEliminar.setVisible(false); btnEliminar.setManaged(false);
        statsBox.setVisible(false);    statsBox.setManaged(false);
        seccionHijos.setVisible(false);seccionHijos.setManaged(false);
        txtNombre.clear();

        switch (tipo) {
            case EMPRESA -> {
                seccionUbicacion.setVisible(false); seccionUbicacion.setManaged(false);
                seccionPadre.setVisible(false);     seccionPadre.setManaged(false);
            }
            case SUCURSAL -> {
                seccionUbicacion.setVisible(true); seccionUbicacion.setManaged(true);
                txtUbicacion.clear();
                cargarComboPadre(TipoNodo.EMPRESA, idPadre);
            }
            case DEPARTAMENTO -> {
                seccionUbicacion.setVisible(false); seccionUbicacion.setManaged(false);
                cargarComboPadre(TipoNodo.SUCURSAL, idPadre);
            }
            case PUESTO -> {
                seccionUbicacion.setVisible(false); seccionUbicacion.setManaged(false);
                cargarComboPadre(TipoNodo.DEPARTAMENTO, idPadre);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarComboPadre(TipoNodo tipoPadre, Long idSeleccionado) {
        seccionPadre.setVisible(true); seccionPadre.setManaged(true);

        switch (tipoPadre) {
            case EMPRESA -> {
                lblPadre.setText("Empresa a la que pertenece");
                List<EmpresaDTO> empresas = fachada.listarEmpresas(null);
                cbxPadre.setItems(FXCollections.observableArrayList(empresas));
                cbxPadre.setConverter(new StringConverter<>() {
                    public String toString(Object o)    { return o instanceof EmpresaDTO e ? e.getNombre() : ""; }
                    public Object fromString(String s)  { return null; }
                });
                if (idSeleccionado != null) {
                    empresas.stream().filter(e -> e.getId().equals(idSeleccionado)).findFirst()
                            .ifPresent(cbxPadre::setValue);
                }
            }
            case SUCURSAL -> {
                lblPadre.setText("Sucursal a la que pertenece");
                
                Long idEmpresa = nodoSeleccionado != null ? (
                        nodoSeleccionado.tipo() == TipoNodo.EMPRESA ? nodoSeleccionado.id() :
                        nodoSeleccionado.tipo() == TipoNodo.SUCURSAL ? nodoSeleccionado.idPadre() : null
                ) : null;
                List<SucursalDTO> sucursales = fachada.listarSucursales(null, idEmpresa);
                cbxPadre.setItems(FXCollections.observableArrayList(sucursales));
                cbxPadre.setConverter(new StringConverter<>() {
                    public String toString(Object o)    { return o instanceof SucursalDTO s ? s.getNombre() : ""; }
                    public Object fromString(String s)  { return null; }
                });
                if (idSeleccionado != null) {
                    sucursales.stream().filter(s -> s.getId().equals(idSeleccionado)).findFirst()
                              .ifPresent(cbxPadre::setValue);
                }
            }
            case DEPARTAMENTO -> {
                lblPadre.setText("Departamento al que pertenece");
                Long idSucursal = nodoSeleccionado != null ? nodoSeleccionado.id() : null;
                List<DepartamentoDTO> deptos = fachada.listarDepartamentos(null, idSucursal);
                cbxPadre.setItems(FXCollections.observableArrayList(deptos));
                cbxPadre.setConverter(new StringConverter<>() {
                    public String toString(Object o)    { return o instanceof DepartamentoDTO d ? d.getNombre() : ""; }
                    public Object fromString(String s)  { return null; }
                });
                if (idSeleccionado != null) {
                    deptos.stream().filter(d -> d.getId().equals(idSeleccionado)).findFirst()
                          .ifPresent(cbxPadre::setValue);
                }
            }
            default -> { seccionPadre.setVisible(false); seccionPadre.setManaged(false); }
        }
    }

    private Long obtenerIdPadreDelCombo() {
        Object sel = cbxPadre.getValue();
        if (sel instanceof EmpresaDTO e)      return e.getId();
        if (sel instanceof SucursalDTO s)     return s.getId();
        if (sel instanceof DepartamentoDTO d) return d.getId();
        return idPadreFormulario;
    }

    private void mostrarStatsEmpresa(NodoOrg nodo) {
        statsBox.getChildren().clear();
        List<SucursalDTO> suc = fachada.listarSucursales(null, nodo.id());
        agregarStatCard(suc.size(), "sucursales", "fas-map-marker-alt");
        statsBox.setVisible(true); statsBox.setManaged(true);
    }

    private void mostrarStatsSucursal(NodoOrg nodo) {
        statsBox.getChildren().clear();
        List<DepartamentoDTO> dep = fachada.listarDepartamentos(null, nodo.id());
        agregarStatCard(dep.size(), "departamentos", "fas-sitemap");
        statsBox.setVisible(true); statsBox.setManaged(true);
    }

    private void mostrarStatsDepartamento(NodoOrg nodo) {
        statsBox.getChildren().clear();
        List<PuestoDTO> puestos = fachada.listarPuestos(nodo.id());
        agregarStatCard(puestos.size(), "puestos", "fas-user");
        statsBox.setVisible(true); statsBox.setManaged(true);
    }

    private void agregarStatCard(int cantidad, String etiqueta, String iconCode) {
        VBox card = new VBox(2);
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(120);

        Label num = new Label(String.valueOf(cantidad));
        num.setGraphic(crearIcono(iconCode, 16));
        num.setContentDisplay(ContentDisplay.TOP);
        num.getStyleClass().add("stat-number");

        Label lbl = new Label(etiqueta);
        lbl.getStyleClass().add("stat-label");

        card.getChildren().addAll(num, lbl);
        statsBox.getChildren().add(card);
    }

    private void mostrarHijosEmpresa(NodoOrg nodo) {
        listaHijos.getChildren().clear();
        lblTituloHijos.setText("Sucursales");
        btnAgregarHijo.setText("Agregar sucursal");
        List<SucursalDTO> sucursales = fachada.listarSucursales(null, nodo.id());
        for (SucursalDTO s : sucursales) {
            listaHijos.getChildren().add(crearTarjetaHijo(s.getNombre(), s.getUbicacion(),
                    "fas-map-marker-alt", "badge-sucursal", "SUCURSAL",
                    () -> seleccionarPorId(TipoNodo.SUCURSAL, s.getId())));
        }
        seccionHijos.setVisible(true); seccionHijos.setManaged(true);
    }

    private void mostrarHijosSucursal(NodoOrg nodo) {
        listaHijos.getChildren().clear();
        lblTituloHijos.setText("Departamentos");
        btnAgregarHijo.setText("Agregar departamento");
        List<DepartamentoDTO> deptos = fachada.listarDepartamentos(null, nodo.id());
        for (DepartamentoDTO d : deptos) {
            listaHijos.getChildren().add(crearTarjetaHijo(d.getNombre(), null,
                    "fas-building", "badge-departamento", "DEPARTAMENTO",
                    () -> seleccionarPorId(TipoNodo.DEPARTAMENTO, d.getId())));
        }
        seccionHijos.setVisible(true); seccionHijos.setManaged(true);
    }

    private void mostrarHijosDepartamento(NodoOrg nodo) {
        listaHijos.getChildren().clear();
        lblTituloHijos.setText("Puestos");
        btnAgregarHijo.setText("Agregar puesto");
        List<PuestoDTO> puestos = fachada.listarPuestos(nodo.id());
        for (PuestoDTO p : puestos) {
            listaHijos.getChildren().add(crearTarjetaHijo(p.getNombre(), null,
                    "fas-user", "badge-puesto", "PUESTO",
                    () -> seleccionarPorId(TipoNodo.PUESTO, p.getId())));
        }
        seccionHijos.setVisible(true); seccionHijos.setManaged(true);
    }

    /** Crea una tarjeta inline para listar un hijo con botón de seleccionar. */
    private HBox crearTarjetaHijo(String nombre, String subtexto, String iconCode, String badgeClass,
                                   String badgeText, Runnable onSeleccionar) {
        HBox card = new HBox(10);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(8, 12, 8, 12));
        card.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 8; " +
                      "-fx-border-color: #E5E7EB; -fx-border-radius: 8; -fx-border-width: 1; -fx-cursor: hand;");

        FontIcon icono = crearIcono(iconCode, 18);
        VBox texto = new VBox(2);
        Label lblNombre = new Label(nombre);
        lblNombre.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        lblNombre.setGraphic(icono);
        lblNombre.setContentDisplay(ContentDisplay.LEFT);
        texto.getChildren().add(lblNombre);
        if (subtexto != null && !subtexto.isBlank()) {
            Label lblSub = new Label(subtexto);
            lblSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #9CA3AF;");
            texto.getChildren().add(lblSub);
        }
        HBox.setHgrow(texto, Priority.ALWAYS);

        Label badge = new Label(badgeText);
        badge.getStyleClass().add(badgeClass);

        Label flecha = new Label("→");
        flecha.setStyle("-fx-text-fill: #9CA3AF; -fx-font-size: 14px;");

        card.getChildren().addAll(texto, badge, flecha);
        card.setOnMouseClicked(e -> onSeleccionar.run());

        // Hover
        card.setOnMouseEntered(e -> card.setStyle("-fx-background-color: #EBF4FB; -fx-background-radius: 8; " +
                "-fx-border-color: #13507d; -fx-border-radius: 8; -fx-border-width: 1; -fx-cursor: hand;"));
        card.setOnMouseExited(e -> card.setStyle("-fx-background-color: #F9FAFB; -fx-background-radius: 8; " +
                "-fx-border-color: #E5E7EB; -fx-border-radius: 8; -fx-border-width: 1; -fx-cursor: hand;"));

        return card;
    }

    private FontIcon crearIcono(String code, int size) {
        FontIcon icon = new FontIcon(code);
        icon.setIconSize(size);
        icon.getStyleClass().addAll("icono-inline", "icono-azul");
        return icon;
    }

    private FontIcon crearIconoTipo(TipoNodo tipo, int size) {
        FontIcon icon = crearIcono(switch (tipo) {
            case EMPRESA      -> "fas-building";
            case SUCURSAL     -> "fas-map-marker-alt";
            case DEPARTAMENTO -> "fas-sitemap";
            case PUESTO       -> "fas-user";
        }, size);
        icon.getStyleClass().add(switch (tipo) {
            case EMPRESA      -> "icono-empresa";
            case SUCURSAL     -> "icono-sucursal";
            case DEPARTAMENTO -> "icono-departamento";
            case PUESTO       -> "icono-puesto";
        });
        return icon;
    }

    private void configurarIconos() {
        if (lblEncabezadoIcono != null) {
            lblEncabezadoIcono.setGraphic(crearIcono("fas-project-diagram", 24));
        }
        if (lblPlaceholderIcon != null) {
            lblPlaceholderIcon.setGraphic(crearIcono("fas-project-diagram", 52));
        }
        if (btnEliminar != null) {
            btnEliminar.setGraphic(crearIcono("fas-trash-alt", 14));
            btnEliminar.setContentDisplay(ContentDisplay.LEFT);
        }
        if (btnNuevaEmpresa != null) {
            btnNuevaEmpresa.setGraphic(crearIcono("fas-plus", 14));
            btnNuevaEmpresa.setContentDisplay(ContentDisplay.LEFT);
        }
        if (btnLimpiar != null) {
            btnLimpiar.setGraphic(crearIcono("fas-times", 14));
            btnLimpiar.setContentDisplay(ContentDisplay.LEFT);
        }
        if (btnGuardar != null) {
            btnGuardar.setGraphic(crearIcono("fas-save", 14));
            btnGuardar.setContentDisplay(ContentDisplay.LEFT);
        }
    }

    /** Selecciona un nodo del árbol por tipo + id. */
    private void seleccionarPorId(TipoNodo tipo, Long id) {
        buscarYSeleccionar(treeOrganizacion.getRoot(), tipo, id);
    }

    private boolean buscarYSeleccionar(TreeItem<NodoOrg> nodo, TipoNodo tipo, Long id) {
        if (nodo == null) return false;
        if (nodo.getValue() != null && nodo.getValue().tipo() == tipo && id.equals(nodo.getValue().id())) {
            treeOrganizacion.getSelectionModel().select(nodo);
            treeOrganizacion.scrollTo(treeOrganizacion.getSelectionModel().getSelectedIndex());
            return true;
        }
        for (TreeItem<NodoOrg> hijo : nodo.getChildren()) {
            if (buscarYSeleccionar(hijo, tipo, id)) return true;
        }
        return false;
    }

    private void actualizarBreadcrumb(TreeItem<NodoOrg> item) {
        breadcrumbBox.getChildren().clear();
        List<String> partes = new ArrayList<>();
        TreeItem<NodoOrg> actual = item;
        while (actual != null && actual.getValue() != null) {
            partes.add(0, actual.getValue().nombre());
            actual = actual.getParent();
        }
        for (int i = 0; i < partes.size(); i++) {
            if (i > 0) {
                Label sep = new Label(" › ");
                sep.getStyleClass().add("breadcrumb-sep");
                breadcrumbBox.getChildren().add(sep);
            }
            Label lbl = new Label(partes.get(i));
            lbl.getStyleClass().add(i == partes.size() - 1 ? "breadcrumb-item-active" : "breadcrumb-item");
            breadcrumbBox.getChildren().add(lbl);
        }
    }
    
    private void configurarCabeceraParaTipo(NodoOrg nodo) {
        lblIconoTipo.setText("");
        lblIconoTipo.setGraphic(crearIconoTipo(nodo.tipo(), 28));
        switch (nodo.tipo()) {
            case EMPRESA -> {
                lblTituloDetalle.setText(nodo.nombre());
                lblBadgeTipo.setText("EMPRESA");
                lblBadgeTipo.getStyleClass().setAll("badge-empresa");
            }
            case SUCURSAL -> {
                lblTituloDetalle.setText(nodo.nombre());
                lblBadgeTipo.setText("SUCURSAL");
                lblBadgeTipo.getStyleClass().setAll("badge-sucursal");
            }
            case DEPARTAMENTO -> {
                lblTituloDetalle.setText(nodo.nombre());
                lblBadgeTipo.setText("DEPARTAMENTO");
                lblBadgeTipo.getStyleClass().setAll("badge-departamento");
            }
            case PUESTO -> {
                lblTituloDetalle.setText(nodo.nombre());
                lblBadgeTipo.setText("PUESTO");
                lblBadgeTipo.getStyleClass().setAll("badge-puesto");
            }
        }
    }

    private void mostrarPlaceholder() {
        panelPlaceholder.setVisible(true);  panelPlaceholder.setManaged(true);
        panelDetalle.setVisible(false);     panelDetalle.setManaged(false);
    }

    private void mostrarPanelDetalle() {
        panelPlaceholder.setVisible(false); panelPlaceholder.setManaged(false);
        panelDetalle.setVisible(true);      panelDetalle.setManaged(true);
    }

    private boolean validarFormulario() {
        boolean ok = true;
        if (txtNombre.getText() == null || txtNombre.getText().trim().isEmpty()) {
            errNombre.setText("El nombre es obligatorio.");
            errNombre.setVisible(true); errNombre.setManaged(true);
            ok = false;
        } else {
            ocultarError(errNombre);
        }

        if (seccionPadre.isVisible() && cbxPadre.getValue() == null) {
            errPadre.setText("Debes seleccionar el padre.");
            errPadre.setVisible(true); errPadre.setManaged(true);
            ok = false;
        } else {
            ocultarError(errPadre);
        }

        return ok;
    }

    private void ocultarError(Label lbl) {
        if (lbl != null) { lbl.setVisible(false); lbl.setManaged(false); }
    }

    private SucursalDTO buscarSucursalPorId(Long id) {
        if (nodoSeleccionado == null || nodoSeleccionado.idPadre() == null) return null;
        return fachada.listarSucursales(null, nodoSeleccionado.idPadre())
                      .stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
    }

    private String nombreTipo(TipoNodo tipo) {
        return switch (tipo) {
            case EMPRESA      -> "empresa";
            case SUCURSAL     -> "sucursal";
            case DEPARTAMENTO -> "departamento";
            case PUESTO       -> "puesto";
        };
    }

    private void mostrarSpinner(boolean visible) {
        Platform.runLater(() -> {
            if (progressArbol != null) {
                progressArbol.setVisible(visible);
                progressArbol.setManaged(visible);
            }
        });
    }

    private void mostrarExito(String mensaje) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Éxito"); a.setHeaderText(null); a.setContentText(mensaje); a.showAndWait();
    }

    @Override
    public void mostrarError(String titulo, String mensaje) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error"); a.setHeaderText(titulo); a.setContentText(mensaje); a.showAndWait();
    }

    private boolean confirmarEliminacion(String tipo, String nombre) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.setTitle("Confirmar eliminación");
        a.setHeaderText("¿Eliminar " + tipo + "?");
        a.setContentText("¿Está seguro de eliminar \"" + nombre + "\"? Esta acción no se puede deshacer.");
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    @Override
    public void setDashBoard(MenuController dbc) { this.dbc = dbc; }
}
