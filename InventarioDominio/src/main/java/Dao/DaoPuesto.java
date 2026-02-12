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
 * Implementación del DAO para la entidad {@link Puesto}. Gestiona los cargos
 * laborales y su relación con los departamentos.
 *
 * * @author JMorales
 */
public class DaoPuesto extends DaoGenerico<Puesto, Long> implements IDaoPuesto {

    public DaoPuesto() {
        super(Puesto.class);
    }

    @Override
    public Puesto busquedaEspecifica(String nombre) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Puesto> cq = cb.createQuery(Puesto.class);
        Root<Puesto> root = cq.from(Puesto.class);

        cq.select(root)
                .where(cb.equal(cb.lower(root.get("nombre")),
                        nombre.toLowerCase()));

        List<Puesto> resultados = em.createQuery(cq).getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public List<Puesto> busquedaPorDepartamento(Long idDepartamento) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Puesto> cq = cb.createQuery(Puesto.class);
        Root<Puesto> root = cq.from(Puesto.class);

        List<Predicate> predicados = new ArrayList<>();

        if (idDepartamento != null) {
            Join<Puesto, Departamento> join
                    = root.join("departamento");

            predicados.add(
                    cb.equal(join.get("id"), idDepartamento)
            );
        }

        cq.select(root)
                .where(predicados.toArray(new Predicate[0]));

        return em.createQuery(cq).getResultList();
    }
}
