package Dao;

import Entidades.Empresa;
import Entidades.Sucursal;
import Interfaces.IDaoSucursal;
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
 * Implementación del DAO para la entidad {@link Sucursal}.
 * Gestiona la persistencia y consultas avanzadas de las sedes físicas de las empresas.
 * * @author JMorales
 */
public class DaoSucursal extends DaoGenerico<Sucursal, Long> implements IDaoSucursal {

    private final EntityManagerFactory emf;

    public DaoSucursal() {
        super(Sucursal.class);
        this.emf = Conexion.getInstancia().getEntityManagerFactory();
    }
    
    public DaoSucursal(EntityManagerFactory emf) {
        super(Sucursal.class,emf);
        this.emf = emf;
    }

    /**
     * Realiza una búsqueda exacta de una sucursal por su nombre y ubicación.
     * <p>
     * Ambos criterios deben coincidir exactamente (ignorando mayúsculas/minúsculas). 
     * Si un parámetro se envía vacío, no se aplicará dicho filtro en la consulta.
     * </p>
     * * @param nombre Nombre exacto de la sucursal.
     * @param ubicacion Dirección o zona exacta de la sucursal.
     * @return La {@link Sucursal} que coincide con ambos criterios.
     * @throws jakarta.persistence.NoResultException Si no se encuentra ninguna coincidencia exacta.
     */
    @Override
    public Sucursal busquedaEspecifica(String nombre, String ubicacion) {
        try (EntityManager em = getEntityManager()) {
            List<Predicate> predicados = new ArrayList<>();
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Sucursal> cq = cb.createQuery(Sucursal.class);
            Root<Sucursal> root = cq.from(Sucursal.class);

            if (!nombre.isEmpty()) {
                predicados.add(cb.equal(cb.lower(root.get("nombre")), nombre.toLowerCase()));
            }
            if (!ubicacion.isEmpty()) {
                predicados.add(cb.equal(cb.lower(root.get("ubicacion")), ubicacion.toLowerCase()));
            }

            cq.select(root);
            cq.where(predicados.toArray(new Predicate[0]));
            return em.createQuery(cq).getSingleResult();
        }
    }

    /**
     * Filtra sucursales mediante coincidencias parciales y relación con empresa.
     * <p>
     * <b>Lógica de filtrado:</b>
     * <ul>
     * <li>Si los campos de texto están vacíos y el ID de empresa es nulo, 
     * retorna la <b>lista completa</b> de sucursales.</li>
     * <li>Usa el operador {@code LIKE} para el nombre y la ubicación, permitiendo búsquedas parciales.</li>
     * <li>Filtra por una empresa específica si se proporciona su identificador.</li>
     * </ul>
     * </p>
     * * @param cadena Parte del nombre de la sucursal a buscar.
     * @param cadenaUbicacion Parte de la ubicación a buscar.
     * @param idEmpresa Identificador de la {@link Empresa} dueña de la sucursal.
     * @return Lista de sucursales que cumplen con los criterios.
     */
    @Override
    public List<Sucursal> busquedaConFiltros(String cadena, String cadenaUbicacion, Long idEmpresa) {
        try (EntityManager em = getEntityManager()) {
            List<Predicate> predicados = new ArrayList<>();
            CriteriaBuilder cb = emf.getCriteriaBuilder();
            CriteriaQuery<Sucursal> cq = cb.createQuery(Sucursal.class);
            Root<Sucursal> root = cq.from(Sucursal.class);
            Join<Sucursal, Empresa> join = root.join("empresa");

            if (!cadena.isEmpty()) {
                predicados.add(cb.like(cb.lower(root.get("Nombre")), "%" + cadena.toLowerCase() + "%"));
            }
            if (!cadenaUbicacion.isEmpty()) {
                predicados.add(cb.like(cb.lower(root.get("Ubicacion")), "%" + cadenaUbicacion.toLowerCase() + "%"));
            }
            if (idEmpresa != null) {
                predicados.add(cb.equal(join.get("id"), idEmpresa));
            }

            cq.select(root);
            cq.where(predicados.toArray(new Predicate[0]));
            return em.createQuery(cq).getResultList();
        }
    }
}