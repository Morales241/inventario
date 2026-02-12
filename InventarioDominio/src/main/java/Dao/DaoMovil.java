package Dao;

import Entidades.Movil;
import Interfaces.IDaoMovil;

/**
 * DAO para la gestión de dispositivos de tipo {@link Movil}.
 * Hereda las operaciones CRUD de {@link DaoGenerico}.
 * @author JMorales
 */
public class DaoMovil extends DaoGenerico<Movil, Long> implements IDaoMovil {

    public DaoMovil() {
        super(Movil.class);
    }
}
