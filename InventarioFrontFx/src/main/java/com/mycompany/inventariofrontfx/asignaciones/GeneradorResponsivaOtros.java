package com.mycompany.inventariofrontfx.asignaciones;

import Dtos.ModeloDTO;
import Dtos.OtroEquipoDTO;
import Dtos.UsuarioDTO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Generador de parámetros para la responsiva de otros equipos (impresoras,
 * monitores, escáneres, proyectores)
 */
public class GeneradorResponsivaOtros {

    public static Map<String, Object> generarParametros(UsuarioDTO usuario,
            OtroEquipoDTO equipo,
            ModeloDTO modelo,
            LocalDate fechaAsignacion) {
        Map<String, Object> p = new HashMap<>();

        p.put("fecha", fechaAsignacion.format(
                DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "MX"))));
        p.put("nombreEmpleado", safe(usuario.getNombre()));
        p.put("puesto", safe(usuario.getNombrePuesto()));
        p.put("numeroCarta", "RESP-OTRO-" + equipo.getGryFormateado() + "-"
                + fechaAsignacion.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        p.put("gry", equipo.getGryFormateado());
        p.put("tipoEquipo", safe(equipo.getTipo()));
        p.put("numeroSerie", safe(equipo.getIdentificador()));

        if (modelo != null) {
            p.put("marca", safe(modelo.getMarca()));
            p.put("modelo", safe(modelo.getMarca() + " " + modelo.getNombre()));
        } else {
            p.put("marca", safe(equipo.getNombreModelo()));
            p.put("modelo", safe(equipo.getNombreModelo()));
        }

        // Construir descripción de otros accesorios desde los campos extra
        StringBuilder extras = new StringBuilder();
        if (equipo.getTituloCampoExtra() != null && !equipo.getTituloCampoExtra().isBlank()) {
            extras.append(equipo.getTituloCampoExtra())
                    .append(": ").append(safe(equipo.getContenidoCampoExtra()));
        }
        if (equipo.getTituloCampoExtra2() != null && !equipo.getTituloCampoExtra2().isBlank()) {
            if (!extras.isEmpty()) {
                extras.append(" | ");
            }
            extras.append(equipo.getTituloCampoExtra2())
                    .append(": ").append(safe(equipo.getContenidoCampoExtra2()));
        }
        p.put("otrosAccesorios", extras.isEmpty() ? "Ninguno" : extras.toString());

        return p;
    }

    private static String safe(String s) {
        return s != null && !s.isBlank() ? s : "—";
    }
}
