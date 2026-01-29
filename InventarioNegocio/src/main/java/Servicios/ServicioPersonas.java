package Servicios;

import Dao.DaoTrabajador;
import Dao.DaoUsuario;
import Dtos.TrabajadorDto;
import Dtos.UsuarioDto;
import Entidades.Trabajador;
import Entidades.UsuarioSistema;
import Mappers.MapperTrabajador;
import Mappers.MapperUsuario;
import java.util.List;

public class ServicioPersonas {

    private final DaoUsuario daoUsuario = new DaoUsuario();
    private final DaoTrabajador daoTrabajador = new DaoTrabajador();

    public UsuarioDto login(String user, String pass) throws Exception {
        UsuarioSistema u = daoUsuario.login(user, pass);
        if (u == null) {
            throw new Exception("Usuario o contraseña incorrectos.");
        }
        return MapperUsuario.converter.mapToDto(u);
    }
    
    /**
     * Busca trabajadores por nombre, número de nómina o puesto.
     * @param busquedaGlobal
     * @return 
     */
    public List<TrabajadorDto> buscarTrabajadores(String busquedaGlobal) {
        return MapperTrabajador.converter.mapToDtoList(
            daoTrabajador.busquedaConFiltros(busquedaGlobal, busquedaGlobal, busquedaGlobal)
        );
    }
    
    public TrabajadorDto obtenerTrabajador(Long id) {
        return MapperTrabajador.converter.mapToDto(daoTrabajador.buscarPorId(id));
    }

    public void guardarTrabajador(TrabajadorDto dto) throws Exception {
        if (dto.getNoNomina() == null || dto.getNoNomina().isEmpty()) throw new Exception("El No. Nómina es obligatorio");
        if (dto.getNombre() == null || dto.getNombre().isEmpty()) throw new Exception("El Nombre es obligatorio");
        if (dto.getIdPuesto() == null) throw new Exception("El Puesto es obligatorio");

        Trabajador entidad = MapperTrabajador.converter.mapToEntity(dto);
        
        if (dto.getId() != null && dto.getId() > 0) {
            
            daoTrabajador.actualizar(entidad);
        } else {
            
            daoTrabajador.guardar(entidad);
        }
    }
    
    public void cambiarEstadoTrabajador(Long id, boolean activo) throws Exception {
        Trabajador t = daoTrabajador.buscarPorId(id);
        if(t != null) {
            t.setActivo(activo);
            daoTrabajador.actualizar(t);
        }
    }
}