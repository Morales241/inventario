package Interfaces;

import Dtos.TrabajadorDto;
import Dtos.UsuarioDto;
import java.util.List;

/**
 *
 * @author JMorales
 */
public interface IFachadaPersonas {
    public UsuarioDto login(String user, String pass) throws Exception;

    public List<TrabajadorDto> listarTrabajadores(boolean soloActivos);

    public void guardarTrabajador(TrabajadorDto dto) throws Exception;
}
