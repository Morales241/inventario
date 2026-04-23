package com.mycompany.inventariofrontfx.asignaciones;

import Dtos.EquipoEscritorioDTO;
import Dtos.ModeloDTO;
import Dtos.UsuarioDTO;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Generador de parámetros para la responsiva de equipos de cómputo.
 */
public class GeneradorResponsivaComputo {

    /**
     * Genera el mapa de parámetros para el jasper de cómputo.
     *
     * @param usuario Datos del empleado
     * @param equipo EquipoEscritorioDTO con campos propios del equipo
     * @param modelo ModeloDTO con RAM, procesador, almacenamiento, marca
     * @param fechaAsignacion Fecha de entrega
     */
    public static Map<String, Object> generarParametros(UsuarioDTO usuario,
            EquipoEscritorioDTO equipo,
            ModeloDTO modelo,
            LocalDate fechaAsignacion) {
        Map<String, Object> p = new HashMap<>();

        p.put("fecha", fechaAsignacion.format(
                DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "MX"))));
        p.put("nombreEmpleado", safe(usuario.getNombre()));
        p.put("puesto", safe(usuario.getNombrePuesto()));
        p.put("numeroCarta", "RESP-" + equipo.getGry() + "-"
                + fechaAsignacion.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        p.put("gry", equipo.getGry());
        p.put("tipoEquipo", safe(equipo.getTipo()));
        p.put("numeroSerie", safe(equipo.getIdentificador()));

        // Marca y nombre del modelo vienen de ModeloDTO
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

        // Accesorios físicos — solo mochila y mouse existen en EquipoEscritorioDTO
        p.put("mouse", bool(equipo.getMouse()));
        p.put("Mochila", bool(equipo.getMochila()));
        // Teclado, monitor y cable no están en el DTO → false (el jasper los muestra sin marcar)
        p.put("teclado", false);
        p.put("monitor", false);
        p.put("cableCorriente", false);

        // Software
        p.put("sistemaOperativo", equipo.getSisOpertativo() != null
                && !equipo.getSisOpertativo().isBlank());
        p.put("antivirus", false);
        p.put("paqueteriaOffice", false);
        p.put("lectorPDF", false);
        p.put("otrosAccesorios", "Ninguno");

        return p;
    }

    private static String safe(String s) {
        return s != null && !s.isBlank() ? s : "—";
    }

    private static Boolean bool(Boolean b) {
        return b != null && b;
    }
}
