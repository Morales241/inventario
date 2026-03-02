package com.mycompany.inventariofrontfx.colaboradores;

import Dtos.EquipoBaseDTO;
import Dtos.PuestoDTO;
import Dtos.UsuarioDTO;
import InterfacesFachada.IFachadaOrganizacion;
import InterfacesFachada.IFachadaPersonas;
import InterfacesFachada.IFachadaPrestamos;
import com.mycompany.inventariofrontfx.inventario.InventarioController;
import fabricaFachadas.FabricaFachadas;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * FXML Controller class
 *
 * @author tacot
 */
public class ColaboradoresController implements Initializable {

    @FXML
    private TextField txtFiltro;
    @FXML
    private TableView<UsuarioDTO> tablaColaboradores;
    @FXML
    private TableColumn<UsuarioDTO, Long> colId;
    @FXML
    private TableColumn<UsuarioDTO, String> colNombre;
    @FXML
    private TableColumn<UsuarioDTO, String> colPuesto;
    @FXML
    private TableColumn<UsuarioDTO, Integer> colEquiposAsignados;
    ;
    @FXML
    private TableColumn<UsuarioDTO, Void> colAcciones;

    private ObservableList<UsuarioDTO> listaColaboradores;

    private final IFachadaPersonas fachadaColaborador
            = FabricaFachadas.getFachadaPersonas();

    private final IFachadaPrestamos fachadaPrestamos
            = FabricaFachadas.getFachadaPrestamos();

    private final IFachadaOrganizacion fachadaOrganizacion
            = FabricaFachadas.getFachadaOrganizacion();

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        configurarColumnas();
        configurarFiltroReactivo();
        cargarDatos();
        configurarAcciones();
    }

    private void configurarColumnas() {

        colId.setCellValueFactory(data
                -> new SimpleObjectProperty<>(data.getValue().getId()));

        colNombre.setCellValueFactory(data
                -> new SimpleObjectProperty<>(data.getValue().getNombre()));

        colPuesto.setCellValueFactory(data
                -> new SimpleObjectProperty<>(data.getValue().getNombrePuesto()));

        colEquiposAsignados.setCellValueFactory(data
                -> new SimpleObjectProperty<>(data.getValue().getNumeroDeEquipos()));
    }

    private void configurarAcciones() {

        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final FontIcon viewIcon = new FontIcon("fas-eye");
            private final FontIcon editIcon = new FontIcon("fas-edit");
            private final FontIcon deleteIcon = new FontIcon("fas-trash");

            private final Button btnVer = new Button("", viewIcon);
            private final Button btnEditar = new Button("", editIcon);
            private final Button btnEliminar = new Button("", deleteIcon);

            private final HBox container = new HBox(8, btnVer, btnEditar, btnEliminar);

            {
                viewIcon.setIconSize(16);
                editIcon.setIconSize(16);
                deleteIcon.setIconSize(16);

                btnVer.getStyleClass().add("btn-ver");
                btnEditar.getStyleClass().add("btn-editar");
                btnEliminar.getStyleClass().add("btn-eliminar");

                Tooltip.install(btnVer, new Tooltip("Ver detalles"));
                Tooltip.install(btnEditar, new Tooltip("Editar equipo"));
                Tooltip.install(btnEliminar, new Tooltip("Eliminar equipo"));

                container.setAlignment(Pos.CENTER);

                btnVer.setOnAction(e -> {
                    UsuarioDTO usuario = getTableRow().getItem();
                    if (usuario != null) {
//                        visualizarEquipo(equipo);
                    }
                });

                btnEditar.setOnAction(e -> {
                    UsuarioDTO usuario = getTableRow().getItem();
                    if (usuario != null) {
//                        cargarEquipoParaEditar(usuario);
                    }
                });

                btnEliminar.setOnAction(e -> {
                    UsuarioDTO usuario = getTableRow().getItem();
                    if (usuario != null) {
//                        confirmarEliminacion(usuario);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });
    }

    private void cargarDatos() {
        listaColaboradores = FXCollections.observableArrayList(
                fachadaColaborador.buscarUsuarios(null)
        );

        listaColaboradores.forEach(c -> {

            int numeroEquipos = fachadaPrestamos.obtenerEquiposDeUsuarios(c.getId()).size();

            String nombrePuesto = fachadaOrganizacion.buscarPuestoEspecifico(c.getIdPuesto()).getNombre();

            c.setNumeroDeEquipos(numeroEquipos);
            c.setNombrePuesto(nombrePuesto);
        });

        tablaColaboradores.setItems(listaColaboradores);
    }

    private void configurarFiltroReactivo() {
        txtFiltro.textProperty().addListener((obs, oldValue, newValue) -> {
            aplicarFiltro(newValue);
        });
    }

    private void aplicarFiltro(String texto) {
        cargarDatosAsync();
    }

    private void cargarDatosAsync() {

        Task<List<UsuarioDTO>> task = new Task<>() {
            @Override
            protected List<UsuarioDTO> call() {

                return fachadaColaborador.buscarUsuarios(txtFiltro.getText());

            }
        };

        tablaColaboradores.setDisable(true);

        task.setOnSucceeded(e -> {
            tablaColaboradores.getItems().setAll(task.getValue());
            tablaColaboradores.setDisable(false);
        });

        task.setOnFailed(e -> {
            tablaColaboradores.setDisable(false);
            task.getException().printStackTrace();
        });

        new Thread(task).start();
    }

    private void confirmarEliminacion(UsuarioDTO usuario) {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Eliminar equipo");
        alert.setContentText(
                "¿Desea eliminar el Usuario: "
                + usuario.getNombre() + "?");

        alert.showAndWait()
                .filter(btn -> btn == ButtonType.OK)
                .ifPresent(b -> {

                    try {
                        fachadaColaborador.cambiarEstadoUsuario(Long.MIN_VALUE, false);
                    } catch (Exception ex) {
                        System.getLogger(InventarioController.class.getName()).log(System.Logger.Level.ERROR, (String) null, ex);
                    }
                    cargarDatos();
                });
    }

    private void cargarEquipoParaEditar(UsuarioDTO usuario) {

    }

    @FXML
    private void agregarEquipo() throws IOException {
        cargarDatos();
    }
}
