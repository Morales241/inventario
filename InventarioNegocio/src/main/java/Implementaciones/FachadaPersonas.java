package Implementaciones;

import Dtos.UsuarioDTO;
import Dtos.CuentaSistemaDTO;
import InterfacesFachada.IFachadaPersonas;
import Servicios.ServicioPersonas;
import interfacesServicios.IServicioPersonas;
import java.util.List;

/**
 * Implementación de la fachada para operaciones de gestión de personas.
 * <p>
 * Proporciona acceso simplificado a la lógica de negocio del servicio de personas 
 * (usuarios y cuentas de sistema). Utiliza el patrón Facade para abstraer la 
 * complejidad de las operaciones.
 * </p>
 * @author JMorales
 */
public class FachadaPersonas implements IFachadaPersonas {

    private final IServicioPersonas servicioPersonas;

    public FachadaPersonas(IServicioPersonas servicioPersonas) {
        this.servicioPersonas = servicioPersonas;
    }

    public FachadaPersonas() {
        this(new ServicioPersonas());
    }

    // CUENTAS DE SISTEMA
    
    @Override
    public CuentaSistemaDTO login(String user, String pass) {
        return servicioPersonas.login(user, pass);
    }

    @Override
    public CuentaSistemaDTO buscarPorUsername(String username) {
        return servicioPersonas.buscarPorUsername(username);
    }

    @Override
    public CuentaSistemaDTO guardarCuentaSistema(CuentaSistemaDTO dto) {
        return servicioPersonas.guardarCuentaSistema(dto);
    }

    @Override
    public void eliminarCuentaSistema(Long id) {
        servicioPersonas.eliminarCuentaSistema(id);
    }

    @Override
    public List<CuentaSistemaDTO> listarCuentasSistema() {
        return servicioPersonas.listarCuentasSistema();
    }

    @Override
    public List<CuentaSistemaDTO> buscarCuentasSistema(String filtro) {
        return servicioPersonas.buscarCuentasSistema(filtro);
    }

    // USUARIOS
    
    @Override
    public List<UsuarioDTO> buscarUsuarios(String busquedaGlobal) {
        return servicioPersonas.buscarUsuarios(busquedaGlobal);
    }

    @Override
    public UsuarioDTO obtenerUsuario(Long id) {
        return servicioPersonas.obtenerUsuario(id);
    }

    @Override
    public void guardarUsuario(UsuarioDTO dto) {
        servicioPersonas.guardarUsuario(dto);
    }

    @Override
    public void cambiarEstadoUsuario(Long id, boolean activo) {
        servicioPersonas.cambiarEstadoUsuario(id, activo);
    }

    @Override
    public List<UsuarioDTO> listarUsuariosActivos() {
        return servicioPersonas.listarUsuariosActivos();
    }

    @Override
    public List<UsuarioDTO> buscarUsuariosPorDepartamento(Long idDepartamento) {
        return servicioPersonas.buscarUsuariosPorDepartamento(idDepartamento);
    }

    @Override
    public List<UsuarioDTO> buscarUsuariosPorPuesto(Long idPuesto) {
        return servicioPersonas.buscarUsuariosPorPuesto(idPuesto);
    }

    @Override
    public boolean usuarioTieneEquiposAsignados(Long idUsuario) {
        return servicioPersonas.usuarioTieneEquiposAsignados(idUsuario);
    }
}