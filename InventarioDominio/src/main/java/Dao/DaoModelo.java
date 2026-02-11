package Dao;

import Entidades.Modelo;
import Interfaces.IDaoModelo;
import conexion.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación del DAO para la entidad {@link Modelo}.
 * Permite la búsqueda técnica de hardware por número de serie y características de componentes.
 * * @author JMorales
 */
public class DaoModelo extends DaoGenerico<Modelo, Long> implements IDaoModelo {

    private EntityManagerFactory emf;

    public DaoModelo() {
        super(Modelo.class);
        this.emf = Conexion.getInstancia().getEntityManagerFactory();
    }
    
    public DaoModelo(EntityManagerFactory emf) {
        super(Modelo.class, emf);
        this.emf = emf;
    }

    /**
     * Metodo Obsoleto, regresa null
     */
    @Override
    public Modelo busquedaEspecifica(String noSerie) {
        return null;
    }

    /**
     * Realiza una consulta avanzada de modelos de hardware utilizando múltiples criterios de filtrado.
     * <p>
     * El método construye una consulta dinámica basada en los parámetros proporcionados. 
     * Cada parámetro se evalúa de forma independiente; si el valor no está vacío, se añade 
     * como una restricción (AND) a la consulta final.
     * </p>
     * * <b>Comportamiento de los filtros:</b>
     * <ul>
     * <li>Si todos los parámetros (marca, memoriaRam, almacenamiento, procesador) son cadenas vacías, 
     * el método <b>retorna la lista completa</b> de todos los modelos registrados en la base de datos.</li>
     * <li>Si uno o más parámetros contienen texto, se realiza una búsqueda exacta (case-insensitive) 
     * que debe cumplir con todos los criterios proporcionados simultáneamente.</li>
     * </ul>
     * * @param marca Marca del fabricante a filtrar (ej. "Dell", "HP"). Si está vacío, se ignora.
     * @param memoriaRam Capacidad o tipo de RAM a filtrar (ej. "16GB"). Si está vacío, se ignora.
     * @param almacenamiento Capacidad de almacenamiento a filtrar (ej. "512GB SSD"). Si está vacío, se ignora.
     * @param procesador Modelo del procesador a filtrar (ej. "i7-12700"). Si está vacío, se ignora.
     * @return Una {@code List<Modelo>} con los resultados que coinciden con los filtros. 
     * Si no hay coincidencias, retorna una lista vacía. Si no se pasan filtros, retorna todos los modelos.
     */
    @Override
    public List<Modelo> busquedaConFiltros(String marca, String memoriaRam, String almacenamiento, String procesador) {
        
        try (EntityManager em = getEntityManager()) {

            List<Predicate> predicados = new ArrayList<>();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Modelo> cq = cb.createQuery(Modelo.class);

            Root<Modelo> root = cq.from(Modelo.class);


            if (!marca.isEmpty()) {
                predicados.add(cb.equal(cb.lower(root.get("marca")), marca.toLowerCase()));
            }

            if (!memoriaRam.isEmpty()) {
                predicados.add(cb.equal(cb.lower(root.get("memoriaRam")), memoriaRam.toLowerCase()));
            }

            if (!almacenamiento.isEmpty()) {
                predicados.add(cb.equal(cb.lower(root.get("almacenamiento")), almacenamiento.toLowerCase()));
            }
            
            if (!procesador.isEmpty()) {
                predicados.add(cb.equal(cb.lower(root.get("procesador")), procesador.toLowerCase()));
            }

            cq.select(root);
            
            // Se aplica la lista de predicados. Si la lista está vacía, no se aplican filtros (WHERE 1=1).
            cq.where(predicados.toArray(new Predicate[0]));

            return em.createQuery(cq).getResultList();

        }
    }
}