package interfacesServicios;

import Dtos.CuentaSistemaDTO;
import Dtos.UsuarioDTO;
import java.util.List;

/**
 * Interfaz del servicio de personas — se agregan los dos métodos de paginación.
 */
public interface IServicioPersonas {

    // ── Cuentas de sistema ────────────────────────────────────────────────────
    CuentaSistemaDTO login(String username, String password);
    CuentaSistemaDTO buscarPorUsername(String username);
    CuentaSistemaDTO guardarCuentaSistema(CuentaSistemaDTO dto);
    void eliminarCuentaSistema(Long id);
    List<CuentaSistemaDTO> listarCuentasSistema();
    List<CuentaSistemaDTO> buscarCuentasSistema(String filtro);

    // ── Usuarios ──────────────────────────────────────────────────────────────
    List<UsuarioDTO> buscarUsuarios(String criterioGlobal);

    /** Devuelve una página de usuarios activos con equipos ya contados. */
    List<UsuarioDTO> buscarUsuariosPaginado(String criterioGlobal, int pagina, int tamano);

    /** Cuenta usuarios activos que coinciden con el filtro (para calcular páginas). */
    long contarUsuarios(String criterioGlobal);

    UsuarioDTO obtenerUsuario(Long id);
    void guardarUsuario(UsuarioDTO dto);
    void cambiarEstadoUsuario(Long id, boolean activo);
    List<UsuarioDTO> listarUsuariosActivos();
    List<UsuarioDTO> buscarUsuariosPorDepartamento(Long idDepartamento);
    List<UsuarioDTO> buscarUsuariosPorPuesto(Long idPuesto);
    boolean usuarioTieneEquiposAsignados(Long idUsuario);
}
