package Dao;

import Entidades.Modelo;
import Interfaces.IDaoModelo;
import conexion.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del DAO para la entidad {@link Modelo}. Permite la búsqueda
 * técnica de hardware por número de serie y características de componentes.
 *
 * * @author JMorales
 */
public class DaoModelo extends DaoGenerico<Modelo, Long> implements IDaoModelo {

    public DaoModelo() {
        super(Modelo.class);
    }

    @Override
    public Modelo busquedaEspecifica(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return null;
        }

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Modelo> cq = cb.createQuery(Modelo.class);
        Root<Modelo> root = cq.from(Modelo.class);

        cq.select(root)
                .where(cb.equal(cb.lower(root.get("nombre")), nombre.toLowerCase()));

        List<Modelo> resultados = em.createQuery(cq).getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public List<Modelo> busquedaConFiltros(String nombre,
            String marca,
            Integer memoriaRam,
            Integer almacenamiento,
            String procesador) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Modelo> cq = cb.createQuery(Modelo.class);
        Root<Modelo> root = cq.from(Modelo.class);

        List<Predicate> predicados = new ArrayList<>();

        if (nombre != null && !nombre.isBlank()) {
            predicados.add(cb.like(
                    cb.lower(root.get("nombre")),
                    "%" + nombre.toLowerCase() + "%"
            ));
        }

        if (marca != null && !marca.isBlank()) {
            predicados.add(
                    cb.equal(cb.lower(root.get("marca")),
                            marca.toLowerCase())
            );
        }

        if (memoriaRam != null) {
            predicados.add(
                    cb.equal(root.get("memoriaRam"), memoriaRam)
            );
        }

        if (almacenamiento != null) {
            predicados.add(
                    cb.equal(root.get("almacenamiento"), almacenamiento)
            );
        }

        if (procesador != null && !procesador.isBlank()) {
            predicados.add(
                    cb.equal(cb.lower(root.get("procesador")),
                            procesador.toLowerCase())
            );
        }

        cq.select(root)
                .where(predicados.toArray(new Predicate[0]));

        return em.createQuery(cq).getResultList();
    }

    public boolean existeModeloDuplicado(String marca,
            String nombre,
            Integer ram,
            Integer almacenamiento,
            String procesador) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Modelo> root = cq.from(Modelo.class);

        cq.select(cb.count(root))
                .where(
                        cb.and(
                                cb.equal(cb.lower(root.get("marca")), marca.toLowerCase()),
                                cb.equal(cb.lower(root.get("nombre")), nombre.toLowerCase()),
                                cb.equal(root.get("memoriaRam"), ram),
                                cb.equal(root.get("almacenamiento"), almacenamiento),
                                cb.equal(cb.lower(root.get("procesador")), procesador.toLowerCase())
                        )
                );

        return em.createQuery(cq).getSingleResult() > 0;
    }
}
