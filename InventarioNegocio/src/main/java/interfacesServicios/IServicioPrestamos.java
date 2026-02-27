/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package interfacesServicios;

import Dtos.AsignacionDTO;
import java.util.List;

/**
 *
 * @author tacot
 */
public interface IServicioPrestamos {

    public void asignarEquipo(Long idEquipo, Long idUsuarios);

    public void devolverEquipo(Long idAsignacion);

    public List<AsignacionDTO> obtenerEquiposDeUsuarios(Long idUsuarios);
}
