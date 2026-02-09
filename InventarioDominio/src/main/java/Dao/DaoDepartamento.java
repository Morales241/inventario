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
 *
 * @author JMorales
 */
public class DaoDepartamento extends DaoGenerico<Departamento, Long> implements IDaoDepartamento {

    private EntityManagerFactory emf;

    public DaoDepartamento() {
        super(Departamento.class);
        this.emf = Conexion.getInstancia().getEntityManagerFactory();
    }
    
    public DaoDepartamento(EntityManagerFactory emf) {
        super(Departamento.class,emf);
        this.emf = emf;
    }

    @Override
    public Departamento busquedaEspecifica(String nombre) {

        try (EntityManager em = getEntityManager()) {

            List<Predicate> predicados = new ArrayList<>();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Departamento> cq = cb.createQuery(Departamento.class);

            Root<Departamento> root = cq.from(Departamento.class);

            predicados.add(cb.equal(cb.lower(root.get("nombre")), nombre.toLowerCase()));

            cq.select(root);

            cq.where(predicados);

            return em.createQuery(cq).getSingleResult();

        }
    }

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

            cq.where(predicados);

            return em.createQuery(cq).getResultList();

        }
    }
}
