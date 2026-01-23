package Mappers;

import Dtos.*;
import Entidades.*;
import java.util.function.Function;

public class MapperEstructura {

    public static final Mapper<Empresa, EmpresaDto> empresa = new Mapper<>(
            (entity) -> new EmpresaDto(entity.getIdEmpresa(), entity.getNombre()),
            (dto) -> {
                Empresa e = new Empresa();
                e.setIdEmpresa(dto.getId());
                e.setNombre(dto.getNombre());
                return e;
            }
    );

    public static final Mapper<Sucursal, SucursalDto> sucursal = new Mapper<>(
            (entity) -> {
                SucursalDto dto = new SucursalDto();
                dto.setId(entity.getIdSucursal());
                dto.setNombre(entity.getNombre());
                dto.setUbicacion(entity.getUbicacion());
                if (entity.getEmpresa() != null) {
                    dto.setIdEmpresa(entity.getEmpresa().getIdEmpresa());
                    dto.setNombreEmpresa(entity.getEmpresa().getNombre());
                }
                return dto;
            },
            (dto) -> {
                Sucursal s = new Sucursal();
                s.setIdSucursal(dto.getId());
                s.setNombre(dto.getNombre());
                s.setUbicacion(dto.getUbicacion());
                // Relación (Solo ID para guardar)
                if (dto.getIdEmpresa() != null) {
                    Empresa e = new Empresa();
                    e.setIdEmpresa(dto.getIdEmpresa());
                    s.setEmpresa(e);
                }
                return s;
            }
    );

    // DEPARTAMENTO
    public static final Mapper<Departamento, DepartamentoDto> departamento = new Mapper<>(
            (entity) -> {
                DepartamentoDto dto = new DepartamentoDto();
                dto.setId(entity.getIdDepartamento());
                dto.setNombre(entity.getNombre());
                if (entity.getSucursal() != null) {
                    dto.setIdSucursal(entity.getSucursal().getIdSucursal());
                    dto.setNombreSucursal(entity.getSucursal().getNombre());
                }
                return dto;
            },
            (dto) -> {
                Departamento d = new Departamento();
                d.setIdDepartamento(dto.getId());
                d.setNombre(dto.getNombre());
                if (dto.getIdSucursal() != null) {
                    Sucursal s = new Sucursal();
                    s.setIdSucursal(dto.getIdSucursal());
                    d.setSucursal(s);
                }
                return d;
            }
    );

    // PUESTO 
    public static final Mapper<Puesto, PuestoDto> puesto = new Mapper<>(
            (entity) -> {
                PuestoDto dto = new PuestoDto();
                dto.setId(entity.getIdPuesto());
                dto.setNombre(entity.getNombre());
                if (entity.getDepartamento() != null) {
                    dto.setIdDepartamento(entity.getDepartamento().getIdDepartamento());
                    dto.setNombreDepartamento(entity.getDepartamento().getNombre());
                }
                return dto;
            },
            (dto) -> {
                Puesto p = new Puesto();
                p.setIdPuesto(dto.getId());
                p.setNombre(dto.getNombre());
                if (dto.getIdDepartamento() != null) {
                    Departamento d = new Departamento();
                    d.setIdDepartamento(dto.getIdDepartamento());
                    p.setDepartamento(d);
                }
                return p;
            }
    );
}
