package Servicios;

import conexion.Conexion;
import jakarta.persistence.EntityManager;
import java.util.function.Function;

public abstract class ServicioBase {

    /**
     * Ejecuta una operación de solo lectura (sin transacción).
     * @param <T>
     * @param accion
     * @return 
     */
    protected <T> T ejecutarLectura(Function<EntityManager, T> accion) {
        EntityManager em = Conexion.getEntityManager();
        try {
            return accion.apply(em);
        } finally {
            em.close();
        }
    }

    /**
     * Ejecuta una operación con transacción.
     * @param <T>
     * @param accion
     * @return 
     */
    protected <T> T ejecutarTransaccion(Function<EntityManager, T> accion) {
        EntityManager em = Conexion.getEntityManager();
        try {
            em.getTransaction().begin();

            T resultado = accion.apply(em);

            em.getTransaction().commit();
            return resultado;

        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
