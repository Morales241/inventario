package Dao;

import Entidades.OtroEquipo;
import Interfaces.IDaoOtroEquipo;

/**
 * DAO para la gestión de equipos diversos (periféricos u otros) representados por {@link OtroEquipo}.
 * @author JMorales
 */
public class DaoOtroEquipo extends DaoGenerico<OtroEquipo, Long> implements IDaoOtroEquipo {

    public DaoOtroEquipo() {
        super(OtroEquipo.class);
    }
}
