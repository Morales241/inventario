package com.mycompany.inventariofrontfx.asignaciones;

import Dtos.AsignacionDTO;
import Dtos.EquipoBaseDTO;
import Dtos.UsuarioDTO;
import Enums.EstadoEquipo;
import InterfacesFachada.IFachadaEquipos;
import InterfacesFachada.IFachadaPersonas;
import InterfacesFachada.IFachadaPrestamos;
import com.mycompany.inventariofrontfx.menu.MenuController;
import fabricaFachadas.FabricaFachadas;
import interfaces.BaseController;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

public class FormAsignacionesController implements Initializable, BaseController {

    private MenuController dbc;
    
    private final IFachadaPrestamos fachadaPrestamos = FabricaFachadas.getFachadaPrestamos();
    private final IFachadaPersonas fachadaPersonas = FabricaFachadas.getFachadaPersonas();
    private final IFachadaEquipos fachadaEquipos = FabricaFachadas.getFachadaEquipos();

    @FXML
    private TextField txtBuscarUsuario;
    @FXML
    private ListView<UsuarioDTO> listUsuarios;
    @FXML
    private TextField txtBuscarEquipo;
    @FXML
    private ListView<EquipoBaseDTO> listEquipos;
    @FXML
    private DatePicker dateEntrega;
    @FXML
    private Button btnConfirmar;
    @FXML
    private Button btnCancelar;
    @FXML
    private Label iconoTrabajador;
    @FXML
    private Label iconoEquipo;
    @FXML
    private Label iconoDetalles;
    
    // Labels para resumen
    @FXML
    private Label resumenTrabajador;
    @FXML
    private Label resumenEquipo;
    @FXML
    private Label resumenFechaEntrega;
    @FXML
    private Label lblUsuarioSeleccionado;
    @FXML
    private Label lblEquipoSeleccionado;

    private UsuarioDTO usuarioSeleccionado;
    private EquipoBaseDTO equipoSeleccionado;
    private List<UsuarioDTO> usuariosCache;
    private List<EquipoBaseDTO> equiposCache;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarListas();
        cargarDatosIniciales();
        configurarIconos();
        
        dateEntrega.setValue(LocalDate.now());
        actualizarResumen();
        
        txtBuscarUsuario.textProperty().addListener((obs, old, newVal) -> 
            buscarUsuarios(newVal));
        
        txtBuscarEquipo.textProperty().addListener((obs, old, newVal) -> 
            buscarEquipos(newVal));
        
        listUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            usuarioSeleccionado = selected;
            actualizarResumen();
            if (selected != null) {
                lblUsuarioSeleccionado.setText(selected.getNombre() + " - " + selected.getNoNomina());
            } else {
                lblUsuarioSeleccionado.setText("Ninguno seleccionado");
            }
        });
        
        listEquipos.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            equipoSeleccionado = selected;
            actualizarResumen();
            if (selected != null) {
                lblEquipoSeleccionado.setText("GRY: " + selected.getGryFormateado() + " - " + selected.getNombreModelo());
            } else {
                lblEquipoSeleccionado.setText("Ninguno seleccionado");
            }
        });
        
        dateEntrega.valueProperty().addListener((obs, old, newVal) -> 
            actualizarResumen());
    }

    private void configurarListas() {
        listUsuarios.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(UsuarioDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getNombre() + " - " + item.getNoNomina());
                }
            }
        });

        listEquipos.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(EquipoBaseDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("GRY: " + item.getGryFormateado() + " - " + item.getNombreModelo() + 
                           " (" + item.getIdentificador() + ")");
                }
            }
        });
    }

    private void cargarDatosIniciales() {
        buscarUsuarios("");
        buscarEquipos("");
    }

    private void buscarUsuarios(String filtro) {
        Task<List<UsuarioDTO>> task = new Task<>() {
            @Override
            protected List<UsuarioDTO> call() {
                usuariosCache = fachadaPersonas.buscarUsuarios(filtro)
                    .stream()
                    .filter(UsuarioDTO::getActivo)
                    .collect(Collectors.toList());
                return usuariosCache;
            }
        };

        task.setOnSucceeded(e -> {
            listUsuarios.setItems(FXCollections.observableArrayList(task.getValue()));
        });

        new Thread(task).start();
    }

    private void buscarEquipos(String filtro) {
        Task<List<EquipoBaseDTO>> task = new Task<>() {
            @Override
            protected List<EquipoBaseDTO> call() {
                equiposCache = fachadaEquipos.buscarConFiltros(
                    filtro, null, null, EstadoEquipo.EN_STOCK);
                return equiposCache;
            }
        };

        task.setOnSucceeded(e -> {
            listEquipos.setItems(FXCollections.observableArrayList(task.getValue()));
        });

        new Thread(task).start();
    }

    private void actualizarResumen() {
        if (usuarioSeleccionado != null) {
            resumenTrabajador.setText(usuarioSeleccionado.getNombre());
        } else {
            resumenTrabajador.setText("No seleccionado");
        }
        
        if (equipoSeleccionado != null) {
            resumenEquipo.setText("GRY: " + equipoSeleccionado.getGryFormateado() + " - " + 
                                  equipoSeleccionado.getNombreModelo());
        } else {
            resumenEquipo.setText("No seleccionado");
        }
        
        if (dateEntrega.getValue() != null) {
            resumenFechaEntrega.setText(dateEntrega.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            resumenFechaEntrega.setText("No definida");
        }
    }

    private FontIcon crearIcono(String code, int size) {
        FontIcon icon = new FontIcon(code);
        icon.setIconSize(size);
        icon.getStyleClass().add("icono-azul");
        return icon;
    }

    private void configurarIconos() {
        if (iconoTrabajador != null) {
            iconoTrabajador.setGraphic(crearIcono("fas-user-tie", 24));
            iconoTrabajador.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
        if (iconoEquipo != null) {
            iconoEquipo.setGraphic(crearIcono("fas-laptop", 24));
            iconoEquipo.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
        if (iconoDetalles != null) {
            iconoDetalles.setGraphic(crearIcono("fas-clipboard-list", 20));
            iconoDetalles.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        }
    }

    @FXML
    private void confirmarAsignacion() {
        if (!validarAsignacion()) {
            return;
        }

        try {
            fachadaPrestamos.asignarEquipo(
                equipoSeleccionado.getIdEquipo(), 
                usuarioSeleccionado.getId()
            );
            
            mostrarExito("Equipo asignado correctamente");
            
            // Ir a la pantalla de responsiva
            mostrarResponsiva();
            
        } catch (Exception e) {
            mostrarError("Error al asignar: " + e.getMessage());
        }
    }

    /**
     * Muestra la pantalla de responsiva después de una asignación exitosa
     */
    private void mostrarResponsiva() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ResponsivaAsignacion.fxml"));
            Parent vista = loader.load();
            
            ResponsivaAsignacionController controller = loader.getController();
            controller.setDashBoard(dbc);
            controller.setDatosAsignacion(usuarioSeleccionado, equipoSeleccionado, dateEntrega.getValue());
            
            dbc.getCenterContainer().setContent(vista);
            dbc.getCenterContainer().setVvalue(0);
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al mostrar responsiva");
            // Si hay error, volver a la lista
            volverALista();
        }
    }

    private boolean validarAsignacion() {
        if (usuarioSeleccionado == null) {
            mostrarAdvertencia("Debe seleccionar un usuario");
            return false;
        }
        
        if (equipoSeleccionado == null) {
            mostrarAdvertencia("Debe seleccionar un equipo");
            return false;
        }
        
        if (dateEntrega.getValue() == null) {
            mostrarAdvertencia("Debe seleccionar fecha de entrega");
            return false;
        }
        
        // Validar que el usuario no tenga ya este equipo
        List<AsignacionDTO> asignacionesActivas = 
            fachadaPrestamos.obtenerEquiposDeUsuarios(usuarioSeleccionado.getId());
        
        boolean yaAsignado = asignacionesActivas.stream()
            .anyMatch(a -> a.getIdEquipo().equals(equipoSeleccionado.getIdEquipo()));
        
        if (yaAsignado) {
            mostrarAdvertencia("Este equipo ya está asignado al usuario seleccionado");
            return false;
        }
        
        return true;
    }

    @FXML
    private void cancelar() {
        volverALista();
    }

    private void volverALista() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Asignaciones.fxml"));
            Parent vista = loader.load();
            
            Object controller = loader.getController();
            if (controller instanceof BaseController baseController) {
                baseController.setDashBoard(dbc);
            }

            dbc.getCenterContainer().setContent(vista);
            dbc.getCenterContainer().setVvalue(0);
            
        } catch (Exception e) {
            e.printStackTrace();
            mostrarError("Error al volver");
        }
    }

    private void mostrarAdvertencia(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Validación");
        alert.setHeaderText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Éxito");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @Override
    public void setDashBoard(MenuController dbc) {
        this.dbc = dbc;
    }
}