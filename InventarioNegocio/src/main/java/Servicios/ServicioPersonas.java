package Servicios;

import Dao.DaoTrabajador;
import Dao.DaoUsuario;
import Dtos.TrabajadorDto;
import Dtos.UsuarioDto;
import Entidades.Trabajador;
import Entidades.UsuarioSistema;
import Mappers.MapperTrabajador;
import Mappers.MapperUsuario;
import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de personas en el sistema.
 * <p>
 * Coordina las operaciones relacionadas con autenticación de usuarios y administración de trabajadores.
 * Incluye validaciones de integridad de datos y cambios de estado para el control de acceso.
 * </p>
 */
public class ServicioPersonas {

    private final DaoUsuario daoUsuario;
    private final DaoTrabajador daoTrabajador;

    public ServicioPersonas() {
        daoUsuario = new DaoUsuario();
        daoTrabajador = new DaoTrabajador();
    }
    
    /**
     * Autentica un usuario en el sistema validando credenciales.
     * <p>
     * <b>Comportamiento:</b>
     * <ul>
     * <li>Valida el usuario y contraseña contra la base de datos.</li>
     * <li>Si las credenciales son incorrectas, lanza una excepción.</li>
     * <li>Si son válidas, retorna el objeto user con su información completa.</li>
     * </ul>
     * </p>
     * @param user Nombre de usuario o email de acceso.
     * @param pass Contraseña del usuario (se valida contra BD).
     * @return UsuarioDto con la información del usuario autenticado.
     * @throws Exception Si el usuario no existe o la contraseña es incorrecta.
     */
    public UsuarioDto login(String user, String pass) throws Exception {
        UsuarioSistema u = daoUsuario.login(user, pass);
        if (u == null) {
            throw new Exception("Usuario o contraseña incorrectos.");
        }
        return MapperUsuario.converter.mapToDto(u);
    }
    
    /**
     * Busca trabajadores por nombre, número de nómina o puesto.
     * @param busquedaGlobal
     * @return 
     */
    /**
     * Busca trabajadores aplicando un criterio global en múltiples campos.
     * <p>
     * <b>Campos de búsqueda:</b>
     * <ul>
     * <li>Nombre del trabajador (coincidencia parcial).</li>
     * <li>Número de nómina (coincidencia parcial).</li>
     * <li>Descripción del puesto (coincidencia parcial).</li>
     * </ul>
     * <li>Si la búsqueda está vacía, retorna una lista vacía o limitada según BD.</li>
     * </p>
     * @param busquedaGlobal Texto a buscar en nombre, nómina y puesto. Puede ser nulo/vacío.
     * @return Lista de TrabajadorDto que coinciden con el criterio.
     */
    public List<TrabajadorDto> buscarTrabajadores(String busquedaGlobal) {
        return MapperTrabajador.converter.mapToDtoList(
            daoTrabajador.busquedaConFiltros(busquedaGlobal, busquedaGlobal, busquedaGlobal)
        );
    }
    
    /**
     * Obtiene los datos completos de un trabajador por su ID.
     * <p>Útil para cargar información detallada y relaciones dentro de formularios de edición.</p>
     * @param id Identificador único del trabajador.
     * @return TrabajadorDto con la información completa, o null si no existe.
     */
    public TrabajadorDto obtenerTrabajador(Long id) {
        return MapperTrabajador.converter.mapToDto(daoTrabajador.buscarPorId(id));
    }

    /**
     * Guarda o actualiza los datos de un trabajador en la base de datos.
     * <p>
     * <b>Validaciones obligatorias:</b>
     * <ul>
     * <li>Número de nómina (único identificador).</li>
     * <li>Nombre del trabajador.</li>
     * <li>ID del puesto asociado.</li>
     * </ul>
     * <b>Flujo de ejecución:</b>
     * <ul>
     * <li>Si el ID es mayor a 0, realiza <b>actualización</b>.</li>
     * <li>Si el ID es nulo o 0, realiza <b>nuevo registro</b>.</li>
     * </ul>
     * </p>
     * @param dto Datos del trabajador a guardar/actualizar.
     * @throws Exception Si faltan datos obligatorios o hay error en persistencia.
     */
    public void guardarTrabajador(TrabajadorDto dto) throws Exception {
        if (dto.getNoNomina() == null || dto.getNoNomina().isEmpty()) throw new Exception("El No. Nómina es obligatorio");
        if (dto.getNombre() == null || dto.getNombre().isEmpty()) throw new Exception("El Nombre es obligatorio");
        if (dto.getIdPuesto() == null) throw new Exception("El Puesto es obligatorio");

        Trabajador entidad = MapperTrabajador.converter.mapToEntity(dto);
        
        if (dto.getId() != null && dto.getId() > 0) {
            
            daoTrabajador.actualizar(entidad);
        } else {
            
            daoTrabajador.guardar(entidad);
        }
    }
    
    /**
     * Cambia el estado de actividad de un trabajador en el sistema.
     * <p>
     * <b>Impacto del cambio:</b>
     * <ul>
     * <li>Si {@code activo = true}, el trabajador puede recibir asignaciones de equipos.</li>
     * <li>Si {@code activo = false}, el trabajador está dado de baja y no puede recibir equipos nuevos.</li>
     * </ul>
     * </p>
     * @param id Identificador único del trabajador.
     * @param activo Nuevo estado de actividad (true = activo, false = dado de baja).
     * @throws Exception Si el trabajador no existe en la BD.
     */
    public void cambiarEstadoTrabajador(Long id, boolean activo) throws Exception {
        Trabajador t = daoTrabajador.buscarPorId(id);
        if(t != null) {
            t.setActivo(activo);
            daoTrabajador.actualizar(t);
        }
    }
}