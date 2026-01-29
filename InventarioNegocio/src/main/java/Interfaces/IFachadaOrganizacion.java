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

    public List<EmpresaDto> listarEmpresas(String filtroNombre);

    public void guardarEmpresa(EmpresaDto dto) throws Exception;

    public void eliminarEmpresa(Long id) throws Exception;

    public List<SucursalDto> listarSucursales(String filtro, Long idEmpresa);

    public void guardarSucursal(SucursalDto dto) throws Exception;

    public void eliminarSucursal(Long id) throws Exception;

    public List<DepartamentoDto> listarDepartamentos(String nombre, Long idSucursal);

    public void guardarDepartamento(DepartamentoDto dto) throws Exception;

    public void eliminarDepartamento(Long id) throws Exception;

    public List<PuestoDto> listarPuestos(Long idDepto);
    
    public void guardarPuesto(PuestoDto dto) throws Exception;
    
    public void eliminarPuesto(Long id) throws Exception;
}
