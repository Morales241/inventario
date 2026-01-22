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

public class DaoEquipoAsignado extends DaoGenerico<EquipoAsignado, Long> implements IDaoEquipoAsignado {

    private final EntityManagerFactory emf = Conexion.getInstancia().getEntityManagerFactory();

    public DaoEquipoAsignado() {
        super(EquipoAsignado.class);
    }

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