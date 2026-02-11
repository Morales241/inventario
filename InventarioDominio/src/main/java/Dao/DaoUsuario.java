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

/**
 * Implementación del DAO para la entidad {@link UsuarioSistema}.
 * Encargado de la seguridad, autenticación y gestión de cuentas de usuario.
 * * @author JMorales
 */
public class DaoUsuario extends DaoGenerico<UsuarioSistema, Long> implements IDaoUsuario {

    private final EntityManagerFactory emf;

    public DaoUsuario() {
        super(UsuarioSistema.class);
        this.emf = Conexion.getInstancia().getEntityManagerFactory();
    }
    
    public DaoUsuario(EntityManagerFactory emf) {
        super(UsuarioSistema.class,emf);
        this.emf = emf;
    }

    /**
     * Valida las credenciales de un usuario para el acceso al sistema.
     * * @param username Nombre de usuario.
     * @param password Contraseña (debe estar pre-procesada/hasheada si aplica).
     * @return El objeto {@link UsuarioSistema} si las credenciales son válidas, 
     * o {@code null} si el usuario o la contraseña son incorrectos.
     */
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

    /**
     * Busca un usuario únicamente por su nombre de usuario (identificador único).
     * * @param username Nombre de usuario a localizar.
     * @return El {@link UsuarioSistema} correspondiente o {@code null} si no existe.
     */
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