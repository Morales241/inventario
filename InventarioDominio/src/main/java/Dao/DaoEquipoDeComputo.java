package Dao;

import Entidades.EquipoDeComputo;
import Entidades.Modelo;
import Entidades.Sucursal;
import Enums.EstadoEquipo;
import Interfaces.IDaoEquipoDeComputo;
import conexion.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class DaoEquipoDeComputo extends DaoGenerico<EquipoDeComputo, Long> implements IDaoEquipoDeComputo {

    private EntityManagerFactory emf;

    public DaoEquipoDeComputo() {
        super(EquipoDeComputo.class);
        this.emf = Conexion.getInstancia().getEntityManagerFactory();
    }
    
    public DaoEquipoDeComputo(EntityManagerFactory emf) {
        super(EquipoDeComputo.class, emf);
        this.emf = emf;
    }

    @Override
    public List<EquipoDeComputo> buscarConFiltros(Integer gri, EstadoEquipo estado, String busquedaModelo) {
        
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<EquipoDeComputo> cq = cb.createQuery(EquipoDeComputo.class);
            Root<EquipoDeComputo> root = cq.from(EquipoDeComputo.class);

            List<Predicate> predicados = new ArrayList<>();

            if (gri != null && gri > 0) {
                predicados.add(cb.equal(root.get("gri"), gri));
            }

            if (estado != null) {
                predicados.add(cb.equal(root.get("estado"), estado));
            }

            if (busquedaModelo != null && !busquedaModelo.isEmpty()) {
                Join<EquipoDeComputo, Modelo> joinModelo = root.join("modelo");
                
                Predicate porMarca = cb.like(cb.lower(joinModelo.get("marca")), "%" + busquedaModelo.toLowerCase() + "%");
                Predicate porNombreModelo = cb.like(cb.lower(joinModelo.get("nombre")), "%" + busquedaModelo.toLowerCase() + "%");
                
                predicados.add(cb.or(porMarca, porNombreModelo));
            }

            cq.select(root);
            cq.where(predicados); 

            return em.createQuery(cq).getResultList();
        }
    }
}