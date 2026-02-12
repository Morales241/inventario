package Servicios;

import Dao.*;
import Dtos.*;
import Entidades.*;
import Mappers.MapperModelo;
import Enums.EstadoEquipo;
import excepciones.NegocioException;
import excepciones.RecursoNoEncontradoException;
import excepciones.ReglaNegocioException;
import interfacesServicios.IServicioEquipos;
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
public class ServicioEquipos implements IServicioEquipos {

    private final DaoEquipoDeComputo daoGeneral;
    private final DaoEquipoDeEscritorio daoEscritorio;
    private final DaoMovil daoMovil;
    private final DaoOtroEquipo daoOtro;
    private final DaoSucursal daoSucursal;
    private final DaoModelo daoModelo;

    public ServicioEquipos() {
        this.daoGeneral = new DaoEquipoDeComputo();
        this.daoEscritorio = new DaoEquipoDeEscritorio();
        this.daoMovil = new DaoMovil();
        this.daoOtro = new DaoOtroEquipo();
        this.daoSucursal = new DaoSucursal();
        this.daoModelo = new DaoModelo();
    }

    @Override
    public List<EquipoBaseDTO> buscarEquipos(Integer gry, EstadoEquipo estado, String criterio) {

        List<EquipoDeComputo> lista
                = daoGeneral.buscarConFiltros(gry, estado, criterio);

        return lista.stream()
                .map(this::mapearPolimorfico)
                .toList();
    }

    @Override
    public EquipoBaseDTO obtenerPorId(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        EquipoDeComputo equipo = daoGeneral.buscarPorId(id);

        if (equipo == null) {
            throw new RecursoNoEncontradoException("Equipo no encontrado");
        }

        return mapearPolimorfico(equipo);
    }

    @Override
    public EquipoBaseDTO buscarPorGry(Integer gry) {
        if (gry == null) {
            throw new IllegalArgumentException("GRY inválido");
        }

        EquipoDeComputo equipo = daoGeneral.buscarPorGry(gry);

        if (equipo == null) {
            throw new RecursoNoEncontradoException("Equipo no encontrado");
        }

        return mapearPolimorfico(equipo);
    }

    @Override
    public EquipoEscritorioDTO guardarEscritorio(EquipoEscritorioDTO dto) {
        validarEquipo(dto);

        EquipoDeEscritorio entidad = MapperEquipos.escritorio.mapToEntity(dto);

        validarDuplicidadGry(dto.getGry(), dto.getIdEquipo());

        setRelaciones(entidad, dto);

        entidad = persistir(entidad, dto.getIdEquipo(), daoEscritorio);

        return MapperEquipos.escritorio.mapToDto(entidad);
    }

    @Override
    public MovilDTO guardarMovil(MovilDTO dto) {
        validarEquipo(dto);

        Movil entidad = MapperEquipos.movil.mapToEntity(dto);

        validarDuplicidadGry(dto.getGry(), dto.getIdEquipo());

        setRelaciones(entidad, dto);

        entidad = persistir(entidad, dto.getIdEquipo(), daoMovil);

        return MapperEquipos.movil.mapToDto(entidad);
    }

    @Override
    public OtroEquipoDTO guardarOtro(OtroEquipoDTO dto) {
        validarEquipo(dto);

        OtroEquipo entidad = MapperEquipos.otro.mapToEntity(dto);

        validarDuplicidadGry(dto.getGry(), dto.getIdEquipo());

        setRelaciones(entidad, dto);

        entidad = persistir(entidad, dto.getIdEquipo(), daoOtro);

        return MapperEquipos.otro.mapToDto(entidad);
    }

    @Override
    public void eliminarEquipo(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        EquipoDeComputo equipo = daoGeneral.buscarPorId(id);

        if (equipo == null) {
            throw new RecursoNoEncontradoException("Equipo no encontrado");
        }

        if (equipo.getEstado() == EstadoEquipo.ASIGNADO) {
            throw new ReglaNegocioException(
                    "No se puede eliminar un equipo asignado");
        }

        daoGeneral.eliminar(id);
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

    private void setRelaciones(EquipoDeComputo entidad,
            EquipoBaseDTO dto) {

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

    private <T extends EquipoDeComputo> T persistir(T entidad, Long id, DaoGenerico<T, Long> dao) {

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
    }

    @Override
    public List<ModeloDTO> listarModelos() {
        return MapperModelo.converter.mapToDtoList(
                daoModelo.buscarTodos()
        );
    }

    @Override
    public ModeloDTO buscarModeloPorId(Long id) {

        if (id == null) {
            throw new IllegalArgumentException("ID inválido");
        }

        Modelo modelo = daoModelo.buscarPorId(id);

        if (modelo == null) {
            throw new NegocioException("Modelo no encontrado");
        }

        return MapperModelo.converter.mapToDto(modelo);
    }

    @Override
    public List<ModeloDTO> buscarModelosConFiltros(
            String nombre,
            String marca,
            Integer memoriaRam,
            Integer almacenamiento,
            String procesador) {

        String nombreFiltro = (nombre != null) ? nombre.trim() : null;
        String marcaFiltro = (marca != null) ? marca.trim() : null;
        String procesadorFiltro = (procesador != null) ? procesador.trim() : null;

        if (memoriaRam != null && memoriaRam <= 0) {
            throw new NegocioException("La memoria RAM debe ser mayor a 0");
        }

        if (almacenamiento != null && almacenamiento <= 0) {
            throw new NegocioException("El almacenamiento debe ser mayor a 0");
        }

        List<Modelo> modelos = daoModelo.busquedaConFiltros(nombreFiltro,
                marcaFiltro,
                memoriaRam,
                almacenamiento,
                procesadorFiltro
        );

        return MapperModelo.converter.mapToDtoList(modelos);
    }
}
