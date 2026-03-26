package Servicios;

import conexion.Conexion;
import jakarta.persistence.EntityManager;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ServicioBase {
    protected static final Logger logger = LoggerFactory.getLogger(ServicioBase.class);

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
                logger.error("No se pudo obtener el EntityManager para lectura");
                throw new IllegalStateException("No se pudo obtener el EntityManager");
            }
            logger.debug("EntityManager abierto para lectura");
            return accion.apply(em);
        } catch (Exception e) {
            logger.error("Error durante lectura en base de datos", e);
            throw e;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                logger.debug("EntityManager cerrado");
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
                logger.debug("Transacción iniciada");
            }

            T resultado = accion.apply(em);

            if (transaccionIniciada) {
                em.getTransaction().commit();
                logger.debug("Transacción completada exitosamente");
            }
            return resultado;

        } catch (RuntimeException e) {
            if (transaccionIniciada && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                logger.warn("Transacción reversada por error", e);
            }
            logger.error("Error en transacción", e);
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
                logger.error("No se pudo obtener EntityManager para transacción");
                throw new IllegalStateException("No se pudo obtener el EntityManager");
            }
            
            em.getTransaction().begin();
            logger.debug("Transacción iniciada con nuevo EntityManager");

            T resultado = accion.apply(em);

            em.getTransaction().commit();
            logger.debug("Transacción completada exitosamente");
            return resultado;

        } catch (RuntimeException e) {
            if (em != null && em.getTransaction().isActive()) {
                em.getTransaction().rollback();
                logger.warn("Transacción reversada por error", e);
            }
            logger.error("Error en transacción de base de datos", e);
            throw e;
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
                logger.debug("EntityManager cerrado");
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