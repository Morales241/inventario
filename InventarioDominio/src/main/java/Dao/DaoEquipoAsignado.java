package Dao;

import Entidades.EquipoAsignado;
import Entidades.Trabajador;
import Interfaces.IDaoEquipoAsignado;
import conexion.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 * Implementación del DAO para la entidad {@link EquipoAsignado}.
 * Gestiona la relación de asignación entre equipos y trabajadores.
 * * @author JMorales
 */
public class DaoEquipoAsignado extends DaoGenerico<EquipoAsignado, Long> implements IDaoEquipoAsignado {

    private EntityManagerFactory emf;

    public DaoEquipoAsignado() {
        super(EquipoAsignado.class);
        this.emf = Conexion.getInstancia().getEntityManagerFactory();
    }
    
    public DaoEquipoAsignado(EntityManagerFactory emf) {
        super(EquipoAsignado.class, emf);
        this.emf = emf;
    }

    /**
     * Busca los equipos actualmente asignados a un trabajador específico que no han sido devueltos.
     * Un equipo se considera activo si su fecha de devolución es nula.
     * * @param idTrabajador Identificador único del trabajador.
     * @return Lista de {@link EquipoAsignado} que el trabajador posee actualmente.
     */
    @Override
    public List<EquipoAsignado> buscarPorTrabajadorActivo(Long idTrabajador) {
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<EquipoAsignado> cq = cb.createQuery(EquipoAsignado.class);
            Root<EquipoAsignado> root = cq.from(EquipoAsignado.class);

            Join<EquipoAsignado, Trabajador> joinTrabajador = root.join("trabajador");
            
            Predicate esElTrabajador = cb.equal(joinTrabajador.get("idTrabajador"), idTrabajador);
            Predicate aunActivo = cb.isNull(root.get("fechaDevolucion"));

            cq.where(cb.and(esElTrabajador, aunActivo));

            return em.createQuery(cq).getResultList();
        }
    }
}