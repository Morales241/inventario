package com.mycompany.inventariofrontfx.asignaciones;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import Dtos.EquipoBaseDTO;
import Dtos.UsuarioDTO;

public class GeneradorResponsivaOtros {
    public static Map<String, Object> generarParametros(UsuarioDTO usuario, EquipoBaseDTO equipo, java.time.LocalDate fechaAsignacion) {
        Map<String, Object> p = new HashMap<>();
        p.put("fecha", fechaAsignacion.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "MX"))));
        p.put("nombreEmpleado", safe(usuario.getNombre()));
        p.put("puesto", safe(usuario.getNombrePuesto()));
        p.put("numeroCarta", "RESP-OTRO-" + equipo.getGry() + "-" + fechaAsignacion.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        p.put("gry", equipo.getGry());
        p.put("marca", safe(equipo.getNombreModelo()));
        p.put("tipoEquipo", safe(equipo.getTipo()));
        p.put("numeroSerie", safe(equipo.getIdentificador()));
        p.put("modelo", safe(equipo.getNombreModelo()));
        // Accesorios genéricos
        p.put("Mochila", false);
        p.put("otrosAccesorios", safe(equipo.getOtrosAccesorios()));
        // Software (no aplica)
        p.put("sistemaOperativo", false);
        p.put("antivirus", false);
        p.put("paqueteriaOffice", false);
        p.put("lectorPDF", false);
        // Otros campos pueden ir en otrosAccesorios
        return p;
    }
    private static String safe(String s) { return s == null ? "—" : s; }
}
