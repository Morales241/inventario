package com.mycompany.inventariofrontfx.asignaciones;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import Dtos.EquipoEscritorioDTO;
import Dtos.UsuarioDTO;

public class GeneradorResponsivaComputo {
    public static Map<String, Object> generarParametros(UsuarioDTO usuario, EquipoEscritorioDTO equipo, java.time.LocalDate fechaAsignacion) {
        Map<String, Object> p = new HashMap<>();
        p.put("fecha", fechaAsignacion.format(DateTimeFormatter.ofPattern("dd 'de' MMMM 'de' yyyy", new Locale("es", "MX"))));
        p.put("nombreEmpleado", safe(usuario.getNombre()));
        p.put("puesto", safe(usuario.getNombrePuesto()));
        p.put("numeroCarta", "RESP-" + equipo.getGry() + "-" + fechaAsignacion.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        p.put("gry", equipo.getGry());
        p.put("marca", safe(equipo.getNombreModelo()));
        p.put("tipoEquipo", safe(equipo.getTipo()));
        p.put("numeroSerie", safe(equipo.getIdentificador()));
        p.put("modelo", safe(equipo.getNombreModelo()));
        p.put("memoriaRam", safe(equipo.getMemoriaRam()));
        p.put("procesador", safe(equipo.getSisOpertativo()));
        p.put("almacenamiento", safe(equipo.getAlmacenamiento()));
        p.put("mouse", bool(equipo.getMouse()));
        p.put("teclado", bool(equipo.getTeclado()));
        p.put("monitor", bool(equipo.getMonitor()));
        p.put("cableCorriente", bool(equipo.getCableCorriente()));
        p.put("Mochila", bool(equipo.getMochila()));
        p.put("sistemaOperativo", true); // Puedes ajustar según datos
        p.put("antivirus", false);
        p.put("paqueteriaOffice", false);
        p.put("lectorPDF", false);
        p.put("otrosAccesorios", safe(equipo.getOtrosAccesorios()));
        return p;
    }
    private static String safe(String s) { return s == null ? "—" : s; }
    private static Boolean bool(Boolean b) { return b != null && b; }
}
