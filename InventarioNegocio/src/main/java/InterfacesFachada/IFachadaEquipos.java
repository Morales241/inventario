package InterfacesFachada;

import Dtos.AsignacionDTO;
import Dtos.EquipoBaseDTO;
import Dtos.EquipoEscritorioDTO;
import Dtos.ModeloDTO;
import Dtos.MovilDTO;
import Dtos.OtroEquipoDTO;
import Enums.EstadoEquipo;
import java.util.List;

/**
 * Contrato de fachada para operaciones de gestión de equipos de cómputo.
 * <p>
 * Define los métodos públicos para buscar, guardar, eliminar equipos y gestionar el catálogo de modelos.
 * Implementa el patrón Facade para simplificar la interacción con la capa de servicios de equipos.
 * </p>
 * @author JMorales
 */
public interface IFachadaEquipos {
    
    /**
     * Busca equipos aplicando filtros dinámicos (GRI, estado, criterio).
     * @param gri Identificador único (GRI) del equipo, puede ser nulo.
     * @param estado {@link EstadoEquipo} (DISPONIBLE, ASIGNADO), puede ser nulo.
     * @param criterioBusqueda Texto para búsqueda parcial en marca/modelo.
     * @return Lista de EquipoBaseDTO con los equipos que coinciden.
     */
    public List<EquipoBaseDTO> buscarEquipos(Integer gri, EstadoEquipo estado, String criterioBusqueda);
    
    /**
     * Obtiene los datos completos de un equipo para edición o visualización detallada.
     * @param id Identificador único del equipo.
     * @return EquipoBaseDTO con la información del equipo.
     * @throws Exception Si el equipo no existe.
     */
    public EquipoBaseDTO obtenerEquipoPorId(Long id) throws Exception;
    
    /**
     * Obtiene los datos completos de un equipo para edición o visualización detallada.
     * @param gry Identificador único del equipo.
     * @return EquipoBaseDTO con la información del equipo.
     * @throws Exception Si el equipo no existe.
     */
    public EquipoBaseDTO buscarPorGry(Integer gry) throws Exception;
    
    /**
     * Guarda o actualiza un equipo de escritorio.
     * @param dto Datos del equipo de escritorio.
     * @return EquipoEscritorioDTO persistido con ID asignado.
     * @throws Exception Si hay validación fallida o error en BD.
     */
    public EquipoEscritorioDTO guardarEscritorio(EquipoEscritorioDTO dto) throws Exception;
    
    /**
     * Guarda o actualiza un equipo móvil.
     * @param dto Datos del equipo móvil (teléfono, tablet).
     * @return MovilDTO persistido con ID asignado.
     * @throws Exception Si hay validación fallida o error en BD.
     */
    public MovilDTO guardarMovil(MovilDTO dto) throws Exception;
    
    /**
     * Guarda o actualiza otro tipo de equipo.
     * @param dto Datos del otro equipo (accesorios, periféricos).
     * @return OtroEquipoDTO persistido con ID asignado.
     * @throws Exception Si hay validación fallida o error en BD.
     */
    public OtroEquipoDTO guardarOtro(OtroEquipoDTO dto) throws Exception;
    
    /**
     * Elimina un equipo si está disponible (no asignado).
     * @param id Identificador del equipo a eliminar.
     * @throws Exception Si el equipo está asignado o no existe.
     */
    public void eliminarEquipo(Long id) throws Exception;
    
    /**
     * Lista todos los modelos de equipos del catálogo.
     * @return Lista de ModeloDTO disponibles.
     */
    public List<ModeloDTO> listarModelos();
    
    /**
     * Guarda o actualiza un modelo de equipo en el catálogo.
     * @param dto Datos del modelo (marca, RAM, almacenamiento, procesador).
     * @return ModeloDTO persistido con ID asignado.
     * @throws Exception Si el nombre está vacío o hay error en BD.
     */
    public ModeloDTO guardarModelo(ModeloDTO dto) throws Exception;
    
    /**
     * Busca modelos aplicando filtros técnicos (marca, RAM, almacenamiento, procesador).
     * @param marca Marca del fabricante (búsqueda parcial).
     * @param memoriaRam Capacidad de RAM.
     * @param almacenamiento Capacidad de disco.
     * @param procesador Tipo de procesador.
     * @return Lista de ModeloDTO que cumplen los criterios.
     */
    public List<ModeloDTO> buscarModelosConFiltros(
            String nombre,
            String marca,
            Integer memoriaRam,
            Integer almacenamiento,
            String procesador);    
    
    /**
     * Obtiene un modelo específico por su ID.
     * @param id Identificador del modelo.
     * @return ModeloDTO con los detalles técnicos del modelo.
     */
    public ModeloDTO buscarModeloPorId(Long id);
}
