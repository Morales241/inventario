package Dao;

import Entidades.EquipoAsignado;
import Entidades.EquipoDeComputo;
import Entidades.Usuario;
import Interfaces.IDaoEquipoAsignado;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de EquipoAsignado con paginación y búsqueda en bd
 */
public class DaoEquipoAsignado extends DaoGenerico<EquipoAsignado, Long>
        implements IDaoEquipoAsignado {

    public DaoEquipoAsignado() {
        super(EquipoAsignado.class);
    }

    @Override
    public List<EquipoAsignado> buscarPorUsuarioActivo(Long idUsuario) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipoAsignado> cq = cb.createQuery(EquipoAsignado.class);
        Root<EquipoAsignado> root = cq.from(EquipoAsignado.class);
        root.fetch("Usuario", JoinType.LEFT);
        root.fetch("equipoDeComputo", JoinType.LEFT);

        Join<EquipoAsignado, Usuario> joinUsuario = root.join("Usuario");
        Predicate esUsuario = cb.equal(joinUsuario.get("idUsuario"), idUsuario);
        Predicate activo = cb.isNull(root.get("fechaDevolucion"));

        cq.select(root).where(cb.and(esUsuario, activo)).distinct(true);
        return em.createQuery(cq).getResultList();
    }

    @Override
    public boolean existeAsignacionActiva(Long idEquipo) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<EquipoAsignado> root = cq.from(EquipoAsignado.class);

        Predicate equipo = cb.equal(root.get("equipoDeComputo").get("id"), idEquipo);
        Predicate activo = cb.isNull(root.get("fechaDevolucion"));

        cq.select(cb.count(root)).where(cb.and(equipo, activo));
        return em.createQuery(cq).getSingleResult() > 0;
    }

    @Override
    public List<EquipoAsignado> buscarActivasPaginado(int pagina, int tamano) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipoAsignado> cq = cb.createQuery(EquipoAsignado.class);
        Root<EquipoAsignado> root = cq.from(EquipoAsignado.class);

        root.fetch("Usuario", JoinType.LEFT);
        root.fetch("equipoDeComputo", JoinType.LEFT);

        cq.select(root)
                .where(cb.isNull(root.get("fechaDevolucion")))
                .distinct(true)
                .orderBy(cb.desc(root.get("fechaEntrega")));

        return em.createQuery(cq)
                .setFirstResult(pagina * tamano)
                .setMaxResults(tamano)
                .getResultList();
    }

    /**
     * Cuenta el total de asignaciones activas (para calcular páginas).
     */
    @Override
    public long contarActivas() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<EquipoAsignado> root = cq.from(EquipoAsignado.class);

        cq.select(cb.countDistinct(root))
                .where(cb.isNull(root.get("fechaDevolucion")));

        return em.createQuery(cq).getSingleResult();
    }

    /**
     * Devuelve una página de asignaciones que coincidan con el filtro.
     */
    public List<EquipoAsignado> buscarConFiltroPaginado(String filtro, int pagina, int tamano) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipoAsignado> cq = cb.createQuery(EquipoAsignado.class);
        Root<EquipoAsignado> root = cq.from(EquipoAsignado.class);

        root.fetch("Usuario", JoinType.LEFT);
        root.fetch("equipoDeComputo", JoinType.LEFT);

        List<Predicate> predicates = buildFiltroPredicados(cb, root, filtro);

        cq.select(root)
                .where(predicates.toArray(new Predicate[0]))
                .distinct(true)
                .orderBy(cb.desc(root.get("fechaEntrega")));

        return em.createQuery(cq)
                .setFirstResult(pagina * tamano)
                .setMaxResults(tamano)
                .getResultList();
    }

    /**
     * Cuenta las asignaciones que coinciden con el filtro 
     */
    public long contarConFiltro(String filtro) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<EquipoAsignado> root = cq.from(EquipoAsignado.class);

        List<Predicate> predicates = buildFiltroPredicados(cb, root, filtro);

        cq.select(cb.countDistinct(root))
                .where(predicates.toArray(new Predicate[0]));

        return em.createQuery(cq).getSingleResult();
    }

    private List<Predicate> buildFiltroPredicados(CriteriaBuilder cb,
            Root<EquipoAsignado> root,
            String filtro) {
        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isNull(root.get("fechaDevolucion")));

        if (filtro != null && !filtro.isBlank()) {
            String lower = filtro.trim().toLowerCase();

            Join<EquipoAsignado, Usuario> joinUsuario = root.join("Usuario", JoinType.LEFT);
            Join<EquipoAsignado, EquipoDeComputo> joinEquipo = root.join("equipoDeComputo", JoinType.LEFT);

            List<Predicate> orPredicates = new ArrayList<>();

            orPredicates.add(cb.like(cb.lower(joinUsuario.get("nombre")), "%" + lower + "%"));

            orPredicates.add(cb.like(cb.lower(joinEquipo.get("indetificador")), "%" + lower + "%"));

            if (filtro.trim().matches("\\d+")) {
                orPredicates.add(cb.equal(joinEquipo.get("gry"), Integer.parseInt(filtro.trim())));
            }

            predicates.add(cb.or(orPredicates.toArray(new Predicate[0])));
        }

        return predicates;
    }
}
