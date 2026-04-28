package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;

/**
 * Panel de filtro estilo Excel para columnas de TableView en JavaFX.
 *
 * que hace esto Agrega un icono que es este ▼ al encabezado de una columna,
 * caundo haces clic abre un Popup flotante que tiene el campo de búsqueda de
 * texto, un checkbox "Seleccionar todo", una lista de valores únicos con
 * checkboxes individuales y botones de aplicar y limpiar
 *
 * @param <T> Tipo de los ítems de la tabla (EquipoBaseDTO, UsuarioDTO, etc.)
 */
public class ColumnFilterPanel<T> {

    private static final double POPUP_WIDTH = 220;
    private static final double MAX_LISTA_HEIGHT = 200;
    private static final String COLOR_PRIMARY = "#13507d";
    private static final String COLOR_ACTIVO = "#0F766E";

    private final TableColumn<T, String> columna;
    private final java.util.function.Function<T, String> extractor;
    private final Consumer<Set<String>> callbackAplicar;

    /**
     * Todos los valores únicos de esta columna en la página actual.
     */
    private final List<String> todosLosValores = new ArrayList<>();

    /**
     * Valores actualmente seleccionados (el filtro activo).
     */
    private final Set<String> seleccionados = new LinkedHashSet<>();

    /**
     * true si hay un filtro activo (al menos 1 valor excluido).
     */
    private boolean filtroActivo = false;

    private Popup popup;
    private Label iconoFiltro;
    private Label iconoChevron;

    /**
     * @param columna La TableColumn a la que se añade el filtro
     * @param extractor Función que extrae el valor String del ítem (p.ej.
     * equipo::getTipo)
     * @param callbackAplicar Callback que recibe el conjunto de valores
     * seleccionados cuando el usuario pulsa Aplicar. Si el conjunto está vacío
     * = sin filtro.
     */
    public ColumnFilterPanel(TableColumn<T, String> columna,
            java.util.function.Function<T, String> extractor,
            Consumer<Set<String>> callbackAplicar) {
        this.columna = columna;
        this.extractor = extractor;
        this.callbackAplicar = callbackAplicar;

        instalarEncabezado();
    }

    /**
     * Actualiza la lista de valores únicos disponibles en el filtro
     *
     * @param items Lista de ítems actualmente en la tabla
     */
    public void actualizarValores(Collection<T> items) {
        todosLosValores.clear();

        items.stream()
                .map(extractor)
                .filter(v -> v != null && !v.isBlank())
                .distinct()
                .sorted()
                .forEach(todosLosValores::add);

        seleccionados.retainAll(todosLosValores);
        actualizarIcono();
    }

    /**
     * Devuelve true si el valor dado pasa el filtro activo. Úsalo en el stream
     * de filtrado local: .filter(e -> filtroTipo.acepta(e.getTipo()))
     */
    public boolean acepta(String valor) {
        if (!filtroActivo || seleccionados.isEmpty()) {
            return true;
        }
        return seleccionados.contains(valor);
    }

    /**
     * Limpia el filtro programáticamente (sin abrir el popup).
     */
    public void limpiar() {
        seleccionados.clear();
        filtroActivo = false;
        actualizarIcono();
    }

    /**
     * ¿Tiene filtro activo?
     */
    public boolean estaActivo() {
        return filtroActivo;
    }

    private void instalarEncabezado() {
        String textoOriginal = columna.getText();
        columna.setText("");

        HBox headerBox = new HBox(4);
        headerBox.setAlignment(Pos.CENTER);
        headerBox.setMaxWidth(Double.MAX_VALUE);

        Label lblTexto = new Label(textoOriginal);
        lblTexto.setStyle("-fx-font-weight: bold; -fx-text-fill: " + COLOR_PRIMARY + "; -fx-font-size: 12.5px;");
        lblTexto.setMaxWidth(Double.MAX_VALUE);
        lblTexto.setAlignment(Pos.CENTER);
        HBox.setHgrow(lblTexto, Priority.ALWAYS);

        iconoFiltro = new Label("●");
        iconoFiltro.setStyle(estiloIcono(false));
        iconoFiltro.setVisible(false);
        iconoFiltro.setManaged(false);

        iconoChevron = new Label("▼");
        iconoChevron.setStyle(estiloIcono(false));
        iconoChevron.setVisible(true);
        iconoChevron.setManaged(true);

        HBox iconos = new HBox(4, iconoFiltro, iconoChevron);
        iconos.setAlignment(Pos.CENTER_RIGHT);
        iconos.setOnMouseClicked(e -> {
            e.consume();
            togglePopup(iconos);
        });

        headerBox.getChildren().addAll(lblTexto, iconos);

        iconoFiltro.setOnMouseClicked(e -> {
            e.consume();
            togglePopup(iconos);
        });
        iconoChevron.setOnMouseClicked(e -> {
            e.consume();
            togglePopup(iconos);
        });

        lblTexto.setOnMouseClicked(e -> {
        });

        columna.setGraphic(headerBox);
    }

    private void actualizarIcono() {
        if (iconoFiltro != null) {
            iconoFiltro.setVisible(filtroActivo);
            iconoFiltro.setManaged(filtroActivo);
            iconoFiltro.setStyle(estiloIcono(filtroActivo));
        }
        if (iconoChevron != null) {
            iconoChevron.setVisible(!filtroActivo);
            iconoChevron.setManaged(!filtroActivo);
            iconoChevron.setStyle(estiloIcono(!filtroActivo));
        }
    }

    private String estiloIcono(boolean activo) {
        String color = activo ? COLOR_ACTIVO : "#9CA3AF";
        return "-fx-text-fill: " + color + "; "
                + "-fx-font-size: 11px; "
                + "-fx-cursor: hand; "
                + "-fx-padding: 2 3 2 3; "
                + "-fx-background-radius: 3; "
                + "-fx-background-color: " + (activo ? "#D1FAE5" : "transparent") + ";";
    }

    private void togglePopup(Node ancla) {
        if (popup != null && popup.isShowing()) {
            popup.hide();
            popup = null;
            return;
        }
        abrirPopup(ancla);
    }

    private void abrirPopup(Node ancla) {
        popup = new Popup();
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        VBox contenido = construirContenidoPopup();
        contenido.setPrefWidth(POPUP_WIDTH);
        contenido.setStyle(
                "-fx-background-color: white; "
                + "-fx-border-color: #E5E7EB; "
                + "-fx-border-width: 1; "
                + "-fx-border-radius: 8; "
                + "-fx-background-radius: 8; "
                + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 10, 0, 0, 3); "
                + "-fx-padding: 0;"
        );

        popup.getContent().add(contenido);

        javafx.geometry.Bounds b = ancla.localToScreen(ancla.getBoundsInLocal());
        if (b != null) {
            popup.show(ancla, b.getMinX() - POPUP_WIDTH + b.getWidth(), b.getMaxY() + 2);
        } else {
            popup.show(ancla.getScene().getWindow());
        }

        // Auto-foco en el campo de búsqueda (UX Excel)
        Platform.runLater(() -> {
            Node lookup = contenido.lookup(".text-field");
            if (lookup != null) {
                lookup.requestFocus();
            }
        });
    }

    private VBox construirContenidoPopup() {
        VBox root = new VBox(0);

        Node graphic = columna.getGraphic();
        String textoColumna;

        if (graphic instanceof HBox hb) {
            textoColumna = ((Label) hb.getChildren().get(0)).getText();
        } else {
            textoColumna = "columna";
        }

        Label titulo = new Label("Filtrar por " + textoColumna);
        titulo.setStyle(
                "-fx-font-weight: bold; "
                + "-fx-font-size: 12px; "
                + "-fx-text-fill: " + COLOR_PRIMARY + "; "
                + "-fx-padding: 10 12 8 12;"
        );

        root.getChildren().add(titulo);
        root.getChildren().add(new Separator());

        TextField txtBuscar = new TextField();
        txtBuscar.setPromptText("Buscar...");
        txtBuscar.setStyle(
                "-fx-background-color: #F9FAFB; "
                + "-fx-border-color: #E5E7EB; "
                + "-fx-border-radius: 6; "
                + "-fx-background-radius: 6; "
                + "-fx-padding: 6 10; "
                + "-fx-font-size: 12px;"
        );
        VBox.setMargin(txtBuscar, new Insets(8, 10, 6, 10));

        VBox listaBox = new VBox(2);
        listaBox.setPadding(new Insets(4, 8, 4, 8));

        ScrollPane scroll = new ScrollPane(listaBox);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setPrefHeight(MAX_LISTA_HEIGHT);
        scroll.setMaxHeight(MAX_LISTA_HEIGHT);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        Set<String> selActual = new LinkedHashSet<>(
                filtroActivo ? seleccionados : todosLosValores
        );

        CheckBox ckbTodos = new CheckBox("(Seleccionar todo)");
        ckbTodos.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #374151;");
        ckbTodos.setSelected(selActual.size() == todosLosValores.size());
        ckbTodos.setIndeterminate(!selActual.isEmpty() && selActual.size() < todosLosValores.size());
        VBox.setMargin(ckbTodos, new Insets(2, 8, 4, 10));

        List<CheckBox> ckbItems = new ArrayList<>();
        for (String valor : todosLosValores) {
            CheckBox ck = new CheckBox(valor);
            ck.setStyle("-fx-font-size: 12px; -fx-text-fill: #374151;");
            ck.setSelected(selActual.contains(valor));
            ck.setPadding(new Insets(3, 4, 3, 4));

            ck.selectedProperty().addListener((obs, old, newVal) -> {
                if (newVal) {
                    selActual.add(valor);
                } else {
                    selActual.remove(valor);
                }
                actualizarEstadoTodos(ckbTodos, ckbItems);
            });

            ckbItems.add(ck);
            listaBox.getChildren().add(ck);
        }

        ckbTodos.setOnAction(e -> {
            boolean marcarTodos = ckbTodos.isSelected();
            for (CheckBox ck : ckbItems) {
                if (ck.isVisible()) {
                    ck.setSelected(marcarTodos);
                }
            }
            if (marcarTodos) {
                selActual.addAll(todosLosValores);
            } else {
                selActual.clear();
            }
        });

        PauseTransition debounce = new PauseTransition(Duration.millis(200));
        txtBuscar.textProperty().addListener((obs, old, val) -> {
            debounce.setOnFinished(ev -> filtrarListaPopup(val, ckbItems, listaBox));
            debounce.playFromStart();
        });

        Button btnAplicar = new Button("Aplicar");
        btnAplicar.setPrefWidth(90);
        btnAplicar.setStyle(
                "-fx-background-color: " + COLOR_PRIMARY + "; "
                + "-fx-text-fill: white; "
                + "-fx-font-weight: bold; "
                + "-fx-font-size: 12px; "
                + "-fx-background-radius: 6; "
                + "-fx-padding: 7 0; "
                + "-fx-cursor: hand;"
        );
        btnAplicar.setOnAction(e -> {
            aplicarFiltro(selActual);
            if (popup != null) {
                popup.hide();
            }
        });

        // UX: Aplicar con ENTER desde el campo de búsqueda
        txtBuscar.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {
                aplicarFiltro(selActual);
                if (popup != null) {
                    popup.hide();
                }
            }
        });

        Button btnLimpiar = new Button("Limpiar");
        btnLimpiar.setPrefWidth(90);
        btnLimpiar.setStyle(
                "-fx-background-color: transparent; "
                + "-fx-text-fill: #6B7280; "
                + "-fx-font-size: 12px; "
                + "-fx-border-color: #E5E7EB; "
                + "-fx-border-radius: 6; "
                + "-fx-background-radius: 6; "
                + "-fx-padding: 7 0; "
                + "-fx-cursor: hand;"
        );
        btnLimpiar.setOnAction(e -> {
            limpiar();
            callbackAplicar.accept(new LinkedHashSet<>());
            if (popup != null) {
                popup.hide();
            }
        });

        HBox botonesBox = new HBox(8, btnLimpiar, btnAplicar);
        botonesBox.setAlignment(Pos.CENTER_RIGHT);
        botonesBox.setPadding(new Insets(8, 10, 10, 10));
        botonesBox.setStyle("-fx-border-color: #E5E7EB; -fx-border-width: 1 0 0 0;");

        root.getChildren().addAll(txtBuscar, ckbTodos, scroll, new Separator(), botonesBox);
        return root;
    }

    private void filtrarListaPopup(String busqueda, List<CheckBox> ckbItems, VBox listaBox) {
        String lower = busqueda == null ? "" : busqueda.trim().toLowerCase();
        for (CheckBox ck : ckbItems) {
            boolean visible = lower.isEmpty() || ck.getText().toLowerCase().contains(lower);
            ck.setVisible(visible);
            ck.setManaged(visible);
        }
    }

    private void actualizarEstadoTodos(CheckBox ckbTodos, List<CheckBox> ckbItems) {
        long totalVisibles = ckbItems.stream().filter(CheckBox::isManaged).count();
        long seleccionadosVis = ckbItems.stream().filter(c -> c.isManaged() && c.isSelected()).count();

        if (seleccionadosVis == 0) {
            ckbTodos.setSelected(false);
            ckbTodos.setIndeterminate(false);
        } else if (seleccionadosVis == totalVisibles) {
            ckbTodos.setSelected(true);
            ckbTodos.setIndeterminate(false);
        } else {
            ckbTodos.setSelected(false);
            ckbTodos.setIndeterminate(true);
        }
    }

    private void aplicarFiltro(Set<String> seleccion) {
        seleccionados.clear();
        seleccionados.addAll(seleccion);

        filtroActivo = !seleccionados.isEmpty()
                && seleccionados.size() < todosLosValores.size();

        actualizarIcono();
        callbackAplicar.accept(new LinkedHashSet<>(seleccionados));
    }
}
