package Servicios;

import Dao.*;
import Dtos.*;
import Mappers.*;
import java.util.List;

public class ServicioOrganizacion {

    private final DaoEmpresa daoEmpresa = new DaoEmpresa();
    private final DaoSucursal daoSucursal = new DaoSucursal();
    private final DaoDepartamento daoDepto = new DaoDepartamento();
    private final DaoPuesto daoPuesto = new DaoPuesto();

    public List<EmpresaDto> listarEmpresas() {
        return MapperEstructura.empresa.mapToDtoList(daoEmpresa.buscarTodos());
    }

    public List<SucursalDto> listarSucursalesPorEmpresa(Long idEmpresa) {
        return MapperEstructura.sucursal.mapToDtoList(daoSucursal.busquedaConFiltros("", "", idEmpresa));
    }

    public List<DepartamentoDto> listarDeptosPorSucursal(Long idSucursal) {
        return MapperEstructura.departamento.mapToDtoList(daoDepto.busquedaConFiltros("", idSucursal));
    }

    public List<PuestoDto> listarPuestosPorDepto(Long idDepto) {
        return MapperEstructura.puesto.mapToDtoList(daoPuesto.busquedaPorDepartamento(idDepto));
    }


    public void guardarEmpresa(EmpresaDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isEmpty()) {
            throw new Exception("El nombre de la Empresa es obligatorio");
        }
        daoEmpresa.guardar(MapperEstructura.empresa.mapToEntity(dto));
    }
    
    
    public void guardarSucursal(SucursalDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isEmpty()) {
            throw new Exception("El nombre de la sucursal es obligatorio");
        }
        if (dto.getNombreEmpresa()== null || dto.getNombreEmpresa().isEmpty()) {
            throw new Exception("El nombre de la empresa asociada es obligatorio");
        }
        if (dto.getUbicacion()== null || dto.getUbicacion().isEmpty()) {
            throw new Exception("La ubicación de la sucursal es obligatorio");
        }
        daoSucursal.guardar(MapperEstructura.sucursal.mapToEntity(dto));
    }
    
    
    public void guardarDepartamento(DepartamentoDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isEmpty()) {
            throw new Exception("El nombre del departamento es obligatorio");
        }
        if (dto.getNombreSucursal()== null || dto.getNombreSucursal().isEmpty()) {
            throw new Exception("El nombre de la sucursal es obligatorio");
        }
        daoDepto.guardar(MapperEstructura.departamento.mapToEntity(dto));
    }
    
    
    public void guardarPuesto(PuestoDto dto) throws Exception {
        if (dto.getNombre()== null || dto.getNombre().isEmpty()) {
            throw new Exception("El nombre de la sucursal es obligatorio");
        }
        if (dto.getNombreDepartamento()== null || dto.getNombreDepartamento().isEmpty()) {
            throw new Exception("El nombre de la sucursal es obligatorio");
        }
        daoPuesto.guardar(MapperEstructura.puesto.mapToEntity(dto));
    }
    
    
}