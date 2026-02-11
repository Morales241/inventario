package Interfaces;

import Dtos.TrabajadorDto;
import Dtos.UsuarioDto;
import java.util.List;

/**
 * Contrato de fachada para operaciones de gestión de personas (usuarios y trabajadores).
 * <p>
 * Define métodos para autenticación, búsqueda, gestión de trabajadores y control de acceso.
 * Implementa el patrón Facade para simplificar la interacción con la capa de servicios de personas.
 * </p>
 * @author JMorales
 */
public interface IFachadaPersonas {
    
    /**
     * Autentica un usuario validando credenciales de acceso.
     * @param user Nombre de usuario o email.
     * @param pass Contraseña del usuario.
     * @return UsuarioDto con información del usuario autenticado.
     * @throws Exception Si el usuario o contraseña son incorrectos.
     */
    public UsuarioDto login(String user, String pass) throws Exception;
    
    /**
     * Busca trabajadores por nombre, nómina o puesto.
     * @param busquedaGlobal Criterio de búsqueda (búsqueda parcial en múltiples campos).
     * @return Lista de TrabajadorDto que coinciden.
     */
    public List<TrabajadorDto> buscarTrabajadores(String busquedaGlobal);
    
    /**
     * Obtiene los datos completos de un trabajador.
     * @param id Identificador del trabajador.
     * @return TrabajadorDto con la información completa.
     */
    public TrabajadorDto obtenerTrabajador(Long id);
    
    /**
     * Guarda o actualiza los datos de un trabajador.
     * @param dto Datos del trabajador (nombre, nómina, puesto).
     * @throws Exception Si faltan datos obligatorios o hay error en BD.
     */
    public void guardarTrabajador(TrabajadorDto dto) throws Exception;
    
    /**
     * Cambia el estado de un trabajador (activo/dado de baja).
     * @param id Identificador del trabajador.
     * @param activo true para activar, false para desactivar.
     * @throws Exception Si el trabajador no existe.
     */
    public void cambiarEstadoTrabajador(Long id, boolean activo) throws Exception;
}
