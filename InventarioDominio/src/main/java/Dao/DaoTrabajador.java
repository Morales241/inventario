package Dao;

import Entidades.Puesto;
import Entidades.Trabajador;
import Interfaces.IDaoTrabajador;
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
 * Implementación del DAO para la entidad {@link Trabajador}.
 * Proporciona métodos para gestionar el personal y sus asignaciones de puesto.
 * * @author JMorales
 */
public class DaoTrabajador extends DaoGenerico<Trabajador, Long> implements IDaoTrabajador {

    private final EntityManagerFactory emf;

    public DaoTrabajador() {
        super(Trabajador.class);
        this.emf = Conexion.getInstancia().getEntityManagerFactory();
    }
    
    public DaoTrabajador(EntityManagerFactory emf) {
        super(Trabajador.class,emf);
        this.emf = emf;
    }

    /**
     * Busca un trabajador específico combinando nombre y número de nómina.
     * * @param nombre Nombre completo del trabajador.
     * @param nomina Número identificador de nómina.
     * @return El {@link Trabajador} encontrado.
     * @throws jakarta.persistence.NoResultException Si no hay coincidencia exacta.
     */
    @Override
    public Trabajador busquedaEspecifica(String nombre, String nomina) {
        try (EntityManager em = getEntityManager()) {
            List<Predicate> predicados = new ArrayList<>();
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Trabajador> cq = cb.createQuery(Trabajador.class);
            Root<Trabajador> root = cq.from(Trabajador.class);

            if (!nombre.isEmpty()) {
                predicados.add(cb.equal(cb.lower(root.get("nombre")), nombre.toLowerCase()));
            }
            if (!nomina.isEmpty()) {
                predicados.add(cb.equal(cb.lower(root.get("nomina")), nomina.toLowerCase()));
            }

            cq.select(root);
            cq.where(predicados.toArray(new Predicate[0]));
            return em.createQuery(cq).getSingleResult();
        }
    }

    /**
     * Consulta de trabajadores con filtros dinámicos.
     * <p>
     * Permite filtrar por fragmentos del nombre, número de nómina y el nombre del puesto que desempeña.
     * Si todos los parámetros se pasan como cadenas vacías, se retornan <b>todos los trabajadores</b>.
     * </p>
     * * @param cadena Búsqueda parcial por nombre.
     * @param numero Búsqueda parcial por número de nómina.
     * @param puesto Nombre del puesto para realizar el Join.
     * @return Lista de trabajadores que coinciden con los filtros aplicados.
     */
    @Override
    public List<Trabajador> busquedaConFiltros(String cadena, String numero, String puesto) {
        try (EntityManager em = getEntityManager()) {
            List<Predicate> predicados = new ArrayList<>();
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Trabajador> cq = cb.createQuery(Trabajador.class);
            Root<Trabajador> root = cq.from(Trabajador.class);
            Join<Trabajador, Puesto> join = root.join("puesto");

            if (!cadena.isEmpty()) {
                predicados.add(cb.like(cb.lower(root.get("nombre")), "%" + cadena.toLowerCase() + "%"));
            }
            if (!numero.isEmpty()) {
                predicados.add(cb.like(cb.lower(root.get("nomina")), "%" + numero.toLowerCase() + "%"));
            }
            if (!puesto.isEmpty()) {
                predicados.add(cb.equal(cb.lower(join.get("nombre")), puesto.toLowerCase()));
            }
            
            cq.select(root);
            cq.where(predicados.toArray(new Predicate[0]));
            return em.createQuery(cq).getResultList();
        }
    }
}