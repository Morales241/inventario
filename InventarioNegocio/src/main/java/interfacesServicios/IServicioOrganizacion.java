/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfacesServicios;

import Dtos.DepartamentoDTO;
import Dtos.EmpresaDTO;
import Dtos.PuestoDTO;
import Dtos.SucursalDTO;
import java.util.List;

/**
 *
 * @author tacot
 */
public interface IServicioOrganizacion {

    public List<EmpresaDTO> listarEmpresas(String filtro);

    public EmpresaDTO guardarEmpresa(EmpresaDTO dto);

    public void eliminarEmpresa(Long id);

    public List<SucursalDTO> listarSucursales(String filtro, Long idEmpresa);

    public SucursalDTO guardarSucursal(SucursalDTO dto);

    public void eliminarSucursal(Long id);

    public List<DepartamentoDTO> listarDepartamentos(String nombre, Long idSucursal);

    public DepartamentoDTO guardarDepartamento(DepartamentoDTO dto);

    public void eliminarDepartamento(Long id);

    public List<PuestoDTO> listarPuestos(Long idDepartamento);

    public PuestoDTO guardarPuesto(PuestoDTO dto);

    public void eliminarPuesto(Long id);
    
    public PuestoDTO buscarPuestoEspecifico(Long id);
    
    public List<PuestoDTO> busquedaPorEmpresa(Long idEmpresa);
    
    public EmpresaDTO buscarEmpresaPorPuesto(Long idPuesto);
}
