package util;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;


public final class ValidacionUtil {

    private ValidacionUtil() {}

    private static final String CSS_ERROR = "campo-error";

    public static void marcarError(Control control) {
        if (control != null && !control.getStyleClass().contains(CSS_ERROR)) {
            control.getStyleClass().add(CSS_ERROR);
        }
    }

    public static void marcarOk(Control control) {
        if (control != null) {
            control.getStyleClass().remove(CSS_ERROR);
        }
    }

    public static void resetCampo(Control control) {
        marcarOk(control);
    }

    /**
     * Valida que un TextField no esté vacío.
     * Marca el campo en rojo si falla; lo limpia si pasa.
     *
     * @return true si tiene contenido, false si está vacío.
     */
    public static boolean requerido(TextField campo) {
        boolean ok = campo != null
                && campo.getText() != null
                && !campo.getText().trim().isEmpty();
        if (ok) marcarOk(campo); else marcarError(campo);
        return ok;
    }

    /**
     * Valida que un TextField sea un número entero positivo.
     */
    public static boolean esEnteroPositivo(TextField campo) {
        if (!requerido(campo)) return false;
        try {
            int val = Integer.parseInt(campo.getText().trim());
            boolean ok = val > 0;
            if (ok) marcarOk(campo); else marcarError(campo);
            return ok;
        } catch (NumberFormatException e) {
            marcarError(campo);
            return false;
        }
    }

    /**
     * Valida que un TextField sea un número decimal (Double) positivo o cero.
     * Campo opcional: si está vacío, se considera válido (retorna true).
     */
    public static boolean esDecimalOpcional(TextField campo) {
        if (campo == null || campo.getText() == null || campo.getText().trim().isEmpty()) {
            marcarOk(campo);
            return true;
        }
        try {
            double val = Double.parseDouble(campo.getText().trim().replace(",", "."));
            boolean ok = val >= 0;
            if (ok) marcarOk(campo); else marcarError(campo);
            return ok;
        } catch (NumberFormatException e) {
            marcarError(campo);
            return false;
        }
    }

    /**
     * Valida que un TextField sea un número entero positivo.
     * Campo opcional: si está vacío, se considera válido.
     */
    public static boolean esEnteroOpcional(TextField campo) {
        if (campo == null || campo.getText() == null || campo.getText().trim().isEmpty()) {
            marcarOk(campo);
            return true;
        }
        return esEnteroPositivo(campo);
    }

    /**
     * Valida que un TextField tenga exactamente N dígitos numéricos.
     */
    public static boolean esNDigitos(TextField campo, int n) {
        if (!requerido(campo)) return false;
        boolean ok = campo.getText().trim().matches("\\d{" + n + "}");
        if (ok) marcarOk(campo); else marcarError(campo);
        return ok;
    }

    /**
     * Valida que un TextField tenga entre minLen y maxLen dígitos numéricos.
     */
    public static boolean esNDigitos(TextField campo, int minLen, int maxLen) {
        if (!requerido(campo)) return false;
        boolean ok = campo.getText().trim().matches("\\d{" + minLen + "," + maxLen + "}");
        if (ok) marcarOk(campo); else marcarError(campo);
        return ok;
    }

    /**
     * Valida que un TextField tenga formato de correo electrónico básico.
     */
    public static boolean esEmail(TextField campo) {
        if (!requerido(campo)) return false;
        boolean ok = campo.getText().trim().matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
        if (ok) marcarOk(campo); else marcarError(campo);
        return ok;
    }

    /**
     * Valida que un TextField tenga formato de correo electrónico básico (campo opcional).
     */
    public static boolean esEmailOpcional(TextField campo) {
        if (campo == null || campo.getText() == null || campo.getText().trim().isEmpty()) {
            marcarOk(campo);
            return true;
        }
        return esEmail(campo);
    }

    /**
     * Valida que un ComboBox tenga algo seleccionado.
     */
    public static boolean seleccionado(ComboBox<?> combo) {
        boolean ok = combo != null && combo.getValue() != null;
        if (ok) marcarOk(combo); else marcarError(combo);
        return ok;
    }

    /**
     * Valida que un DatePicker tenga una fecha seleccionada.
     */
    public static boolean fechaSeleccionada(DatePicker picker) {
        boolean ok = picker != null && picker.getValue() != null;
        if (ok) marcarOk(picker); else marcarError(picker);
        return ok;
    }

    /**
     * Valida que un ListView tenga un elemento seleccionado.
     */
    public static boolean seleccionado(ListView<?> lista) {
        boolean ok = lista != null
                && lista.getSelectionModel().getSelectedItem() != null;
        if (ok) marcarOk(lista); else marcarError(lista);
        return ok;
    }

    /** Muestra un label de error (visible, con texto). */
    public static void mostrarLabelError(Label label, String mensaje) {
        if (label != null) {
            label.setText(mensaje);
            label.setVisible(true);
            label.setManaged(true);
            if (!label.getStyleClass().contains("label-error")) {
                label.getStyleClass().add("label-error");
            }
        }
    }

    /** Oculta un label de error. */
    public static void ocultarLabel(Label label) {
        if (label != null) {
            label.setText("");
            label.setVisible(false);
            label.setManaged(false);
        }
    }
    /** Quita el estilo de error de todos los controles dados. */
    public static void resetTodos(Control... controles) {
        for (Control c : controles) {
            resetCampo(c);
        }
    }
}