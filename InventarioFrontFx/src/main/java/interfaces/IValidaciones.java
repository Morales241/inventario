package interfaces;

import Dtos.EquipoBaseDTO;

/**
 *
 * @author JMorales
 */
public interface IValidaciones<T>  {
    
    public boolean validarFormulario();
    
    public <T extends EquipoBaseDTO> T getDatosEntidad();
    
    public <T extends EquipoBaseDTO>void cargarEquipoParaEditar(T dto);
}
