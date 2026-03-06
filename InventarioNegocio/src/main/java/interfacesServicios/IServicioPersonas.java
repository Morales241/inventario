// InventarioNegocio/src/main/java/interfacesServicios/IServicioPersonas.java
package interfacesServicios;

import Dtos.UsuarioDTO;
import Dtos.CuentaSistemaDTO;
import java.util.List;

/**
 * Interfaz para el servicio de gestión de personas en el sistema.
 * Define operaciones para autenticación y administración de usuarios (empleados)
 * y cuentas de sistema.
 */
public interface IServicioPersonas {

    /**
     * Autentica un usuario validando credenciales de acceso.
     * @param username Nombre de usuario
     * @param password Contraseña del usuario
     * @return CuentaSistemaDTO con información del usuario autenticado
     */
    CuentaSistemaDTO login(String username, String password);
    
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
     * @param criterioGlobal Criterio de búsqueda (búsqueda parcial en múltiples campos)
     * @return Lista de UsuarioDTO que coinciden
     */
    List<UsuarioDTO> buscarUsuarios(String criterioGlobal);
    
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