package InterfacesFachada;

import Dtos.DepartamentoDTO;
import Dtos.EmpresaDTO;
import Dtos.PuestoDTO;
import Dtos.SucursalDTO;
import java.util.List;

/**
 * Contrato de fachada para operaciones de gestión de la estructura organizacional.
 * <p>
 * Define métodos para CRUD de empresas, sucursales, departamentos y puestos de trabajo.
 * Implementa el patrón Facade para simplificar la interacción con la capa de servicios organizacionales.
 * </p>
 * @author JMorales
 */
public interface IFachadaOrganizacion {

    /**
     * Lista empresas con filtro opcional por nombre.
     * @param filtroNombre Criterio de búsqueda (puede ser nulo/vacío).
     * @return Lista de EmpresaDTO.
     */
    public List<EmpresaDTO> listarEmpresas(String filtroNombre);

    /**
     * Guarda o actualiza una empresa.
     * @param dto Datos de la empresa.
     * @throws Exception Si el nombre está vacío o hay error en BD.
     */
    public void guardarEmpresa(EmpresaDTO dto) throws Exception;

    /**
     * Elimina una empresa si no tiene sucursales vinculadas.
     * @param id Identificador de la empresa.
     * @throws Exception Si tiene sucursales o no existe.
     */
    public void eliminarEmpresa(Long id) throws Exception;

    /**
     * Lista sucursales de una empresa con filtros opcionales.
     * @param filtro Formato "nombre,ubicación" (puede ser nulo/vacío).
     * @param idEmpresa Identificador de la empresa.
     * @return Lista de SucursalDTO.
     */
    public List<SucursalDTO> listarSucursales(String filtro, Long idEmpresa);

    /**
     * Guarda o actualiza una sucursal.
     * @param dto Datos de la sucursal.
     * @throws Exception Si faltan datos obligatorios o hay error en BD.
     */
    public void guardarSucursal(SucursalDTO dto) throws Exception;

    /**
     * Elimina una sucursal si no tiene departamentos o equipos vinculados.
     * @param id Identificador de la sucursal.
     * @throws Exception Si tiene referencias o no existe.
     */
    public void eliminarSucursal(Long id) throws Exception;

    /**
     * Lista departamentos de una sucursal con filtro opcional.
     * @param nombre Criterio de búsqueda por nombre (puede ser nulo/vacío).
     * @param idSucursal Identificador de la sucursal.
     * @return Lista de DepartamentoDTO.
     */
    public List<DepartamentoDTO> listarDepartamentos(String nombre, Long idSucursal);

    /**
     * Guarda o actualiza un departamento.
     * @param dto Datos del departamento.
     * @throws Exception Si faltan datos obligatorios o hay error en BD.
     */
    public void guardarDepartamento(DepartamentoDTO dto) throws Exception;

    /**
     * Elimina un departamento si no tiene puestos vinculados.
     * @param id Identificador del departamento.
     * @throws Exception Si tiene referencias o no existe.
     */
    public void eliminarDepartamento(Long id) throws Exception;

    /**
     * Lista todos los puestos de un departamento.
     * @param idDepto Identificador del departamento.
     * @return Lista de PuestoDTO.
     */
    public List<PuestoDTO> listarPuestos(Long idDepto);
    
    /**
     * Guarda o actualiza un puesto de trabajo.
     * @param dto Datos del puesto.
     * @throws Exception Si faltan datos obligatorios o hay error en BD.
     */
    public void guardarPuesto(PuestoDTO dto) throws Exception;
    
    /**
     * Elimina un puesto si no tiene trabajadores asignados.
     * @param id Identificador del puesto.
     * @throws Exception Si tiene referencias o no existe.
     */
    public void eliminarPuesto(Long id) throws Exception;
    
    /**
     * Busca un puesto en especifico por su identificador
     * @param id Identificador del puesto.
     */
    public PuestoDTO buscarPuestoEspecifico(Long id);
    
    /**
     * Busca todos los puestos que tengan relación con el identificador de una empresa
     * @param idEmpresa identificador de empresa
     * @return lista de puestos
     */
    public List<PuestoDTO> busquedaPorEmpresa(Long idEmpresa);
    
    /**
     * Busca una empresa especifica a travez del identificador de un puesto
     * @param id identificador del puesto
     * @return empresa especifica
     */
     public EmpresaDTO buscarEmpresaPorPuesto(Long id);
}
