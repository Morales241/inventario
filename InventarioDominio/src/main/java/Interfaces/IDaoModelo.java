/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interfaces;

import Entidades.Modelo;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IDaoModelo {
    public Modelo busquedaEspecifica(String nombre);
    
    public List<Modelo> busquedaConFiltros(String marca, String memoriaRam, String almacenamiento, String procesador);
}
