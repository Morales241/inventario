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
    public List<EquipoAsignado> buscarPorUsuarioActivo(Long idUsuario);
    public boolean existeAsignacionActiva(Long idEquipo);
    public List<EquipoAsignado> buscarActivasPaginado(int pagina, int tamano);
    public long contarActivas();
    public List<EquipoAsignado> buscarConFiltroPaginado(String filtro, int pagina, int tamano);
    public long contarConFiltro(String filtro);
   
}
