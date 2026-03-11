package Servicios;

import conexion.Conexion;
import jakarta.persistence.EntityManager;
import java.util.function.Function;

public abstract class ServicioBase {

    /**
     * Versión que recibe un EntityManager ya abierto (para uso interno)
     */
    protected <T> T ejecutarLectura(EntityManager em, Function<EntityManager, T> accion) {
        return accion.apply(em);
    }

    /**
     * Versión estándar que abre su propio EntityManager
     */
    protected <T> T ejecutarLectura(Function<EntityManager, T> accion) {
        EntityManager em = null;
        try {
            em = Conexion.getEntityManager();
            if (em == null) {
                throw new IllegalStateException("No se pudo obtener el EntityManager");
            }
            return accion.apply(em);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    /**
     * Versión que recibe un EntityManager ya abierto (para uso interno)
     */
    protected <T> T ejecutarTransaccion(EntityManager em, Function<EntityManager, T> accion) {
        boolean transaccionIniciada = false;
        try {
            if (!em.getTransaction().isActive()) {
                em.getTransaction().begin();
                transaccionIniciada = true;
            }

            T resultado = accion.apply(em);

            if (transaccionIniciada) {
                em.getTransaction().commit();
            }
            return resultado;

        } catch (RuntimeException e) {
            if (transaccionIniciada && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        }
    }

    /**
     * Versión estándar que abre su propio EntityManager
     */
    protected <T> T ejecutarTransaccion(Function<EntityManager, T> accion) {
        EntityManager em = null;
        try {
            em = Conexion.getEntityManager();
            if (em == null) {
                throw new IllegalStateException("No se pudo obtener el EntityManager");
            }
            
            em.getTransaction().begin();

            T resultado = accion.apply(em);

            em.getTransaction().commit();
            return resultado;

        } catch (RuntimeException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
    
    protected void configurarDAOs(EntityManager em, Object... daos) {
        for (Object dao : daos) {
            if (dao != null) {
                try {
                    java.lang.reflect.Method method = dao.getClass().getMethod("setEntityManager", EntityManager.class);
                    method.invoke(dao, em);
                } catch (Exception e) {
                }
            }
        }
    }
}