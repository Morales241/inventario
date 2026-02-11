package Servicios;

import Dao.*;
import Dtos.*;
import Entidades.*;
import Mappers.MapperEquipos;
import Mappers.MapperModelo;
import Enums.EstadoEquipo;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio de lógica de negocio para la gestión integral de equipos de cómputo.
 * <p>
 * Coordina las operaciones entre los diferentes tipos de hardware (Móviles, Escritorio, Otros)
 * y sus modelos asociados. Maneja la validación de integridad de datos antes de la persistencia.
 * </p>
 */
public class ServicioEquipos {

    private final DaoEquipoDeComputo daoGeneral;
    private final DaoEquipoDeEscritorio daoEscritorio;
    private final DaoMovil daoMovil;
    private final DaoOtroEquipo daoOtro;
    private final DaoModelo daoModelo;

    public ServicioEquipos() {
        daoModelo = new DaoModelo();
        daoOtro = new DaoOtroEquipo();
        daoEscritorio = new DaoEquipoDeEscritorio();
        daoGeneral = new DaoEquipoDeComputo();
        daoMovil = new DaoMovil();
    }

    
    
    /**
     * Busca equipos aplicando filtros dinámicos y polimórficos.
     * <p>
     * <b>Lógica de resultados:</b>
     * <ul>
     * <li>Si {@code gri} es nulo, {@code estado} es nulo y {@code criterioBusqueda} está vacío, 
     * retorna <b>todos</b> los equipos del sistema.</li>
     * <li>El resultado es una lista de {@link EquipoBaseDTO}, donde cada objeto se mapea 
     * según su tipo real (Móvil, Escritorio, etc.) mediante el uso de {@code instanceof}.</li>
     * </ul>
     * </p>
     * @param gri Identificador interno (GRI) para búsqueda exacta.
     * @param estado Estado actual del equipo (DISPONIBLE, ASIGNADO, etc.).
     * @param criterioBusqueda Texto para buscar por marca o nombre del modelo (coincidencia parcial).
     * @return Lista de DTOs con la información técnica y administrativa de los equipos.
     */
    public List<EquipoBaseDTO> buscarEquipos(Integer gri, EstadoEquipo estado, String criterioBusqueda) {
        List<EquipoDeComputo> lista = daoGeneral.buscarConFiltros(gri, estado, criterioBusqueda);
        
        return lista.stream().map(equipo -> {
            if (equipo instanceof EquipoDeEscritorio) {
                return MapperEquipos.escritorio.mapToDto((EquipoDeEscritorio) equipo);
            } else if (equipo instanceof Movil) {
                return MapperEquipos.movil.mapToDto((Movil) equipo);
            } else {
                return MapperEquipos.otro.mapToDto((OtroEquipo) equipo);
            }
        }).collect(Collectors.toList());
    }

    /**
     * Obtiene un equipo específico por ID para RELLENAR EL FORMULARIO de edición.
     * Devuelve un Object que deberás castear en la vista (instanceof) 
     * o puedes hacer 3 métodos separados.
     */
    public EquipoBaseDTO obtenerEquipoPorId(Long id) throws Exception {
        EquipoDeComputo e = daoGeneral.buscarPorId(id);
        if (e == null) throw new Exception("Equipo no encontrado");

        if (e instanceof EquipoDeEscritorio) {
            return MapperEquipos.escritorio.mapToDto((EquipoDeEscritorio) e);
        } else if (e instanceof Movil) {
            return MapperEquipos.movil.mapToDto((Movil) e);
        } else {
            return MapperEquipos.otro.mapToDto((OtroEquipo) e);
        }
    }

    /**
     * Guarda o actualiza un equipo de escritorio en la base de datos.
     * <p>
     * <b>Flujo de ejecución:</b>
     * <ul>
     * <li>Valida que el GRI sea válido y que esté seleccionada una sucursal.</li>
     * <li>Si el ID del equipo es mayor a 0, realiza una <b>actualización</b>.</li>
     * <li>Si el ID es nulo o 0, realiza un <b>nuevo registro</b>.</li>
     * <li>Retorna el DTO persistido con su ID generado/actualizado.</li>
     * </ul>
     * </p>
     * @param dto Datos del equipo de escritorio a guardar/actualizar.
     * @return EquipoEscritorioDTO con la información persistida.
     * @throws Exception Si el GRI es inválido, la sucursal no está seleccionada, o hay error en BD.
     */
    public EquipoEscritorioDTO guardarEscritorio(EquipoEscritorioDTO dto) throws Exception {
        validarDatosComunes(dto);
        EquipoDeEscritorio entidad = MapperEquipos.escritorio.mapToEntity(dto);
        
        if (dto.getIdEquipo() != null && dto.getIdEquipo() > 0) {
            entidad = daoEscritorio.actualizar(entidad);
        } else {
            entidad = daoEscritorio.guardar(entidad);
        }
        
        return MapperEquipos.escritorio.mapToDto(entidad);
    }

    /**
     * Guarda o actualiza un equipo móvil (teléfono, tablet, etc.) en la base de datos.
     * <p>
     * <b>Flujo de ejecución:</b>
     * <ul>
     * <li>Valida que el GRI sea válido y que esté seleccionada una sucursal.</li>
     * <li>Si el ID del equipo es mayor a 0, realiza una <b>actualización</b>.</li>
     * <li>Si el ID es nulo o 0, realiza un <b>nuevo registro</b>.</li>
     * <li>Retorna el DTO persistido con su ID generado/actualizado.</li>
     * </ul>
     * </p>
     * @param dto Datos del equipo móvil a guardar/actualizar.
     * @return MovilDTO con la información persistida.
     * @throws Exception Si el GRI es inválido, la sucursal no está seleccionada, o hay error en BD.
     */
    public MovilDTO guardarMovil(MovilDTO dto) throws Exception {
        validarDatosComunes(dto);
        
        Movil entidad = MapperEquipos.movil.mapToEntity(dto);
        
        if (dto.getIdEquipo() != null && dto.getIdEquipo() > 0) {
            entidad = daoMovil.actualizar(entidad);
        } else {
            entidad = daoMovil.guardar(entidad);
        }
        
        return MapperEquipos.movil.mapToDto(entidad);
    }

    /**
     * Guarda o actualiza otro tipo de equipo (accesorios, periféricos, etc.) en la base de datos.
     * <p>
     * <b>Flujo de ejecución:</b>
     * <ul>
     * <li>Valida que el GRI sea válido y que esté seleccionada una sucursal.</li>
     * <li>Si el ID del equipo es mayor a 0, realiza una <b>actualización</b>.</li>
     * <li>Si el ID es nulo o 0, realiza un <b>nuevo registro</b>.</li>
     * <li>Retorna el DTO persistido con su ID generado/actualizado.</li>
     * </ul>
     * </p>
     * @param dto Datos del otro tipo de equipo a guardar/actualizar.
     * @return OtroEquipoDTO con la información persistida.
     * @throws Exception Si el GRI es inválido, la sucursal no está seleccionada, o hay error en BD.
     */
    public OtroEquipoDTO guardarOtro(OtroEquipoDTO dto) throws Exception {
        validarDatosComunes(dto);
        OtroEquipo entidad = MapperEquipos.otro.mapToEntity(dto);
        
        if (dto.getIdEquipo() != null && dto.getIdEquipo() > 0) {
            entidad = daoOtro.actualizar(entidad);
        } else {
            entidad = daoOtro.guardar(entidad);
        }
        
        return MapperEquipos.otro.mapToDto(entidad);
    }

    /**
     * Elimina un equipo del sistema si está disponible.
     * <p>
     * <b>Restricciones de negocio:</b>
     * <ul>
     * <li>No permite eliminar equipos en estado {@code ASIGNADO}.</li>
     * <li>Solo se pueden eliminar equipos en estado {@code DISPONIBLE}.</li>
     * </ul>
     * </p>
     * @param id ID del equipo a eliminar.
     * @throws Exception Si el equipo está asignado o no existe en la BD.
     */
    public void eliminarEquipo(Long id) throws Exception {
        EquipoDeComputo e = daoGeneral.buscarPorId(id);
        if (e.getEstado() == EstadoEquipo.ASIGNADO) {
            throw new Exception("No se puede eliminar un equipo que está actualmente asignado a un empleado.");
        }
        daoGeneral.eliminar(id);
    }

    /**
     * Lista los modelos de equipos disponibles en el catálogo, con filtro opcional.
     * <p>
     * <b>Lógica de búsqueda:</b>
     * <ul>
     * <li>Si {@code noSerie} está vacío o es nulo, retorna <b>todos</b> los modelos.</li>
     * <li>Si {@code noSerie} contiene texto, busca modelos que coincidan con el número de serie.</li>
     * </ul>
     * </p>
     * @param noSerie Número de serie para filtrar (búsqueda exacta), puede ser nulo/vacío.
     * @return Lista de ModeloDto con las configuraciones disponibles.
     */
    public List<ModeloDto> listarModelos(String noSerie) {
        if(noSerie != null && !noSerie.isEmpty()){
            return MapperModelo.converter.mapToDtoList((List<Modelo>) daoModelo.busquedaEspecifica(noSerie));
        }
        return MapperModelo.converter.mapToDtoList(daoModelo.buscarTodos());
    }
    
    /**
     * Registra o actualiza un modelo de hardware en el catálogo.
     * @param dto Datos del modelo a guardar.
     * @return El DTO del modelo persistido con su ID generado.
     * @throws Exception Si el nombre del modelo está vacío.
     */
    public ModeloDto guardarModelo(ModeloDto dto) throws Exception {
        if(dto.getNombre().isEmpty()) throw new Exception("El nombre del modelo es requerido");
        
        return MapperModelo.converter.mapToDto(daoModelo.guardar(MapperModelo.converter.mapToEntity(dto)));
    }

    private void validarDatosComunes(EquipoBaseDTO dto) throws Exception {
        if (dto.getGry() == null || dto.getGry() <= 0) throw new Exception("El GRI es obligatorio y válido");
        if (dto.getIdSucursal() == null) throw new Exception("Debe seleccionar una sucursal");
    }
    
    /**
     * Filtra el catálogo de modelos basándose en especificaciones técnicas.
     * <p>Retorna todos los modelos si los parámetros de filtrado se envían vacíos.</p>
     * @param marca Marca del fabricante.
     * @param memoriaRam Capacidad de RAM.
     * @param almacenamiento Capacidad de disco.
     * @param procesador Tipo de procesador.
     * @return Lista de modelos que cumplen con los criterios técnicos.
     */
    public List<ModeloDto> busquedaConFiltros(String marca, String memoriaRam, String almacenamiento, String procesador){
        
        return MapperModelo.converter.mapToDtoList(daoModelo.busquedaConFiltros(marca, memoriaRam, almacenamiento, procesador));
    }
    
    /**
     * Obtiene los datos de un modelo específico por su ID.
     * <p>Útil para cargar detalles técnicos de un modelo en formularios de edición.</p>
     * @param id Identificador único del modelo.
     * @return ModeloDto con la información técnica completa del modelo.
     */
    public ModeloDto busquedarModeloPorId(Long id){
        return MapperModelo.converter.mapToDto(daoModelo.buscarPorId(id));
    }
}