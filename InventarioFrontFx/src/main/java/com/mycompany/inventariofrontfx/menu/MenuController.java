package com.mycompany.inventariofrontfx.menu;

import Dtos.CuentaSistemaDTO;
import Utilidades.SesionActual;
import interfaces.BaseController;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * Controlador del menú principal.
 *
 * CAMBIOS: 1. aplicarSesion(CuentaSistemaDTO): recibe la cuenta del login,
 * muestra nombre + rol en el header y llama a aplicarPermisos() para ocultar o
 * deshabilitar secciones según el rol. 2. Organización y Cuentas ahora navegan
 * a sus FXMLs reales. 3. El botón "Cerrar Sesión" tiene lógica real: limpia
 * SesionActual y vuelve al login. 4. aplicarPermisos() es el único lugar donde
 * se define qué puede ver cada rol. Cuando el alumno defina los permisos
 * exactos, solo hay que editar ese método.
 */
public class MenuController implements Initializable {

    @FXML
    private ToggleGroup menu;
    @FXML
    private ScrollPane centerContainer;
    @FXML
    private ToggleButton btnDashBoard;
    @FXML
    private ToggleButton btnInventario;
    @FXML
    private ToggleButton btnCuentas;
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

        menu.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                manejarNavegacion(((ToggleButton) newVal).getText());
            } else if (oldVal != null) {
                oldVal.setSelected(true);
            }
        });

        btnConfiguracion.setOnAction(e -> toggleSubMenu());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Sesión y permisos
    // ─────────────────────────────────────────────────────────────────────────
    /**
     * Llamado por LogInController justo después de cargar el menú. Muestra los
     * datos del usuario en el header y aplica permisos.
     */
    public void aplicarSesion(CuentaSistemaDTO cuenta) {
        if (cuenta == null) {
            return;
        }

        // Mostrar nombre y rol en el header
        if (nombreUser != null) {
            nombreUser.setText(cuenta.getUsername());
        }
        if (rolUser != null) {
            rolUser.setText(formatearRol(cuenta.getRol()));
        }

        aplicarPermisos(cuenta.getRol());
    }

    /**
     * Define qué botones del menú puede ver cada rol.
     */
    private void aplicarPermisos(String rol) {
        if (rol == null) {
            return;
        }

        switch (rol.toUpperCase()) {
            case "ADMIN" -> {
                // Admin ve todo — no ocultar nada
            }
            case "OPERARIO" -> {
                // Ocultar configuración, organización, cuentas, auditoría
                ocultarBoton(btnConfiguracion);
                ocultarBoton(btnOrganizacion);
                ocultarBoton(btnAuditoria);
                if (containerSubMenu != null) {
                    containerSubMenu.setVisible(false);
                    containerSubMenu.setManaged(false);
                }
            }
            case "INVITADO" -> {
                // Solo puede ver Inventario
                ocultarBoton(btnAsignaciones);
                ocultarBoton(btnCuentas);
                ocultarBoton(btnConfiguracion);
                ocultarBoton(btnOrganizacion);
                ocultarBoton(btnAuditoria);
                if (containerSubMenu != null) {
                    containerSubMenu.setVisible(false);
                    containerSubMenu.setManaged(false);
                }
            }
        }
    }

    private void ocultarBoton(ToggleButton btn) {
        if (btn != null) {
            btn.setVisible(false);
            btn.setManaged(false);
        }
    }

    private String formatearRol(String rol) {
        if (rol == null) {
            return "";
        }
        return switch (rol.toUpperCase()) {
            case "ADMIN" ->
                "🔑 Administrador";
            case "OPERARIO" ->
                "📦 Operario";
            case "INVITADO" ->
                "👁 Invitado";
            default ->
                rol;
        };
    }

    private void manejarNavegacion(String opcion) {
        switch (opcion) {
            case "DashBoard" ->
                cambiarDePantalla(null, "Dashboard", "Resumen general del inventario de TI");
            case "Inventario" ->
                cambiarDePantalla("/com/mycompany/inventariofrontfx/inventario/Inventario.fxml",
                        "Inventario", "Gestión de equipos de TI");
            case "Usuarios" ->
                cambiarDePantalla("/com/mycompany/inventariofrontfx/usuarios/Usuarios.fxml",
                        "Usuarios", "Gestión de usuarios y personal");
            case "Asignaciones" ->
                cambiarDePantalla("/com/mycompany/inventariofrontfx/asignaciones/Asignaciones.fxml",
                        "Asignación de Equipo", "Asignar equipos a trabajadores");
            case "Organización" ->
                cambiarDePantalla("/com/mycompany/inventariofrontfx/organizacion/Organizacion.fxml",
                        "Estructura Organizacional", "Gestión de empresas, sucursales, departamentos y puestos");
            case "Cuentas" ->
                cambiarDePantalla("/com/mycompany/inventariofrontfx/cuentas/Cuentas.fxml",
                        "Cuentas del Sistema", "Administración de usuarios del sistema y roles de acceso");
            case "Auditoria" ->
                cambiarDePantalla(null, "Auditoría del Sistema", "Registro de movimientos y cambios");
            default -> {
            }
        }
    }

    @FXML
    public void cerrarSesion() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cerrar sesión");
        confirm.setHeaderText("¿Deseas cerrar la sesión?");
        confirm.setContentText("Se regresará a la pantalla de inicio.");

        confirm.showAndWait().filter(btn -> btn == ButtonType.OK).ifPresent(b -> {
//            SesionActual.cerrarSesion();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mycompany/inventariofrontfx/LogIn.fxml"));
                Parent loginVista = loader.load();
                Stage stage = (Stage) centerContainer.getScene().getWindow();
                stage.setScene(new Scene(loginVista));
                stage.setMaximized(false);
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    public void cambiarDePantalla(String rutaFXML, String titulo, String subtitulo) {
        try {
            if (titulo != null) {
                tituloSeccion.setText(titulo);
            }
            if (subtitulo != null) {
                subTituloSeccion.setText(subtitulo);
            }

            if (rutaFXML != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
                Parent vista = loader.load();

                Object controller = loader.getController();
                if (controller instanceof BaseController bc) {
                    bc.setDashBoard(this);
                }

                centerContainer.setContent(vista);
                centerContainer.setVvalue(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cambiarDePantalla(String rutaFXML) throws IOException {
        if (rutaFXML != null) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent vista = loader.load();
            Object controller = loader.getController();
            if (controller instanceof BaseController bc) {
                bc.setDashBoard(this);
            }
            centerContainer.setContent(vista);
            centerContainer.setVvalue(0);
        }
    }

    private void toggleSubMenu() {
        boolean visible = containerSubMenu.isVisible();
        containerSubMenu.setVisible(!visible);
        containerSubMenu.setManaged(!visible);
    }

    private void colocarIconos() {
        btnDashBoard.setGraphic(crearIcono("fas-chart-line"));
        btnInventario.setGraphic(crearIcono("fas-boxes"));
        btnCuentas.setGraphic(crearIcono("fas-users"));
        btnAsignaciones.setGraphic(crearIcono("fas-clipboard-list"));
        btnOrganizacion.setGraphic(crearIcono("fas-sitemap"));
        btnConfiguracion.setGraphic(crearIcono("fas-cog"));
        btnUsuarios.setGraphic(crearIcono("fas-user-circle"));
        btnAuditoria.setGraphic(crearIcono("fas-eye"));
        menu.getToggles().forEach(t -> {
            if (t instanceof ToggleButton btn) {
                btn.setGraphicTextGap(10);
            }
        });
    }

    private FontIcon crearIcono(String code) {
        FontIcon icon = new FontIcon(code);
        icon.getStyleClass().add("menu-icon");
        return icon;
    }

    public ToggleGroup getMenu() {
        return menu;
    }

    public void setMenu(ToggleGroup m) {
        this.menu = m;
    }

    public ScrollPane getCenterContainer() {
        return centerContainer;
    }

    public void setCenterContainer(ScrollPane sp) {
        this.centerContainer = sp;
    }

    public ToggleButton getBtnInventario() {
        return btnInventario;
    }

    public ToggleButton getBtnCuentas() {
        return btnCuentas;
    }

    public ToggleButton getBtnAsignaciones() {
        return btnAsignaciones;
    }

    public ToggleButton getBtnOrganizacion() {
        return btnOrganizacion;
    }

    public ToggleButton getBtnConfiguracion() {
        return btnConfiguracion;
    }

    public ToggleButton getBtnUsuarios() {
        return btnUsuarios;
    }

    public ToggleButton getBtnAuditoria() {
        return btnAuditoria;
    }

    public Label getTituloSeccion() {
        return tituloSeccion;
    }

    public Label getSubTituloSeccion() {
        return subTituloSeccion;
    }

    public VBox getContainerSubMenu() {
        return containerSubMenu;
    }

    public Label getNombreUser() {
        return nombreUser;
    }

    public Label getRolUser() {
        return rolUser;
    }

    public void setNombreUser(Label l) {
        this.nombreUser = l;
    }

    public void setRolUser(Label l) {
        this.rolUser = l;
    }
}
