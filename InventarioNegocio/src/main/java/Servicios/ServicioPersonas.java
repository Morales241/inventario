// InventarioNegocio/src/main/java/Servicios/ServicioPersonas.java
package Servicios;

import Dao.DaoCuentaSistema;
import Dao.DaoPuesto;
import Dao.DaoUsuario;
import Dtos.CuentaSistemaDTO;
import Dtos.UsuarioDTO;
import Entidades.CuentaSistema;
import Entidades.Puesto;
import Entidades.Usuario;
import excepciones.RecursoNoEncontradoException;
import excepciones.ReglaNegocioException;
import interfacesServicios.IServicioPersonas;
import jakarta.persistence.EntityManager;
import java.util.List;
import mapper.MapperCuentaSistema;
import mapper.MapperUsuario;

/**
 * Servicio de lógica de negocio para la gestión de personas en el sistema.
 * <p>
 * Coordina las operaciones relacionadas con autenticación de usuarios del sistema
 * y administración de Usuarios (empleados). Incluye validaciones de integridad 
 * de datos y cambios de estado para el control de acceso.
 * </p>
 */
public class ServicioPersonas extends ServicioBase implements IServicioPersonas {

    private final UsuarioServicio usuarioServicio;
    private final CuentaSistemaServicio cuentaSistemaServicio;
    private final DaoUsuario daoUsuario;
    private final DaoPuesto daoPuesto;

    public ServicioPersonas() {
        this.usuarioServicio = new UsuarioServicio();
        this.cuentaSistemaServicio = new CuentaSistemaServicio();
        this.daoUsuario = new DaoUsuario();
        this.daoPuesto = new DaoPuesto();
    }

    private void configurarDAOs(EntityManager em) {
        daoUsuario.setEntityManager(em);
        daoPuesto.setEntityManager(em);
    }

    // CUENTAS DE SISTEMA 
    
    @Override
    public CuentaSistemaDTO login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        return cuentaSistemaServicio.login(username.trim(), password);
    }

    @Override
    public CuentaSistemaDTO buscarPorUsername(String username) {
        return cuentaSistemaServicio.buscarPorUsername(username);
    }

    @Override
    public CuentaSistemaDTO guardarCuentaSistema(CuentaSistemaDTO dto) {
        return cuentaSistemaServicio.guardar(dto);
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
    public List<CuentaSistemaDTO> buscarCuentasSistema(String filtro) {
        return cuentaSistemaServicio.buscarConFiltro(filtro);
    }

    // USUARIOS
    
    @Override
    public List<UsuarioDTO> buscarUsuarios(String criterioGlobal) {
        return usuarioServicio.buscarConFiltroGlobal(criterioGlobal);
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
            
            List<UsuarioDTO> todos = usuarioServicio.buscarTodos();
            return todos.stream()
                .filter(UsuarioDTO::getActivo)
                .toList();
        });
    }

    @Override
    public List<UsuarioDTO> buscarUsuariosPorDepartamento(Long idDepartamento) {
        if (idDepartamento == null) {
            throw new IllegalArgumentException("ID de departamento inválido");
        }
        
        return ejecutarLectura(em -> {
            configurarDAOs(em);
            usuarioServicio.configurarEntityManager(em);
            
            // Necesitarías implementar este método en el DAO
            // Por ahora, retornamos lista vacía como placeholder
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
        if (idUsuario == null) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        
        return ejecutarLectura(em -> {
            configurarDAOs(em);
            
            Usuario usuario = daoUsuario.buscarPorId(idUsuario);
            if (usuario == null) {
                throw new RecursoNoEncontradoException(
                    "Usuario con ID " + idUsuario + " no encontrado");
            }
            
            return usuario.getEquiposAsignados() != null && 
                   !usuario.getEquiposAsignados().isEmpty() &&
                   usuario.getEquiposAsignados().stream()
                        .anyMatch(a -> a.getFechaDevolucion() == null);
        });
    }

    // SERVICIOS ESPECÍFICOS

    /**
     * Servicio para Usuarios (Empleados)
     */
    private class UsuarioServicio extends ServicioGenerico<Usuario, UsuarioDTO, Long> {
        
        private final DaoUsuario dao;
        private final DaoPuesto daoPuesto;
        
        public UsuarioServicio() {
            super(new DaoUsuario(), MapperUsuario.converter, Usuario.class);
            this.dao = (DaoUsuario) super.dao;
            this.daoPuesto = new DaoPuesto();
        }
        
        @Override
        protected void configurarEntityManager(EntityManager em) {
            dao.setEntityManager(em);
            daoPuesto.setEntityManager(em);
        }
        
        @Override
        protected void validarNegocio(UsuarioDTO dto, boolean esNuevo) {
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
                validarNominaUnica(dto.getNoNomina());
            } else {
                validarNominaUnicaEnActualizacion(dto.getNoNomina(), dto.getId());
            }
        }
        
        @Override
        protected Long extraerId(UsuarioDTO dto) {
            return dto.getId();
        }
        
        @Override
        protected void validarEliminacion(Usuario entidad) {
            // No se elimina físicamente, solo se cambia estado
            // Esta validación es para el método eliminar que no usaremos
            throw new ReglaNegocioException(
                "Los usuarios no se eliminan físicamente. Use cambiarEstadoUsuario para darlos de baja");
        }
        
        /**
         * Valida que el número de nómina sea único al crear
         */
        private void validarNominaUnica(String noNomina) {
            ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                Usuario existente = dao.busquedaEspecifica(null, noNomina);
                if (existente != null) {
                    throw new ReglaNegocioException(
                        "Ya existe un usuario con el número de nómina: " + noNomina);
                }
                return null;
            });
        }
        
        /**
         * Valida que el número de nómina sea único al actualizar
         */
        private void validarNominaUnicaEnActualizacion(String noNomina, Long idActual) {
            ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                Usuario existente = dao.busquedaEspecifica(null, noNomina);
                if (existente != null && !existente.getIdUsuario().equals(idActual)) {
                    throw new ReglaNegocioException(
                        "Ya existe un usuario con el número de nómina: " + noNomina);
                }
                return null;
            });
        }
        
        @Override
        public UsuarioDTO guardar(UsuarioDTO dto) {
            return ejecutarTransaccion(em -> {
                configurarEntityManager(em);
                
                // Validaciones de negocio
                validarNegocio(dto, dto.getId() == null);
                
                // Obtener y validar el puesto
                Puesto puesto = daoPuesto.buscarPorId(dto.getIdPuesto());
                if (puesto == null) {
                    throw new RecursoNoEncontradoException(
                        "Puesto con ID " + dto.getIdPuesto() + " no encontrado");
                }
                
                // Mapear y establecer relaciones
                Usuario entidad = mapper.mapToEntity(dto);
                entidad.setPuesto(puesto);
                
                // Guardar o actualizar
                if (dto.getId() != null && dto.getId() > 0) {
                    Usuario existente = dao.buscarPorId(dto.getId());
                    if (existente == null) {
                        throw new RecursoNoEncontradoException(
                            "Usuario con ID " + dto.getId() + " no encontrado");
                    }
                    
                    // Preservar el estado actual si no se envía
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
         * Busca usuarios con filtro global (nombre, nómina, puesto)
         */
        public List<UsuarioDTO> buscarConFiltroGlobal(String criterioGlobal) {
            String criterio = (criterioGlobal != null) ? criterioGlobal.trim() : "";
            
            String primerParametro = null;
            String segundoParametro = null;
            
            if (!criterio.isEmpty()) {
                boolean tieneNumeros = criterio.matches(".*\\d.*");
                if (tieneNumeros) {
                    segundoParametro = criterio;
                } else {
                    primerParametro = criterio;
                }
            }
            
            final String nombreFiltro = primerParametro;
            final String numeroFiltro = segundoParametro;
            
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                return mapper.mapToDtoList(
                    dao.busquedaConFiltros(nombreFiltro, numeroFiltro, null));
            });
        }
        
        /**
         * Cambia el estado de un usuario (activo/inactivo)
         */
        public void cambiarEstado(Long id, boolean activo) {
            if (id == null) {
                throw new IllegalArgumentException("ID inválido");
            }
            
            ejecutarTransaccion(em -> {
                configurarEntityManager(em);
                
                Usuario usuario = dao.buscarPorId(id);
                if (usuario == null) {
                    throw new RecursoNoEncontradoException(
                        "Usuario con ID " + id + " no encontrado");
                }
                
                // Validar que no tenga equipos asignados si se va a desactivar
                if (!activo && tieneEquiposAsignados(usuario)) {
                    throw new ReglaNegocioException(
                        "No se puede desactivar un usuario que tiene equipos asignados");
                }
                
                usuario.setActivo(activo);
                dao.actualizar(usuario);
                
                return null;
            });
        }
        
        /**
         * Verifica si un usuario tiene equipos asignados activos
         */
        private boolean tieneEquiposAsignados(Usuario usuario) {
            return usuario.getEquiposAsignados() != null && 
                   !usuario.getEquiposAsignados().isEmpty() &&
                   usuario.getEquiposAsignados().stream()
                        .anyMatch(asignacion -> asignacion.getFechaDevolucion() == null);
        }
        
        @Override
        public List<UsuarioDTO> buscarConFiltro(String filtro) {
            return buscarConFiltroGlobal(filtro);
        }
    }

    /**
     * Servicio para Cuentas de Sistema (Autenticación)
     */
    private class CuentaSistemaServicio extends ServicioGenerico<CuentaSistema, CuentaSistemaDTO, Long> {
        
        private final DaoCuentaSistema dao;
        
        public CuentaSistemaServicio() {
            super(new DaoCuentaSistema(), MapperCuentaSistema.converter, CuentaSistema.class);
            this.dao = (DaoCuentaSistema) super.dao;
        }
        
        @Override
        protected void configurarEntityManager(EntityManager em) {
            dao.setEntityManager(em);
        }
        
        @Override
        protected void validarNegocio(CuentaSistemaDTO dto, boolean esNuevo) {
            if (dto.getUsername() == null || dto.getUsername().isBlank()) {
                throw new ReglaNegocioException("El nombre de usuario es obligatorio");
            }
            
            if (dto.getRol() == null || dto.getRol().isBlank()) {
                throw new ReglaNegocioException("El rol del usuario es obligatorio");
            }
            
            if (esNuevo) {
                validarUsernameUnico(dto.getUsername());
            } else {
                validarUsernameUnicoEnActualizacion(dto.getUsername(), dto.getId());
            }
        }
        
        @Override
        protected Long extraerId(CuentaSistemaDTO dto) {
            return dto.getId();
        }
        
        @Override
        protected void validarEliminacion(CuentaSistema entidad) {
            // Prevenir eliminar el último administrador
            if (esUltimoAdministrador(entidad)) {
                throw new ReglaNegocioException(
                    "No se puede eliminar el último administrador del sistema");
            }
        }
        
        /**
         * Valida que el username sea único al crear
         */
        private void validarUsernameUnico(String username) {
            ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                CuentaSistema existente = dao.busquedaEspecifica(username);
                if (existente != null) {
                    throw new ReglaNegocioException(
                        "Ya existe un usuario con el nombre: " + username);
                }
                return null;
            });
        }
        
        /**
         * Valida que el username sea único al actualizar
         */
        private void validarUsernameUnicoEnActualizacion(String username, Long idActual) {
            ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                CuentaSistema existente = dao.busquedaEspecifica(username);
                if (existente != null && !existente.getId().equals(idActual)) {
                    throw new ReglaNegocioException(
                        "Ya existe un usuario con el nombre: " + username);
                }
                return null;
            });
        }
        
        /**
         * Verifica si es el último administrador
         */
        private boolean esUltimoAdministrador(CuentaSistema cuenta) {
            if (!"ADMIN".equals(cuenta.getRol().name())) {
                return false;
            }
            
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                long totalAdmins = dao.buscarTodos().stream()
                    .filter(c -> "ADMIN".equals(c.getRol().name()))
                    .count();
                    
                return totalAdmins <= 1;
            });
        }
        
        /**
         * Método específico: Login de usuario
         */
        public CuentaSistemaDTO login(String username, String password) {
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                CuentaSistema usuario = dao.login(username, password);
                
                if (usuario == null) {
                    throw new ReglaNegocioException("Usuario o contraseña incorrectos");
                }
                
                return mapper.mapToDto(usuario);
            });
        }
        
        /**
         * Método específico: Buscar por username
         */
        public CuentaSistemaDTO buscarPorUsername(String username) {
            if (username == null || username.isBlank()) {
                throw new IllegalArgumentException("Username inválido");
            }
            
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                CuentaSistema usuario = dao.busquedaEspecifica(username);
                if (usuario == null) {
                    throw new RecursoNoEncontradoException(
                        "Usuario de sistema no encontrado: " + username);
                }
                
                return mapper.mapToDto(usuario);
            });
        }
        
        @Override
        public CuentaSistemaDTO guardar(CuentaSistemaDTO dto) {
            return ejecutarTransaccion(em -> {
                configurarEntityManager(em);
                
                validarNegocio(dto, dto.getId() == null);
                
                CuentaSistema entidad = mapper.mapToEntity(dto);
                
                // Nota: La contraseña debe ser hasheada antes de guardar
                // Por ahora asumimos que viene hasheada desde el frontend
                
                if (dto.getId() != null && dto.getId() > 0) {
                    CuentaSistema existente = dao.buscarPorId(dto.getId());
                    if (existente == null) {
                        throw new RecursoNoEncontradoException(
                            "Cuenta de sistema con ID " + dto.getId() + " no encontrada");
                    }
                    
                    // Preservar la contraseña si no se envía una nueva
                    if (dto.getPassword() == null) {
                        entidad.setPassword(existente.getPassword());
                    }
                    
                    entidad = dao.actualizar(entidad);
                } else {
                    entidad = dao.guardar(entidad);
                }
                
                return mapper.mapToDto(entidad);
            });
        }
        
        @Override
        public List<CuentaSistemaDTO> buscarConFiltro(String filtro) {
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                return mapper.mapToDtoList(
                    dao.buscarTodos().stream()
                        .filter(c -> c.getUsername().toLowerCase().contains(filtro.toLowerCase()))
                        .toList()
                );
            });
        }
    }
}