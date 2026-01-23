package Servicios;

import Dao.DaoTrabajador;
import Dao.DaoUsuario;
import Dtos.TrabajadorDto;
import Dtos.UsuarioDto;
import Entidades.UsuarioSistema;
import Mappers.MapperTrabajador;
import Mappers.MapperUsuario;
import java.util.List;

public class ServicioPersonas {

    private DaoUsuario daoUsuario = new DaoUsuario();
    private DaoTrabajador daoTrabajador = new DaoTrabajador();

    public UsuarioDto login(String user, String pass) throws Exception {
        UsuarioSistema u = daoUsuario.login(user, pass);
        if (u == null) {
            throw new Exception("Usuario o contraseña incorrectos.");
        }
        return MapperUsuario.converter.mapToDto(u);
    }

    public List<TrabajadorDto> listarTrabajadores(boolean soloActivos) {
        return MapperTrabajador.converter.mapToDtoList(daoTrabajador.busquedaConFiltros("", "", "")); 
    }

    public void guardarTrabajador(TrabajadorDto dto) throws Exception {
        if (dto.getNoNomina() == null) throw new Exception("Falta No. Nómina");
        daoTrabajador.guardar(MapperTrabajador.converter.mapToEntity(dto));
    }
}