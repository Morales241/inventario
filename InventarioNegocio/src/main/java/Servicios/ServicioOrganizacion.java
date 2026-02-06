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

    public List<EmpresaDto> listarEmpresas(String filtroNombre) {
        if(filtroNombre != null && !filtroNombre.isEmpty()){
            return MapperEstructura.empresa.mapToDtoList(daoEmpresa.buscarPorCoincidencias(filtroNombre));
        }
        return MapperEstructura.empresa.mapToDtoList(daoEmpresa.buscarTodos());
    }

    public void guardarEmpresa(EmpresaDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getNombre().isEmpty()) throw new Exception("Nombre obligatorio");
        
        var entidad = MapperEstructura.empresa.mapToEntity(dto);
        if (dto.getId() != null && dto.getId() > 0) {
            daoEmpresa.actualizar(entidad);
        } else {
            daoEmpresa.guardar(entidad);
        }
    }
    
    public void eliminarEmpresa(Long id) throws Exception {
        try {
            daoEmpresa.eliminar(id);
        } catch (Exception e) {
            throw new Exception("No se puede eliminar: La empresa tiene sucursales vinculadas.");
        }
    }

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

    public void guardarSucursal(SucursalDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getIdEmpresa() == null) throw new Exception("Datos incompletos");
        
        var entidad = MapperEstructura.sucursal.mapToEntity(dto);
        if (dto.getId() != null && dto.getId() > 0) daoSucursal.actualizar(entidad);
        else daoSucursal.guardar(entidad);
    }
    
    public void eliminarSucursal(Long id) throws Exception {
         try { daoSucursal.eliminar(id); } 
         catch (Exception e) { throw new Exception("No se puede eliminar: Tiene departamentos o equipos vinculados."); }
    }

    public List<DepartamentoDto> listarDepartamentos(String nombre, Long idSucursal) {
        String busqueda = nombre != null ? nombre : "";
        return MapperEstructura.departamento.mapToDtoList(daoDepto.busquedaConFiltros(busqueda, idSucursal));
    }

    public void guardarDepartamento(DepartamentoDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getIdSucursal() == null) throw new Exception("Datos incompletos");
        
        var entidad = MapperEstructura.departamento.mapToEntity(dto);
        if (dto.getId() != null && dto.getId() > 0) daoDepto.actualizar(entidad);
        else daoDepto.guardar(entidad);
    }
    
    public void eliminarDepartamento(Long id) throws Exception {
         try { daoDepto.eliminar(id); } 
         catch (Exception e) { throw new Exception("No se puede eliminar: Tiene puestos vinculados."); }
    }

    public List<PuestoDto> listarPuestos(Long idDepto) {
        return MapperEstructura.puesto.mapToDtoList(daoPuesto.busquedaPorDepartamento(idDepto));
    }

    public void guardarPuesto(PuestoDto dto) throws Exception {
        if (dto.getNombre() == null || dto.getIdDepartamento() == null) throw new Exception("Datos incompletos");
        
        var entidad = MapperEstructura.puesto.mapToEntity(dto);
        if (dto.getId() != null && dto.getId() > 0) daoPuesto.actualizar(entidad);
        else daoPuesto.guardar(entidad);
    }
    
    public void eliminarPuesto(Long id) throws Exception {
         try { daoPuesto.eliminar(id); } 
         catch (Exception e) { throw new Exception("No se puede eliminar: Tiene trabajadores vinculados."); }
    }
}