package Implementaciones;

import Dtos.AsignacionDTO;
import Entidades.Usuario;
import InterfacesFachada.IFachadaPrestamos;
import Servicios.ServicioPrestamos;
import interfacesServicios.IServicioPrestamos;
import java.util.List;

/**
 * Implementación de la fachada para operaciones de asignación y devolución de equipos.
 * <p>
 * Proporciona acceso simplificado a la lógica de negocio del servicio de asignaciones.
 * Gestiona el ciclo de vida de los préstamos de equipos a usuarios.
 * Utiliza el patrón Facade para abstraer la complejidad de las operaciones.
 * </p>
 * @author JMorales
 */
public class FachadaPrestamos implements IFachadaPrestamos {

    private final IServicioPrestamos servicioPrestamos;

    public FachadaPrestamos(IServicioPrestamos servicioPrestamos) {
        this.servicioPrestamos = servicioPrestamos;
    }

    public FachadaPrestamos() {
        this(new ServicioPrestamos());
    }

    
    @Override
    public void asignarEquipo(Long idEquipo, Long idUsuario) {
        servicioPrestamos.asignarEquipo(idEquipo, idUsuario);
    }

    @Override
    public void devolverEquipo(Long idAsignacion) {
        servicioPrestamos.devolverEquipo(idAsignacion);
    }

    @Override
    public List<AsignacionDTO> obtenerEquiposDeUsuarios(Long idUsuario) {
        return servicioPrestamos.obtenerEquiposDeUsuarios(idUsuario);
    }
    
    @Override
    public List<AsignacionDTO> obtenerTodasLasAsignacionesActivas() {
        return servicioPrestamos.obtenerTodasLasAsignacionesActivas();
    }

    @Override
    public List<AsignacionDTO> obtenerHistorialUsuario(Long idUsuario) {
        return servicioPrestamos.obtenerHistorialUsuario(idUsuario);
    }

    @Override
    public List<AsignacionDTO> obtenerHistorialEquipo(Long idEquipo) {
        return servicioPrestamos.obtenerHistorialEquipo(idEquipo);
    }

    @Override
    public boolean equipoEstaAsignado(Long idEquipo) {
        return servicioPrestamos.equipoEstaAsignado(idEquipo);
    }

    @Override
    public Usuario obtenerUsuarioActualDeEquipo(Long idEquipo) {
        return servicioPrestamos.obtenerUsuarioActualDeEquipo(idEquipo);
    }

    @Override
    public List<AsignacionDTO> buscarAsignaciones(String filtro) {
        return servicioPrestamos.buscarAsignaciones(filtro);
    }

    @Override
    public List<AsignacionDTO> listarAsignaciones() {
        return servicioPrestamos.listarAsignaciones();
    }

    @Override
    public List<AsignacionDTO> obtenerAsignacionesActivasPorEquipo(Long idEquipo) {
        return servicioPrestamos.obtenerAsignacionesActivasPorEquipo(idEquipo);
    }

    @Override
    public List<AsignacionDTO> obtenerAsignacionesPorRangoFechas(String fechaInicio, String fechaFin) {
        return servicioPrestamos.obtenerAsignacionesPorRangoFechas(fechaInicio, fechaFin);
    }

    @Override
    public int contarEquiposAsignadosAUsuario(Long idUsuario) {
        return servicioPrestamos.contarEquiposAsignadosAUsuario(idUsuario);
    }

    @Override
    public AsignacionDTO obtenerUltimaAsignacionDeEquipo(Long idEquipo) {
        return servicioPrestamos.obtenerUltimaAsignacionDeEquipo(idEquipo);
    }

    @Override
    public boolean usuarioPuedeRecibirMasEquipos(Long idUsuario, int limiteMaximo) {
        return servicioPrestamos.usuarioPuedeRecibirMasEquipos(idUsuario, limiteMaximo);
    }
    
    @Override
    public List<AsignacionDTO> buscarAsignacionesPaginado(String filtro, int pagina, int tamano) {
        return servicioPrestamos.buscarAsignacionesPaginado(filtro, pagina, tamano);
    }
 
    
    @Override
    public long contarAsignaciones(String filtro) {
        return servicioPrestamos.contarAsignaciones(filtro);
    }
}