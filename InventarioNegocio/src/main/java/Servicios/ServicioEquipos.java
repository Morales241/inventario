package Servicios;

import Dao.DaoEquipoDeComputo;
import Dao.DaoModelo;
import Dtos.EquipoDto;
import Dtos.ModeloDto;
import Entidades.EquipoDeComputo;
import Mappers.MapperEquipo;
import Mappers.MapperModelo;
import java.util.List;

public class ServicioEquipos {

    private DaoEquipoDeComputo daoEquipo = new DaoEquipoDeComputo();
    private DaoModelo daoModelo = new DaoModelo();

    public List<EquipoDto> buscarEquipos(String criterio) {
        // Aquí podrías implementar lógica para decidir si buscas por serie, por marca, etc.
        // Usamos el buscarConFiltros que hicimos en el DAO
        List<EquipoDeComputo> lista = daoEquipo.buscarConFiltros(null, null, null, criterio);
        return MapperEquipo.converter.mapToDtoList(lista);
    }
    
    public List<ModeloDto> listarModelos() {
        return MapperModelo.converter.mapToDtoList(daoModelo.buscarTodos());
    }

    public void guardarEquipo(EquipoDto dto) throws Exception {
        
        if (dto.getGri() == null) throw new Exception("El GRI es obligatorio");
        if (dto.getSerie() == null) throw new Exception("La serie es obligatoria");
        
        // El Mapper se encarga de saber si es Movil, Escritorio u Otro
        EquipoDeComputo entidad = MapperEquipo.converter.mapToEntity(dto);
        
        // Si el ID existe, es edición, si no, es nuevo
        if (dto.getId() != null && dto.getId() > 0) {
            daoEquipo.actualizar(entidad);
        } else {
            daoEquipo.guardar(entidad);
        }
    }
}