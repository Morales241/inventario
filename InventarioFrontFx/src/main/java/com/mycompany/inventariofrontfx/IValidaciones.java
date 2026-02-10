package com.mycompany.inventariofrontfx;

/**
 *
 * @author JMorales
 */
public interface IValidaciones<T>  {
    
    public boolean validarFormulario();
    
    public T getDatosEntidad();
}
