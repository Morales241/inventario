package InterfacesFachada;

import Dtos.UsuarioDTO;
import Dtos.CuentaSistemaDTO;
import java.util.List;

/**
 * Contrato de fachada para operaciones de gestión de personas (usuarios y cuentas de sistema).
 * <p>
 * Define métodos para autenticación, búsqueda, gestión de usuarios y control de acceso.
 * Implementa el patrón Facade para simplificar la interacción con la capa de 
 * servicios de personas.
 * </p>
 * @author JMorales
 */
public interface IFachadaPersonas {
    
    /**
     * Autentica un usuario validando credenciales de acceso.
     * @param user Nombre de usuario
     * @param pass Contraseña del usuario
     * @return CuentaSistemaDTO con información del usuario autenticado
     */
    CuentaSistemaDTO login(String user, String pass);
    
    /**
     * Busca una cuenta de sistema por nombre de usuario.
     * @param username Nombre de usuario a buscar
     * @return CuentaSistemaDTO con los datos de la cuenta
     */
    CuentaSistemaDTO buscarPorUsername(String username);
    
    /**
     * Guarda o actualiza una cuenta de sistema.
     * @param dto Datos de la cuenta de sistema
     * @return CuentaSistemaDTO persistido con ID asignado
     */
    CuentaSistemaDTO guardarCuentaSistema(CuentaSistemaDTO dto);
    
    /**
     * Elimina una cuenta de sistema.
     * @param id Identificador de la cuenta
     */
    void eliminarCuentaSistema(Long id);
    
    /**
     * Lista todas las cuentas de sistema.
     * @return Lista de CuentaSistemaDTO
     */
    List<CuentaSistemaDTO> listarCuentasSistema();
    
    /**
     * Busca cuentas de sistema por filtro (username o rol).
     * @param filtro Criterio de búsqueda
     * @return Lista de cuentas que coinciden
     */
    List<CuentaSistemaDTO> buscarCuentasSistema(String filtro);

    /**
     * Busca usuarios por nombre, nómina o puesto.
     * @param busquedaGlobal Criterio de búsqueda (búsqueda parcial en múltiples campos)
     * @return Lista de UsuarioDTO que coinciden
     */
    List<UsuarioDTO> buscarUsuarios(String busquedaGlobal);
    
    /**
     * Obtiene los datos completos de un usuario.
     * @param id Identificador del usuario
     * @return UsuarioDTO con la información completa
     */
    UsuarioDTO obtenerUsuario(Long id);
    
    /**
     * Guarda o actualiza los datos de un usuario.
     * @param dto Datos del usuario (nombre, nómina, puesto)
     */
    void guardarUsuario(UsuarioDTO dto);
    
    /**
     * Cambia el estado de un usuario (activo/inactivo).
     * @param id Identificador del usuario
     * @param activo true para activar, false para desactivar
     */
    void cambiarEstadoUsuario(Long id, boolean activo);
    
    /**
     * Lista todos los usuarios activos.
     * @return Lista de usuarios activos
     */
    List<UsuarioDTO> listarUsuariosActivos();
    
    /**
     * Busca usuarios por departamento.
     * @param idDepartamento Identificador del departamento
     * @return Lista de usuarios del departamento
     */
    List<UsuarioDTO> buscarUsuariosPorDepartamento(Long idDepartamento);
    
    /**
     * Busca usuarios por puesto.
     * @param idPuesto Identificador del puesto
     * @return Lista de usuarios con ese puesto
     */
    List<UsuarioDTO> buscarUsuariosPorPuesto(Long idPuesto);
    
    /**
     * Verifica si un usuario tiene equipos asignados activos.
     * @param idUsuario Identificador del usuario
     * @return true si tiene equipos asignados activos
     */
    boolean usuarioTieneEquiposAsignados(Long idUsuario);
}