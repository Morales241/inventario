package Interfaces;

import Entidades.EquipoDeComputo;
import Enums.CondicionFisica;
import Enums.EstadoEquipo;
import Enums.TipoEquipo;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IDaoEquipoDeComputo extends IDaoGenerico<EquipoDeComputo, Long>{
    public List<EquipoDeComputo> buscarConFiltros(Integer gri, EstadoEquipo estado, String busquedaModelo);
    public EquipoDeComputo buscarPorGry(Integer gry);
    public List<EquipoDeComputo> buscarConFiltros(String texto, TipoEquipo tipo, CondicionFisica condicion, EstadoEquipo estado); 
}
