package Dao;

import Entidades.Departamento;
import Entidades.Sucursal;
import Interfaces.IDaoDepartamento;
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

    public DaoDepartamento() {
        super(Departamento.class);
    }

    @Override
    public Departamento busquedaEspecifica(String nombre) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Departamento> cq = cb.createQuery(Departamento.class);
        Root<Departamento> root = cq.from(Departamento.class);

        cq.select(root)
          .where(cb.equal(cb.lower(root.get("nombre")), nombre.toLowerCase()));

        List<Departamento> resultados = em.createQuery(cq).getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public List<Departamento> busquedaConFiltros(String nombre, Long idSucursal) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Departamento> cq = cb.createQuery(Departamento.class);
        Root<Departamento> root = cq.from(Departamento.class);

        List<Predicate> predicados = new ArrayList<>();

        if (nombre != null && !nombre.isBlank()) {
            predicados.add(
                cb.like(cb.lower(root.get("nombre")),
                        "%" + nombre.toLowerCase() + "%")
            );
        }

        if (idSucursal != null) {
            Join<Departamento, Sucursal> join = root.join("sucursal");
            predicados.add(cb.equal(join.get("id"), idSucursal));
        }

        cq.select(root).where(predicados.toArray(new Predicate[0]));

        return em.createQuery(cq).getResultList();
    }
}
