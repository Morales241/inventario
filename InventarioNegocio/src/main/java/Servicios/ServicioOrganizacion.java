package Servicios;

import Dao.*;
import Dtos.*;
import Entidades.*;
import excepciones.RecursoNoEncontradoException;
import excepciones.ReglaNegocioException;
import interfacesServicios.IServicioOrganizacion;
import jakarta.persistence.EntityManager;
import java.util.List;
import mapper.MapperEstructura;

/**
 * Servicio de lógica de negocio para la gestión de la estructura organizacional.
 * <p>
 * Coordina operaciones CRUD para empresas, sucursales, departamentos y puestos.
 * Implementa validaciones de integridad referencial para evitar eliminaciones en
 * cascada.
 * </p>
 */
public class ServicioOrganizacion extends ServicioBase implements IServicioOrganizacion {

    private final EmpresaServicio empresaServicio;
    private final SucursalServicio sucursalServicio;
    private final DepartamentoServicio departamentoServicio;
    private final PuestoServicio puestoServicio;

    public ServicioOrganizacion() {
        this.empresaServicio = new EmpresaServicio();
        this.sucursalServicio = new SucursalServicio();
        this.departamentoServicio = new DepartamentoServicio();
        this.puestoServicio = new PuestoServicio();
    }

    // EMPRESAS 
    @Override
    public List<EmpresaDTO> listarEmpresas(String filtro) {
        if (filtro != null && !filtro.isBlank()) {
            return empresaServicio.buscarConFiltro(filtro);
        }
        return empresaServicio.buscarTodos();
    }

    @Override
    public EmpresaDTO guardarEmpresa(EmpresaDTO dto) {
        return empresaServicio.guardar(dto);
    }

    @Override
    public void eliminarEmpresa(Long id) {
        empresaServicio.eliminar(id);
    }

    // SUCURSALES 
    @Override
    public List<SucursalDTO> listarSucursales(String filtro, Long idEmpresa) {
        return sucursalServicio.listarConFiltros(filtro, idEmpresa);
    }

    @Override
    public SucursalDTO guardarSucursal(SucursalDTO dto) {
        return sucursalServicio.guardar(dto);
    }

    @Override
    public void eliminarSucursal(Long id) {
        sucursalServicio.eliminar(id);
    }

    // DEPARTAMENTOS 
    @Override
    public List<DepartamentoDTO> listarDepartamentos(String nombre, Long idSucursal) {
        return departamentoServicio.listarConFiltros(nombre, idSucursal);
    }

    @Override
    public DepartamentoDTO guardarDepartamento(DepartamentoDTO dto) {
        return departamentoServicio.guardar(dto);
    }

    @Override
    public void eliminarDepartamento(Long id) {
        departamentoServicio.eliminar(id);
    }

    // PUESTOS
    @Override
    public List<PuestoDTO> listarPuestos(Long idDepartamento) {
        return puestoServicio.listarPorDepartamento(idDepartamento);
    }

    @Override
    public PuestoDTO guardarPuesto(PuestoDTO dto) {
        return puestoServicio.guardar(dto);
    }

    @Override
    public void eliminarPuesto(Long id) {
        puestoServicio.eliminar(id);
    }

    @Override
    public PuestoDTO buscarPuestoEspecifico(Long id) {
        return puestoServicio.buscarPorId(id);
    }

    @Override
    public List<PuestoDTO> busquedaPorEmpresa(Long idEmpresa) {
        return puestoServicio.buscarPorEmpresa(idEmpresa);
    }

    @Override
    public EmpresaDTO buscarEmpresaPorPuesto(Long idPuesto) {
        return empresaServicio.buscarPorPuesto(idPuesto);
    }

    // SERVICIOS INTERNOS ESPECÍFICOS 

    /**
     * Servicio genérico para Empresa
     */
    private class EmpresaServicio extends ServicioGenerico<Empresa, EmpresaDTO, Long> {
        
        private final DaoEmpresa dao;
        
        public EmpresaServicio() {
            super(new DaoEmpresa(), MapperEstructura.empresa, Empresa.class);
            this.dao = (DaoEmpresa) super.dao;
        }
        
        @Override
        protected void configurarEntityManager(EntityManager em) {
            dao.setEntityManager(em);
        }
        
        @Override
        protected void validarNegocio(EmpresaDTO dto, boolean esNuevo) {
            if (dto.getNombre() == null || dto.getNombre().isBlank()) {
                throw new ReglaNegocioException("El nombre de la empresa es obligatorio");
            }
        }
        
        @Override
        protected Long extraerId(EmpresaDTO dto) {
            return dto.getId();
        }
        
        @Override
        protected void validarEliminacion(Empresa entidad) {
            if (entidad.getSucursales() != null && !entidad.getSucursales().isEmpty()) {
                throw new ReglaNegocioException(
                    "No se puede eliminar una empresa con sucursales asociadas");
            }
        }
        
        public List<EmpresaDTO> buscarConFiltro(String filtro) {
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                return mapper.mapToDtoList(dao.buscarPorCoincidencias(filtro));
            });
        }
        
        /**
         * Método específico: Buscar empresa por ID de puesto
         */
        public EmpresaDTO buscarPorPuesto(Long idPuesto) {
            if (idPuesto == null) {
                throw new IllegalArgumentException("ID de puesto inválido");
            }
            
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                Empresa empresa = dao.buscarEmpresaPorPuesto(idPuesto);
                if (empresa == null) {
                    throw new RecursoNoEncontradoException(
                        "Empresa no encontrada para el puesto especificado");
                }
                
                return mapper.mapToDto(empresa);
            });
        }
    }

    /**
     * Servicio genérico para Sucursal
     */
    private class SucursalServicio extends ServicioGenerico<Sucursal, SucursalDTO, Long> {
        
        private final DaoSucursal dao;
        private final DaoEmpresa daoEmpresa;
        
        public SucursalServicio() {
            super(new DaoSucursal(), MapperEstructura.sucursal, Sucursal.class);
            this.dao = (DaoSucursal) super.dao;
            this.daoEmpresa = new DaoEmpresa();
        }
        
        @Override
        protected void configurarEntityManager(EntityManager em) {
            dao.setEntityManager(em);
            daoEmpresa.setEntityManager(em);
        }
        
        @Override
        protected void validarNegocio(SucursalDTO dto, boolean esNuevo) {
            if (dto.getNombre() == null || dto.getNombre().isBlank()) {
                throw new ReglaNegocioException("El nombre de la sucursal es obligatorio");
            }
            
            if (dto.getIdEmpresa() == null) {
                throw new ReglaNegocioException("Debe asociar la sucursal a una empresa");
            }
        }
        
        @Override
        protected Long extraerId(SucursalDTO dto) {
            return dto.getId();
        }
        
        @Override
        protected void validarEliminacion(Sucursal entidad) {
            if (entidad.getDepartamentos() != null && !entidad.getDepartamentos().isEmpty()) {
                throw new ReglaNegocioException(
                    "No se puede eliminar la sucursal: tiene departamentos asociados");
            }
        }
        
        @Override
        public SucursalDTO guardar(SucursalDTO dto) {
            return ejecutarTransaccion(em -> {
                configurarEntityManager(em);
                
                // Validar y obtener la empresa asociada
                Empresa empresa = daoEmpresa.buscarPorId(dto.getIdEmpresa());
                if (empresa == null) {
                    throw new RecursoNoEncontradoException(
                        "Empresa con ID " + dto.getIdEmpresa() + " no encontrada");
                }
                
                // Mapear y establecer relaciones
                Sucursal entidad = mapper.mapToEntity(dto);
                entidad.setEmpresa(empresa);
                
                // Guardar o actualizar
                if (dto.getId() != null && dto.getId() > 0) {
                    // Verificar que existe
                    Sucursal existente = dao.buscarPorId(dto.getId());
                    if (existente == null) {
                        throw new RecursoNoEncontradoException(
                            "Sucursal con ID " + dto.getId() + " no encontrada");
                    }
                    entidad = dao.actualizar(entidad);
                } else {
                    entidad = dao.guardar(entidad);
                }
                
                return mapper.mapToDto(entidad);
            });
        }
       
        public List<SucursalDTO> listarConFiltros(String filtro, Long idEmpresa) {
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                String nombre = "";
                String ubicacion = "";
                
                if (filtro != null && !filtro.isBlank()) {
                    String[] partes = filtro.split(",");
                    nombre = partes.length > 0 ? partes[0].trim() : "";
                    ubicacion = partes.length > 1 ? partes[1].trim() : "";
                }
                
                return mapper.mapToDtoList(
                    dao.busquedaConFiltros(nombre, ubicacion, idEmpresa));
            });
        }

        @Override
        public List<SucursalDTO> buscarConFiltro(String filtro) {
            return null;
        }
    }

    /**
     * Servicio genérico para Departamento
     */
    private class DepartamentoServicio extends ServicioGenerico<Departamento, DepartamentoDTO, Long> {
        
        private final DaoDepartamento dao;
        private final DaoSucursal daoSucursal;
        
        public DepartamentoServicio() {
            super(new DaoDepartamento(), MapperEstructura.departamento, Departamento.class);
            this.dao = (DaoDepartamento) super.dao;
            this.daoSucursal = new DaoSucursal();
        }
        
        @Override
        protected void configurarEntityManager(EntityManager em) {
            dao.setEntityManager(em);
            daoSucursal.setEntityManager(em);
        }
        
        @Override
        protected void validarNegocio(DepartamentoDTO dto, boolean esNuevo) {
            if (dto.getNombre() == null || dto.getNombre().isBlank()) {
                throw new ReglaNegocioException("El nombre del departamento es obligatorio");
            }
            
            if (dto.getIdSucursal() == null) {
                throw new ReglaNegocioException("Debe asociar el departamento a una sucursal");
            }
        }
        
        @Override
        protected Long extraerId(DepartamentoDTO dto) {
            return dto.getId();
        }
        
        @Override
        protected void validarEliminacion(Departamento entidad) {
            if (entidad.getPuestos() != null && !entidad.getPuestos().isEmpty()) {
                throw new ReglaNegocioException(
                    "No se puede eliminar el departamento: tiene puestos asociados");
            }
        }
        
        @Override
        public DepartamentoDTO guardar(DepartamentoDTO dto) {
            return ejecutarTransaccion(em -> {
                configurarEntityManager(em);
                
                // Validar y obtener la sucursal asociada
                Sucursal sucursal = daoSucursal.buscarPorId(dto.getIdSucursal());
                if (sucursal == null) {
                    throw new RecursoNoEncontradoException(
                        "Sucursal con ID " + dto.getIdSucursal() + " no encontrada");
                }
                
                // Mapear y establecer relaciones
                Departamento entidad = mapper.mapToEntity(dto);
                entidad.setSucursal(sucursal);
                
                // Guardar o actualizar
                if (dto.getId() != null && dto.getId() > 0) {
                    Departamento existente = dao.buscarPorId(dto.getId());
                    if (existente == null) {
                        throw new RecursoNoEncontradoException(
                            "Departamento con ID " + dto.getId() + " no encontrado");
                    }
                    entidad = dao.actualizar(entidad);
                } else {
                    entidad = dao.guardar(entidad);
                }
                
                return mapper.mapToDto(entidad);
            });
        }
        
        /**
         * Método específico: Listar departamentos con filtros
         */
        public List<DepartamentoDTO> listarConFiltros(String nombre, Long idSucursal) {
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                String filtro = nombre != null ? nombre : "";
                
                return mapper.mapToDtoList(
                    dao.busquedaConFiltros(filtro, idSucursal));
            });
        }

        @Override
        public List<DepartamentoDTO> buscarConFiltro(String filtro) {
            return null;
        }
    }

    /**
     * Servicio genérico para Puesto
     */
    private class PuestoServicio extends ServicioGenerico<Puesto, PuestoDTO, Long> {
        
        private final DaoPuesto dao;
        private final DaoDepartamento daoDepartamento;
        
        public PuestoServicio() {
            super(new DaoPuesto(), MapperEstructura.puesto, Puesto.class);
            this.dao = (DaoPuesto) super.dao;
            this.daoDepartamento = new DaoDepartamento();
        }
        
        @Override
        protected void configurarEntityManager(EntityManager em) {
            dao.setEntityManager(em);
            daoDepartamento.setEntityManager(em);
        }
        
        @Override
        protected void validarNegocio(PuestoDTO dto, boolean esNuevo) {
            if (dto.getNombre() == null || dto.getNombre().isBlank()) {
                throw new ReglaNegocioException("El nombre del puesto es obligatorio");
            }
            
            if (dto.getIdDepartamento() == null) {
                throw new ReglaNegocioException("Debe asociar el puesto a un departamento");
            }
        }
        
        @Override
        protected Long extraerId(PuestoDTO dto) {
            return dto.getId();
        }
        
        @Override
        protected void validarEliminacion(Puesto entidad) {
            if (entidad.getUsuarios() != null && !entidad.getUsuarios().isEmpty()) {
                throw new ReglaNegocioException(
                    "No se puede eliminar el puesto: tiene trabajadores asociados");
            }
        }
        
        @Override
        public PuestoDTO guardar(PuestoDTO dto) {
            return ejecutarTransaccion(em -> {
                configurarEntityManager(em);
                
                // Validar y obtener el departamento asociado
                Departamento departamento = daoDepartamento.buscarPorId(dto.getIdDepartamento());
                if (departamento == null) {
                    throw new RecursoNoEncontradoException(
                        "Departamento con ID " + dto.getIdDepartamento() + " no encontrado");
                }
                
                // Mapear y establecer relaciones
                Puesto entidad = mapper.mapToEntity(dto);
                entidad.setDepartamento(departamento);
                
                // Guardar o actualizar
                if (dto.getId() != null && dto.getId() > 0) {
                    Puesto existente = dao.buscarPorId(dto.getId());
                    if (existente == null) {
                        throw new RecursoNoEncontradoException(
                            "Puesto con ID " + dto.getId() + " no encontrado");
                    }
                    entidad = dao.actualizar(entidad);
                } else {
                    entidad = dao.guardar(entidad);
                }
                
                return mapper.mapToDto(entidad);
            });
        }
        
        /**
         * Método específico: Listar puestos por departamento
         */
        public List<PuestoDTO> listarPorDepartamento(Long idDepartamento) {
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                return mapper.mapToDtoList(
                    dao.busquedaPorDepartamento(idDepartamento));
            });
        }
        
        /**
         * Método específico: Buscar puestos por empresa
         */
        public List<PuestoDTO> buscarPorEmpresa(Long idEmpresa) {
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                return mapper.mapToDtoList(
                    dao.busquedaPorEmpresa(idEmpresa));
            });
        }
        
        public List<PuestoDTO> buscarConFiltro(String filtro) {
            // Implementación específica para búsqueda por nombre
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                Puesto puesto = dao.busquedaEspecifica(filtro);
                if (puesto != null) {
                    return List.of(mapper.mapToDto(puesto));
                }
                return List.of();
            });
        }
    }
}