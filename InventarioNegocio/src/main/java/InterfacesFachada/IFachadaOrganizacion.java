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
 * Implementa el patrón Facade para simplificar la interacción con la capa de 
 * servicios organizacionales.
 * </p>
 * @author JMorales
 */
public interface IFachadaOrganizacion {
    
    /**
     * Lista empresas con filtro opcional por nombre.
     * @param filtroNombre Criterio de búsqueda (puede ser nulo/vacío)
     * @return Lista de EmpresaDTO
     */
    List<EmpresaDTO> listarEmpresas(String filtroNombre);

    /**
     * Lista todas las empresas sin filtros.
     * @return Lista completa de EmpresaDTO
     */
    List<EmpresaDTO> listarTodasEmpresas();

    /**
     * Guarda o actualiza una empresa.
     * @param dto Datos de la empresa
     */
    void guardarEmpresa(EmpresaDTO dto);

    /**
     * Elimina una empresa si no tiene sucursales vinculadas.
     * @param id Identificador de la empresa
     */
    void eliminarEmpresa(Long id);

    /**
     * Lista sucursales de una empresa con filtros opcionales.
     * @param filtro Formato "nombre,ubicación" (puede ser nulo/vacío)
     * @param idEmpresa Identificador de la empresa
     * @return Lista de SucursalDTO
     */
    List<SucursalDTO> listarSucursales(String filtro, Long idEmpresa);

    /**
     * Lista todas las sucursales sin filtros.
     * @return Lista de SucursalDTO
     */
    List<SucursalDTO> listarTodasSucursales();

    /**
     * Guarda o actualiza una sucursal.
     * @param dto Datos de la sucursal
     */
    void guardarSucursal(SucursalDTO dto);

    /**
     * Elimina una sucursal si no tiene departamentos o equipos vinculados.
     * @param id Identificador de la sucursal
     */
    void eliminarSucursal(Long id);

    /**
     * Lista departamentos de una sucursal con filtro opcional.
     * @param nombre Criterio de búsqueda por nombre (puede ser nulo/vacío)
     * @param idSucursal Identificador de la sucursal
     * @return Lista de DepartamentoDTO
     */
    List<DepartamentoDTO> listarDepartamentos(String nombre, Long idSucursal);

    /**
     * Lista todos los departamentos sin filtros.
     * @return Lista de DepartamentoDTO
     */
    List<DepartamentoDTO> listarTodosDepartamentos();

    /**
     * Guarda o actualiza un departamento.
     * @param dto Datos del departamento
     */
    void guardarDepartamento(DepartamentoDTO dto);

    /**
     * Elimina un departamento si no tiene puestos vinculados.
     * @param id Identificador del departamento
     */
    void eliminarDepartamento(Long id);

    /**
     * Lista todos los puestos de un departamento.
     * @param idDepto Identificador del departamento
     * @return Lista de PuestoDTO
     */
    List<PuestoDTO> listarPuestos(Long idDepto);
    
    /**
     * Lista todos los puestos sin filtros.
     * @return Lista de PuestoDTO
     */
    List<PuestoDTO> listarTodosPuestos();
    
    /**
     * Guarda o actualiza un puesto de trabajo.
     * @param dto Datos del puesto
     */
    void guardarPuesto(PuestoDTO dto);
    
    /**
     * Elimina un puesto si no tiene trabajadores asignados.
     * @param id Identificador del puesto
     */
    void eliminarPuesto(Long id);
    
    /**
     * Busca un puesto específico por su identificador.
     * @param id Identificador del puesto
     * @return PuestoDTO con los datos del puesto
     */
    PuestoDTO buscarPuestoEspecifico(Long id);
    
    /**
     * Busca todos los puestos que tengan relación con el identificador de una empresa.
     * @param idEmpresa Identificador de empresa
     * @return Lista de puestos
     */
    List<PuestoDTO> busquedaPorEmpresa(Long idEmpresa);
    
    /**
     * Busca una empresa específica a través del identificador de un puesto.
     * @param idPuesto Identificador del puesto
     * @return EmpresaDTO con los datos de la empresa
     */
    EmpresaDTO buscarEmpresaPorPuesto(Long idPuesto);
}