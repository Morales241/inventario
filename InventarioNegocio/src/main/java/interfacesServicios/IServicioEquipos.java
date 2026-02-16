package interfacesServicios;

import Dtos.EquipoBaseDTO;
import Dtos.EquipoEscritorioDTO;
import Dtos.ModeloDTO;
import Dtos.MovilDTO;
import Dtos.OtroEquipoDTO;
import Entidades.EquipoDeComputo;
import Enums.CondicionFisica;
import Enums.EstadoEquipo;
import Enums.TipoEquipo;
import java.util.List;

/**
 *
 * @author tacot
 */
public interface IServicioEquipos {

    List<EquipoBaseDTO> buscarEquipos(Integer gry, EstadoEquipo estado, String criterio);

    EquipoBaseDTO obtenerPorId(Long id);

    public EquipoBaseDTO buscarPorGry(Integer gry);

    EquipoEscritorioDTO guardarEscritorio(EquipoEscritorioDTO dto);

    MovilDTO guardarMovil(MovilDTO dto);

    OtroEquipoDTO guardarOtro(OtroEquipoDTO dto);

    void eliminarEquipo(Long id);

    ModeloDTO guardarModelo(ModeloDTO dto);

    List<ModeloDTO> listarModelos();

    ModeloDTO buscarModeloPorId(Long id);

    public List<ModeloDTO> buscarModelosConFiltros(
            String nombre,
            String marca,
            Integer memoriaRam,
            Integer almacenamiento,
            String procesador);

    public List<EquipoBaseDTO> buscarConFiltros(String texto, TipoEquipo tipo, CondicionFisica condicion, EstadoEquipo estado);
}
