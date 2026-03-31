package Implementaciones;

import Dtos.CuentaSistemaDTO;
import Dtos.UsuarioDTO;
import InterfacesFachada.IFachadaPersonas;
import Servicios.ServicioPersonas;
import interfacesServicios.IServicioPersonas;
import java.util.List;

/**
 * Fachada de personas — se delegan los dos métodos nuevos de paginación.
 */
public class FachadaPersonas implements IFachadaPersonas {

    private final IServicioPersonas servicioPersonas;

    public FachadaPersonas(IServicioPersonas servicioPersonas) {
        this.servicioPersonas = servicioPersonas;
    }

    public FachadaPersonas() {
        this(new ServicioPersonas());
    }

    // ── Cuentas de sistema ────────────────────────────────────────────────────
    @Override public CuentaSistemaDTO login(String u, String p)               { return servicioPersonas.login(u, p); }
    @Override public CuentaSistemaDTO buscarPorUsername(String u)             { return servicioPersonas.buscarPorUsername(u); }
    @Override public CuentaSistemaDTO guardarCuentaSistema(CuentaSistemaDTO d){ return servicioPersonas.guardarCuentaSistema(d); }
    @Override public void eliminarCuentaSistema(Long id)                      { servicioPersonas.eliminarCuentaSistema(id); }
    @Override public List<CuentaSistemaDTO> listarCuentasSistema()            { return servicioPersonas.listarCuentasSistema(); }
    @Override public List<CuentaSistemaDTO> buscarCuentasSistema(String f)    { return servicioPersonas.buscarCuentasSistema(f); }

    // ── Usuarios ──────────────────────────────────────────────────────────────
    @Override public List<UsuarioDTO> buscarUsuarios(String b)                { return servicioPersonas.buscarUsuarios(b); }
    @Override public UsuarioDTO obtenerUsuario(Long id)                       { return servicioPersonas.obtenerUsuario(id); }
    @Override public void guardarUsuario(UsuarioDTO dto)                      { servicioPersonas.guardarUsuario(dto); }
    @Override public void cambiarEstadoUsuario(Long id, boolean a)            { servicioPersonas.cambiarEstadoUsuario(id, a); }
    @Override public List<UsuarioDTO> listarUsuariosActivos()                 { return servicioPersonas.listarUsuariosActivos(); }
    @Override public List<UsuarioDTO> buscarUsuariosPorDepartamento(Long id)  { return servicioPersonas.buscarUsuariosPorDepartamento(id); }
    @Override public List<UsuarioDTO> buscarUsuariosPorPuesto(Long id)        { return servicioPersonas.buscarUsuariosPorPuesto(id); }
    @Override public boolean usuarioTieneEquiposAsignados(Long id)            { return servicioPersonas.usuarioTieneEquiposAsignados(id); }

    /** NUEVO: devuelve una página de usuarios con equipos ya contados. */
    @Override
    public List<UsuarioDTO> buscarUsuariosPaginado(String busqueda, int pagina, int tamano) {
        return servicioPersonas.buscarUsuariosPaginado(busqueda, pagina, tamano);
    }

    /** NUEVO: cuenta total para calcular páginas. */
    @Override
    public long contarUsuarios(String busqueda) {
        return servicioPersonas.contarUsuarios(busqueda);
    }
}
