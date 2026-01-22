/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Entidades.EquipoDeEscritorio;
import Interfaces.IDaoEquipoDeEscritorio;

/**
 *
 * @author JMorales
 */
public class DaoEquipoDeEscritorio extends DaoGenerico<EquipoDeEscritorio, Long> implements IDaoEquipoDeEscritorio {
    public DaoEquipoDeEscritorio() {
        super(EquipoDeEscritorio.class);
    }
}
