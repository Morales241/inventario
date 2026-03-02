package Servicios;

import mapper.MapperEstructura;
import Dao.*;
import Dtos.*;
import Entidades.Departamento;
import Entidades.Empresa;
import Entidades.Puesto;
import Entidades.Sucursal;
import excepciones.RecursoNoEncontradoException;
import excepciones.ReglaNegocioException;
import interfacesServicios.IServicioOrganizacion;
import java.util.List;

/**
 * Servicio de lógica de negocio para la gestión de la estructura
 * organizacional.
 * <p>
 * Coordina operaciones CRUD para empresas, sucursales, departamentos y puestos.
 * Implementa valiÚciones de integridad referencial para evitar eliminaciones en
 * cascada.
 * </p>
 */
public class ServicioOrganizacion extends ServicioBase implements IServicioOrganizacion {

    private final DaoEmpresa daoEmpresa;
    private final DaoSucursal daoSucursal;
    private final DaoDepartamento daoDepartamento;
    private final DaoPuesto daoPuesto;

    public ServicioOrganizacion() {
        this.daoEmpresa = new DaoEmpresa();
        this.daoSucursal = new DaoSucursal();
        this.daoDepartamento = new DaoDepartamento();
        this.daoPuesto = new DaoPuesto();
    }
    
    private void configurarEntityManager(jakarta.persistence.EntityManager em) {
        daoEmpresa.setEntityManager(em);
        daoSucursal.setEntityManager(em);
        daoDepartamento.setEntityManager(em);
        daoPuesto.setEntityManager(em);
    }

     // ========================= EMPRESAS =========================

    @Override
    public List<EmpresaDTO> listarEmpresas(String filtro) {

        return ejecutarLectura(em -> {
            configurarEntityManager(em);

            if (filtro != null && !filtro.isBlank()) {
                return MapperEstructura.empresa
                        .mapToDtoList(daoEmpresa.buscarPorCoincidencias(filtro));
            }

            return MapperEstructura.empresa
                    .mapToDtoList(daoEmpresa.buscarTodos());
        });
    }

    @Override
    public EmpresaDTO guardarEmpresa(EmpresaDTO dto) {

        if (dto == null || dto.getNombre() == null || dto.getNombre().isBlank())
            throw new ReglaNegocioException("El nombre de la empresa es obligatorio");

        return ejecutarTransaccion(em -> {
            configurarEntityManager(em);

            Empresa entidad = MapperEstructura.empresa.mapToEntity(dto);

            if (dto.getId() != null && dto.getId() > 0) {

                Empresa existente = daoEmpresa.buscarPorId(dto.getId());
                if (existente == null)
                    throw new RecursoNoEncontradoException("Empresa no encontrada");

                entidad = daoEmpresa.actualizar(entidad);

            } else {
                entidad = daoEmpresa.guardar(entidad);
            }

            return MapperEstructura.empresa.mapToDto(entidad);
        });
    }

    @Override
    public void eliminarEmpresa(Long id) {

        if (id == null)
            throw new IllegalArgumentException("ID inválido");

        ejecutarTransaccion(em -> {
            configurarEntityManager(em);

            Empresa empresa = daoEmpresa.buscarPorId(id);

            if (empresa == null)
                throw new RecursoNoEncontradoException("Empresa no encontrada");

            if (empresa.getSucursales() != null && !empresa.getSucursales().isEmpty())
                throw new ReglaNegocioException(
                        "No se puede eliminar una empresa con sucursales asociadas");

            daoEmpresa.eliminar(id);
            return null;
        });
    }

    // ========================= SUCURSALES =========================

    @Override
    public List<SucursalDTO> listarSucursales(String filtro, Long idEmpresa) {

        return ejecutarLectura(em -> {
            configurarEntityManager(em);

            String nombre = "";
            String ubicacion = "";

            if (filtro != null && !filtro.isBlank()) {
                String[] partes = filtro.split(",");
                nombre = partes.length > 0 ? partes[0].trim() : "";
                ubicacion = partes.length > 1 ? partes[1].trim() : "";
            }

            return MapperEstructura.sucursal
                    .mapToDtoList(daoSucursal.busquedaConFiltros(nombre, ubicacion, idEmpresa));
        });
    }

    @Override
    public SucursalDTO guardarSucursal(SucursalDTO dto) {

        if (dto == null || dto.getNombre() == null || dto.getNombre().isBlank())
            throw new ReglaNegocioException("El nombre de la sucursal es obligatorio");

        if (dto.getIdEmpresa() == null)
            throw new ReglaNegocioException("Debe asociar la sucursal a una empresa");

        return ejecutarTransaccion(em -> {
            configurarEntityManager(em);

            Empresa empresa = daoEmpresa.buscarPorId(dto.getIdEmpresa());
            if (empresa == null)
                throw new RecursoNoEncontradoException("Empresa no encontrada");

            Sucursal entidad = MapperEstructura.sucursal.mapToEntity(dto);
            entidad.setEmpresa(empresa);

            if (dto.getId() != null && dto.getId() > 0) {

                Sucursal existente = daoSucursal.buscarPorId(dto.getId());
                if (existente == null)
                    throw new RecursoNoEncontradoException("Sucursal no encontrada");

                entidad = daoSucursal.actualizar(entidad);

            } else {
                entidad = daoSucursal.guardar(entidad);
            }

            return MapperEstructura.sucursal.mapToDto(entidad);
        });
    }

    @Override
    public void eliminarSucursal(Long id) {

        if (id == null)
            throw new IllegalArgumentException("ID inválido");

        ejecutarTransaccion(em -> {
            configurarEntityManager(em);

            Sucursal sucursal = daoSucursal.buscarPorId(id);

            if (sucursal == null)
                throw new RecursoNoEncontradoException("Sucursal no encontrada");

            if (sucursal.getDepartamentos() != null &&
                !sucursal.getDepartamentos().isEmpty())
                throw new ReglaNegocioException(
                        "No se puede eliminar la sucursal: tiene departamentos asociados");

            daoSucursal.eliminar(id);
            return null;
        });
    }

    // ========================= DEPARTAMENTOS =========================

    @Override
    public List<DepartamentoDTO> listarDepartamentos(String nombre, Long idSucursal) {

        return ejecutarLectura(em -> {
            configurarEntityManager(em);

            String filtro = nombre != null ? nombre : "";

            return MapperEstructura.departamento
                    .mapToDtoList(daoDepartamento.busquedaConFiltros(filtro, idSucursal));
        });
    }

    @Override
    public DepartamentoDTO guardarDepartamento(DepartamentoDTO dto) {

        if (dto == null || dto.getNombre() == null || dto.getNombre().isBlank())
            throw new ReglaNegocioException("El nombre del departamento es obligatorio");

        if (dto.getIdSucursal() == null)
            throw new ReglaNegocioException("Debe asociar el departamento a una sucursal");

        return ejecutarTransaccion(em -> {
            configurarEntityManager(em);

            Sucursal sucursal = daoSucursal.buscarPorId(dto.getIdSucursal());
            if (sucursal == null)
                throw new RecursoNoEncontradoException("Sucursal no encontrada");

            Departamento entidad = MapperEstructura.departamento.mapToEntity(dto);
            entidad.setSucursal(sucursal);

            if (dto.getId() != null && dto.getId() > 0) {

                Departamento existente = daoDepartamento.buscarPorId(dto.getId());
                if (existente == null)
                    throw new RecursoNoEncontradoException("Departamento no encontrado");

                entidad = daoDepartamento.actualizar(entidad);

            } else {
                entidad = daoDepartamento.guardar(entidad);
            }

            return MapperEstructura.departamento.mapToDto(entidad);
        });
    }

    @Override
    public void eliminarDepartamento(Long id) {

        if (id == null)
            throw new IllegalArgumentException("ID inválido");

        ejecutarTransaccion(em -> {
            configurarEntityManager(em);

            Departamento departamento = daoDepartamento.buscarPorId(id);

            if (departamento == null)
                throw new RecursoNoEncontradoException("Departamento no encontrado");

            if (departamento.getPuestos() != null &&
                !departamento.getPuestos().isEmpty())
                throw new ReglaNegocioException(
                        "No se puede eliminar el departamento: tiene puestos asociados");

            daoDepartamento.eliminar(id);
            return null;
        });
    }

    // ========================= PUESTOS =========================

    @Override
    public List<PuestoDTO> listarPuestos(Long idDepartamento) {

        return ejecutarLectura(em -> {
            configurarEntityManager(em);
            return MapperEstructura.puesto
                    .mapToDtoList(daoPuesto.busquedaPorDepartamento(idDepartamento));
        });
    }

    @Override
    public PuestoDTO guardarPuesto(PuestoDTO dto) {

        if (dto == null || dto.getNombre() == null || dto.getNombre().isBlank())
            throw new ReglaNegocioException("El nombre del puesto es obligatorio");

        if (dto.getIdDepartamento() == null)
            throw new ReglaNegocioException("Debe asociar el puesto a un departamento");

        return ejecutarTransaccion(em -> {
            configurarEntityManager(em);

            Departamento departamento = daoDepartamento.buscarPorId(dto.getIdDepartamento());
            if (departamento == null)
                throw new RecursoNoEncontradoException("Departamento no encontrado");

            Puesto entidad = MapperEstructura.puesto.mapToEntity(dto);
            entidad.setDepartamento(departamento);

            if (dto.getId() != null && dto.getId() > 0) {

                Puesto existente = daoPuesto.buscarPorId(dto.getId());
                if (existente == null)
                    throw new RecursoNoEncontradoException("Puesto no encontrado");

                entidad = daoPuesto.actualizar(entidad);

            } else {
                entidad = daoPuesto.guardar(entidad);
            }

            return MapperEstructura.puesto.mapToDto(entidad);
        });
    }

    @Override
    public void eliminarPuesto(Long id) {

        if (id == null)
            throw new IllegalArgumentException("ID inválido");

        ejecutarTransaccion(em -> {
            configurarEntityManager(em);

            Puesto puesto = daoPuesto.buscarPorId(id);

            if (puesto == null)
                throw new RecursoNoEncontradoException("Puesto no encontrado");

            if (puesto.getUsuarios()!= null &&
                !puesto.getUsuarios().isEmpty())
                throw new ReglaNegocioException(
                        "No se puede eliminar el puesto: tiene trabajadores asociados");

            daoPuesto.eliminar(id);
            return null;
        });
    }

    @Override
    public PuestoDTO buscarPuestoEspecifico(Long id) {
        return MapperEstructura.puesto.mapToDto(daoPuesto.buscarPorId(id));
    }
}
