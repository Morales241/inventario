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

    public DaoTrabajador() {
        super(Trabajador.class);
    }

    @Override
    public Trabajador busquedaEspecifica(String nombre, String nomina) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Trabajador> cq = cb.createQuery(Trabajador.class);
        Root<Trabajador> root = cq.from(Trabajador.class);

        List<Predicate> predicados = new ArrayList<>();

        if (nombre != null && !nombre.isBlank()) {
            predicados.add(
                cb.equal(cb.lower(root.get("nombre")),
                         nombre.toLowerCase())
            );
        }

        if (nomina != null && !nomina.isBlank()) {
            predicados.add(
                cb.equal(cb.lower(root.get("nomina")),
                         nomina.toLowerCase())
            );
        }

        cq.select(root)
          .where(predicados.toArray(new Predicate[0]));

        List<Trabajador> resultados = em.createQuery(cq).getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public List<Trabajador> busquedaConFiltros(String cadena,
                                               String numero,
                                               String puesto) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Trabajador> cq = cb.createQuery(Trabajador.class);
        Root<Trabajador> root = cq.from(Trabajador.class);

        List<Predicate> predicados = new ArrayList<>();

        if (cadena != null && !cadena.isBlank()) {
            predicados.add(
                cb.like(cb.lower(root.get("nombre")),
                        "%" + cadena.toLowerCase() + "%")
            );
        }

        if (numero != null && !numero.isBlank()) {
            predicados.add(
                cb.like(cb.lower(root.get("nomina")),
                        "%" + numero.toLowerCase() + "%")
            );
        }

        if (puesto != null && !puesto.isBlank()) {
            Join<Trabajador, Puesto> join = root.join("puesto");
            predicados.add(
                cb.equal(cb.lower(join.get("nombre")),
                         puesto.toLowerCase())
            );
        }

        cq.select(root)
          .where(predicados.toArray(new Predicate[0]));

        return em.createQuery(cq).getResultList();
    }
}
