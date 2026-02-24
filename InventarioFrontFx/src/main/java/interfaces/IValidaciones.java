package interfaces;

import Dtos.EquipoBaseDTO;

/**
 *
 * @author JMorales
 */
public interface IValidaciones<T>  {
    
    public boolean validarFormulario();
    
    public T getDatosEntidad();
    
    public <T extends EquipoBaseDTO>void cargarEquipoParaEditar(T dto);
}
