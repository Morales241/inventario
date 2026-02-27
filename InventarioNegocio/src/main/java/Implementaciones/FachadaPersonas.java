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
 * Proporciona acceso simplificado a la lógica de negocio del servicio de personas (usuarios y Usuarios).
 * Utiliza el patrón Facade para abstraer la complejidad de las operaciones.
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

    @Override
    public CuentaSistemaDTO login(String user, String pass) {
        return servicioPersonas.login(user, pass);
    }

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
}

