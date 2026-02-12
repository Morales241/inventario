package Dao;

import Entidades.Empresa;
import Interfaces.IDaoEmpresa;
import conexion.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 * Implementación del DAO para la entidad {@link Empresa}.
 * Proporciona métodos para la búsqueda de empresas por nombre exacto o coincidencias parciales.
 * * @author JMorales
 */
public class DaoEmpresa extends DaoGenerico<Empresa, Long> implements IDaoEmpresa {

    public DaoEmpresa() {
        super(Empresa.class);
    }

    @Override
    public Empresa buscarPorNombre(String nombre) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Empresa> cq = cb.createQuery(Empresa.class);
        Root<Empresa> root = cq.from(Empresa.class);

        cq.select(root)
          .where(cb.equal(cb.lower(root.get("nombre")), nombre.toLowerCase()));

        List<Empresa> resultados = em.createQuery(cq).getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public List<Empresa> buscarPorCoincidencias(String cadena) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Empresa> cq = cb.createQuery(Empresa.class);
        Root<Empresa> root = cq.from(Empresa.class);

        cq.select(root)
          .where(cb.like(cb.lower(root.get("nombre")),
                "%" + cadena.toLowerCase() + "%"));

        return em.createQuery(cq).getResultList();
    }
}
