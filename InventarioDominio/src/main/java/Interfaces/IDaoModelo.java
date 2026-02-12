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
public interface IDaoModelo extends IDaoGenerico<Modelo, Long> {

    public Modelo busquedaEspecifica(String nombre);

    public List<Modelo> busquedaConFiltros(String nombre, String marca, Integer memoriaRam, Integer almacenamiento, String procesador);
}
