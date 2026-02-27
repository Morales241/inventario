package Dao;

import Entidades.EquipoAsignado;
import Entidades.Usuario;
import Interfaces.IDaoEquipoAsignado;
import conexion.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 * Implementación del DAO para la entidad {@link EquipoAsignado}.
 * Gestiona la relación de asignación entre equipos y Usuarioes.
 * * @author JMorales
 */
public class DaoEquipoAsignado extends DaoGenerico<EquipoAsignado, Long> implements IDaoEquipoAsignado {

    public DaoEquipoAsignado() {
        super(EquipoAsignado.class);
    }

    @Override
    public List<EquipoAsignado> buscarPorUsuarioActivo(Long idUsuario) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipoAsignado> cq = cb.createQuery(EquipoAsignado.class);
        Root<EquipoAsignado> root = cq.from(EquipoAsignado.class);

        Join<EquipoAsignado, Usuario> joinUsuario = root.join("Usuario");

        Predicate esUsuario = cb.equal(joinUsuario.get("id"), idUsuario);
        Predicate activo = cb.isNull(root.get("fechaDevolucion"));

        cq.select(root).where(cb.and(esUsuario, activo));

        return em.createQuery(cq).getResultList();
    }

    @Override
    public boolean existeAsignacionActiva(Long idEquipo) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<EquipoAsignado> root = cq.from(EquipoAsignado.class);

        Predicate equipo = cb.equal(root.get("equipo").get("id"), idEquipo);
        Predicate activo = cb.isNull(root.get("fechaDevolucion"));

        cq.select(cb.count(root))
          .where(cb.and(equipo, activo));

        Long cantidad = em.createQuery(cq).getSingleResult();

        return cantidad > 0;
    }
}
