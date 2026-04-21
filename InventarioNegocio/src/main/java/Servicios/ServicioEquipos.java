package Servicios;

import Dao.*;
import Dtos.*;
import Entidades.*;
import Enums.CondicionFisica;
import Enums.EstadoEquipo;
import Enums.TipoEquipo;
import Interfaces.IDaoEquipoDeComputo;
import Interfaces.IDaoEquipoDeEscritorio;
import Interfaces.IDaoGenerico;
import Interfaces.IDaoModelo;
import Interfaces.IDaoMovil;
import Interfaces.IDaoOtroEquipo;
import Interfaces.IDaoSucursal;
import excepciones.RecursoNoEncontradoException;
import excepciones.ReglaNegocioException;
import interfacesServicios.IServicioEquipos;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import mapper.Mapper;
import mapper.MapperEquipos;
import mapper.MapperModelo;

/**
 * Servicio de lógica de negocio para la gestión integral de equipos de cómputo.
 */
public class ServicioEquipos extends ServicioBase implements IServicioEquipos {

    private final EquipoEscritorioServicio escritorioServicio;
    private final EquipoMovilServicio movilServicio;
    private final EquipoOtroServicio otroServicio;
    private final ModeloServicio modeloServicio;
    private final IDaoEquipoDeComputo daoGeneral;

    public ServicioEquipos() {
        this.daoGeneral = new DaoEquipoDeComputo();
        this.escritorioServicio = new EquipoEscritorioServicio(daoGeneral);
        this.movilServicio = new EquipoMovilServicio(daoGeneral);
        this.otroServicio = new EquipoOtroServicio(daoGeneral);
        this.modeloServicio = new ModeloServicio();
    }

    public ServicioEquipos(IDaoEquipoDeComputo daoGeneral, IDaoEquipoDeEscritorio daoEscritorio, IDaoMovil daoMovil,
            IDaoOtroEquipo daoOtro, IDaoModelo daoModelo, IDaoSucursal daoSucursal) {
        this.daoGeneral = daoGeneral;
        this.escritorioServicio = new EquipoEscritorioServicio(daoGeneral, daoEscritorio, daoModelo, daoSucursal);
        this.movilServicio = new EquipoMovilServicio(daoGeneral, daoMovil, daoModelo, daoSucursal);
        this.otroServicio = new EquipoOtroServicio(daoGeneral, daoOtro, daoModelo, daoSucursal);
        this.modeloServicio = new ModeloServicio(daoModelo);
    }

    private void configurarGeneral(EntityManager em) {
        if (daoGeneral != null) {
            daoGeneral.setEntityManager(em);
        }
    }

    // MÉTODOS GENERALES
    @Override
    public List<EquipoBaseDTO> buscarEquipos(Integer gry, EstadoEquipo estado, String criterio) {
        return ejecutarLectura(em -> {
            configurarGeneral(em);

            List<EquipoDeComputo> lista = daoGeneral.buscarConFiltros(gry, estado, criterio);

            return lista.stream()
                    .map(this::mapearPolimorfico)
                    .toList();
        });
    }

    @Override
    public List<EquipoBaseDTO> listarEquipos() {
        return buscarConFiltros(null, null, null, null);
    }

    @Override
    public EquipoBaseDTO obtenerPorId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        return ejecutarLectura(em -> {
            configurarGeneral(em);

            EquipoDeComputo equipo = daoGeneral.buscarPorId(id);

            if (equipo == null) {
                throw new RecursoNoEncontradoException("Equipo no encontrado");
            }

            return mapearPolimorfico(equipo);
        });
    }

    @Override
    public EquipoBaseDTO buscarPorGry(Integer gry) {
        if (gry == null) {
            throw new IllegalArgumentException("GRY inválido");
        }

        return ejecutarLectura(em -> {
            configurarGeneral(em);

            EquipoDeComputo equipo = daoGeneral.buscarPorGry(gry);

            if (equipo == null) {
                throw new RecursoNoEncontradoException("Equipo no encontrado");
            }

            return mapearPolimorfico(equipo);
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EquipoBaseDTO> T buscarPorId(Long id) {
        EquipoBaseDTO equipo = obtenerPorId(id);
        return (T) equipo;
    }

    @Override
    public List<EquipoBaseDTO> buscarConFiltros(String texto, TipoEquipo tipo, CondicionFisica condicion, EstadoEquipo estado) {
        return ejecutarLectura(em -> {
            configurarGeneral(em);

            List<EquipoDeComputo> equipos = daoGeneral.buscarConFiltros(texto, tipo, condicion, estado);

            return equipos.stream()
                    .map(equipo -> MapperEquipos.mapCommonToDto(equipo, new EquipoBaseDTO()))
                    .collect(Collectors.toList());
        });
    }

    @Override
    public List<EquipoBaseDTO> buscarConFiltrosPaginado(String texto,
            TipoEquipo tipo,
            CondicionFisica condicion,
            EstadoEquipo estado,
            int pagina,
            int tamano) {
        return ejecutarLectura(em -> {
            configurarGeneral(em);

            // daoGeneral es DaoEquipoDeComputo — hacer cast para acceder al método nuevo
            DaoEquipoDeComputo dao = (DaoEquipoDeComputo) daoGeneral;

            List<EquipoDeComputo> equipos
                    = dao.buscarConFiltrosPaginado(texto, tipo, condicion, estado, pagina, tamano);

            return equipos.stream()
                    .map(this::mapearPolimorfico)
                    .collect(Collectors.toList());
        });
    }

    /**
     * NUEVO: COUNT en BD — no trae objetos completos. Se usa para calcular el
     * total de páginas en el paginador.
     */
    @Override
    public long contarConFiltros(String texto,
            TipoEquipo tipo,
            CondicionFisica condicion,
            EstadoEquipo estado) {
        return ejecutarLectura(em -> {
            configurarGeneral(em);
            DaoEquipoDeComputo dao = (DaoEquipoDeComputo) daoGeneral;
            return dao.contarConFiltros(texto, tipo, condicion, estado);
        });
    }

    @Override
    public void eliminarEquipo(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        ejecutarTransaccion(em -> {
            configurarGeneral(em);

            EquipoDeComputo equipo = daoGeneral.buscarPorId(id);

            if (equipo == null) {
                throw new RecursoNoEncontradoException("Equipo no encontrado");
            }

            if (equipo.getEstado() == EstadoEquipo.ASIGNADO) {
                throw new ReglaNegocioException(
                        "No se puede eliminar un equipo asignado");
            }

            daoGeneral.eliminar(id);
            return null;
        });
    }

    // EQUIPOS DE ESCRITORIO
    @Override
    public EquipoEscritorioDTO guardarEscritorio(EquipoEscritorioDTO dto) {
        return escritorioServicio.guardar(dto);
    }

    // EQUIPOS MÓVILES 
    @Override
    public MovilDTO guardarMovil(MovilDTO dto) {
        return movilServicio.guardar(dto);
    }

    // OTROS EQUIPOS 
    @Override
    public OtroEquipoDTO guardarOtro(OtroEquipoDTO dto) {
        return otroServicio.guardar(dto);
    }

    // MODELOS 
    @Override
    public ModeloDTO guardarModelo(ModeloDTO dto) {
        return modeloServicio.guardar(dto);
    }

    @Override
    public List<ModeloDTO> listarModelos() {
        return modeloServicio.buscarTodos();
    }

    @Override
    public ModeloDTO buscarModeloPorId(Long id) {
        return modeloServicio.buscarPorId(id);
    }

    @Override
    public List<ModeloDTO> buscarModelosConFiltros(String nombre, String marca,
            Integer memoriaRam, Integer almacenamiento, String procesador) {
        return modeloServicio.buscarConFiltros(nombre, marca, memoriaRam, almacenamiento, procesador);
    }

    // MÉTODOS AUXILIARES
    private EquipoBaseDTO mapearPolimorfico(EquipoDeComputo equipo) {
        if (equipo instanceof EquipoDeEscritorio e) {
            return MapperEquipos.escritorio.mapToDto(e);
        }
        if (equipo instanceof Movil m) {
            return MapperEquipos.movil.mapToDto(m);
        }
        return MapperEquipos.otro.mapToDto((OtroEquipo) equipo);
    }

    // SERVICIOS ESPECIFICOS
    /**
     * Servicio base para todos los tipos de equipos
     */
    private abstract class EquipoBaseServicio<T extends EquipoDeComputo, D extends EquipoBaseDTO> extends ServicioGenerico<T, D, Long> {

        protected final IDaoSucursal daoSucursal;
        protected final IDaoModelo daoModelo;
        protected final IDaoGenerico<T, Long> daoEquipo;
        protected final IDaoEquipoDeComputo daoGeneral; // Referencia al daoGeneral de la clase externa

        public EquipoBaseServicio(IDaoGenerico<T, Long> dao, Mapper<T, D> mapper, Class<T> claseEntidad, IDaoEquipoDeComputo daoGeneral) {
            super(dao, mapper, claseEntidad);
            this.daoEquipo = dao;
            this.daoSucursal = new DaoSucursal();
            this.daoModelo = new DaoModelo();
            this.daoGeneral = daoGeneral;
        }

        public EquipoBaseServicio(IDaoGenerico<T, Long> dao, Mapper<T, D> mapper, Class<T> claseEntidad,
                IDaoEquipoDeComputo daoGeneral, IDaoModelo daoModelo, IDaoSucursal daoSucursal) {
            super(dao, mapper, claseEntidad);
            this.daoEquipo = dao;
            this.daoGeneral = daoGeneral;
            this.daoModelo = daoModelo;
            this.daoSucursal = daoSucursal;
        }

        @Override
        protected void configurarEntityManager(EntityManager em) {
            if (dao != null) {
                dao.setEntityManager(em);
            }
            if (daoSucursal != null) {
                daoSucursal.setEntityManager(em);
            }
            if (daoModelo != null) {
                daoModelo.setEntityManager(em);
            }
            if (daoEquipo != null && daoEquipo != dao) {
                daoEquipo.setEntityManager(em);
            }
            if (daoGeneral != null) {
                daoGeneral.setEntityManager(em);
            }
        }

        @Override
        protected void validarNegocio(D dto, boolean esNuevo, EntityManager em) {
            if (dto.getGry() == null || dto.getGry() <= 0) {
                throw new ReglaNegocioException("El GRY es obligatorio y debe ser mayor a 0");
            }

            if (dto.getIdentificador() == null || dto.getIdentificador().isBlank()) {
                throw new ReglaNegocioException("El identificador es obligatorio y no puede estar en blanco");
            }

            if (dto.getIdSucursal() == null) {
                throw new ReglaNegocioException("Debe seleccionar una sucursal");
            }

            if (dto.getIdModelo() == null) {
                throw new ReglaNegocioException("Debe seleccionar un modelo");
            }

            validarDuplicidadGry(dto.getGry(), dto.getIdEquipo(), em);
        }

        @Override
        protected Long extraerId(D dto) {
            return dto.getIdEquipo();
        }

        @Override
        protected void validarEliminacion(T entidad) {
            if (entidad.getEstado() == EstadoEquipo.ASIGNADO) {
                throw new ReglaNegocioException(
                        "No se puede eliminar un equipo asignado");
            }
        }

        protected void validarDuplicidadGry(Integer gry, Long idActual, EntityManager em) {
            // Verificar que daoGeneral no sea null
            if (daoGeneral == null) {
                throw new IllegalStateException("daoGeneral no está inicializado");
            }

            // Configurar el EntityManager en daoGeneral
            daoGeneral.setEntityManager(em);

            EquipoDeComputo existente = daoGeneral.buscarPorGry(gry);

            if (existente != null) {
                if (idActual == null || !existente.getId().equals(idActual)) {
                    throw new ReglaNegocioException(
                            "Ya existe un equipo con el GRY: " + gry);
                }
            }
        }

        protected void establecerRelaciones(T entidad, D dto, EntityManager em) {
            if (daoSucursal != null) {
                daoSucursal.setEntityManager(em);
            }
            if (daoModelo != null) {
                daoModelo.setEntityManager(em);
            }

            Sucursal sucursal = daoSucursal.buscarPorId(dto.getIdSucursal());
            if (sucursal == null) {
                throw new RecursoNoEncontradoException(
                        "Sucursal con ID " + dto.getIdSucursal() + " no encontrada");
            }

            Modelo modelo = daoModelo.buscarPorId(dto.getIdModelo());
            if (modelo == null) {
                throw new RecursoNoEncontradoException(
                        "Modelo con ID " + dto.getIdModelo() + " no encontrado");
            }

            entidad.setSucursal(sucursal);
            entidad.setModelo(modelo);
        }

        @Override
        public D guardar(D dto) {
            return ejecutarTransaccion(em -> {
                configurarEntityManager(em);
                validarNegocio(dto, dto.getIdEquipo() == null, em);

                T entidad;

                if (dto.getIdEquipo() != null && dto.getIdEquipo() > 0) {
                    
                    entidad = dao.buscarPorId(dto.getIdEquipo());
                    if (entidad == null) {
                        throw new RecursoNoEncontradoException("Equipo no encontrado para actualizar");
                    }

                    actualizarCamposBase(entidad, dto);
                    actualizarCamposEspecificos(entidad, dto);
                    establecerRelaciones(entidad, dto, em);

                    entidad = dao.actualizar(entidad);

                } else {
                    entidad = mapper.mapToEntity(dto);
                    establecerRelaciones(entidad, dto, em);
                    entidad.setEstado(EstadoEquipo.EN_STOCK);
                    entidad = dao.guardar(entidad);
                }

                return mapper.mapToDto(entidad);
            });
        }
        
        private void actualizarCamposBase(T existente, D dto) {
            existente.setGry(dto.getGry());
            existente.setIdentificador(dto.getIdentificador());
            existente.setFactura(dto.getFactura());
            existente.setObservaciones(dto.getObservaciones());
            existente.setFechaCompra(dto.getFechaCompra());
            existente.setPrecio(dto.getPrecio());
            existente.setVersion(dto.getVersion());

            if (dto.getCondicion() != null) {
                existente.setCondicion(CondicionFisica.valueOf(dto.getCondicion()));
            }
            if (dto.getTipo() != null) {
                existente.setTipo(TipoEquipo.valueOf(dto.getTipo()));
            }
        }
        
        protected void actualizarCamposEspecificos(T existente, D dto) {
            // Implementado en EquipoEscritorioServicio, EquipoMovilServicio, EquipoOtroServicio
        }
    }

    /**
     * Servicio para Equipos de Escritorio
     */
    private class EquipoEscritorioServicio extends EquipoBaseServicio<EquipoDeEscritorio, EquipoEscritorioDTO> {

        public EquipoEscritorioServicio(IDaoEquipoDeComputo daoGeneral) {
            super(new DaoEquipoDeEscritorio(), MapperEquipos.escritorio, EquipoDeEscritorio.class, daoGeneral);
        }

        public EquipoEscritorioServicio(IDaoEquipoDeComputo daoGeneral, IDaoEquipoDeEscritorio dao,
                IDaoModelo daoModelo, IDaoSucursal daoSucursal) {
            super(dao, MapperEquipos.escritorio, EquipoDeEscritorio.class, daoGeneral, daoModelo, daoSucursal);
        }

        @Override
        protected void validarNegocio(EquipoEscritorioDTO dto, boolean esNuevo, EntityManager em) {
            super.validarNegocio(dto, esNuevo, em);

            if (dto.getNombreEquipo() == null || dto.getNombreEquipo().isBlank()) {
                throw new ReglaNegocioException("El nombre del equipo es obligatorio");
            }

//            if (dto.getUserRed() == null || dto.getPrecio() == 0.0) {
//                throw new ReglaNegocioException("El usuario de red es obligatorio");
//            }
        }

        @Override
        public List<EquipoEscritorioDTO> buscarConFiltro(String filtro) {
            return null;
        }
        
        @Override
        protected void actualizarCamposEspecificos(EquipoDeEscritorio existente, EquipoEscritorioDTO dto) {
            existente.setNombreEquipo(dto.getNombreEquipo());
            existente.setCuenta(dto.getCuenta());
            existente.setFinalGarantia(dto.getFinalGarantia());
            existente.setMochila(dto.getMochila());
            existente.setMouse(dto.getMouse());
            existente.setSisOpertativo(dto.getSisOpertativo());
            existente.setUserRed(dto.getUserRed());
        }
    }

    /**
     * Servicio para Equipos Móviles
     */
    private class EquipoMovilServicio extends EquipoBaseServicio<Movil, MovilDTO> {

        public EquipoMovilServicio(IDaoEquipoDeComputo daoGeneral) {
            super(new DaoMovil(), MapperEquipos.movil, Movil.class, daoGeneral);
        }

        public EquipoMovilServicio(IDaoEquipoDeComputo daoGeneral, IDaoMovil dao,
                IDaoModelo daoModelo, IDaoSucursal daoSucursal) {
            super(dao, MapperEquipos.movil, Movil.class, daoGeneral, daoModelo, daoSucursal);
        }

        @Override
        protected void validarNegocio(MovilDTO dto, boolean esNuevo, EntityManager em) {
            super.validarNegocio(dto, esNuevo, em);

            if (dto.getNumCelular() == null || dto.getNumCelular().isBlank()) {
                throw new ReglaNegocioException("El número de celular es obligatorio");
            }

            if (dto.getCorreoCuenta() == null || dto.getCorreoCuenta().isBlank()) {
                throw new ReglaNegocioException("El correo de la cuenta es obligatorio");
            }

            if (dto.getContrasenaCuenta() == null || dto.getContrasenaCuenta().isBlank()) {
                throw new ReglaNegocioException("La contraseña de la cuenta es obligatoria");
            }
        }

        @Override
        public List<MovilDTO> buscarConFiltro(String filtro) {
            return null;
        }
        
        @Override
        protected void actualizarCamposEspecificos(Movil existente, MovilDTO dto) {
            existente.setNumCelular(dto.getNumCelular());
            existente.setCargador(dto.getCargador());
            existente.setFunda(dto.getFunda());
            existente.setManosLibres(dto.getManosLibres());
            existente.setCorreoCuenta(dto.getCorreoCuenta());
            existente.setContrasenaCuenta(dto.getContrasenaCuenta());
        }
        
    }

    /**
     * Servicio para Otros Equipos
     */
    private class EquipoOtroServicio extends EquipoBaseServicio<OtroEquipo, OtroEquipoDTO> {

        public EquipoOtroServicio(IDaoEquipoDeComputo daoGeneral) {
            super(new DaoOtroEquipo(), MapperEquipos.otro, OtroEquipo.class, daoGeneral);
        }

        public EquipoOtroServicio(IDaoEquipoDeComputo daoGeneral, IDaoOtroEquipo dao,
                IDaoModelo daoModelo, IDaoSucursal daoSucursal) {
            super(dao, MapperEquipos.otro, OtroEquipo.class, daoGeneral, daoModelo, daoSucursal);
        }

        @Override
        protected void validarNegocio(OtroEquipoDTO dto, boolean esNuevo, EntityManager em) {
            super.validarNegocio(dto, esNuevo, em);

            if (dto.getTituloCampoExtra() == null || dto.getTituloCampoExtra().isBlank()) {
                throw new ReglaNegocioException("El título del campo extra es obligatorio");
            }

            if (dto.getContenidoCampoExtra() == null || dto.getContenidoCampoExtra().isBlank()) {
                throw new ReglaNegocioException("El contenido del campo extra es obligatorio");
            }
        }

        @Override
        public List<OtroEquipoDTO> buscarConFiltro(String filtro) {
            return null;
        }
        
        @Override
        protected void actualizarCamposEspecificos(OtroEquipo existente, OtroEquipoDTO dto) {
            existente.setTituloCampoExtra(dto.getTituloCampoExtra());
            existente.setContenidoCampoExtra(dto.getContenidoCampoExtra());
            existente.setTituloCampoExtra2(dto.getTituloCampoExtra2());
            existente.setContenidoCampoExtra2(dto.getContenidoCampoExtra2());
        }   
    }

    /**
     * Servicio para Modelos
     */
    private class ModeloServicio extends ServicioGenerico<Modelo, ModeloDTO, Long> {

        private final IDaoModelo dao;

        public ModeloServicio() {
            super(new DaoModelo(), MapperModelo.converter, Modelo.class);
            this.dao = (IDaoModelo) super.dao;
        }

        public ModeloServicio(IDaoModelo dao) {
            super(dao, MapperModelo.converter, Modelo.class);
            this.dao = dao;
        }

        @Override
        protected void configurarEntityManager(EntityManager em) {
            if (dao != null) {
                dao.setEntityManager(em);
            }
        }

        @Override
        protected void validarNegocio(ModeloDTO dto, boolean esNuevo, EntityManager em) {
            if (dto.getNombre() == null || dto.getNombre().isBlank()) {
                throw new ReglaNegocioException("El nombre del modelo es obligatorio");
            }

            if (dto.getMarca() == null || dto.getMarca().isBlank()) {
                throw new ReglaNegocioException("La marca es obligatoria");
            }

            if (dto.getMemoriaRam() == null || dto.getMemoriaRam() <= 0) {
                throw new ReglaNegocioException("La memoria RAM debe ser mayor a 0");
            }

            if (dto.getAlmacenamiento() == null || dto.getAlmacenamiento() <= 0) {
                throw new ReglaNegocioException("El almacenamiento debe ser mayor a 0");
            }

            if (dto.getProcesador() == null || dto.getProcesador().isBlank()) {
                throw new ReglaNegocioException("El procesador es obligatorio");
            }

            if (esNuevo) {
                validarModeloDuplicado(dto, em);
            }
        }

        @Override
        protected Long extraerId(ModeloDTO dto) {
            return dto.getIdModelo();
        }

        @Override
        protected void validarEliminacion(Modelo entidad) {
            if (entidad.getEquipos() != null && !entidad.getEquipos().isEmpty()) {
                throw new ReglaNegocioException(
                        "No se puede eliminar el modelo porque tiene equipos asociados");
            }
        }

        private void validarModeloDuplicado(ModeloDTO dto, EntityManager em) {
            configurarEntityManager(em);

            boolean existe = ((DaoModelo) dao).existeModeloDuplicado(
                    dto.getMarca(),
                    dto.getNombre(),
                    dto.getMemoriaRam(),
                    dto.getAlmacenamiento(),
                    dto.getProcesador()
            );

            if (existe) {
                throw new ReglaNegocioException(
                        "Ya existe un modelo con las mismas características");
            }
        }

        /**
         * Método específico: Buscar modelos con filtros técnicos
         */
        public List<ModeloDTO> buscarConFiltros(String nombre, String marca, Integer memoriaRam, Integer almacenamiento, String procesador) {
            return ejecutarLectura(em -> {
                configurarEntityManager(em);

                List<Modelo> modelos = dao.busquedaConFiltros(
                        nombre != null ? nombre.trim() : null,
                        marca != null ? marca.trim() : null,
                        memoriaRam,
                        almacenamiento,
                        procesador != null ? procesador.trim() : null
                );

                return mapper.mapToDtoList(modelos);
            });
        }

        @Override
        public List<ModeloDTO> buscarConFiltro(String filtro) {
            return buscarConFiltros(filtro, null, null, null, null);
        }
    }
}
