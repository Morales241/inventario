package Servicios;

import Dao.DaoEquipoAsignado;
import Dao.DaoEquipoDeComputo;
import Dao.DaoUsuario;
import Dtos.AsignacionDTO;
import Entidades.EquipoAsignado;
import Entidades.EquipoDeComputo;
import Entidades.Usuario;
import Enums.EstadoEquipo;
import excepciones.RecursoNoEncontradoException;
import excepciones.ReglaNegocioException;
import interfacesServicios.IServicioPrestamos;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import mapper.MapperAsignacion;

/**
 * Servicio de lógica de negocio para la gestión de asignaciones y préstamos de equipos.
 * <p>
 * Coordina las operaciones de asignación, devolución y consulta de equipos asignados
 * a usuarios. Implementa validaciones de negocio para mantener la integridad de los
 * préstamos y el estado de los equipos.
 * </p>
 */
public class ServicioPrestamos extends ServicioBase implements IServicioPrestamos {

    private final AsignacionServicio asignacionServicio;
    private final DaoEquipoDeComputo daoEquipo;
    private final DaoUsuario daoUsuario;
    private final DaoEquipoAsignado daoAsignacion;

    public ServicioPrestamos() {
        this.daoEquipo = new DaoEquipoDeComputo();
        this.daoUsuario = new DaoUsuario();
        this.daoAsignacion = new DaoEquipoAsignado();
        this.asignacionServicio = new AsignacionServicio(daoAsignacion, daoEquipo, daoUsuario);
    }
    
    public ServicioPrestamos(DaoEquipoDeComputo daoEquipo,
                            DaoUsuario daoUsuario,
                            DaoEquipoAsignado daoAsignacion) {
        this.daoEquipo = daoEquipo;
        this.daoUsuario = daoUsuario;
        this.daoAsignacion = daoAsignacion;
        this.asignacionServicio = new AsignacionServicio(daoAsignacion, daoEquipo, daoUsuario);
    }

    private void configurarDAOs(EntityManager em) {
        if (daoEquipo != null) {
            daoEquipo.setEntityManager(em);
        }
        if (daoUsuario != null) {
            daoUsuario.setEntityManager(em);
        }
        if (daoAsignacion != null) {
            daoAsignacion.setEntityManager(em);
        }
    }

    @Override
    public void asignarEquipo(Long idEquipo, Long idUsuario) {
        if (idEquipo == null || idUsuario == null) {
            throw new IllegalArgumentException("IDs inválidos");
        }

        ejecutarTransaccion(em -> {
            configurarDAOs(em);
            asignacionServicio.configurarEntityManager(em);

            // Validar que el equipo existe y está disponible
            EquipoDeComputo equipo = daoEquipo.buscarPorId(idEquipo);
            if (equipo == null) {
                throw new RecursoNoEncontradoException("Equipo con ID " + idEquipo + " no encontrado");
            }

            // Validar que el usuario existe y está activo
            Usuario usuario = daoUsuario.buscarPorId(idUsuario);
            if (usuario == null) {
                throw new RecursoNoEncontradoException("Usuario con ID " + idUsuario + " no encontrado");
            }

            // Usar el servicio de asignaciones para crear la asignación
            asignacionServicio.crearAsignacion(equipo, usuario, em);

            return null;
        });
    }

    @Override
    public void devolverEquipo(Long idAsignacion) {
        if (idAsignacion == null) {
            throw new IllegalArgumentException("ID de asignación inválido");
        }

        ejecutarTransaccion(em -> {
            configurarDAOs(em);
            asignacionServicio.configurarEntityManager(em);

            asignacionServicio.procesarDevolucion(idAsignacion, em);

            return null;
        });
    }

    @Override
    public List<AsignacionDTO> obtenerEquiposDeUsuarios(Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }

        return ejecutarLectura(em -> {
            configurarDAOs(em);
            asignacionServicio.configurarEntityManager(em);

            // Verificar que el usuario existe
            Usuario usuario = daoUsuario.buscarPorId(idUsuario);
            if (usuario == null) {
                throw new RecursoNoEncontradoException("Usuario con ID " + idUsuario + " no encontrado");
            }

            return asignacionServicio.obtenerAsignacionesActivasPorUsuario(idUsuario);
        });
    }

    @Override
    public List<AsignacionDTO> obtenerTodasLasAsignacionesActivas() {
        return ejecutarLectura(em -> {
            asignacionServicio.configurarEntityManager(em);
            return asignacionServicio.obtenerTodasActivas();
        });
    }

    @Override
    public List<AsignacionDTO> obtenerHistorialUsuario(Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }

        return ejecutarLectura(em -> {
            configurarDAOs(em);
            asignacionServicio.configurarEntityManager(em);

            // Verificar que el usuario existe
            Usuario usuario = daoUsuario.buscarPorId(idUsuario);
            if (usuario == null) {
                throw new RecursoNoEncontradoException("Usuario con ID " + idUsuario + " no encontrado");
            }

            return asignacionServicio.obtenerHistorialPorUsuario(idUsuario);
        });
    }

    @Override
    public List<AsignacionDTO> obtenerHistorialEquipo(Long idEquipo) {
        if (idEquipo == null) {
            throw new IllegalArgumentException("ID de equipo inválido");
        }

        return ejecutarLectura(em -> {
            configurarDAOs(em);
            asignacionServicio.configurarEntityManager(em);
            return asignacionServicio.obtenerAsignacionesPorEquipo(idEquipo);
        });
    }

    @Override
    public boolean equipoEstaAsignado(Long idEquipo) {
        if (idEquipo == null) {
            throw new IllegalArgumentException("ID de equipo inválido");
        }

        return ejecutarLectura(em -> {
            configurarDAOs(em);
            asignacionServicio.configurarEntityManager(em);
            return asignacionServicio.tieneAsignacionActiva(idEquipo);
        });
    }

    @Override
    public Usuario obtenerUsuarioActualDeEquipo(Long idEquipo) {
        if (idEquipo == null) {
            throw new IllegalArgumentException("ID de equipo inválido");
        }

        return ejecutarLectura(em -> {
            configurarDAOs(em);
            asignacionServicio.configurarEntityManager(em);

            List<EquipoAsignado> activas = daoEquipo.buscarPorId(idEquipo)
                .getAsignaciones().stream()
                .filter(a -> a.getFechaDevolucion() == null)
                .toList();

            if (activas.isEmpty()) {
                return null;
            }

            return activas.get(0).getUsuario();
        });
    }

    @Override
    public List<AsignacionDTO> buscarAsignaciones(String filtro) {
        if (filtro == null || filtro.isBlank()) {
            return obtenerTodasLasAsignacionesActivas();
        }

        return ejecutarLectura(em -> {
            configurarDAOs(em);
            
            String filtroLower = filtro.toLowerCase();
            
            // Buscar en todas las asignaciones
            List<EquipoAsignado> todas = daoAsignacion.buscarTodos();
            List<EquipoAsignado> filtradas = todas.stream()
                .filter(a -> 
                    (a.getUsuario() != null && a.getUsuario().getNombre().toLowerCase().contains(filtroLower)) ||
                    (a.getEquipoDeComputo() != null && 
                        (a.getEquipoDeComputo().getIdentificador() != null && 
                         a.getEquipoDeComputo().getIdentificador().toLowerCase().contains(filtroLower)) ||
                        (a.getEquipoDeComputo().getGry() != null && 
                         a.getEquipoDeComputo().getGry().toString().contains(filtroLower)))
                )
                .toList();
                
            return MapperAsignacion.converter.mapToDtoList(filtradas);
        });
    }

    @Override
    public List<AsignacionDTO> listarAsignaciones() {
        return asignacionServicio.buscarTodos();
    }

    @Override
    public List<AsignacionDTO> obtenerAsignacionesActivasPorEquipo(Long idEquipo) {
        if (idEquipo == null) {
            throw new IllegalArgumentException("ID de equipo inválido");
        }

        return ejecutarLectura(em -> {
            configurarDAOs(em);
            
            // Verificar que el equipo existe
            EquipoDeComputo equipo = daoEquipo.buscarPorId(idEquipo);
            if (equipo == null) {
                throw new RecursoNoEncontradoException("Equipo con ID " + idEquipo + " no encontrado");
            }
            
            // Buscar asignaciones activas del equipo
            List<EquipoAsignado> todas = daoAsignacion.buscarTodos();
            List<EquipoAsignado> activas = todas.stream()
                .filter(a -> a.getEquipoDeComputo() != null && 
                             a.getEquipoDeComputo().getId().equals(idEquipo) &&
                             a.getFechaDevolucion() == null)
                .toList();
                
            return MapperAsignacion.converter.mapToDtoList(activas);
        });
    }

    @Override
    public List<AsignacionDTO> obtenerAsignacionesPorRangoFechas(String fechaInicio, String fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Fechas inválidas");
        }

        return ejecutarLectura(em -> {
            configurarDAOs(em);
            
            try {
                LocalDate inicio = LocalDate.parse(fechaInicio);
                LocalDate fin = LocalDate.parse(fechaFin);
                
                if (inicio.isAfter(fin)) {
                    throw new ReglaNegocioException("La fecha de inicio no puede ser mayor a la fecha fin");
                }
                
                // Usar Criteria API para búsqueda por rango de fechas
                CriteriaBuilder cb = em.getCriteriaBuilder();
                CriteriaQuery<EquipoAsignado> cq = cb.createQuery(EquipoAsignado.class);
                Root<EquipoAsignado> root = cq.from(EquipoAsignado.class);
                
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaEntrega"), inicio));
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaEntrega"), fin));
                
                cq.select(root).where(predicates.toArray(new Predicate[0]));
                cq.orderBy(cb.desc(root.get("fechaEntrega")));
                
                List<EquipoAsignado> resultados = em.createQuery(cq).getResultList();
                return MapperAsignacion.converter.mapToDtoList(resultados);
                
            } catch (DateTimeParseException e) {
                throw new ReglaNegocioException("Formato de fecha inválido. Use formato ISO: YYYY-MM-DD");
            }
        });
    }

    @Override
    public int contarEquiposAsignadosAUsuario(Long idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }

        return ejecutarLectura(em -> {
            configurarDAOs(em);
            
            // Verificar que el usuario existe
            Usuario usuario = daoUsuario.buscarPorId(idUsuario);
            if (usuario == null) {
                throw new RecursoNoEncontradoException("Usuario con ID " + idUsuario + " no encontrado");
            }
            
            // Contar asignaciones activas del usuario
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<EquipoAsignado> root = cq.from(EquipoAsignado.class);
            
            Predicate usuarioPredicate = cb.equal(root.get("usuario").get("idUsuario"), idUsuario);
            Predicate activoPredicate = cb.isNull(root.get("fechaDevolucion"));
            
            cq.select(cb.count(root))
              .where(cb.and(usuarioPredicate, activoPredicate));
            
            return em.createQuery(cq).getSingleResult().intValue();
        });
    }

    @Override
    public AsignacionDTO obtenerUltimaAsignacionDeEquipo(Long idEquipo) {
        if (idEquipo == null) {
            throw new IllegalArgumentException("ID de equipo inválido");
        }

        return ejecutarLectura(em -> {
            configurarDAOs(em);
            
            // Verificar que el equipo existe
            EquipoDeComputo equipo = daoEquipo.buscarPorId(idEquipo);
            if (equipo == null) {
                throw new RecursoNoEncontradoException("Equipo con ID " + idEquipo + " no encontrado");
            }
            
            // Buscar la última asignación (por fecha de entrega)
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<EquipoAsignado> cq = cb.createQuery(EquipoAsignado.class);
            Root<EquipoAsignado> root = cq.from(EquipoAsignado.class);
            
            Predicate equipoPredicate = cb.equal(root.get("equipoDeComputo").get("id"), idEquipo);
            
            cq.select(root)
              .where(equipoPredicate)
              .orderBy(cb.desc(root.get("fechaEntrega")));
            
            List<EquipoAsignado> resultados = em.createQuery(cq)
                                                .setMaxResults(1)
                                                .getResultList();
            
            return resultados.isEmpty() ? null : MapperAsignacion.converter.mapToDto(resultados.get(0));
        });
    }

    @Override
    public boolean usuarioPuedeRecibirMasEquipos(Long idUsuario, int limiteMaximo) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("ID de usuario inválido");
        }
        
        if (limiteMaximo <= 0) {
            limiteMaximo = 5; // Valor por defecto si no se especifica
        }

        int equiposActuales = contarEquiposAsignadosAUsuario(idUsuario);
        return equiposActuales < limiteMaximo;
    }

    /**
     * Servicio para Asignaciones de Equipos
     */
    private class AsignacionServicio extends ServicioGenerico<EquipoAsignado, AsignacionDTO, Long> {
        
        private final DaoEquipoAsignado dao;
        private final DaoEquipoDeComputo daoEquipo;
        private final DaoUsuario daoUsuario;
        
        public AsignacionServicio() {
            super(new DaoEquipoAsignado(), MapperAsignacion.converter, EquipoAsignado.class);
            this.dao = (DaoEquipoAsignado) super.dao;
            this.daoEquipo = new DaoEquipoDeComputo();
            this.daoUsuario = new DaoUsuario();
        }
        
        public AsignacionServicio(DaoEquipoAsignado dao, 
                                  DaoEquipoDeComputo daoEquipo,
                                  DaoUsuario daoUsuario) {
            super(dao, MapperAsignacion.converter, EquipoAsignado.class);
            this.dao = dao;
            this.daoEquipo = daoEquipo;
            this.daoUsuario = daoUsuario;
        }
        
        @Override
        protected void configurarEntityManager(EntityManager em) {
            if (dao != null) {
                dao.setEntityManager(em);
            }
            if (daoEquipo != null) {
                daoEquipo.setEntityManager(em);
            }
            if (daoUsuario != null) {
                daoUsuario.setEntityManager(em);
            }
        }
        
        @Override
        protected void validarNegocio(AsignacionDTO dto, boolean esNuevo, EntityManager em) {
            // Las asignaciones no se crean directamente desde DTO
            // Se crean a través de los métodos específicos
            throw new ReglaNegocioException(
                "Las asignaciones deben crearse a través del método asignarEquipo");
        }
        
        @Override
        protected Long extraerId(AsignacionDTO dto) {
            return dto.getId();
        }
        
        @Override
        protected void validarEliminacion(EquipoAsignado entidad) {
            // No se deben eliminar asignaciones, solo se marcan como devueltas
            throw new ReglaNegocioException(
                "No se pueden eliminar asignaciones. Use devolverEquipo para finalizarlas");
        }
        
        /**
         * Crea una nueva asignación de equipo
         */
        public void crearAsignacion(EquipoDeComputo equipo, Usuario usuario, EntityManager em) {
            // Asegurar que los DAOs usan el EM correcto
            configurarEntityManager(em);
            
            // Validaciones de negocio
            if (!Boolean.TRUE.equals(usuario.getActivo())) {
                throw new ReglaNegocioException(
                    "El usuario " + usuario.getNombre() + " está inactivo");
            }
            
            if (equipo.getEstado() != EstadoEquipo.EN_STOCK) {
                throw new ReglaNegocioException(
                    "El equipo GRY " + equipo.getGry() + " no está disponible");
            }
            
            // Validar que el usuario no tenga ya este equipo asignado
            boolean yaAsignado = dao.buscarPorUsuarioActivo(usuario.getIdUsuario())
                .stream()
                .anyMatch(a -> a.getEquipoDeComputo().getId().equals(equipo.getId()));
            
            if (yaAsignado) {
                throw new ReglaNegocioException(
                    "El equipo GRY " + equipo.getGry() + " ya está asignado a este usuario");
            }
            
            // Crear la asignación
            EquipoAsignado asignacion = new EquipoAsignado();
            asignacion.setEquipoDeComputo(equipo);
            asignacion.setUsuario(usuario);
            asignacion.setFechaEntrega(LocalDate.now());
            
            // Actualizar estado del equipo
            equipo.setEstado(EstadoEquipo.ASIGNADO);
            
            // Persistir
            daoEquipo.actualizar(equipo);
            dao.guardar(asignacion);
        }
        
        /**
         * Procesa la devolución de un equipo
         */
        public void procesarDevolucion(Long idAsignacion, EntityManager em) {
            configurarEntityManager(em);
            
            EquipoAsignado asignacion = dao.buscarPorId(idAsignacion);
            
            if (asignacion == null) {
                throw new RecursoNoEncontradoException(
                    "Asignación con ID " + idAsignacion + " no encontrada");
            }
            
            if (asignacion.getFechaDevolucion() != null) {
                throw new ReglaNegocioException(
                    "El equipo ya fue devuelto el " + asignacion.getFechaDevolucion());
            }
            
            // Actualizar la asignación
            asignacion.setFechaDevolucion(LocalDate.now());
            
            // Actualizar estado del equipo
            EquipoDeComputo equipo = asignacion.getEquipoDeComputo();
            equipo.setEstado(EstadoEquipo.EN_STOCK);
            
            // Persistir cambios
            dao.actualizar(asignacion);
            daoEquipo.actualizar(equipo);
        }
        
        /**
         * Obtiene todas las asignaciones activas de un usuario
         */
        public List<AsignacionDTO> obtenerAsignacionesActivasPorUsuario(Long idUsuario) {
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                List<EquipoAsignado> asignaciones = dao.buscarPorUsuarioActivo(idUsuario);
                return mapper.mapToDtoList(asignaciones);
            });
        }
        
        /**
         * Obtiene el historial completo de asignaciones de un usuario
         */
        public List<AsignacionDTO> obtenerHistorialPorUsuario(Long idUsuario) {
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                List<EquipoAsignado> todas = dao.buscarTodos();
                List<EquipoAsignado> delUsuario = todas.stream()
                    .filter(a -> a.getUsuario().getIdUsuario().equals(idUsuario))
                    .toList();
                    
                return mapper.mapToDtoList(delUsuario);
            });
        }
        
        /**
         * Obtiene todas las asignaciones activas del sistema
         */
        public List<AsignacionDTO> obtenerTodasActivas() {
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                List<EquipoAsignado> todas = dao.buscarTodos();
                List<EquipoAsignado> activas = todas.stream()
                    .filter(a -> a.getFechaDevolucion() == null)
                    .toList();
                    
                return mapper.mapToDtoList(activas);
            });
        }
        
        /**
         * Obtiene todas las asignaciones de un equipo específico
         */
        public List<AsignacionDTO> obtenerAsignacionesPorEquipo(Long idEquipo) {
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                
                List<EquipoAsignado> todas = dao.buscarTodos();
                List<EquipoAsignado> delEquipo = todas.stream()
                    .filter(a -> a.getEquipoDeComputo().getId().equals(idEquipo))
                    .toList();
                    
                return mapper.mapToDtoList(delEquipo);
            });
        }
        
        /**
         * Verifica si un equipo tiene asignación activa
         */
        public boolean tieneAsignacionActiva(Long idEquipo) {
            return ejecutarLectura(em -> {
                configurarEntityManager(em);
                return dao.existeAsignacionActiva(idEquipo);
            });
        }
        
        @Override
        public List<AsignacionDTO> buscarConFiltro(String filtro) {
            return ServicioPrestamos.this.buscarAsignaciones(filtro);
        }
    }
    
    @Override
    public List<AsignacionDTO> buscarAsignacionesPaginado(String filtro,
                                                           int pagina,
                                                           int tamano) {
        return ejecutarLectura(em -> {
            configurarDAOs(em);
 
            List<Entidades.EquipoAsignado> entidades;
 
            if (filtro == null || filtro.isBlank()) {
                entidades = daoAsignacion.buscarActivasPaginado(pagina, tamano);
            } else {
                entidades = daoAsignacion.buscarConFiltroPaginado(filtro, pagina, tamano);
            }
 
            return mapper.MapperAsignacion.converter.mapToDtoList(entidades);
        });
    }
 
    @Override
    public long contarAsignaciones(String filtro) {
        return ejecutarLectura(em -> {
            configurarDAOs(em);
 
            if (filtro == null || filtro.isBlank()) {
                return daoAsignacion.contarActivas();
            } else {
                return daoAsignacion.contarConFiltro(filtro);
            }
        });
    }
}