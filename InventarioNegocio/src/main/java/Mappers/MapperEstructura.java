package Mappers;

import Dtos.*;
import Entidades.*;

/**
 * Mapeador centralizado para transformaciones de la estructura organizacional.
 * <p>
 * Proporciona instancias de {@link Mapper} para Empresa, Sucursal, Departamento y Puesto.
 * Maneja las conversiones bidireccionales preservando las relaciones jerárquicas.
 * </p>
 */
public class MapperEstructura {
    
    public static final Mapper<Empresa, EmpresaDto> empresa = new Mapper<>(
            (e) -> new EmpresaDto(e.getIdEmpresa(), e.getNombre()),
            (d) -> {
                Empresa e = new Empresa();
                e.setIdEmpresa(d.getId());
                e.setNombre(d.getNombre());
                return e;
            }
    );

    public static final Mapper<Sucursal, SucursalDto> sucursal = new Mapper<>(
            (e) -> {
                SucursalDto dto = new SucursalDto();
                dto.setId(e.getIdSucursal());
                dto.setNombre(e.getNombre());
                dto.setUbicacion(e.getUbicacion());
                if (e.getEmpresa() != null) dto.setIdEmpresa(e.getEmpresa().getIdEmpresa());
                return dto;
            },
            (d) -> {
                Sucursal s = new Sucursal();
                s.setIdSucursal(d.getId());
                s.setNombre(d.getNombre());
                s.setUbicacion(d.getUbicacion());
                if (d.getIdEmpresa() != null) {
                    Empresa emp = new Empresa();
                    emp.setIdEmpresa(d.getIdEmpresa());
                    s.setEmpresa(emp);
                }
                return s;
            }
    );

    public static final Mapper<Departamento, DepartamentoDto> departamento = new Mapper<>(
            (e) -> {
                DepartamentoDto dto = new DepartamentoDto();
                dto.setId(e.getIdDepartamento());
                dto.setNombre(e.getNombre());
                if (e.getSucursal() != null) {
                    dto.setIdSucursal(e.getSucursal().getIdSucursal());
                }
                return dto;
            },
            (d) -> {
                Departamento dep = new Departamento();
                dep.setIdDepartamento(d.getId());
                dep.setNombre(d.getNombre());
                if (d.getIdSucursal() != null) {
                    Sucursal s = new Sucursal();
                    s.setIdSucursal(d.getIdSucursal());
                    dep.setSucursal(s);
                }
                return dep;
            }
    );
    
    public static final Mapper<Puesto, PuestoDto> puesto = new Mapper<>(
            (e) -> {
                PuestoDto dto = new PuestoDto();
                dto.setId(e.getIdPuesto());
                dto.setNombre(e.getNombre());
                if (e.getDepartamento() != null) {
                    dto.setIdDepartamento(e.getDepartamento().getIdDepartamento());
                    dto.setNombreDepartamento(e.getDepartamento().getNombre());
                }
                return dto;
            },
            (d) -> {
                Puesto p = new Puesto();
                p.setIdPuesto(d.getId());
                p.setNombre(d.getNombre());
                if (d.getIdDepartamento() != null) {
                    Departamento dep = new Departamento();
                    dep.setIdDepartamento(d.getIdDepartamento());
                    p.setDepartamento(dep);
                }
                return p;
            }
    );
}