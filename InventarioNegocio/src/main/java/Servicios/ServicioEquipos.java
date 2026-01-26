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

    private final DaoEquipoDeComputo daoEquipo = new DaoEquipoDeComputo();
    private final DaoModelo daoModelo = new DaoModelo();

    public List<EquipoDto> buscarEquipos(String criterio) {
        List<EquipoDeComputo> lista = daoEquipo.buscarConFiltros(null, null, null, criterio);
        return MapperEquipo.converter.mapToDtoList(lista);
    }
    
    public List<ModeloDto> listarModelos() {
        return MapperModelo.converter.mapToDtoList(daoModelo.buscarTodos());
    }

    public void guardarEquipo(EquipoDto dto) throws Exception {
        
        if (dto.getGri() == null) throw new Exception("El GRI es obligatorio");
        if (dto.getSerie() == null) throw new Exception("La serie es obligatoria");
        
        EquipoDeComputo entidad = MapperEquipo.converter.mapToEntity(dto);
        
        if (dto.getId() != null && dto.getId() > 0) {
            daoEquipo.actualizar(entidad);
        } else {
            daoEquipo.guardar(entidad);
        }
    }
}