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

/**
 * DAO especializado para la gestión de {@link EquipoDeComputo}.
 * Permite filtrar equipos por GRI, estado físico y descripciones del modelo.
 * * @author JMorales
 */
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

    /**
     * Realiza una búsqueda avanzada de equipos de cómputo.
     * @param gri Número de identificación GRI (opcional).
     * @param estado Estado del equipo según {@link EstadoEquipo} (opcional).
     * @param busquedaModelo Cadena para buscar en la marca o el nombre del modelo.
     * @return Lista de equipos filtrados.
     */
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
            cq.where(predicados.toArray(new Predicate[0])); 
            return em.createQuery(cq).getResultList();
        }
    }
}