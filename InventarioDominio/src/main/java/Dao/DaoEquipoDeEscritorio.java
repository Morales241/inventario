/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Entidades.EquipoDeEscritorio;
import Interfaces.IDaoEquipoDeEscritorio;
import conexion.Conexion;
import jakarta.persistence.EntityManagerFactory;

/**
 *
 * @author JMorales
 */
public class DaoEquipoDeEscritorio extends DaoGenerico<EquipoDeEscritorio, Long> implements IDaoEquipoDeEscritorio {
    
    private EntityManagerFactory emf = Conexion.getInstancia().getEntityManagerFactory();
 
    public DaoEquipoDeEscritorio() {
        super(EquipoDeEscritorio.class);
        this.emf = Conexion.getInstancia().getEntityManagerFactory();
    }
    
    public DaoEquipoDeEscritorio(EntityManagerFactory emf) {
        super(EquipoDeEscritorio.class, emf);
        this.emf = emf;
    }
    
}
