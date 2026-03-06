// InventarioNegocio/src/main/java/interfacesServicios/IServicioOrganizacion.java
package interfacesServicios;

import Dtos.DepartamentoDTO;
import Dtos.EmpresaDTO;
import Dtos.PuestoDTO;
import Dtos.SucursalDTO;
import java.util.List;

/**
 * Interfaz para el servicio de gestión de la estructura organizacional.
 * Define operaciones CRUD y búsquedas específicas para empresas, sucursales,
 * departamentos y puestos.
 */
public interface IServicioOrganizacion {
    
    /**
     * Lista empresas con filtro opcional por nombre.
     * @param filtroNombre Criterio de búsqueda (puede ser nulo/vacío)
     * @return Lista de EmpresaDTO
     */
    List<EmpresaDTO> listarEmpresas(String filtroNombre);
    
    /**
     * Guarda o actualiza una empresa.
     * @param dto Datos de la empresa
     * @return EmpresaDTO persistido con ID asignado
     */
    EmpresaDTO guardarEmpresa(EmpresaDTO dto);
    
    /**
     * Elimina una empresa si no tiene sucursales vinculadas.
     * @param id Identificador de la empresa
     */
    void eliminarEmpresa(Long id);

    // ========================= SUCURSALES =========================
    
    /**
     * Lista sucursales de una empresa con filtros opcionales.
     * @param filtro Formato "nombre,ubicación" (puede ser nulo/vacío)
     * @param idEmpresa Identificador de la empresa
     * @return Lista de SucursalDTO
     */
    List<SucursalDTO> listarSucursales(String filtro, Long idEmpresa);
    
    /**
     * Guarda o actualiza una sucursal.
     * @param dto Datos de la sucursal
     * @return SucursalDTO persistido con ID asignado
     */
    SucursalDTO guardarSucursal(SucursalDTO dto);
    
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
     * Guarda o actualiza un departamento.
     * @param dto Datos del departamento
     * @return DepartamentoDTO persistido con ID asignado
     */
    DepartamentoDTO guardarDepartamento(DepartamentoDTO dto);
    
    /**
     * Elimina un departamento si no tiene puestos vinculados.
     * @param id Identificador del departamento
     */
    void eliminarDepartamento(Long id);

    /**
     * Lista todos los puestos de un departamento.
     * @param idDepartamento Identificador del departamento
     * @return Lista de PuestoDTO
     */
    List<PuestoDTO> listarPuestos(Long idDepartamento);
    
    /**
     * Guarda o actualiza un puesto de trabajo.
     * @param dto Datos del puesto
     * @return PuestoDTO persistido con ID asignado
     */
    PuestoDTO guardarPuesto(PuestoDTO dto);
    
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