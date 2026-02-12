package Dao;

import Entidades.UsuarioSistema;
import Interfaces.IDaoUsuario;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 * Implementación del DAO para la entidad {@link UsuarioSistema}. Encargado de
 * la seguridad, autenticación y gestión de cuentas de usuario.
 *
 * * @author JMorales
 */
public class DaoUsuario extends DaoGenerico<UsuarioSistema, Long> implements IDaoUsuario {

    public DaoUsuario() {
        super(UsuarioSistema.class);
    }

    @Override
    public UsuarioSistema login(String username, String password) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UsuarioSistema> cq
                = cb.createQuery(UsuarioSistema.class);
        Root<UsuarioSistema> root = cq.from(UsuarioSistema.class);

        Predicate pUser = cb.equal(root.get("username"), username);
        Predicate pPass = cb.equal(root.get("password"), password);

        cq.select(root)
                .where(cb.and(pUser, pPass));

        List<UsuarioSistema> resultados
                = em.createQuery(cq).getResultList();

        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public UsuarioSistema busquedaEspecifica(String username) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<UsuarioSistema> cq
                = cb.createQuery(UsuarioSistema.class);
        Root<UsuarioSistema> root = cq.from(UsuarioSistema.class);

        cq.select(root)
                .where(cb.equal(root.get("username"), username));

        List<UsuarioSistema> resultados
                = em.createQuery(cq).getResultList();

        return resultados.isEmpty() ? null : resultados.get(0);
    }
}
