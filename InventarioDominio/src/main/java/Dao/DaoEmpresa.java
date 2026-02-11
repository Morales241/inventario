package Dao;

import Entidades.Empresa;
import Interfaces.IDaoEmpresa;
import conexion.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 * Implementación del DAO para la entidad {@link Empresa}.
 * Proporciona métodos para la búsqueda de empresas por nombre exacto o coincidencias parciales.
 * * @author JMorales
 */
public class DaoEmpresa extends DaoGenerico<Empresa, Long> implements IDaoEmpresa {

    private EntityManagerFactory emf;

    /**
     * Constructor por defecto que inicializa la fábrica de EntityManager desde la conexión global.
     */
    public DaoEmpresa() {
        super(Empresa.class);
        
        this.emf = Conexion.getInstancia().getEntityManagerFactory();
    }
    
    /**
     * Constructor que permite inyectar una fábrica de EntityManager específica.
     * @param emf Fábrica de gestores de entidades a utilizar.
     */
    public DaoEmpresa(EntityManagerFactory emf) {
        super(Empresa.class, emf);
        this.emf = emf;
    }

    /**
     * Busca una empresa por su nombre exacto, ignorando mayúsculas y minúsculas.
     * @param nombre El nombre de la empresa a buscar.
     * @return La instancia de {@link Empresa} encontrada.
     * @throws jakarta.persistence.NoResultException si no se encuentra ninguna coincidencia.
     */
    @Override
    public Empresa buscarPorNombre(String nombre) {

        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Empresa> cq = cb.createQuery(Empresa.class);

            Root<Empresa> root = cq.from(Empresa.class);

            Predicate predicate = cb.equal(cb.lower(root.get("nombre")), nombre.toLowerCase());

            cq.select(root);

            cq.where(predicate);

            return em.createQuery(cq).getSingleResult();

        }
    }

    /**
     * Busca empresas cuyo nombre coincida con el patrón de búsqueda proporcionado.
     * @param cadena Cadena de texto para buscar coincidencias en el nombre.
     * @return Una lista de objetos {@link Empresa} que coinciden con el criterio.
     */
    @Override
    public List<Empresa> buscarPorCoincidencias(String cadena) {
        
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Empresa> cq = cb.createQuery(Empresa.class);

            Root<Empresa> root = cq.from(Empresa.class);

            Predicate predicate = cb.like(cb.lower(root.get("nombre")), cadena.toLowerCase());
            
            cq.select(root);

            cq.where(predicate);

            return em.createQuery(cq).getResultList();

        }
    }

}