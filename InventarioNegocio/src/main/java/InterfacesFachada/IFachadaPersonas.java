package InterfacesFachada;

import Dtos.UsuarioDTO;
import Dtos.CuentaSistemaDTO;
import java.util.List;

/**
 * Contrato de fachada para operaciones de gestión de personas (usuarios y Usuarios).
 * <p>
 * Define métodos para autenticación, búsqueda, gestión de Usuarios y control de acceso.
 * Implementa el patrón Facade para simplificar la interacción con la capa de servicios de personas.
 * </p>
 * @author JMorales
 */
public interface IFachadaPersonas {
    
    /**
     * Autentica un usuario validando credenciales de acceso.
     * @param user Nombre de usuario o email.
     * @param pass Contraseña del usuario.
     * @return CuentaSistemaDTO con información del usuario autenticado.
     * @throws Exception Si el usuario o contraseña son incorrectos.
     */
    public CuentaSistemaDTO login(String user, String pass) throws Exception;
    
    /**
     * Busca Usuarios por nombre, nómina o puesto.
     * @param busquedaGlobal Criterio de búsqueda (búsqueda parcial en múltiples campos).
     * @return Lista de UsuarioDTO que coinciden.
     */
    public List<UsuarioDTO> buscarUsuarios(String busquedaGlobal);
    
    /**
     * Obtiene los datos completos de un Usuario.
     * @param id Identificador del Usuario.
     * @return UsuarioDTO con la información completa.
     */
    public UsuarioDTO obtenerUsuario(Long id);
    
    /**
     * Guarda o actualiza los datos de un Usuario.
     * @param dto Datos del Usuario (nombre, nómina, puesto).
     * @throws Exception Si faltan datos obligatorios o hay error en BD.
     */
    public void guardarUsuario(UsuarioDTO dto) throws Exception;
    
    /**
     * Cambia el estado de un Usuario (activo/dado de baja).
     * @param id Identificador del Usuario.
     * @param activo true para activar, false para desactivar.
     * @throws Exception Si el Usuario no existe.
     */
    public void cambiarEstadoUsuario(Long id, boolean activo) throws Exception;
}
