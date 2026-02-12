package Dao;

import Entidades.EquipoDeEscritorio;
import Interfaces.IDaoEquipoDeEscritorio;

/**
 * DAO especializado en la persistencia de equipos de cómputo de tipo
 * {@link EquipoDeEscritorio}.
 *
 * @author JMorales
 */
public class DaoEquipoDeEscritorio extends DaoGenerico<EquipoDeEscritorio, Long> implements IDaoEquipoDeEscritorio {

    public DaoEquipoDeEscritorio() {
        super(EquipoDeEscritorio.class);
    }
}
