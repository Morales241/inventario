package Dao;

import Entidades.UsuarioSistema;
import Enums.RolUsuario;
import Interfaces.IDaoUsuario;
import conexion.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

public class DaoUsuario extends DaoGenerico<UsuarioSistema, Long> implements IDaoUsuario {

    private final EntityManagerFactory emf = Conexion.getInstancia().getEntityManagerFactory();

    public DaoUsuario() {
        super(UsuarioSistema.class);
    }

    @Override
    public UsuarioSistema login(String username, String password) {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<UsuarioSistema> cq = cb.createQuery(UsuarioSistema.class);
            Root<UsuarioSistema> root = cq.from(UsuarioSistema.class);

            Predicate pUser = cb.equal(root.get("username"), username);
            Predicate pPass = cb.equal(root.get("password"), password);

            cq.select(root).where(cb.and(pUser, pPass));

            try {
                return em.createQuery(cq).getSingleResult();
            } catch (NoResultException e) {
                return null; 
            }
        }
    }

    @Override
    public UsuarioSistema busquedaEspecifica(String username) {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<UsuarioSistema> cq = cb.createQuery(UsuarioSistema.class);
            Root<UsuarioSistema> root = cq.from(UsuarioSistema.class);

            Predicate pUser = cb.equal(root.get("username"), username);

            cq.select(root).where(pUser);

            try {
                return em.createQuery(cq).getSingleResult();
            } catch (NoResultException e) {
                return null; 
            }
        }
    }

}