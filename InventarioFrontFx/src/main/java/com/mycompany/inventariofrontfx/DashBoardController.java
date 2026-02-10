package com.mycompany.inventariofrontfx;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * FXML Controller class
 *
 * @author JMorales
 */
public class DashBoardController implements Initializable {

    @FXML
    private ToggleGroup menu;
    @FXML
    private ScrollPane centerContainer;

    @FXML
    private ToggleButton btnDashBoard;
    @FXML
    private ToggleButton btnInventario;
    @FXML
    private ToggleButton btnColaboradores;
    @FXML
    private ToggleButton btnAsignaciones;
    @FXML
    private ToggleButton btnOrganizacion;
    @FXML
    private ToggleButton btnConfiguracion;
    @FXML
    private ToggleButton btnUsuarios;
    @FXML
    private ToggleButton btnAuditoria;
    @FXML
    private Label tituloSeccion;
    @FXML
    private Label subTituloSeccion;
    @FXML
    private VBox containerSubMenu;
    @FXML
    private Label nombreUser;
    @FXML
    private Label rolUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        colocarIconos();

        menu.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                ToggleButton selectedBtn = (ToggleButton) newValue;
                manejarNavegacion(selectedBtn.getText());
            } else if (oldValue != null) {
                oldValue.setSelected(true);
            }
        });

        btnConfiguracion.setOnAction(event -> {
            toggleSubMenu();
        });
    }

    private void colocarIconos() {
        btnDashBoard.setGraphic(crearIcono("fas-chart-line"));
        btnInventario.setGraphic(crearIcono("fas-boxes"));
        btnColaboradores.setGraphic(crearIcono("fas-users"));
        btnAsignaciones.setGraphic(crearIcono("fas-clipboard-list"));
        btnOrganizacion.setGraphic(crearIcono("fas-sitemap"));
        btnConfiguracion.setGraphic(crearIcono("fas-cog"));
        btnUsuarios.setGraphic(crearIcono("fas-user-circle"));
        btnAuditoria.setGraphic(crearIcono("fas-eye"));

        menu.getToggles().forEach(toggle -> {
            if (toggle instanceof ToggleButton btn) {
                btn.setGraphicTextGap(10);
            }
        });
    }

    private FontIcon crearIcono(String iconCode) {
        FontIcon icon = new FontIcon(iconCode);
        icon.getStyleClass().add("menu-icon");
        return icon;
    }

    private void manejarNavegacion(String opcion) {
        System.out.println("Navegando a: " + opcion);

        switch (opcion) {
            case "DashBoard":
                cambiarDePantalla(null, "Dashboard", "Resumen general del inventario de TI");

                break;
            case "Inventario":
                cambiarDePantalla("inventario/Inventario.fxml", "Inventario", "Gestión de equipos de TI");

                break;
            case "Colaboradores":
                cambiarDePantalla(null, "Colaboradores", "Gestión de colaboradores y personal");

                break;
            case "Asignaciones":
                cambiarDePantalla(null, "Asignación de Equipo", "Asignar equipos a trabajadores");

                break;
            case "Organización":
                cambiarDePantalla(null, "Estructura Organizacional", "Gestión de empresas, sucursales, departamentos y puestos");

                break;
            case "Usuarios":
                cambiarDePantalla(null, "Usuarios del Sistema", "Gestión de administradores, técnicos y operarios");

                break;
            case "Auditoria":
                cambiarDePantalla(null, "Auditoría del Sistema", "Registro completo de movimientos y cambios");

                break;
            default:
                break;
        }
    }

    public void cambiarDePantalla(String rutaFXML, String titulo, String subtitulo) {
        try {
            tituloSeccion.setText(titulo);
            subTituloSeccion.setText(subtitulo);

            if (rutaFXML != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
                Parent vista = loader.load();

                Object controller = loader.getController();
                if (controller instanceof BaseController baseController) {
                    baseController.setDashBoard(this);
                }

                // Mostrar la vista
                centerContainer.setContent(vista);

                // Resetear scrolleo
                centerContainer.setVvalue(0);

            }

        } catch (IOException e) {
            System.err.println("Error cargando la vista: " + rutaFXML);
        }
    }

    public void cambiarDePantalla(String rutaFXML) {
        try {
            if (rutaFXML != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
                Parent vista = loader.load();

                Object controller = loader.getController();
                if (controller instanceof BaseController baseController) {
                    baseController.setDashBoard(this);
                    
                }

                // Mostrar la vista
                centerContainer.setContent(vista);

                // Resetear scrolleo
                centerContainer.setVvalue(0);

            }

        } catch (IOException e) {
            System.err.println("Error cargando la vista: " + rutaFXML);
        }
    }

    private void toggleSubMenu() {
        boolean estaDesplegado = containerSubMenu.isVisible();

        containerSubMenu.setVisible(!estaDesplegado);
        containerSubMenu.setManaged(!estaDesplegado);
    }

    public ToggleGroup getMenu() {
        return menu;
    }

    public void setMenu(ToggleGroup menu) {
        this.menu = menu;
    }

    public ScrollPane getCenterContainer() {
        return centerContainer;
    }

    public void setCenterContainer(ScrollPane centerContainer) {
        this.centerContainer = centerContainer;
    }

    public ToggleButton getBtnDashBoard() {
        return btnDashBoard;
    }

    public void setBtnDashBoard(ToggleButton btnDashBoard) {
        this.btnDashBoard = btnDashBoard;
    }

    public ToggleButton getBtnInventario() {
        return btnInventario;
    }

    public void setBtnInventario(ToggleButton btnInventario) {
        this.btnInventario = btnInventario;
    }

    public ToggleButton getBtnColaboradores() {
        return btnColaboradores;
    }

    public void setBtnColaboradores(ToggleButton btnColaboradores) {
        this.btnColaboradores = btnColaboradores;
    }

    public ToggleButton getBtnAsignaciones() {
        return btnAsignaciones;
    }

    public void setBtnAsignaciones(ToggleButton btnAsignaciones) {
        this.btnAsignaciones = btnAsignaciones;
    }

    public ToggleButton getBtnOrganizacion() {
        return btnOrganizacion;
    }

    public void setBtnOrganizacion(ToggleButton btnOrganizacion) {
        this.btnOrganizacion = btnOrganizacion;
    }

    public ToggleButton getBtnConfiguracion() {
        return btnConfiguracion;
    }

    public void setBtnConfiguracion(ToggleButton btnConfiguracion) {
        this.btnConfiguracion = btnConfiguracion;
    }

    public ToggleButton getBtnUsuarios() {
        return btnUsuarios;
    }

    public void setBtnUsuarios(ToggleButton btnUsuarios) {
        this.btnUsuarios = btnUsuarios;
    }

    public ToggleButton getBtnAuditoria() {
        return btnAuditoria;
    }

    public void setBtnAuditoria(ToggleButton btnAuditoria) {
        this.btnAuditoria = btnAuditoria;
    }

    public Label getTituloSeccion() {
        return tituloSeccion;
    }

    public void setTituloSeccion(Label tituloSeccion) {
        this.tituloSeccion = tituloSeccion;
    }

    public Label getSubTituloSeccion() {
        return subTituloSeccion;
    }

    public void setSubTituloSeccion(Label subTituloSeccion) {
        this.subTituloSeccion = subTituloSeccion;
    }

    public VBox getContainerSubMenu() {
        return containerSubMenu;
    }

    public void setContainerSubMenu(VBox containerSubMenu) {
        this.containerSubMenu = containerSubMenu;
    }

    public Label getNombreUser() {
        return nombreUser;
    }

    public void setNombreUser(Label nombreUser) {
        this.nombreUser = nombreUser;
    }

    public Label getRolUser() {
        return rolUser;
    }

    public void setRolUser(Label rolUser) {
        this.rolUser = rolUser;
    }
}
