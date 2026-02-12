package Mappers;

import Dtos.*;
import Entidades.*;
import java.util.Objects;

/**
 * Mapeador centralizado para transformaciones de la estructura organizacional.
 * <p>
 * Proporciona instancias de {@link Mapper} para Empresa, Sucursal, Departamento y Puesto.
 * Maneja las conversiones bidireccionales preservando las relaciones jerárquicas.
 * </p>
 */
public class MapperEstructura {

    public static final Mapper<Empresa, EmpresaDTO> empresa =
            new Mapper<>(

                    (e) -> {
                        EmpresaDTO dto = new EmpresaDTO();
                        dto.setId(e.getIdEmpresa());
                        dto.setNombre(e.getNombre());

                        if (e.getSucursales() != null) {
                            dto.setTotalSucursales(e.getSucursales().size());
                        } else {
                            dto.setTotalSucursales(0);
                        }

                        return dto;
                    },

                    (d) -> {
                        Empresa e = new Empresa();
                        e.setIdEmpresa(d.getId());
                        e.setNombre(d.getNombre());
                        return e;
                    }
            );

    public static final Mapper<Sucursal, SucursalDTO> sucursal =
            new Mapper<>(

                    (e) -> {
                        SucursalDTO dto = new SucursalDTO();
                        dto.setId(e.getIdSucursal());
                        dto.setNombre(e.getNombre());
                        dto.setUbicacion(e.getUbicacion());

                        if (Objects.nonNull(e.getEmpresa())) {
                            dto.setIdEmpresa(e.getEmpresa().getIdEmpresa());
                            dto.setNombreEmpresa(e.getEmpresa().getNombre());
                        }

                        return dto;
                    },

                    (d) -> {
                        Sucursal s = new Sucursal();
                        s.setIdSucursal(d.getId());
                        s.setNombre(d.getNombre());
                        s.setUbicacion(d.getUbicacion());

                        return s;
                    }
            );

    public static final Mapper<Departamento, DepartamentoDTO> departamento =
            new Mapper<>(

                    (e) -> {
                        DepartamentoDTO dto = new DepartamentoDTO();
                        dto.setId(e.getIdDepartamento());
                        dto.setNombre(e.getNombre());

                        if (Objects.nonNull(e.getSucursal())) {
                            dto.setIdSucursal(e.getSucursal().getIdSucursal());
                            dto.setNombreSucursal(e.getSucursal().getNombre());
                        }

                        return dto;
                    },

                    (d) -> {
                        Departamento dep = new Departamento();
                        dep.setIdDepartamento(d.getId());
                        dep.setNombre(d.getNombre());

                        /*
                         * NO se setea Sucursal aquí.
                         * El servicio debe buscarla y asignarla.
                         */

                        return dep;
                    }
            );

    public static final Mapper<Puesto, PuestoDTO> puesto =
            new Mapper<>(

                    (e) -> {
                        PuestoDTO dto = new PuestoDTO();
                        dto.setId(e.getIdPuesto());
                        dto.setNombre(e.getNombre());

                        if (Objects.nonNull(e.getDepartamento())) {
                            dto.setIdDepartamento(e.getDepartamento().getIdDepartamento());
                            dto.setNombreDepartamento(e.getDepartamento().getNombre());
                        }

                        return dto;
                    },

                    (d) -> {
                        Puesto p = new Puesto();
                        p.setIdPuesto(d.getId());
                        p.setNombre(d.getNombre());


                        return p;
                    }
            );
}
