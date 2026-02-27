package Dao;

import Entidades.CuentaSistema;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import Interfaces.IDaoCuentaSistema;

/**
 * Implementación del DAO para la entidad {@link CuentaSistema}. Encargado de
 * la seguridad, autenticación y gestión de cuentas de usuario.
 *
 * * @author JMorales
 */
public class DaoCuentaSistema extends DaoGenerico<CuentaSistema, Long> implements IDaoCuentaSistema {

    public DaoCuentaSistema() {
        super(CuentaSistema.class);
    }

    @Override
    public CuentaSistema login(String username, String password) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CuentaSistema> cq
                = cb.createQuery(CuentaSistema.class);
        Root<CuentaSistema> root = cq.from(CuentaSistema.class);

        Predicate pUser = cb.equal(root.get("username"), username);
        Predicate pPass = cb.equal(root.get("password"), password);

        cq.select(root)
                .where(cb.and(pUser, pPass));

        List<CuentaSistema> resultados
                = em.createQuery(cq).getResultList();

        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public CuentaSistema busquedaEspecifica(String username) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CuentaSistema> cq
                = cb.createQuery(CuentaSistema.class);
        Root<CuentaSistema> root = cq.from(CuentaSistema.class);

        cq.select(root)
                .where(cb.equal(root.get("username"), username));

        List<CuentaSistema> resultados
                = em.createQuery(cq).getResultList();

        return resultados.isEmpty() ? null : resultados.get(0);
    }
}
