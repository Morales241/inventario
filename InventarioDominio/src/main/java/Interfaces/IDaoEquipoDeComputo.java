package Interfaces;

import Entidades.EquipoDeComputo;
import Enums.EstadoEquipo;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IDaoEquipoDeComputo extends IDaoGenerico<EquipoDeComputo, Long>{
    public List<EquipoDeComputo> buscarConFiltros(Integer gri, EstadoEquipo estado, String busquedaModelo);
}
