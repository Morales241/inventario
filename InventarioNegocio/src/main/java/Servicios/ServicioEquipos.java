package Servicios;

import Dao.*;
import Dtos.*;
import Entidades.*;
import Enums.CondicionFisica;
import mapper.MapperModelo;
import Enums.EstadoEquipo;
import Enums.TipoEquipo;
import Interfaces.IDaoEquipoDeComputo;
import Interfaces.IDaoEquipoDeEscritorio;
import Interfaces.IDaoGenerico;
import Interfaces.IDaoModelo;
import Interfaces.IDaoMovil;
import Interfaces.IDaoOtroEquipo;
import Interfaces.IDaoSucursal;
import excepciones.NegocioException;
import excepciones.RecursoNoEncontradoException;
import excepciones.ReglaNegocioException;
import interfacesServicios.IServicioEquipos;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.stream.Collectors;
import mapper.MapperEquipos;

/**
 * Servicio de lógica de negocio para la gestión integral de equipos de cómputo.
 * <p>
 * Coordina las operaciones entre los diferentes tipos de hardware (Móviles,
 * Escritorio, Otros) y sus modelos asociados. Maneja la validación de
 * integridad de datos antes de la persistencia.
 * </p>
 */
public class ServicioEquipos extends ServicioBase implements IServicioEquipos {

    private final IDaoEquipoDeComputo daoGeneral;
    private final IDaoEquipoDeEscritorio daoEscritorio;
    private final IDaoMovil daoMovil;
    private final IDaoOtroEquipo daoOtro;
    private final IDaoSucursal daoSucursal;
    private final IDaoModelo daoModelo;

    public ServicioEquipos() {
        this.daoGeneral = new DaoEquipoDeComputo();
        this.daoEscritorio = new DaoEquipoDeEscritorio();
        this.daoMovil = new DaoMovil();
        this.daoOtro = new DaoOtroEquipo();
        this.daoSucursal = new DaoSucursal();
        this.daoModelo = new DaoModelo();
    }

    private void configurar(EntityManager em) {
        daoGeneral.setEntityManager(em);
        daoEscritorio.setEntityManager(em);
        daoMovil.setEntityManager(em);
        daoOtro.setEntityManager(em);
        daoSucursal.setEntityManager(em);
        daoModelo.setEntityManager(em);
    }

    @Override
    public List<EquipoBaseDTO> buscarEquipos(Integer gry, EstadoEquipo estado, String criterio) {

        return ejecutarLectura(em -> {
            configurar(em);

            List<EquipoDeComputo> lista
                    = daoGeneral.buscarConFiltros(gry, estado, criterio);

            return lista.stream()
                    .map(this::mapearPolimorfico)
                    .toList();
        });
    }

    @Override
    public EquipoBaseDTO obtenerPorId(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        return ejecutarLectura(em -> {
            configurar(em);

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
            configurar(em);

            EquipoDeComputo equipo = daoGeneral.buscarPorGry(gry);

            if (equipo == null) {
                throw new RecursoNoEncontradoException("Equipo no encontrado");
            }

            return mapearPolimorfico(equipo);
        });
    }

    @Override
    public EquipoEscritorioDTO guardarEscritorio(EquipoEscritorioDTO dto) {

        validarEquipo(dto);

        return ejecutarTransaccion(em -> {
            configurar(em);

            validarDuplicidadGry(dto.getGry(), dto.getIdEquipo());

            EquipoDeEscritorio entidad
                    = MapperEquipos.escritorio.mapToEntity(dto);

            setRelaciones(entidad, dto);

            entidad = persistir(entidad, dto.getIdEquipo(), daoEscritorio);

            return MapperEquipos.escritorio.mapToDto(entidad);
        });
    }

    @Override
    public MovilDTO guardarMovil(MovilDTO dto) {

        validarEquipo(dto);

        return ejecutarTransaccion(em -> {
            configurar(em);

            validarDuplicidadGry(dto.getGry(), dto.getIdEquipo());

            Movil entidad = MapperEquipos.movil.mapToEntity(dto);

            setRelaciones(entidad, dto);

            entidad = persistir(entidad, dto.getIdEquipo(), daoMovil);

            return MapperEquipos.movil.mapToDto(entidad);
        });
    }

    @Override
    public OtroEquipoDTO guardarOtro(OtroEquipoDTO dto) {

        validarEquipo(dto);

        return ejecutarTransaccion(em -> {
            configurar(em);

            validarDuplicidadGry(dto.getGry(), dto.getIdEquipo());

            OtroEquipo entidad = MapperEquipos.otro.mapToEntity(dto);

            setRelaciones(entidad, dto);

            entidad = persistir(entidad, dto.getIdEquipo(), daoOtro);

            return MapperEquipos.otro.mapToDto(entidad);
        });
    }

    @Override
    public void eliminarEquipo(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        ejecutarTransaccion(em -> {
            configurar(em);

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

    private void validarEquipo(EquipoBaseDTO dto) {

        if (dto == null) {
            throw new IllegalArgumentException("Datos inválidos");
        }

        if (dto.getGry() == null || dto.getGry() <= 0) {
            throw new ReglaNegocioException("El gry es obligatorio");
        }

        if (dto.getIdSucursal() == null) {
            throw new ReglaNegocioException("Debe seleccionar una sucursal");
        }

        if (dto.getIdModelo() == null) {
            throw new ReglaNegocioException("Debe seleccionar un modelo");
        }
    }

    private void validarDuplicidadGry(Integer gry, Long idActual) {

        EquipoDeComputo existente = daoGeneral.buscarPorGry(gry);

        if (existente != null) {
            if (idActual == null || !existente.getId().equals(idActual)) {
                throw new ReglaNegocioException(
                        "Ya existe un equipo con ese gry");
            }
        }
    }

    private void setRelaciones(EquipoDeComputo entidad, EquipoBaseDTO dto) {

        Sucursal sucursal
                = daoSucursal.buscarPorId(dto.getIdSucursal());

        if (sucursal == null) {
            throw new RecursoNoEncontradoException(
                    "Sucursal no encontrada");
        }

        Modelo modelo
                = daoModelo.buscarPorId(dto.getIdModelo());

        if (modelo == null) {
            throw new RecursoNoEncontradoException(
                    "Modelo no encontrado");
        }

        entidad.setSucursal(sucursal);
        entidad.setModelo(modelo);
    }

    private <T extends EquipoDeComputo> T persistir(T entidad, Long id, IDaoGenerico<T, Long> dao) {

        if (id != null && id > 0) {

            T existente = dao.buscarPorId(id);

            if (existente == null) {
                throw new RecursoNoEncontradoException(
                        "Equipo no encontrado para actualizar");
            }

            // Respetar estado actual (no permitir cambiarlo libremente)
            entidad.setEstado(existente.getEstado());

            return dao.actualizar(entidad);
        }

        entidad.setEstado(EstadoEquipo.EN_STOCK);

        return dao.guardar(entidad);
    }

    private EquipoBaseDTO mapearPolimorfico(EquipoDeComputo equipo) {

        if (equipo instanceof EquipoDeEscritorio e) {
            return MapperEquipos.escritorio.mapToDto(e);
        }

        if (equipo instanceof Movil m) {
            return MapperEquipos.movil.mapToDto(m);
        }

        return MapperEquipos.otro.mapToDto((OtroEquipo) equipo);
    }

    @Override
    public ModeloDTO guardarModelo(ModeloDTO dto) {

        validarModelo(dto);

        return ejecutarTransaccion(em -> {
            configurar(em);

            Modelo entidad = MapperModelo.converter.mapToEntity(dto);

            if (dto.getIdModelo() != null && dto.getIdModelo() > 0) {

                Modelo existente = daoModelo.buscarPorId(dto.getIdModelo());

                if (existente == null) {
                    throw new NegocioException("Modelo no encontrado para actualizar");
                }

                entidad = daoModelo.actualizar(entidad);

            } else {
                entidad = daoModelo.guardar(entidad);
            }

            return MapperModelo.converter.mapToDto(entidad);
        });
    }

    private void validarModelo(ModeloDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Datos inválidos");
        }

        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new NegocioException("El nombre del modelo es obligatorio");
        }

        if (dto.getMarca() == null || dto.getMarca().isBlank()) {
            throw new NegocioException("La marca es obligatoria");
        }

        if (dto.getMemoriaRam() == null || dto.getMemoriaRam() <= 0) {
            throw new NegocioException("La memoria RAM debe ser mayor a 0");
        }

        if (dto.getAlmacenamiento() == null || dto.getAlmacenamiento() <= 0) {
            throw new NegocioException("El almacenamiento debe ser mayor a 0");
        }

        if (dto.getProcesador() == null || dto.getProcesador().isBlank()) {
            throw new NegocioException("El procesador es obligatorio");
        }
    }

    @Override
    public List<ModeloDTO> listarModelos() {

        return ejecutarLectura(em -> {
            configurar(em);
            return MapperModelo.converter.mapToDtoList(
                    daoModelo.buscarTodos());
        });
    }

    @Override
    public ModeloDTO buscarModeloPorId(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        return ejecutarLectura(em -> {
            configurar(em);

            Modelo modelo = daoModelo.buscarPorId(id);

            if (modelo == null) {
                throw new NegocioException("Modelo no encontrado");
            }

            return MapperModelo.converter.mapToDto(modelo);
        });
    }

    @Override
    public List<ModeloDTO> buscarModelosConFiltros(
            String nombre,
            String marca,
            Integer memoriaRam,
            Integer almacenamiento,
            String procesador) {

        return ejecutarLectura(em -> {
            configurar(em);

            List<Modelo> modelos = daoModelo.busquedaConFiltros(
                    nombre != null ? nombre.trim() : null,
                    marca != null ? marca.trim() : null,
                    memoriaRam,
                    almacenamiento,
                    procesador != null ? procesador.trim() : null
            );

            return MapperModelo.converter.mapToDtoList(modelos);
        });
    }

    @Override
    public List<EquipoBaseDTO> buscarConFiltros(String texto, TipoEquipo tipo, CondicionFisica condicion, EstadoEquipo estado) {
        
        return ejecutarLectura(em -> {
            configurar(em);

            List<EquipoDeComputo> equipos = daoGeneral.buscarConFiltros(texto, tipo, condicion, estado);
            return equipos.stream()
                    .map(equipo -> MapperEquipos.mapCommonToDto(equipo, new EquipoBaseDTO())).collect(Collectors.toList());
        });
    }
}
