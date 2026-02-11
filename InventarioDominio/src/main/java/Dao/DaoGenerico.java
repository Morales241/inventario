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
 * Clase abstracta que implementa las operaciones CRUD básicas de forma
 * genérica. Proporciona una base sólida para reducir la duplicación de código
 * en los DAOs específicos.
 *
 * @author JMorales
 * @param <T> El tipo de la entidad de dominio.
 * @param <ID> El tipo del identificador único de la entidad.
 */
public abstract class DaoGenerico<T, ID> implements IDaoGenerico<T, ID> {

    private final Class<T> claseEntidad;
    private final Conexion conexion = Conexion.getInstancia();
    private EntityManagerFactory emf;

    /**
     * Constructor que inicializa el DAO con la clase de la entidad y la fábrica
     * de EntityManager por defecto.
     *
     * @param claseEntidad Clase de la entidad para la que se crea el DAO.
     */
    public DaoGenerico(Class<T> claseEntidad) {
        this.claseEntidad = claseEntidad;
        this.emf = conexion.getEntityManagerFactory();
    }

    /**
     * Constructor que permite inyectar una fábrica de EntityManager específica.
     *
     * @param claseEntidad Clase de la entidad para la que se crea el DAO.
     * @param emf Fábrica de gestores de entidades a utilizar.
     */
    public DaoGenerico(Class<T> claseEntidad, EntityManagerFactory emf) {
        this.claseEntidad = claseEntidad;
        this.emf = emf;
    }

    /**
     * Crea y devuelve un nuevo {@link EntityManager}.
     *
     * @return Una instancia de EntityManager para realizar operaciones en la
     * base de datos.
     */
    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public T guardar(T entidad) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.persist(entidad);
            em.getTransaction().commit();
        }
        return entidad;
    }

    @Override
    public void eliminar(ID id) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            T entidad = em.find(claseEntidad, id);
            if (entidad != null) {
                em.remove(entidad);
            }
            em.getTransaction().commit();
        }
    }

    @Override
    public T actualizar(T entidad) {
        try (EntityManager em = getEntityManager()) {
            em.getTransaction().begin();
            em.merge(entidad);
            em.getTransaction().commit();
        }
        return entidad;
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
