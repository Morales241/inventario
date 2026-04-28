package com.mycompany.inventariofrontfx.asignaciones;

import Dtos.ModeloDTO;
import Dtos.MovilDTO;
import Dtos.UsuarioDTO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Generador de parámetros para la responsiva de equipos móviles.
 */
public class GeneradorResponsivaMovil {

    /**
     * @param usuario Datos del empleado
     * @param equipo MovilDTO con campos propios del móvil
     * @param modelo ModeloDTO con marca, procesador, RAM, almacenamiento
     * @param fechaAsignacion Fecha de entrega
     */
    public static Map<String, Object> generarParametros(UsuarioDTO usuario,
            MovilDTO equipo,
            ModeloDTO modelo,
            LocalDate fechaAsignacion) {
        Map<String, Object> p = new HashMap<>();

        p.put("fecha", fechaAsignacion.format(
                DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "MX"))));
        p.put("nombreEmpleado", safe(usuario.getNombre()));
        p.put("puesto", safe(usuario.getNombrePuesto()));
        p.put("numeroCarta", "RESP-MOV-" + equipo.getGryFormateado() + "-"
                + fechaAsignacion.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        p.put("gry", equipo.getGryFormateado());
        p.put("tipoEquipo", safe(equipo.getTipo()));
        p.put("numeroSerie", safe(equipo.getIdentificador()));

        // Datos técnicos del Modelo
        if (modelo != null) {
            p.put("marca", safe(modelo.getMarca()));
            p.put("modelo", safe(modelo.getMarca() + " " + modelo.getNombre()));
            p.put("memoriaRam", modelo.getMemoriaRam() != null
                    ? String.valueOf(modelo.getMemoriaRam()) : "—");
            p.put("procesador", safe(modelo.getProcesador()));
            p.put("almacenamiento", modelo.getAlmacenamiento() != null
                    ? String.valueOf(modelo.getAlmacenamiento()) : "—");
        } else {
            p.put("marca", safe(equipo.getNombreModelo()));
            p.put("modelo", safe(equipo.getNombreModelo()));
            p.put("memoriaRam", "—");
            p.put("procesador", "—");
            p.put("almacenamiento", "—");
        }

        // Accesorios del móvil (campos reales de MovilDTO)
        p.put("cargador", bool(equipo.getCargador()));
        p.put("funda", bool(equipo.getFunda()));
        // manosLibres → parámetro "audifonos" en el jasper (el campo más cercano disponible)
        p.put("audifonos", bool(equipo.getManosLibres()));

        // Software
        p.put("sistemaOperativo", true);   // los móviles siempre tienen SO
        p.put("antivirus", false);
        p.put("paqueteriaOffice", false);
        p.put("lectorPDF", false);

        // Otros: si hay número de celular, se muestra como información adicional
        String extras = (equipo.getNumCelular() != null && !equipo.getNumCelular().isBlank())
                ? "Núm. celular: " + equipo.getNumCelular()
                : "Ninguno";
        p.put("otrosAccesorios", extras);

        return p;
    }

    private static String safe(String s) {
        return s != null && !s.isBlank() ? s : "—";
    }

    private static Boolean bool(Boolean b) {
        return b != null && b;
    }
}
