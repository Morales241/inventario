package Servicios;

import Dao.DaoCuentaSistema;
import Dao.DaoPuesto;
import Dao.DaoUsuario;
import Dtos.CuentaSistemaDTO;
import Dtos.UsuarioDTO;
import Entidades.CuentaSistema;
import Entidades.Puesto;
import Entidades.Usuario;
import Interfaces.IDaoCuentaSistema;
import Utilidades.ServicioSesion;
import excepciones.RecursoNoEncontradoException;
import excepciones.ReglaNegocioException;
import interfacesServicios.IServicioPersonas;
import jakarta.persistence.EntityManager;
import java.util.List;
import mapper.MapperCuentaSistema;
import mapper.MapperUsuario;

/**
 * Servicio de lógica de negocio para personas.
 *
 */
public class ServicioPersonas extends ServicioBase implements IServicioPersonas {

    private final UsuarioServicio usuarioServicio;
    private final CuentaSistemaServicio cuentaSistemaServicio;
    private final DaoUsuario daoUsuario;
    private final DaoPuesto daoPuesto;

    public ServicioPersonas() {
        this.daoUsuario = new DaoUsuario();
        this.daoPuesto = new DaoPuesto();
        this.usuarioServicio = new UsuarioServicio(daoUsuario, daoPuesto);
        this.cuentaSistemaServicio = new CuentaSistemaServicio(new DaoCuentaSistema());
    }

    public ServicioPersonas(DaoUsuario daoUsuario,
            DaoCuentaSistema daoCuenta,
            DaoPuesto daoPuesto) {
        this.daoUsuario = daoUsuario;
        this.daoPuesto = daoPuesto;
        this.usuarioServicio = new UsuarioServicio(daoUsuario, daoPuesto);
        this.cuentaSistemaServicio = new CuentaSistemaServicio(daoCuenta);
    }

    private void configurarDAOs(EntityManager em) {
        if (daoUsuario != null) {
            daoUsuario.setEntityManager(em);
        }
        if (daoPuesto != null) {
            daoPuesto.setEntityManager(em);
        }
    }

    @Override
    public CuentaSistemaDTO login(String u, String p) {
        return cuentaSistemaServicio.login(u.trim(), p);
    }

    @Override
    public CuentaSistemaDTO buscarPorUsername(String u) {
        return cuentaSistemaServicio.buscarPorUsername(u);
    }

    @Override
    public CuentaSistemaDTO guardarCuentaSistema(CuentaSistemaDTO d) {
        return cuentaSistemaServicio.guardar(d);
    }

    @Override
    public void eliminarCuentaSistema(Long id) {
        cuentaSistemaServicio.eliminar(id);
    }

    @Override
    public List<CuentaSistemaDTO> listarCuentasSistema() {
        return cuentaSistemaServicio.buscarTodos();
    }

    @Override
    public List<CuentaSistemaDTO> buscarCuentasSistema(String f) {
        return cuentaSistemaServicio.buscarConFiltro(f);
    }

    @Override
    public List<UsuarioDTO> buscarUsuarios(String criterioGlobal) {
        return usuarioServicio.buscarConFiltroGlobal(criterioGlobal);
    }

    /**
     * NUEVO: carga paginada. Trae solo los registros de la página solicitada y
     * añade el conteo de equipos asignados con una query COUNT por usuario (no
     * carga la lista completa).
     */
    @Override
    public List<UsuarioDTO> buscarUsuariosPaginado(String criterioGlobal, int pagina, int tamano) {
        String criterio = (criterioGlobal != null) ? criterioGlobal.trim() : "";
        String nombreFiltro = null;
        String numeroFiltro = null;

        if (!criterio.isEmpty()) {
            if (criterio.matches(".*\\d.*")) {
                numeroFiltro = criterio;
            } else {
                nombreFiltro = criterio;
            }
        }

        final String nf = nombreFiltro;
        final String nmf = numeroFiltro;

        return ejecutarLectura(em -> {
            configurarDAOs(em);

            List<Usuario> entidades = daoUsuario.busquedaConFiltrosPaginado(nf, nmf, null, pagina, tamano);

            List<UsuarioDTO> dtos = MapperUsuario.converter.mapToDtoList(entidades);

            for (UsuarioDTO dto : dtos) {
                int equipos = daoUsuario.contarEquiposAsignadosPorUsuario(dto.getId());
                dto.setNumeroDeEquipos(equipos);
            }

            return dtos;
        });
    }

    @Override
    public long contarUsuarios(String criterioGlobal) {
        String criterio = (criterioGlobal != null) ? criterioGlobal.trim() : "";
        String nombreFiltro = null;
        String numeroFiltro = null;

        if (!criterio.isEmpty()) {
            if (criterio.matches(".*\\d.*")) {
                numeroFiltro = criterio;
            } else {
                nombreFiltro = criterio;
            }
        }

        final String nf = nombreFiltro;
        final String nmf = numeroFiltro;

        return ejecutarLectura(em -> {
            configurarDAOs(em);
            return daoUsuario.contarConFiltros(nf, nmf, null);
        });
    }

    @Override
    public UsuarioDTO obtenerUsuario(Long id) {
        return usuarioServicio.buscarPorId(id);
    }

    @Override
    public void guardarUsuario(UsuarioDTO dto) {
        usuarioServicio.guardar(dto);
    }

    @Override
    public void cambiarEstadoUsuario(Long id, boolean activo) {
        usuarioServicio.cambiarEstado(id, activo);
    }

    @Override
    public List<UsuarioDTO> listarUsuariosActivos() {
        return ejecutarLectura(em -> {
            usuarioServicio.configurarEntityManager(em);
            return usuarioServicio.buscarTodos().stream()
                    .filter(UsuarioDTO::getActivo)
                    .toList();
        });
    }

    @Override
    public List<UsuarioDTO> buscarUsuariosPorDepartamento(Long idDep) {
        if (idDep == null) {
            throw new IllegalArgumentException("ID de departamento inválido");
        }
        return ejecutarLectura(em -> {
            configurarDAOs(em);
            usuarioServicio.configurarEntityManager(em);
            return List.of();
        });
    }

    @Override
    public List<UsuarioDTO> buscarUsuariosPorPuesto(Long idPuesto) {
        if (idPuesto == null) {
            throw new IllegalArgumentException("ID de puesto inválido");
        }
        return ejecutarLectura(em -> {
            configurarDAOs(em);
            usuarioServicio.configurarEntityManager(em);
            return usuarioServicio.buscarConFiltroGlobal(null).stream()
                    .filter(u -> u.getIdPuesto() != null && u.getIdPuesto().equals(idPuesto))
                    .toList();
        });
    }

    @Override
    public boolean usuarioTieneEquiposAsignados(Long idUsuario) {
        return ejecutarLectura(em -> {
            configurarDAOs(em);
            return daoUsuario.contarEquiposAsignadosPorUsuario(idUsuario) > 0;
        });
    }

    private class UsuarioServicio extends ServicioGenerico<Usuario, UsuarioDTO, Long> {

        private final DaoUsuario dao;
        private final DaoPuesto daoPuesto;

        public UsuarioServicio() {
            super(new DaoUsuario(), MapperUsuario.converter, Usuario.class);
            this.dao = (DaoUsuario) super.dao;
            this.daoPuesto = new DaoPuesto();
        }

        public UsuarioServicio(DaoUsuario dao, DaoPuesto daoPuesto) {
            super(dao, MapperUsuario.converter, Usuario.class);
            this.dao = dao;
            this.daoPuesto = daoPuesto;
        }

        @Override
        protected void configurarEntityManager(EntityManager em) {
            if (dao != null) {
                dao.setEntityManager(em);
            }
            if (daoPuesto != null) {
                daoPuesto.setEntityManager(em);
            }
        }

        @Override
        protected void validarNegocio(UsuarioDTO dto, boolean esNuevo, EntityManager em) {
            if (dto.getNombre() == null || dto.getNombre().isBlank()) {
                throw new ReglaNegocioException("El nombre del usuario es obligatorio");
            }
            if (dto.getNoNomina() == null || dto.getNoNomina().isBlank()) {
                throw new ReglaNegocioException("El número de nómina es obligatorio");
            }
            if (dto.getIdPuesto() == null) {
                throw new ReglaNegocioException("Debe asignar un puesto al usuario");
            }
            if (esNuevo) {
                validarNominaUnica(dto.getNoNomina(), em);
            } else {
                validarNominaUnicaEnActualizacion(dto.getNoNomina(), dto.getId(), em);
            }
        }

        @Override
        protected Long extraerId(UsuarioDTO dto) {
            return dto.getId();
        }

        @Override
        protected void validarEliminacion(Usuario entidad) {
            throw new ReglaNegocioException(
                    "Los usuarios no se eliminan físicamente. Use cambiarEstadoUsuario");
        }

        private void validarNominaUnica(String noNomina, EntityManager em) {
            configurarEntityManager(em);
            Usuario existente = dao.busquedaEspecifica(null, noNomina);
            if (existente != null) {
                throw new ReglaNegocioException("Ya existe un usuario con nómina: " + noNomina);
            }
        }

        private void validarNominaUnicaEnActualizacion(String noNomina, Long idActual, EntityManager em) {
            configurarEntityManager(em);
            Usuario existente = dao.busquedaEspecifica(null, noNomina);
            if (existente != null && !existente.getIdUsuario().equals(idActual)) {
                throw new ReglaNegocioException("Ya existe un usuario con nómina: " + noNomina);
            }
        }

        @Override
        public UsuarioDTO guardar(UsuarioDTO dto) {
            return ejecutarTransaccion(em -> {
                configurarEntityManager(em);
                validarNegocio(dto, dto.getId() == null, em);
                Puesto puesto = daoPuesto.buscarPorId(dto.getIdPuesto());
                if (puesto == null) {
                    throw new RecursoNoEncontradoException("Puesto " + dto.getIdPuesto() + " no encontrado");
                }
                Usuario entidad = mapper.mapToEntity(dto);
                entidad.setPuesto(puesto);
                if (dto.getId() != null && dto.getId() > 0) {
                    Usuario existente = dao.buscarPorId(dto.getId());
                    if (existente == null) {
                        throw new RecursoNoEncontradoException("Usuario " + dto.getId() + " no encontrado");
                    }
                    if (dto.getActivo() == null) {
                        entidad.setActivo(existente.getActivo());
                    }
                    entidad = dao.actualizar(entidad);
                } else {
                    entidad.setActivo(true);
                    entidad = dao.guardar(entidad);
                }
                return mapper.mapToDto(entidad);
            });
        }

        /**
         * buscarConFiltroGlobal — el filtro activo=true ahora está en el DAO,
         * no se necesita .filter() en Java sobre la lista completa.
         */
        public List<UsuarioDTO> buscarConFiltroGlobal(String criterioGlobal) {
            String criterio = (criterioGlobal != null) ? criterioGlobal.trim() : "";
            String nombreFiltro = null;
            String numeroFiltro = null;
            if (!criterio.isEmpty()) {
                if (criterio.matches(".*\\d.*")) {
                    numeroFiltro = criterio;
                } else {
                    nombreFiltro = criterio;
                }
            }
            final String nf = nombreFiltro;
            final String nmf = numeroFiltro;
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                return mapper.mapToDtoList(dao.busquedaConFiltros(nf, nmf, null));
            });
        }

        public void cambiarEstado(Long id, boolean activo) {
            if (id == null) {
                throw new IllegalArgumentException("ID inválido");
            }
            ejecutarTransaccion(em -> {
                configurarEntityManager(em);
                Usuario usuario = dao.buscarPorId(id);
                if (usuario == null) {
                    throw new RecursoNoEncontradoException("Usuario " + id + " no encontrado");
                }
                if (!activo && tieneEquiposAsignados(usuario)) {
                    throw new ReglaNegocioException(
                            "No se puede desactivar un usuario que tiene equipos asignados");
                }
                usuario.setActivo(activo);
                dao.actualizar(usuario);
                return null;
            });
        }

        private boolean tieneEquiposAsignados(Usuario usuario) {
            return usuario.getEquiposAsignados() != null
                    && !usuario.getEquiposAsignados().isEmpty()
                    && usuario.getEquiposAsignados().stream()
                            .anyMatch(a -> a.getFechaDevolucion() == null);
        }

        @Override
        public List<UsuarioDTO> buscarConFiltro(String filtro) {
            return buscarConFiltroGlobal(filtro);
        }
    }

    private class CuentaSistemaServicio extends ServicioGenerico<CuentaSistema, CuentaSistemaDTO, Long> {

        private final IDaoCuentaSistema dao;

        public CuentaSistemaServicio() {
            super(new DaoCuentaSistema(), MapperCuentaSistema.converter, CuentaSistema.class);
            this.dao = (DaoCuentaSistema) super.dao;
        }

        public CuentaSistemaServicio(DaoCuentaSistema dao) {
            super(dao, MapperCuentaSistema.converter, CuentaSistema.class);
            this.dao = dao;
        }

        @Override
        protected void configurarEntityManager(EntityManager em) {
            if (dao != null) {
                dao.setEntityManager(em);
            }
        }

        @Override
        protected void validarNegocio(CuentaSistemaDTO dto, boolean esNuevo, EntityManager em) {
            if (dto.getUsername() == null || dto.getUsername().isBlank()) {
                throw new ReglaNegocioException("El nombre de usuario es obligatorio");
            }
        }

        @Override
        protected Long extraerId(CuentaSistemaDTO dto) {
            return dto.getId();
        }

        @Override
        protected void validarEliminacion(CuentaSistema e) {
        }

        @Override
        public List<CuentaSistemaDTO> buscarConFiltro(String filtro) {
            return null;
        }
        
        public CuentaSistemaDTO login(String user, String contra){
            
            return ejecutarLectura(em -> {
                dao.setEntityManager(em);
                
                CuentaSistema cuentaLogueada =  dao.login(user, contra);
                
                CuentaSistemaDTO dto = mapper.mapToDto(cuentaLogueada);
                
                ServicioSesion.getInstance().setUsuario(dto);
                
                return dto;
            });
        }
        
        public CuentaSistemaDTO buscarPorUsername(String user){
            
            return ejecutarLectura(em -> {
                dao.setEntityManager(em);
                
                return mapper.mapToDto(dao.busquedaEspecifica(user));
            });
        }
    }
}
