package Dao;

import Entidades.Movil;
import Interfaces.IDaoMovil;
import conexion.Conexion;
import jakarta.persistence.EntityManagerFactory;

/**
 * @author JMorales
 */
public class DaoMovil extends DaoGenerico<Movil, Long> implements IDaoMovil {
    
    private EntityManagerFactory emf;
    
    public DaoMovil() {
        super(Movil.class);
        this.emf = Conexion.getInstancia().getEntityManagerFactory();
    }
    
    public DaoMovil(EntityManagerFactory emf) {
        super(Movil.class,emf);
        this.emf = emf;
    }
}