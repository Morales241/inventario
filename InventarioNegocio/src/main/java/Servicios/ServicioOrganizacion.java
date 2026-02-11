package Servicios;

import Dao.*;
import Dtos.*;
import Mappers.*;
import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de la estructura organizacional.
 * <p>
 * Coordina operaciones CRUD para empresas, sucursales, departamentos y puestos.
 * Implementa valiÚciones de integridad referencial para evitar eliminaciones en cascada.
 * </p>
 */
public class ServicioOrganizacion {

    private final DaoEmpresa daoEmpresa;
    private final DaoSucursal daoSucursal;
    private final DaoDepartamento daoDepto;
    private final DaoPuesto daoPuesto;

    public ServicioOrganizacion() {
        daoEmpresa = new DaoEmpresa();
        daoSucursal = new DaoSucursal();
        daoDepto = new DaoDepartamento();
        daoPuesto = new DaoPuesto();
    }

    
    
    /**
     * Lista empresas disponibles en el sistema, con filtro opcional por nombre.
     * <p>
     * <b>Lógica de búsqueda:</b>
     * <ul>
     * <li>Si {@code filtroNombre} está vacío o nulo, retorna <b>todas</b> las empresas.</li>
     * <li>Si contiene texto, realiza búsqueda por coincidencias en el nombre.</li>
     * </ul>
     * </p>
     * @param filtroNombre Criterio de búsqueda por nombre (puede ser nulo/vacío).
     * @return Lista de EmpresaDto con la estructura de la empresa.
     */
    public List<EmpresaDto> listarEmpresas(String filtroNombre) {
        if(filtroNombre != null && !filtroNombre.isEmpty()){
            return MapperEstructura.empresa.mapToDtoList(daoEmpresa.buscarPorCoincidencias(filtroNombre));
        }
        return MapperEstructura.empresa.mapToDtoList(daoEmpresa.buscarTodos());
    }

    /**
     * Guarda o actualiza los datos de una empresa.
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>El nombre de la empresa es obligatorio.</li>
     * </ul>
     * <b>Flujo:</b>
     * <ul>
     * <li>Si el ID es mayor a 0, realiza <b>actualización</b>.</li>
     * <li>Si el ID es nulo o 0, realiza <b>nuevo registro</b>.</li>
     * </ul>
     * </p>
     * @param dto Datos de la empresa a guardar/actualizar.
     * @throws Exception Si el nombre está vacío o hay error en persistencia.
     */
    public void guardarEmpresa(EmpresaDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isEmpty()) throw new Exception("Nombre obligatorio");
        
        var entidad = MapperEstructura.empresa.mapToEntity(dto);
        if (dto.getId() != null && dto.getId() > 0) {
            daoEmpresa.actualizar(entidad);
        } else {
            daoEmpresa.guardar(entidad);
        }
    }
    
    /**
     * Elimina una empresa del sistema.
     * <p>
     * <b>Restricción de negocio:</b>
     * <ul>
     * <li>No permite eliminar empresas que tengan sucursales vinculadas.</li>
     * <li>Debe existir integridad referencial antes de proceder.</li>
     * </ul>
     * </p>
     * @param id Identificador único de la empresa a eliminar.
     * @throws Exception Si la empresa tiene sucursales vinculadas o no existe.
     */
    public void eliminarEmpresa(Long id) throws Exception {
        try {
            daoEmpresa.eliminar(id);
        } catch (Exception e) {
            throw new Exception("No se puede eliminar: La empresa tiene sucursales vinculadas.");
        }
    }

    /**
     * Lista sucursales de una empresa con filtros opcionales por nombre y ubicación.
     * <p>
     * <b>Formato del filtro:</b>
     * <ul>
     * <li>Si {@code filtro} es nulo/vacío, retorna <b>todas</b> las sucursales de la empresa.</li>
     * <li>Si contiene texto con coma, se divide en: {@code nombre,ubicación}.</li>
     * <li>Cada componente puede estar vacío para ignorar ese criterio.</li>
     * </ul>
     * </p>
     * @param filtro Cadena con formato "nombre,ubicación" (puede ser nulo/vacío).
     * @param idEmpresa ID de la empresa a la que pertenecen las sucursales.
     * @return Lista de SucursalDto que cumplen los criterios.
     */
    public List<SucursalDto> listarSucursales(String filtro, Long idEmpresa) {
        // Si no hay filtro, mandamos vacíos para traer todo
        
        String nombre = "";
        
        String ubicacion = "";
        
        if (filtro != null) {
            String[] cadenas = filtro.split(",");
            
            nombre = cadenas[0];
            ubicacion = cadenas[1];
        }
        
        return MapperEstructura.sucursal.mapToDtoList(daoSucursal.busquedaConFiltros(nombre, ubicacion, idEmpresa));
    }

    /**
     * Guarda o actualiza los datos de una sucursal.
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>El nombre de la sucursal es obligatorio.</li>
     * <li>Debe estar asociada a una empresa (idEmpresa no nulo).</li>
     * </ul>
     * <b>Flujo:</b>
     * <ul>
     * <li>Si el ID es mayor a 0, realiza <b>actualización</b>.</li>
     * <li>Si el ID es nulo o 0, realiza <b>nuevo registro</b>.</li>
     * </ul>
     * </p>
     * @param dto Datos de la sucursal a guardar/actualizar.
     * @throws Exception Si faltan datos obligatorios o hay error en persistencia.
     */
    public void guardarSucursal(SucursalDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getIdEmpresa() == null) throw new Exception("Datos incompletos");
        
        var entidad = MapperEstructura.sucursal.mapToEntity(dto);
        if (dto.getId() != null && dto.getId() > 0) daoSucursal.actualizar(entidad);
        else daoSucursal.guardar(entidad);
    }
    
    /**
     * Elimina una sucursal del sistema.
     * <p>
     * <b>Restricción de negocio:</b>
     * <ul>
     * <li>No permite eliminar sucursales que tengan departamentos o equipos vinculados.</li>
     * <li>Debe estar libre de referencias para proceder con la eliminación.</li>
     * </ul>
     * </p>
     * @param id Identificador único de la sucursal a eliminar.
     * @throws Exception Si tiene departamentos/equipos vinculados o no existe.
     */
    public void eliminarSucursal(Long id) throws Exception {
         try { daoSucursal.eliminar(id); } 
         catch (Exception e) { throw new Exception("No se puede eliminar: Tiene departamentos o equipos vinculados."); }
    }

    /**
     * Lista departamentos de una sucursal con filtro opcional por nombre.
     * <p>
     * <b>Lógica de búsqueda:</b>
     * <ul>
     * <li>Si {@code nombre} está vacío o nulo, retorna <b>todos</b> los departamentos de la sucursal.</li>
     * <li>Si contiene texto, busca departamentos que coincidan en el nombre.</li>
     * </ul>
     * </p>
     * @param nombre Criterio de búsqueda por nombre del departamento (puede ser nulo/vacío).
     * @param idSucursal ID de la sucursal a la que pertenecen los departamentos.
     * @return Lista de DepartamentoDto que cumplen con los criterios.
     */
    public List<DepartamentoDto> listarDepartamentos(String nombre, Long idSucursal) {
        String busqueda = nombre != null ? nombre : "";
        return MapperEstructura.departamento.mapToDtoList(daoDepto.busquedaConFiltros(busqueda, idSucursal));
    }

    /**
     * Guarda o actualiza los datos de un departamento.
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>El nombre del departamento es obligatorio.</li>
     * <li>Debe estar asociado a una sucursal (idSucursal no nulo).</li>
     * </ul>
     * <b>Flujo:</b>
     * <ul>
     * <li>Si el ID es mayor a 0, realiza <b>actualización</b>.</li>
     * <li>Si el ID es nulo o 0, realiza <b>nuevo registro</b>.</li>
     * </ul>
     * </p>
     * @param dto Datos del departamento a guardar/actualizar.
     * @throws Exception Si faltan datos obligatorios o hay error en persistencia.
     */
    public void guardarDepartamento(DepartamentoDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getIdSucursal() == null) throw new Exception("Datos incompletos");
        
        var entidad = MapperEstructura.departamento.mapToEntity(dto);
        if (dto.getId() != null && dto.getId() > 0) daoDepto.actualizar(entidad);
        else daoDepto.guardar(entidad);
    }
    
    /**
     * Elimina un departamento del sistema.
     * <p>
     * <b>Restricción de negocio:</b>
     * <ul>
     * <li>No permite eliminar departamentos que tengan puestos vinculados.</li>
     * <li>Debe estar libre de referencias para proceder con la eliminación.</li>
     * </ul>
     * </p>
     * @param id Identificador único del departamento a eliminar.
     * @throws Exception Si tiene puestos vinculados o no existe.
     */
    public void eliminarDepartamento(Long id) throws Exception {
         try { daoDepto.eliminar(id); } 
         catch (Exception e) { throw new Exception("No se puede eliminar: Tiene puestos vinculados."); }
    }

    /**
     * Lista todos los puestos de un departamento específico.
     * @param idDepto ID del departamento del cual obtener los puestos.
     * @return Lista de PuestoDto pertenecientes al departamento.
     */
    public List<PuestoDto> listarPuestos(Long idDepto) {
        return MapperEstructura.puesto.mapToDtoList(daoPuesto.busquedaPorDepartamento(idDepto));
    }

    /**
     * Guarda o actualiza los datos de un puesto de trabajo.
     * <p>
     * <b>Validaciones:</b>
     * <ul>
     * <li>El nombre del puesto es obligatorio.</li>
     * <li>Debe estar asociado a un departamento (idDepartamento no nulo).</li>
     * </ul>
     * <b>Flujo:</b>
     * <ul>
     * <li>Si el ID es mayor a 0, realiza <b>actualización</b>.</li>
     * <li>Si el ID es nulo o 0, realiza <b>nuevo registro</b>.</li>
     * </ul>
     * </p>
     * @param dto Datos del puesto a guardar/actualizar.
     * @throws Exception Si faltan datos obligatorios o hay error en persistencia.
     */
    public void guardarPuesto(PuestoDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getIdDepartamento() == null) throw new Exception("Datos incompletos");
        
        var entidad = MapperEstructura.puesto.mapToEntity(dto);
        if (dto.getId() != null && dto.getId() > 0) daoPuesto.actualizar(entidad);
        else daoPuesto.guardar(entidad);
    }
    
    /**
     * Elimina un puesto de trabajo del sistema.
     * <p>
     * <b>Restricción de negocio:</b>
     * <ul>
     * <li>No permite eliminar puestos que tengan trabajadores asignados.</li>
     * <li>Debe estar libre de referencias para proceder con la eliminación.</li>
     * </ul>
     * </p>
     * @param id Identificador único del puesto a eliminar.
     * @throws Exception Si tiene trabajadores vinculados o no existe.
     */
    public void eliminarPuesto(Long id) throws Exception {
         try { daoPuesto.eliminar(id); } 
         catch (Exception e) { throw new Exception("No se puede eliminar: Tiene trabajadores vinculados."); }
    }
}