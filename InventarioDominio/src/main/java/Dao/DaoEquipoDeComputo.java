package Dao;

import Entidades.EquipoDeComputo;
import Entidades.Modelo;
import Enums.CondicionFisica;
import Enums.EstadoEquipo;
import Enums.TipoEquipo;
import Interfaces.IDaoEquipoDeComputo;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de EquipoDeComputo con soporte de paginación.
 */
public class DaoEquipoDeComputo extends DaoGenerico<EquipoDeComputo, Long>
        implements IDaoEquipoDeComputo {

    public DaoEquipoDeComputo() {
        super(EquipoDeComputo.class);
    }

    @Override
    public EquipoDeComputo buscarPorGry(Integer gry) {
        if (gry == null) {
            return null;
        }
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipoDeComputo> cq = cb.createQuery(EquipoDeComputo.class);
        Root<EquipoDeComputo> root = cq.from(EquipoDeComputo.class);
        cq.select(root).where(cb.equal(root.get("gry"), gry));
        try {
            return em.createQuery(cq).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<EquipoDeComputo> buscarConFiltros(Integer gry,
            EstadoEquipo estado,
            String busquedaModelo) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipoDeComputo> cq = cb.createQuery(EquipoDeComputo.class);
        Root<EquipoDeComputo> root = cq.from(EquipoDeComputo.class);

        List<Predicate> predicados = new ArrayList<>();

        if (gry != null && gry > 0) {
            predicados.add(cb.equal(root.get("gry"), gry));
        }

        if (estado != null) {
            predicados.add(cb.equal(root.get("estado"), estado));
        }

        if (busquedaModelo != null && !busquedaModelo.isBlank()) {
            Join<EquipoDeComputo, Modelo> joinModelo = root.join("modelo");
            String filtro = "%" + busquedaModelo.toLowerCase() + "%";
            predicados.add(cb.or(
                    cb.like(cb.lower(joinModelo.get("marca")), filtro),
                    cb.like(cb.lower(joinModelo.get("nombre")), filtro)
            ));
        }

        cq.select(root).where(predicados.toArray(Predicate[]::new));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<EquipoDeComputo> buscarConFiltros(String texto,
            TipoEquipo tipo,
            CondicionFisica condicion,
            EstadoEquipo estado) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipoDeComputo> cq = cb.createQuery(EquipoDeComputo.class);
        Root<EquipoDeComputo> root = cq.from(EquipoDeComputo.class);

        // fetch join: trae modelo en la misma query → evita N+1
        root.fetch("modelo", JoinType.LEFT);

        List<Predicate> predicates = buildPredicados(cb, root, texto, tipo, condicion, estado);

        cq.select(root)
                .where(predicates.toArray(Predicate[]::new))
                .distinct(true)
                .orderBy(cb.desc(root.get("id")));

        return em.createQuery(cq).getResultList();
    }

    /**
     * Devuelve solo los registros de la página solicitada.
     *
     * @param texto filtro texto libre (GRY numérico o nombre de modelo)
     * @param tipo filtro por TipoEquipo, null o TODOS = sin filtro
     * @param condicion filtro por CondicionFisica, null o TODAS = sin filtro
     * @param estado filtro por EstadoEquipo, null o TODOS = sin filtro
     * @param pagina número de página comenzando en 0
     * @param tamano filas por página (ej. 25)
     */
    public List<EquipoDeComputo> buscarConFiltrosPaginado(String texto,
            TipoEquipo tipo,
            CondicionFisica condicion,
            EstadoEquipo estado,
            int pagina,
            int tamano) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipoDeComputo> cq = cb.createQuery(EquipoDeComputo.class);
        Root<EquipoDeComputo> root = cq.from(EquipoDeComputo.class);

        // fetch join para evitar N+1 al mapear equipo → modelo
        root.fetch("modelo", JoinType.LEFT);

        List<Predicate> predicates = buildPredicados(cb, root, texto, tipo, condicion, estado);

        cq.select(root)
                .where(predicates.toArray(Predicate[]::new))
                .distinct(true)
                .orderBy(cb.desc(root.get("id")));

        return em.createQuery(cq)
                .setFirstResult(pagina * tamano) 
                .setMaxResults(tamano)
                .getResultList();
    }

    /**
     * Cuenta los equipos que coinciden con los filtros sin traer objetos
     * completos.
     */
    public long contarConFiltros(String texto,
            TipoEquipo tipo,
            CondicionFisica condicion,
            EstadoEquipo estado) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<EquipoDeComputo> root = cq.from(EquipoDeComputo.class);
        
        List<Predicate> predicates = buildPredicados(cb, root, texto, tipo, condicion, estado);

        cq.select(cb.countDistinct(root))
                .where(predicates.toArray(Predicate[]::new));

        return em.createQuery(cq).getSingleResult();
    }

    private List<Predicate> buildPredicados(CriteriaBuilder cb,
            Root<EquipoDeComputo> root,
            String texto,
            TipoEquipo tipo,
            CondicionFisica condicion,
            EstadoEquipo estado) {
        List<Predicate> predicates = new ArrayList<>();

        if (texto != null && !texto.isBlank()) {
            if (texto.matches("\\d+")) {
                predicates.add(cb.equal(root.get("gry"), Integer.valueOf(texto)));
            } else {
                Join<EquipoDeComputo, Modelo> modeloJoin = root.join("modelo", JoinType.LEFT);
                predicates.add(cb.like(
                        cb.lower(modeloJoin.get("nombre")),
                        "%" + texto.toLowerCase() + "%"
                ));
            }
        }

        if (tipo != null && tipo != TipoEquipo.TODOS) {
            predicates.add(cb.equal(root.get("tipo"), tipo));
        }

        if (condicion != null && condicion != CondicionFisica.TODAS) {
            predicates.add(cb.equal(root.get("condicion"), condicion));
        }

        if (estado != null && estado != EstadoEquipo.TODOS) {
            predicates.add(cb.equal(root.get("estado"), estado));
        }

        return predicates;
    }
}
