/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Entidades.OtroEquipo;
import Interfaces.IDaoOtroEquipo;

/**
 *
 * @author JMorales
 */
public class DaoOtroEquipo extends DaoGenerico<OtroEquipo, Long> implements IDaoOtroEquipo {
    public DaoOtroEquipo() {
        super(OtroEquipo.class);
    }
}