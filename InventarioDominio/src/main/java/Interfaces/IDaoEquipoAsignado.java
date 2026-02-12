/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package Interfaces;

import Entidades.EquipoAsignado;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IDaoEquipoAsignado extends IDaoGenerico<EquipoAsignado, Long>{
    public List<EquipoAsignado> buscarPorTrabajadorActivo(Long idTrabajador);
    public boolean existeAsignacionActiva(Long idEquipo);
}
