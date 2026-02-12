package Dao;

import Interfaces.IDaoGenerico;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;

public abstract class DaoGenerico<T, ID> implements IDaoGenerico<T, ID> {

    private final Class<T> claseEntidad;

    protected EntityManager em;
    
    protected DaoGenerico(Class<T> claseEntidad) {
        this.claseEntidad = claseEntidad;
    }
    
    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @Override
    public T guardar(T entidad) {
        em.persist(entidad);
        return entidad;
    }

    @Override
    public T actualizar(T entidad) {
        return em.merge(entidad);
    }

    @Override
    public void eliminar(ID id) {
        T entidad = em.find(claseEntidad, id);
        if (entidad != null) {
            em.remove(entidad);
        }
    }

    @Override
    public T buscarPorId(ID id) {
        return em.find(claseEntidad, id);
    }

    @Override
    public List<T> buscarTodos() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(claseEntidad);
        Root<T> root = cq.from(claseEntidad);
        cq.select(root);
        return em.createQuery(cq).getResultList();
    }
}
