package interfacesServicios;

import Dtos.TrabajadorDTO;
import Dtos.UsuarioDTO;
import java.util.List;

/**
 *
 * @author tacot
 */
public interface IServicioPersonas {
    public UsuarioDTO login(String username, String password);
    
    public List<TrabajadorDTO> buscarTrabajadores(String criterioGlobal);

    public TrabajadorDTO obtenerTrabajador(Long id);

    public TrabajadorDTO guardarTrabajador(TrabajadorDTO dto);

    public void cambiarEstadoTrabajador(Long id, boolean activo);
}
