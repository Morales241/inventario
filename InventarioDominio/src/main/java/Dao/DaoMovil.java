/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Entidades.Movil;
import Interfaces.IDaoMovil;

/**
 *
 * @author JMorales
 */
public class DaoMovil extends DaoGenerico<Movil, Long> implements IDaoMovil {
    public DaoMovil() {
        super(Movil.class);
    }
}