package Interfaces;

import Entidades.CuentaSistema;

/**
 *
 * @author JMorales
 */
public interface IDaoCuentaSistema extends IDaoGenerico<CuentaSistema, Long>{
    public CuentaSistema busquedaEspecifica(String username);
    
    public CuentaSistema login(String username, String password);
}
