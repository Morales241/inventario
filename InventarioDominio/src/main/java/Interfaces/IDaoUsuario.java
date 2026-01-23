package Interfaces;

import Entidades.UsuarioSistema;

/**
 *
 * @author JMorales
 */
public interface IDaoUsuario extends IDaoGenerico<UsuarioSistema, Long>{
    public UsuarioSistema busquedaEspecifica(String username);
    
    public UsuarioSistema login(String username, String password);
}
