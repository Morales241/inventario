package Dao;

import Entidades.Departamento;
import Entidades.Puesto;
import Interfaces.IDaoPuesto;
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
 * Implementación del DAO para la entidad {@link Puesto}.
 * Gestiona los cargos laborales y su relación con los departamentos.
 * * @author JMorales
 */
public class DaoPuesto extends DaoGenerico<Puesto, Long> implements IDaoPuesto {

    private EntityManagerFactory emf;

    public DaoPuesto() {
        super(Puesto.class);
        this.emf = Conexion.getInstancia().getEntityManagerFactory();
    }
    
    public DaoPuesto(EntityManagerFactory emf) {
        super(Puesto.class,emf);
        this.emf = emf;
    }

    /**
     * Busca un puesto por su nombre exacto.
     * @param nombre Nombre del puesto.
     * @return Objeto {@link Puesto} encontrado.
     */
    @Override
    public Puesto busquedaEspecifica(String nombre) {

        try (EntityManager em = getEntityManager()) {
            List<Predicate> predicados = new ArrayList<>();
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Puesto> cq = cb.createQuery(Puesto.class);
            Root<Puesto> root = cq.from(Puesto.class);

            predicados.add(cb.equal(cb.lower(root.get("nombre")), nombre.toLowerCase()));
            cq.select(root);
            cq.where(predicados.toArray(new Predicate[0]));

            return em.createQuery(cq).getSingleResult();
        }
    }
    
    /**
     * Obtiene todos los puestos asociados a un departamento en particular.
     * @param idDepartamento Identificador del departamento.
     * @return Lista de puestos pertenecientes a dicho departamento.
     */
    @Override
    public List<Puesto> busquedaPorDepartamento(Long idDepartamento){
    
        try (EntityManager em = getEntityManager()) {
            List<Predicate> predicados = new ArrayList<>();
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Puesto> cq = cb.createQuery(Puesto.class);
            Root<Puesto> root = cq.from(Puesto.class);
            
            Join<Puesto, Departamento> join = root.join("departamento");

            if (idDepartamento != null) {
                predicados.add(cb.equal(join.get("idDepartamento"), idDepartamento));
            }

            cq.select(root);
            cq.where(predicados.toArray(new Predicate[0]));

            return em.createQuery(cq).getResultList();
        }
    }
}