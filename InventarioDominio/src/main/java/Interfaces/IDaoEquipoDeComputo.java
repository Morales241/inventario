/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interfaces;

import Entidades.EquipoDeComputo;
import Enums.EstadoEquipo;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IDaoEquipoDeComputo {
    public List<EquipoDeComputo> buscarConFiltros(Integer gri, Long idSucursal, EstadoEquipo estado, String busquedaModelo);
}
