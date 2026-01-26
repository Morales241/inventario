/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interfaces;

import Dtos.DepartamentoDto;
import Dtos.EmpresaDto;
import Dtos.PuestoDto;
import Dtos.SucursalDto;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IFachadaOrganizacion {
    public List<EmpresaDto> listarEmpresas();

    public List<SucursalDto> listarSucursalesPorEmpresa(Long idEmpresa);
    
    public List<DepartamentoDto> listarDeptosPorSucursal(Long idSucursal);

    public List<PuestoDto> listarPuestosPorDepto(Long idDepto);
    
    public void guardarEmpresa(EmpresaDto dto) throws Exception;
    
    public void guardarSucursal(SucursalDto dto) throws Exception;
    
    public void guardarDepartamento(DepartamentoDto dto) throws Exception;
    
    public void guardarPuesto(PuestoDto dto) throws Exception;
}
