package interfaces;

import Dtos.EquipoBaseDTO;

/**
 *
 * @author JMorales
 */
public interface ControllerInventario extends BaseController{
    
    public ControllerInventario cambiarPantalla(String rutaFXML);
    
    public void limpiarFormulario();
    
    public <T extends EquipoBaseDTO>void cargarEquipoParaEditar(T equipo);
}
