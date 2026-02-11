/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Entidades.OtroEquipo;
import Interfaces.IDaoOtroEquipo;
import conexion.Conexion;
import jakarta.persistence.EntityManagerFactory;

/**
 * DAO para la gestión de equipos diversos (periféricos u otros) representados por {@link OtroEquipo}.
 * @author JMorales
 */
public class DaoOtroEquipo extends DaoGenerico<OtroEquipo, Long> implements IDaoOtroEquipo {
    private EntityManagerFactory emf;
    
    public DaoOtroEquipo() {
        super(OtroEquipo.class);
        this.emf = Conexion.getInstancia().getEntityManagerFactory();
    }
    
    public DaoOtroEquipo(EntityManagerFactory emf) {
        super(OtroEquipo.class,emf);
        this.emf = emf;
    }
}