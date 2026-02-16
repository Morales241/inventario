package mapper;

import Dtos.*;
import Entidades.*;
import Enums.*;

import java.util.Objects;

public class MapperEquipos {
    
    public static final Mapper<EquipoDeEscritorio, EquipoEscritorioDTO> escritorio =
            new Mapper<>(

                    // ENTITY → DTO
                    (e) -> {
                        EquipoEscritorioDTO dto =
                                mapCommonToDto(e, new EquipoEscritorioDTO());

                        dto.setNombreEquipo(e.getNombreEquipo());
                        dto.setCuenta(e.getCuenta());
                        dto.setFinalGarantia(e.getFinalGarantia());

                        return dto;
                    },

                    // DTO → ENTITY
                    (d) -> {
                        EquipoDeEscritorio e = new EquipoDeEscritorio();
                        mapCommonToEntity(d, e);

                        e.setNombreEquipo(d.getNombreEquipo());
                        e.setCuenta(d.getCuenta());
                        e.setFinalGarantia(d.getFinalGarantia());

                        return e;
                    }
            );

    public static final Mapper<Movil, MovilDTO> movil =
            new Mapper<>(

                    (e) -> {
                        MovilDTO dto =
                                mapCommonToDto(e, new MovilDTO());

                        dto.setNumCelular(e.getNumCelular());
                        dto.setCargador(e.getCargador());
                        dto.setFunda(e.getFunda());
                        dto.setManosLibres(e.getManosLibres());

                        return dto;
                    },

                    (d) -> {
                        Movil e = new Movil();
                        mapCommonToEntity(d, e);

                        e.setNumCelular(d.getNumCelular());
                        e.setCargador(d.getCargador());
                        e.setFunda(d.getFunda());
                        e.setManosLibres(d.getManosLibres());

                        return e;
                    }
            );

    public static final Mapper<OtroEquipo, OtroEquipoDTO> otro =
            new Mapper<>(

                    (e) -> {
                        OtroEquipoDTO dto =
                                mapCommonToDto(e, new OtroEquipoDTO());

                        dto.setTituloCampoExtra(e.getTituloCampoExtra());
                        dto.setTituloCampoExtra2(e.getTituloCampoExtra2());
                        dto.setContenidoCampoExtra(e.getContenidoCampoExtra());
                        dto.setContenidoCampoExtra2(e.getContenidoCampoExtra2());

                        return dto;
                    },

                    (d) -> {
                        OtroEquipo e = new OtroEquipo();
                        mapCommonToEntity(d, e);

                        e.setTituloCampoExtra(d.getTituloCampoExtra());
                        e.setTituloCampoExtra2(d.getTituloCampoExtra2());
                        e.setContenidoCampoExtra(d.getContenidoCampoExtra());
                        e.setContenidoCampoExtra2(d.getContenidoCampoExtra2());

                        return e;
                    }
            );

    public static <T extends EquipoBaseDTO> T mapCommonToDto(
            EquipoDeComputo e,
            T dto
    ) {

        dto.setIdEquipo(e.getId());
        dto.setVersion(e.getVersion());
        dto.setGry(e.getGry());
        dto.setIdentificador(e.getIndetificador());
        dto.setFactura(e.getFactura());
        dto.setObservaciones(e.getObservaciones());
        dto.setFechaCompra(e.getFechaCompra());

        if (Objects.nonNull(e.getEstado())) {
            dto.setEstado(e.getEstado().name());
        }

        if (Objects.nonNull(e.getCondicion())) {
            dto.setCondicion(e.getCondicion().name());
        }

        if (Objects.nonNull(e.getTipo())) {
            dto.setTipo(e.getTipo().name());
        }

        if (Objects.nonNull(e.getSucursal())) {
            dto.setIdSucursal(e.getSucursal().getIdSucursal());
            dto.setNombreSucursal(e.getSucursal().getNombre());
        }

        if (Objects.nonNull(e.getModelo())) {
            dto.setIdModelo(e.getModelo().getId());
            dto.setNombreModelo(
                    e.getModelo().getMarca() + " " +
                            e.getModelo().getNombre()
            );
        }

        return dto; // 🔥 YA NO ES VOID
    }

    private static void mapCommonToEntity(
            EquipoBaseDTO dto,
            EquipoDeComputo e
    ) {

        e.setId(dto.getIdEquipo());
        e.setVersion(dto.getVersion());
        e.setGry(dto.getGry());
        e.setIndetificador(dto.getIdentificador());
        e.setFactura(dto.getFactura());
        e.setObservaciones(dto.getObservaciones());
        e.setFechaCompra(dto.getFechaCompra());

        if (dto.getEstado() != null) {
            e.setEstado(EstadoEquipo.valueOf(dto.getEstado()));
        }

        if (dto.getCondicion() != null) {
            e.setCondicion(CondicionFisica.valueOf(dto.getCondicion()));
        }

        if (dto.getTipo() != null) {
            e.setTipo(TipoEquipo.valueOf(dto.getTipo()));
        }
    }
}
