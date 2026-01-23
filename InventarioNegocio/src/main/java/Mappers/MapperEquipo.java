package Mappers;

import Dtos.EquipoDto;
import Entidades.*;
import Enums.TipoOtroEquipo;

public class MapperEquipo {

    public static final Mapper<EquipoDeComputo, EquipoDto> converter = new Mapper<>(
            (e) -> {
                EquipoDto dto = new EquipoDto();
                dto.setId(e.getIdEquipo());
                dto.setGri(e.getGri());
                dto.setFactura(e.getFactura());
                dto.setEstado(e.getEstado());
                dto.setCondicion(e.getCondicion());

                if (e.getSucursal() != null) {
                    dto.setUbicacionSucursal(e.getSucursal().getNombre());
                }
                if (e.getModelo() != null) {
                    dto.setNombreModelo(e.getModelo().getMarca() + " " + e.getModelo().getNombre());
                }

                if (e instanceof Movil) {
                    Movil m = (Movil) e;
                    dto.setTipoEquipo("Móvil");
                    dto.setDetalleTecnico("IMEI: " + m.getImei() + " - Cel: " + m.getNumCelular());
                } else if (e instanceof EquipoDeEscritorio) {
                    EquipoDeEscritorio pc = (EquipoDeEscritorio) e;
                    dto.setTipoEquipo("Escritorio");
                    dto.setDetalleTecnico("Cuenta: " + pc.getCuenta() + " - Garantía: " + pc.getFinalGarantia());
                } else if (e instanceof OtroEquipo) {
                    OtroEquipo ot = (OtroEquipo) e;
                    dto.setTipoEquipo("Otro");
                    dto.setSerie(ot.getNoSerie());
                    dto.setDetalleTecnico("Serie: " + ot.getNoSerie() + " - Tipo: " + ot.getTipo());
                }
                return dto;
            },
            (dto) -> {
                EquipoDeComputo entity;

                // Decisión de qué instanciar
                String tipo = dto.getTipoEquipo() != null ? dto.getTipoEquipo() : "Otro";

                if ("Móvil".equalsIgnoreCase(tipo)) {
                    entity = new Movil();
                } else if ("Escritorio".equalsIgnoreCase(tipo)) {
                    entity = new EquipoDeEscritorio();
                } else {
                    entity = new OtroEquipo();
                }

                entity.setIdEquipo(dto.getId());
                entity.setGri(dto.getGri());
                entity.setFactura(dto.getFactura());
                entity.setEstado(dto.getEstado());
                entity.setCondicion(dto.getCondicion());

                return entity;
            }
    );
}
