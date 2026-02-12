package Implementaciones;

import Dtos.TrabajadorDTO;
import Dtos.UsuarioDTO;
import InterfacesFachada.IFachadaPersonas;
import Servicios.ServicioPersonas;
import interfacesServicios.IServicioPersonas;
import java.util.List;

/**
 * Implementación de la fachada para operaciones de gestión de personas.
 * <p>
 * Proporciona acceso simplificado a la lógica de negocio del servicio de personas (usuarios y trabajadores).
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
    public UsuarioDTO login(String user, String pass) {
        return servicioPersonas.login(user, pass);
    }

    @Override
    public List<TrabajadorDTO> buscarTrabajadores(String busquedaGlobal) {
        return servicioPersonas.buscarTrabajadores(busquedaGlobal);
    }

    @Override
    public TrabajadorDTO obtenerTrabajador(Long id) {
        return servicioPersonas.obtenerTrabajador(id);
    }

    @Override
    public void guardarTrabajador(TrabajadorDTO dto) {
        servicioPersonas.guardarTrabajador(dto);
    }

    @Override
    public void cambiarEstadoTrabajador(Long id, boolean activo) {
        servicioPersonas.cambiarEstadoTrabajador(id, activo);
    }
}

