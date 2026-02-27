package interfacesServicios;

import Dtos.UsuarioDTO;
import Dtos.CuentaSistemaDTO;
import java.util.List;

/**
 *
 * @author tacot
 */
public interface IServicioPersonas {
    public CuentaSistemaDTO login(String username, String password);
    
    public List<UsuarioDTO> buscarUsuarios(String criterioGlobal);

    public UsuarioDTO obtenerUsuario(Long id);

    public UsuarioDTO guardarUsuario(UsuarioDTO dto);

    public void cambiarEstadoUsuario(Long id, boolean activo);
}
