package com.mycompany.inventariofrontfx.dashboard;

import Dtos.AsignacionDTO;
import Dtos.EquipoBaseDTO;
import Enums.EstadoEquipo;
import Enums.TipoEquipo;
import InterfacesFachada.IFachadaEquipos;
import InterfacesFachada.IFachadaPersonas;
import InterfacesFachada.IFachadaPrestamos;
import com.mycompany.inventariofrontfx.menu.MenuController;
import fabricaFachadas.FabricaFachadas;
import interfaces.BaseController;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;

/**
 * Controlador del Dashboard principal.
 * Muestra KPIs en tiempo real: equipos, asignaciones y usuarios.
 */
public class DashboardController implements Initializable, BaseController {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ── Fachadas ──────────────────────────────────────────────────────────────
    private final IFachadaEquipos   fachadaEquipos   = FabricaFachadas.getFachadaEquipos();
    private final IFachadaPersonas  fachadaPersonas  = FabricaFachadas.getFachadaPersonas();
    private final IFachadaPrestamos fachadaPrestamos = FabricaFachadas.getFachadaPrestamos();

    private MenuController menuController;

    // ── KPI labels ────────────────────────────────────────────────────────────
    @FXML private Label lblTotalEquipos;
    @FXML private Label lblSubTotalEquipos;
    @FXML private Label lblAsignados;
    @FXML private Label lblPctAsignados;
    @FXML private Label lblEnStock;
    @FXML private Label lblPctStock;
    @FXML private Label lblUsuarios;
    @FXML private Label lblAsignaciones;

    // ── Distribución por tipo ─────────────────────────────────────────────────
    @FXML private Label lblLaptop;
    @FXML private Label lblDesktop;
    @FXML private Label lblMovil;
    @FXML private Label lblServidor;
    @FXML private Label lblOtros;

    @FXML private HBox barLaptop;
    @FXML private HBox barDesktop;
    @FXML private HBox barMovil;
    @FXML private HBox barServidor;
    @FXML private HBox barOtros;

    // ── Tabla últimas asignaciones ────────────────────────────────────────────
    @FXML private TableView<AsignacionDTO>      tablaUltimas;
    @FXML private TableColumn<AsignacionDTO, String> colUsuario;
    @FXML private TableColumn<AsignacionDTO, String> colGry;
    @FXML private TableColumn<AsignacionDTO, String> colModelo;
    @FXML private TableColumn<AsignacionDTO, String> colFechaAs;
    @FXML private TableColumn<AsignacionDTO, String> colEstadoAs;

    // ── Spinner ───────────────────────────────────────────────────────────────
    @FXML private ProgressIndicator progressGlobal;

    // ─────────────────────────────────────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarDatos();
    }

    @Override
    public void setDashBoard(MenuController dbc) {
        this.menuController = dbc;
    }

    // ── Configuración de columnas ─────────────────────────────────────────────
    private void configurarTabla() {
        colUsuario.setCellValueFactory(d -> {
            String nombre = d.getValue().getNombreUsuario();
            return new SimpleStringProperty(nombre != null ? nombre : "—");
        });
        colGry.setCellValueFactory(d -> {
            String gry = d.getValue().getIdentificadorEquipo();
            return new SimpleStringProperty(gry != null ? gry : "—");
        });
        colModelo.setCellValueFactory(d -> {
            // AsignacionDTO no incluye modelo; mostramos el ID del equipo como referencia
            Long idEq = d.getValue().getIdEquipo();
            return new SimpleStringProperty(idEq != null ? "Equipo #" + idEq : "—");
        });
        colFechaAs.setCellValueFactory(d -> {
            var fecha = d.getValue().getFechaEntrega();
            return new SimpleStringProperty(fecha != null ? fecha.format(FMT) : "—");
        });
        colEstadoAs.setCellValueFactory(d -> {
            var dev = d.getValue().getFechaDevolucion();
            return new SimpleStringProperty(dev == null ? "✓ Activa" : "Devuelta");
        });
    }

    // ── Carga de datos en background ──────────────────────────────────────────
    private void cargarDatos() {
        mostrarSpinner(true);

        Task<DatosResumen> task = new Task<>() {
            @Override
            protected DatosResumen call() {
                DatosResumen res = new DatosResumen();

                // KPIs principales
                res.total      = fachadaEquipos.contarEquipos(null, null, null, null);
                res.asignados  = fachadaEquipos.contarEquipos(null, null, null, EstadoEquipo.ASIGNADO);
                res.enStock    = fachadaEquipos.contarEquipos(null, null, null, EstadoEquipo.EN_STOCK);
                res.enBaja     = fachadaEquipos.contarEquipos(null, null, null, EstadoEquipo.EN_ESPERA_BAJA);
                res.usuarios   = fachadaPersonas.contarUsuarios(null);
                res.asignaciones = fachadaPrestamos.contarAsignaciones(null);

                // Distribución por tipo (contar sin paginado)
                res.laptop    = contarPorTipo(TipoEquipo.LAPTOP);
                res.desktop   = contarPorTipo(TipoEquipo.DESKTOP) + contarPorTipo(TipoEquipo.AIO);
                res.movil     = contarPorTipo(TipoEquipo.MOVIL);
                res.servidor  = contarPorTipo(TipoEquipo.SERVIDOR);
                res.otros     = res.total - res.laptop - res.desktop - res.movil - res.servidor;
                if (res.otros < 0) res.otros = 0;

                // Últimas 10 asignaciones (se usará para la tabla)
                res.ultimas = fachadaPrestamos.buscarAsignacionesPaginado(null, 0, 10);

                return res;
            }

            @Override
            protected void succeeded() {
                aplicarDatos(getValue());
                mostrarSpinner(false);
            }

            @Override
            protected void failed() {
                mostrarSpinner(false);
                Throwable ex = getException();
                if (ex != null) ex.printStackTrace();
                mostrarError("Error al cargar datos", "No se pudo obtener la información del servidor.");
            }
        };

        new Thread(task).start();
    }

    private long contarPorTipo(TipoEquipo tipo) {
        try {
            return fachadaEquipos.contarEquipos(null, tipo, null, null);
        } catch (Exception e) {
            // Fallback: contar desde lista filtrada
            List<EquipoBaseDTO> lista = fachadaEquipos.buscarConFiltros(null, tipo, null, null);
            return lista != null ? lista.size() : 0;
        }
    }

    // ── Aplicar datos al UI ───────────────────────────────────────────────────
    private void aplicarDatos(DatosResumen r) {
        Platform.runLater(() -> {
            // KPI cards
            lblTotalEquipos.setText(String.valueOf(r.total));
            lblSubTotalEquipos.setText(r.enBaja + " en espera de baja");

            lblAsignados.setText(String.valueOf(r.asignados));
            lblPctAsignados.setText(porcentaje(r.asignados, r.total) + " del total");

            lblEnStock.setText(String.valueOf(r.enStock));
            lblPctStock.setText(porcentaje(r.enStock, r.total) + " disponibles");

            lblUsuarios.setText(String.valueOf(r.usuarios));
            lblAsignaciones.setText(String.valueOf(r.asignaciones));

            // Distribución — Labels
            lblLaptop.setText(String.valueOf(r.laptop));
            lblDesktop.setText(String.valueOf(r.desktop));
            lblMovil.setText(String.valueOf(r.movil));
            lblServidor.setText(String.valueOf(r.servidor));
            lblOtros.setText(String.valueOf(r.otros));

            // Barras de progreso (ancho relativo al total)
            actualizarBarra(barLaptop,   r.laptop,   r.total);
            actualizarBarra(barDesktop,  r.desktop,  r.total);
            actualizarBarra(barMovil,    r.movil,    r.total);
            actualizarBarra(barServidor, r.servidor, r.total);
            actualizarBarra(barOtros,    r.otros,    r.total);

            // Tabla últimas asignaciones
            if (r.ultimas != null) {
                tablaUltimas.getItems().setAll(r.ultimas);
            }
        });
    }

    /** Ajusta el ancho de la barra proporcional al total */
    private void actualizarBarra(HBox bar, long valor, long total) {
        if (bar == null) return;
        if (total <= 0) {
            bar.setPrefWidth(0);
            return;
        }
        // El padre (progress-track) tiene un ancho dinámico; usamos % de 240 px como referencia
        double pct = (double) valor / total;
        bar.setPrefWidth(Math.max(4, pct * 240));
    }

    private String porcentaje(long parte, long total) {
        if (total == 0) return "0%";
        long pct = Math.round((double) parte / total * 100);
        return pct + "%";
    }

    private void mostrarSpinner(boolean visible) {
        Platform.runLater(() -> {
            if (progressGlobal != null) {
                progressGlobal.setVisible(visible);
                progressGlobal.setManaged(visible);
            }
        });
    }

    // ── DTO interno del resumen ───────────────────────────────────────────────
    private static class DatosResumen {
        long total, asignados, enStock, enBaja, usuarios, asignaciones;
        long laptop, desktop, movil, servidor, otros;
        List<AsignacionDTO> ultimas;
    }
}
