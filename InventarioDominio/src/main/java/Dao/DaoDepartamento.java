package Dao;

import Entidades.Departamento;
import Entidades.Empresa;
import Entidades.Sucursal;
import Interfaces.IDaoDepartamento;
import conexion.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del DAO para la entidad {@link Departamento}.
 * Permite realizar búsquedas específicas por nombre y filtrado por sucursal.
 * * @author JMorales
 */
public class DaoDepartamento extends DaoGenerico<Departamento, Long> implements IDaoDepartamento {

    private EntityManagerFactory emf;

    /**
     * Constructor por defecto que utiliza la conexión global.
     */
    public DaoDepartamento() {
        super(Departamento.class);
        this.emf = Conexion.getInstancia().getEntityManagerFactory();
    }
    
    /**
     * Constructor que recibe una fábrica de gestores de entidades específica.
     * @param emf Fábrica de gestores de entidades.
     */
    public DaoDepartamento(EntityManagerFactory emf) {
        super(Departamento.class,emf);
        this.emf = emf;
    }

    /**
     * Realiza una búsqueda exacta de un departamento por su nombre (sin distinguir mayúsculas/minúsculas).
     * @param nombre El nombre del departamento a buscar.
     * @return El objeto {@link Departamento} encontrado.
     * @throws jakarta.persistence.NoResultException si no se encuentra el departamento.
     */
    @Override
    public Departamento busquedaEspecifica(String nombre) {
        try (EntityManager em = getEntityManager()) {
            List<Predicate> predicados = new ArrayList<>();
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Departamento> cq = cb.createQuery(Departamento.class);
            Root<Departamento> root = cq.from(Departamento.class);
            predicados.add(cb.equal(cb.lower(root.get("nombre")), nombre.toLowerCase()));
            cq.select(root);
            cq.where(predicados.toArray(new Predicate[0])); 

            return em.createQuery(cq).getSingleResult();
        }
    }

    /**
     * Busca departamentos aplicando filtros opcionales de nombre y sucursal.
     * @param nombre Cadena para buscar coincidencias parciales en el nombre.
     * @param idSucursal Identificador de la sucursal (opcional).
     * @return Lista de departamentos que cumplen con los criterios.
     */
    @Override
    public List<Departamento> busquedaConFiltros(String nombre, Long  idSucursal) {
        try (EntityManager em = getEntityManager()) {
            List<Predicate> predicados = new ArrayList<>();
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Departamento> cq = cb.createQuery(Departamento.class);
            Root<Departamento> root = cq.from(Departamento.class);
            Join<Departamento, Sucursal> join = root.join("sucursal");

            if (!nombre.isEmpty()) {
                predicados.add(cb.like(cb.lower(root.get("nombre")), nombre.toLowerCase()));
            }
            if (idSucursal != null ) {
                predicados.add(cb.equal(join.get("id"), idSucursal));
            }

            cq.select(root);
            cq.where(predicados.toArray(new Predicate[0]));
            return em.createQuery(cq).getResultList();
        }
    }
}