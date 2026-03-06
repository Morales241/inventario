// InventarioNegocio/src/main/java/interfacesServicios/IServicioEquipos.java
package interfacesServicios;

import Dtos.EquipoBaseDTO;
import Dtos.EquipoEscritorioDTO;
import Dtos.ModeloDTO;
import Dtos.MovilDTO;
import Dtos.OtroEquipoDTO;
import Enums.CondicionFisica;
import Enums.EstadoEquipo;
import Enums.TipoEquipo;
import java.util.List;

/**
 * Interfaz para el servicio de gestión integral de equipos de cómputo.
 * Define operaciones para diferentes tipos de equipos y sus modelos asociados.
 */
public interface IServicioEquipos {
   
    /**
     * Busca equipos aplicando filtros dinámicos.
     * @param gry Identificador único (GRY) del equipo, puede ser nulo
     * @param estado Estado del equipo (EN_STOCK, ASIGNADO, etc.), puede ser nulo
     * @param criterio Texto para búsqueda parcial en marca/modelo
     * @return Lista de EquipoBaseDTO con los equipos que coinciden
     */
    List<EquipoBaseDTO> buscarEquipos(Integer gry, EstadoEquipo estado, String criterio);
    
    /**
     * Obtiene los datos completos de un equipo por su ID.
     * @param id Identificador único del equipo
     * @return EquipoBaseDTO con la información del equipo
     */
    EquipoBaseDTO obtenerPorId(Long id);
    
    /**
     * Busca un equipo por su número GRY.
     * @param gry Identificador GRY del equipo
     * @return EquipoBaseDTO con la información del equipo
     */
    EquipoBaseDTO buscarPorGry(Integer gry);
    
    /**
     * Busca un equipo por su ID y lo retorna con su tipo específico.
     * @param <T> Tipo específico del equipo (Escritorio, Móvil, Otro)
     * @param id Identificador del equipo
     * @return Equipo del tipo específico
     */
    <T extends EquipoBaseDTO> T buscarPorId(Long id);
    
    /**
     * Busca equipos aplicando múltiples filtros avanzados.
     * @param texto Filtro por GRY o modelo
     * @param tipo Filtro por tipo de equipo
     * @param condicion Filtro por condición física
     * @param estado Filtro por estado del equipo
     * @return Lista de equipos filtrados
     */
    List<EquipoBaseDTO> buscarConFiltros(String texto, TipoEquipo tipo, 
                                         CondicionFisica condicion, EstadoEquipo estado);
    
    /**
     * Elimina un equipo si está disponible (no asignado).
     * @param id Identificador del equipo a eliminar
     */
    void eliminarEquipo(Long id);
   
    /**
     * Guarda o actualiza un equipo de escritorio.
     * @param dto Datos del equipo de escritorio
     * @return EquipoEscritorioDTO persistido con ID asignado
     */
    EquipoEscritorioDTO guardarEscritorio(EquipoEscritorioDTO dto);
    
    /**
     * Guarda o actualiza un equipo móvil.
     * @param dto Datos del equipo móvil (teléfono, tablet)
     * @return MovilDTO persistido con ID asignado
     */
    MovilDTO guardarMovil(MovilDTO dto);
    
    /**
     * Guarda o actualiza otro tipo de equipo.
     * @param dto Datos del otro equipo (accesorios, periféricos)
     * @return OtroEquipoDTO persistido con ID asignado
     */
    OtroEquipoDTO guardarOtro(OtroEquipoDTO dto);
    
    /**
     * Guarda o actualiza un modelo de equipo en el catálogo.
     * @param dto Datos del modelo (marca, RAM, almacenamiento, procesador)
     * @return ModeloDTO persistido con ID asignado
     */
    ModeloDTO guardarModelo(ModeloDTO dto);
    
    /**
     * Lista todos los modelos de equipos del catálogo.
     * @return Lista de ModeloDTO disponibles
     */
    List<ModeloDTO> listarModelos();
    
    /**
     * Busca un modelo específico por su ID.
     * @param id Identificador del modelo
     * @return ModeloDTO con los detalles técnicos del modelo
     */
    ModeloDTO buscarModeloPorId(Long id);
    
    /**
     * Busca modelos aplicando filtros técnicos.
     * @param nombre Nombre del modelo (búsqueda parcial)
     * @param marca Marca del fabricante (búsqueda parcial)
     * @param memoriaRam Capacidad de RAM en GB
     * @param almacenamiento Capacidad de almacenamiento en GB
     * @param procesador Tipo de procesador
     * @return Lista de ModeloDTO que cumplen los criterios
     */
    List<ModeloDTO> buscarModelosConFiltros(String nombre, String marca,
                                            Integer memoriaRam, Integer almacenamiento,
                                            String procesador);
}