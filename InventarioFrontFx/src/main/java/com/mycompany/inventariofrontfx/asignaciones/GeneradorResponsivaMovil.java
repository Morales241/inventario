package com.mycompany.inventariofrontfx.asignaciones;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import Dtos.EquipoMovilDTO;
import Dtos.UsuarioDTO;

public class GeneradorResponsivaMovil {
    public static Map<String, Object> generarParametros(UsuarioDTO usuario, EquipoMovilDTO equipo, java.time.LocalDate fechaAsignacion) {
        Map<String, Object> p = new HashMap<>();
        p.put("fecha", fechaAsignacion.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "MX"))));
        p.put("nombreEmpleado", safe(usuario.getNombre()));
        p.put("puesto", safe(usuario.getNombrePuesto()));
        p.put("numeroCarta", "RESP-MOV-" + equipo.getGry() + "-" + fechaAsignacion.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        p.put("gry", equipo.getGry());
        p.put("marca", safe(equipo.getMarca()));
        p.put("tipoEquipo", safe(equipo.getTipo()));
        p.put("numeroSerie", safe(equipo.getIdentificador()));
        p.put("modelo", safe(equipo.getModelo()));
        p.put("memoriaRam", safe(equipo.getMemoriaRam()));
        p.put("procesador", safe(equipo.getProcesador()));
        p.put("almacenamiento", safe(equipo.getAlmacenamiento()));
        p.put("cargador", bool(equipo.getCargador()));
        p.put("funda", bool(equipo.getFunda()));
        p.put("audifonos", bool(equipo.getAudifonos()));
        p.put("Mochila", false); // Normalmente no aplica
        p.put("otrosAccesorios", safe(equipo.getOtrosAccesorios()));
        // Software
        p.put("sistemaOperativo", true);
        p.put("antivirus", false);
        p.put("paqueteriaOffice", false);
        p.put("lectorPDF", false);
        return p;
    }
    private static String safe(String s) { return s == null ? "—" : s; }
    private static Boolean bool(Boolean b) { return b != null && b; }
}
