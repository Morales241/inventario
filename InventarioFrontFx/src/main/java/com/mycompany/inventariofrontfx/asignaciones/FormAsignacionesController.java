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
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
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
    private DatePicker dateRegreso;
    @FXML
    private Button btnConfirmar;

    private UsuarioDTO usuarioSeleccionado;
    private EquipoBaseDTO equipoSeleccionado;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarListas();
        cargarDatosIniciales();
        
        dateEntrega.setValue(LocalDate.now());
        
        txtBuscarUsuario.textProperty().addListener((obs, old, newVal) -> 
            buscarUsuarios(newVal));
        
        txtBuscarEquipo.textProperty().addListener((obs, old, newVal) -> 
            buscarEquipos(newVal));
        
        listUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            usuarioSeleccionado = selected;
        });
        
        listEquipos.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            equipoSeleccionado = selected;
        });
    }

    private void configurarListas() {
        // Personalizar cómo se muestran los usuarios en la lista
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

        // Personalizar cómo se muestran los equipos en la lista
        listEquipos.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(EquipoBaseDTO item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText("GRY: " + item.getGry() + " - " + item.getNombreModelo());
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
                return fachadaPersonas.buscarUsuarios(filtro)
                    .stream()
                    .filter(UsuarioDTO::getActivo)
                    .collect(Collectors.toList());
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
                return fachadaEquipos.buscarConFiltros(
                    filtro, null, null, EstadoEquipo.EN_STOCK);
            }
        };

        task.setOnSucceeded(e -> {
            listEquipos.setItems(FXCollections.observableArrayList(task.getValue()));
        });

        new Thread(task).start();
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
            
            mostrarExito("Equipo asignado correctamente a " + usuarioSeleccionado.getNombre());
            
            // Volver a la pantalla anterior
            volverALista();
            
        } catch (Exception e) {
            mostrarError("Error al asignar: " + e.getMessage());
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
            javafx.scene.Parent vista = loader.load();
            
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