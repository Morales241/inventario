package Dao;

import Entidades.Puesto;
import Entidades.Usuario;
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
import Interfaces.IDaoUsuario;

/**
 * Implementación del DAO para la entidad {@link Usuario}.
 * Proporciona métodos para gestionar el personal y sus asignaciones de puesto.
 * * @author JMorales
 */
public class DaoUsuario extends DaoGenerico<Usuario, Long> implements IDaoUsuario {

    public DaoUsuario() {
        super(Usuario.class);
    }

    @Override
    public Usuario busquedaEspecifica(String nombre, String nomina) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
        Root<Usuario> root = cq.from(Usuario.class);

        List<Predicate> predicados = new ArrayList<>();

        if (nombre != null && !nombre.isBlank()) {
            predicados.add(
                cb.equal(cb.lower(root.get("nombre")),
                         nombre.toLowerCase())
            );
        }

        if (nomina != null && !nomina.isBlank()) {
            predicados.add(
                cb.equal(cb.lower(root.get("noNomina")),
                         nomina.toLowerCase())
            );
        }

        cq.select(root)
          .where(predicados.toArray(new Predicate[0]));

        List<Usuario> resultados = em.createQuery(cq).getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public List<Usuario> busquedaConFiltros(String cadena,
                                               String numero,
                                               String puesto) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
        Root<Usuario> root = cq.from(Usuario.class);

        List<Predicate> predicados = new ArrayList<>();

        if (cadena != null && !cadena.isBlank()) {
            predicados.add(
                cb.like(cb.lower(root.get("nombre")),
                        "%" + cadena.toLowerCase() + "%")
            );
        }

        if (numero != null && !numero.isBlank()) {
            predicados.add(
                cb.like(cb.lower(root.get("noNomina")),
                        "%" + numero.toLowerCase() + "%")
            );
        }

        if (puesto != null && !puesto.isBlank()) {
            Join<Usuario, Puesto> join = root.join("puesto");
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
