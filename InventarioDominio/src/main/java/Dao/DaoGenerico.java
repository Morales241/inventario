package Dao;

import Interfaces.IDaoGenerico;
import conexion.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 *
 * @author JMorales
 * @param <T>
 */
public abstract class DaoGenerico<T, ID> implements IDaoGenerico<T, ID>{

    private final Class<T> claseEntidad;
    private final Conexion conexion = Conexion.getInstancia();
    private EntityManagerFactory emf;

    public DaoGenerico(Class<T> claseEntidad) {
        this.claseEntidad = claseEntidad;
        this.emf = conexion.getEntityManagerFactory();
    }
    
    public DaoGenerico(Class<T> claseEntidad, EntityManagerFactory emf) {
        this.claseEntidad = claseEntidad;
        this.emf = emf;
    }

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    //CRUD basico
    
    @Override
    public void guardar(T entidad) {

        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();

            em.persist(entidad);

            em.getTransaction().commit();
        }
    }

    @Override
    public void eliminar(ID id) {

        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();

            em.remove(id);

            em.getTransaction().commit();
        }
    }

    @Override
    public void actualizar(T entidad) {

        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();

            em.merge(entidad);

            em.getTransaction().commit();
        }
    }
    
    @Override
    public List<T> buscarTodos() {

        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            
            CriteriaQuery<T> cq = cb.createQuery(claseEntidad);
            
            Root<T> root = cq.from(claseEntidad);
            
            cq.select(root);
            
            return em.createQuery(cq).getResultList();
            
        }
    }
    
    @Override
    public T buscarPorId(ID id) {

        try (EntityManager em = getEntityManager()) {
            return em.find(claseEntidad, id);
        }
    }
}