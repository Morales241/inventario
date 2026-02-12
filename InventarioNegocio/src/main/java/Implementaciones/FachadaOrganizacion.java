/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Implementaciones;

import Dtos.DepartamentoDTO;
import Dtos.EmpresaDTO;
import Dtos.PuestoDTO;
import Dtos.SucursalDTO;
import InterfacesFachada.IFachadaOrganizacion;
import Servicios.ServicioOrganizacion;
import interfacesServicios.IServicioOrganizacion;
import java.util.List;

/**
 * Implementación de la fachada para operaciones de gestión de la estructura organizacional.
 * <p>
 * Proporciona acceso simplificado a la lógica de negocio del servicio organizacional.
 * Utiliza el patrón Facade para abstraer la complejidad de las operaciones de empresas,
 * sucursales, departamentos y puestos.
 * </p>
 * @author JMorales
 */
public class FachadaOrganizacion implements IFachadaOrganizacion {

    private final IServicioOrganizacion servicioOrganizacion;

    // Inyección empresarial
    public FachadaOrganizacion(IServicioOrganizacion servicioOrganizacion) {
        this.servicioOrganizacion = servicioOrganizacion;
    }

    public FachadaOrganizacion() {
        this(new ServicioOrganizacion());
    }

    @Override
    public List<EmpresaDTO> listarEmpresas(String filtroNombre) {
        return servicioOrganizacion.listarEmpresas(filtroNombre);
    }

    @Override
    public void guardarEmpresa(EmpresaDTO dto) {
        servicioOrganizacion.guardarEmpresa(dto);
    }

    @Override
    public void eliminarEmpresa(Long id) {
        servicioOrganizacion.eliminarEmpresa(id);
    }

    @Override
    public List<SucursalDTO> listarSucursales(String filtro, Long idEmpresa) {
        return servicioOrganizacion.listarSucursales(filtro, idEmpresa);
    }

    @Override
    public void guardarSucursal(SucursalDTO dto) {
        servicioOrganizacion.guardarSucursal(dto);
    }

    @Override
    public void eliminarSucursal(Long id) {
        servicioOrganizacion.eliminarSucursal(id);
    }


    @Override
    public List<DepartamentoDTO> listarDepartamentos(String nombre, Long idSucursal) {
        return servicioOrganizacion.listarDepartamentos(nombre, idSucursal);
    }

    @Override
    public void guardarDepartamento(DepartamentoDTO dto) {
        servicioOrganizacion.guardarDepartamento(dto);
    }

    @Override
    public void eliminarDepartamento(Long id) {
        servicioOrganizacion.eliminarDepartamento(id);
    }

    @Override
    public List<PuestoDTO> listarPuestos(Long idDepto) {
        return servicioOrganizacion.listarPuestos(idDepto);
    }

    @Override
    public void guardarPuesto(PuestoDTO dto) {
        servicioOrganizacion.guardarPuesto(dto);
    }

    @Override
    public void eliminarPuesto(Long id) {
        servicioOrganizacion.eliminarPuesto(id);
    }
}
