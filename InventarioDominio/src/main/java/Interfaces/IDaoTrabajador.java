/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interfaces;

import Entidades.Trabajador;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IDaoTrabajador {
    public Trabajador busquedaEspecifica(String nombre, String nomina);
    
    public List<Trabajador> busquedaConFiltros(String cadena, String numero, String puesto);
}
