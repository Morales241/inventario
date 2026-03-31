package InterfacesFachada;

import Dtos.CuentaSistemaDTO;
import Dtos.UsuarioDTO;
import java.util.List;

/**
 * Interfaz de la fachada de personas.
 * Se agrega buscarUsuariosPaginado() para soporte de carga paginada.
 */
public interface IFachadaPersonas {

    // ── Cuentas de sistema ────────────────────────────────────────────────────

    CuentaSistemaDTO login(String user, String pass);

    CuentaSistemaDTO buscarPorUsername(String username);

    CuentaSistemaDTO guardarCuentaSistema(CuentaSistemaDTO dto);

    void eliminarCuentaSistema(Long id);

    List<CuentaSistemaDTO> listarCuentasSistema();

    List<CuentaSistemaDTO> buscarCuentasSistema(String filtro);

    // ── Usuarios ──────────────────────────────────────────────────────────────

    List<UsuarioDTO> buscarUsuarios(String busquedaGlobal);

    /**
     * Devuelve una página de usuarios activos.
     *
     * @param busquedaGlobal texto de búsqueda (nombre o nómina), puede ser null o vacío
     * @param pagina         número de página empezando en 0
     * @param tamano         registros por página (p. ej. 25)
     * @return lista de usuarios de esa página, con numeroDeEquipos ya calculado
     */
    List<UsuarioDTO> buscarUsuariosPaginado(String busquedaGlobal, int pagina, int tamano);

    /**
     * Cuenta los usuarios activos que coinciden con el filtro.
     * Se usa para calcular el total de páginas en el paginador.
     */
    long contarUsuarios(String busquedaGlobal);

    UsuarioDTO obtenerUsuario(Long id);

    void guardarUsuario(UsuarioDTO dto);

    void cambiarEstadoUsuario(Long id, boolean activo);

    List<UsuarioDTO> listarUsuariosActivos();

    List<UsuarioDTO> buscarUsuariosPorDepartamento(Long idDepartamento);

    List<UsuarioDTO> buscarUsuariosPorPuesto(Long idPuesto);

    boolean usuarioTieneEquiposAsignados(Long idUsuario);
}
