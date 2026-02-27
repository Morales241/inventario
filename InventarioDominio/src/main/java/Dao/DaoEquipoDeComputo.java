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
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO especializado para la gestión de {@link EquipoDeComputo}. Permite filtrar
 * equipos por GRI, estado físico y descripciones del modelo.
 *
 * * @author JMorales
 */
public class DaoEquipoDeComputo extends DaoGenerico<EquipoDeComputo, Long> implements IDaoEquipoDeComputo {

    public DaoEquipoDeComputo() {
        super(EquipoDeComputo.class);
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

            Predicate porMarca
                    = cb.like(cb.lower(joinModelo.get("marca")), filtro);

            Predicate porNombre
                    = cb.like(cb.lower(joinModelo.get("nombre")), filtro);

            predicados.add(cb.or(porMarca, porNombre));
        }

        cq.select(root)
                .where(predicados.toArray(Predicate[]::new));

        return em.createQuery(cq).getResultList();
    }

    @Override
    public EquipoDeComputo buscarPorGry(Integer gry) {

        if (gry == null) {
            return null;
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipoDeComputo> cq = cb.createQuery(EquipoDeComputo.class);
        Root<EquipoDeComputo> root = cq.from(EquipoDeComputo.class);

        cq.select(root)
                .where(cb.equal(root.get("gry"), gry));

        try {
            return em.createQuery(cq).getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    @Override
    public List<EquipoDeComputo> buscarConFiltros(
            String texto,
            TipoEquipo tipo,
            CondicionFisica condicion,
            EstadoEquipo estado
    ) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EquipoDeComputo> cq
                = cb.createQuery(EquipoDeComputo.class);

        Root<EquipoDeComputo> root = cq.from(EquipoDeComputo.class);

        List<Predicate> predicates = new ArrayList<>();

        if (texto != null && !texto.isBlank()) {

            if (texto.matches("\\d+")) {

                predicates.add(
                        cb.equal(
                                root.get("gry"),
                                Integer.valueOf(texto)
                        )
                );

            } else {

                Join<EquipoDeComputo, Modelo> modeloJoin
                        = root.join("modelo");

                predicates.add(
                        cb.like(
                                cb.lower(modeloJoin.get("nombre")),
                                "%" + texto.toLowerCase() + "%"
                        )
                );
            }
        }

        if (tipo != null && tipo != (TipoEquipo.TODOS)) {
            predicates.add(
                    cb.equal(root.get("tipo"), tipo)
            );
        }

        if (condicion != null  && condicion != (CondicionFisica.TODAS)) {
            predicates.add(
                    cb.equal(root.get("condicion"), condicion)
            );
        }

        if (estado != null && estado != EstadoEquipo.TODOS) {
            predicates.add(
                    cb.equal(root.get("estado"), estado)
            );
        }

        cq.where(predicates.toArray(Predicate[]::new));

        // Orden empresarial por ID descendente
        cq.orderBy(cb.desc(root.get("id")));

        TypedQuery<EquipoDeComputo> query
                = em.createQuery(cq);

        return query.getResultList();
    }

}
