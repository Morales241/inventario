package Dao;

import Entidades.Movil;
import Interfaces.IDaoMovil;
import conexion.Conexion;
import jakarta.persistence.EntityManagerFactory;

/**
 * DAO para la gestión de dispositivos de tipo {@link Movil}.
 * Hereda las operaciones CRUD de {@link DaoGenerico}.
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