package Implementaciones;

import Dtos.TrabajadorDto;
import Dtos.UsuarioDto;
import Interfaces.IFachadaPersonas;
import Servicios.ServicioPersonas;
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

    private final ServicioPersonas servicioPersonas;

    public FachadaPersonas() {
        this.servicioPersonas = new ServicioPersonas();
    }
    
    @Override
    public UsuarioDto login(String user, String pass) throws Exception {
        return servicioPersonas.login(user, pass);
    }

    @Override
    public List<TrabajadorDto> buscarTrabajadores(String busquedaGlobal) {
        return servicioPersonas.buscarTrabajadores(busquedaGlobal);
    }

    @Override
    public TrabajadorDto obtenerTrabajador(Long id) {
        return servicioPersonas.obtenerTrabajador(id);
    }

    @Override
    public void guardarTrabajador(TrabajadorDto dto) throws Exception {
        servicioPersonas.guardarTrabajador(dto);
    }

    @Override
    public void cambiarEstadoTrabajador(Long id, boolean activo) throws Exception{
        servicioPersonas.cambiarEstadoTrabajador(id, activo);
    }
    
}
