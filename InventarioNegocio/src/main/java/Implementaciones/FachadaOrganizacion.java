/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Implementaciones;

import Dtos.DepartamentoDto;
import Dtos.EmpresaDto;
import Dtos.PuestoDto;
import Dtos.SucursalDto;
import Interfaces.IFachadaOrganizacion;
import Servicios.ServicioOrganizacion;
import java.util.List;

/**
 *
 * @author JMorales
 */
public class FachadaOrganizacion implements IFachadaOrganizacion{

    private final ServicioOrganizacion servicioOrganizacion;

    public FachadaOrganizacion() {
        this.servicioOrganizacion = new ServicioOrganizacion();
    }
    
    @Override
    public List<EmpresaDto> listarEmpresas(String filtroNombre) {
        return servicioOrganizacion.listarEmpresas(filtroNombre);
    }

    @Override
    public void guardarEmpresa(EmpresaDto dto) throws Exception {
        servicioOrganizacion.guardarEmpresa(dto);
    }

    @Override
    public void eliminarEmpresa(Long id) throws Exception {
        servicioOrganizacion.eliminarEmpresa(id);
    }

    @Override
    public List<SucursalDto> listarSucursales(String filtro, Long idEmpresa) {
        return servicioOrganizacion.listarSucursales(filtro, idEmpresa);
    }

    @Override
    public void guardarSucursal(SucursalDto dto) throws Exception {
        servicioOrganizacion.guardarSucursal(dto);
    }

    @Override
    public void eliminarSucursal(Long id) throws Exception {
        servicioOrganizacion.eliminarSucursal(id);
    }

    @Override
    public List<DepartamentoDto> listarDepartamentos(String nombre, Long idSucursal) {
        return servicioOrganizacion.listarDepartamentos(nombre, idSucursal);
    }

    @Override
    public void guardarDepartamento(DepartamentoDto dto) throws Exception {
        servicioOrganizacion.guardarDepartamento(dto);
    }

    @Override
    public void eliminarDepartamento(Long id) throws Exception {
        servicioOrganizacion.eliminarDepartamento(id);
    }

    @Override
    public List<PuestoDto> listarPuestos(Long idDepto) {
        return servicioOrganizacion.listarPuestos(idDepto);
    }

    @Override
    public void guardarPuesto(PuestoDto dto) throws Exception {
        servicioOrganizacion.guardarPuesto(dto);
    }

    @Override
    public void eliminarPuesto(Long id) throws Exception {
        servicioOrganizacion.eliminarPuesto(id);
    }
}
