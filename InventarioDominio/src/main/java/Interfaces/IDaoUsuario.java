package Interfaces;

import Entidades.UsuarioSistema;

/**
 *
 * @author JMorales
 */
public interface IDaoUsuario {
    public UsuarioSistema busquedaEspecifica(String username);
    
    public UsuarioSistema Login(String username, String password);
}
