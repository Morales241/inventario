/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interfaces;

import Entidades.Usuario;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IDaoUsuario extends IDaoGenerico<Usuario, Long>{
    public Usuario busquedaEspecifica(String nombre, String nomina);
    
    public List<Usuario> busquedaConFiltros(String cadena, String numero, String puesto);
}
