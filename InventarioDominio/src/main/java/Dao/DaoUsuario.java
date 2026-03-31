package Dao;

import Entidades.Puesto;
import Entidades.Usuario;
import Interfaces.IDaoUsuario;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para Usuario con soporte de paginación y conteo de equipos en una sola
 * consulta.
 */
public class DaoUsuario extends DaoGenerico<Usuario, Long> implements IDaoUsuario {

    public DaoUsuario() {
        super(Usuario.class);
    }

    @Override
    public Usuario busquedaEspecifica(String nombre, String nomina) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
        Root<Usuario> root = cq.from(Usuario.class);
        root.fetch("puesto", JoinType.LEFT);           
        List<Predicate> predicados = new ArrayList<>();

        if (nombre != null && !nombre.isBlank()) {
            predicados.add(cb.equal(cb.lower(root.get("nombre")), nombre.toLowerCase()));
        }
        if (nomina != null && !nomina.isBlank()) {
            predicados.add(cb.equal(cb.lower(root.get("noNomina")), nomina.toLowerCase()));
        }

        cq.select(root).where(predicados.toArray(new Predicate[0])).distinct(true);

        List<Usuario> resultados = em.createQuery(cq).getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public List<Usuario> busquedaConFiltros(String cadena, String numero, String puesto) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
        Root<Usuario> root = cq.from(Usuario.class);

        // FETCH JOIN: trae puesto.nombre en la misma query → evita N+1 al mapear
        root.fetch("puesto", JoinType.LEFT);

        List<Predicate> predicados = buildPredicados(cb, root, cadena, numero, puesto);

        cq.select(root)
                .where(predicados.toArray(new Predicate[0]))
                .distinct(true);

        return em.createQuery(cq).getResultList();
    }

    /**
     * Devuelve una página de usuarios que coincidan con los filtros.
     *
     * @param cadena filtro por nombre (LIKE)
     * @param numero filtro por nómina (LIKE)
     * @param puesto filtro por nombre de puesto (EQUAL)
     * @param pagina número de página, empezando en 0
     * @param tamano cantidad de registros por página (ej. 25 o 50)
     * @return lista de usuarios de esa página
     */
    public List<Usuario> busquedaConFiltrosPaginado(String cadena,
            String numero,
            String puesto,
            int pagina,
            int tamano) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Usuario> cq = cb.createQuery(Usuario.class);
        Root<Usuario> root = cq.from(Usuario.class);

        // fetch join para traer el puesto en la misma query
        root.fetch("puesto", JoinType.LEFT);

        List<Predicate> predicados = buildPredicados(cb, root, cadena, numero, puesto);

        cq.select(root)
                .where(predicados.toArray(new Predicate[0]))
                .distinct(true)
                .orderBy(cb.asc(root.get("nombre")));   // orden estable

        return em.createQuery(cq)
                .setFirstResult(pagina * tamano) // OFFSET = página * tamaño
                .setMaxResults(tamano) // LIMIT  = tamaño
                .getResultList();
    }

    /**
     * Cuenta cuántos usuarios activos coinciden con los filtros, sin traer
     * objetos. Se usa junto con busquedaConFiltrosPaginado() para calcular el
     * total de páginas.
     */
    public long contarConFiltros(String cadena, String numero, String puesto) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<Usuario> root = cq.from(Usuario.class);

        // En la query de conteo NO se hace fetch join (no se necesita el objeto)
        List<Predicate> predicados = buildPredicados(cb, root, cadena, numero, puesto);

        cq.select(cb.countDistinct(root))
                .where(predicados.toArray(new Predicate[0]));

        return em.createQuery(cq).getSingleResult();
    }

    /**
     * Cuenta los equipos actualmente asignados (sin fecha de devolución) a un
     * usuario.
     *
     * Antes: se traía toda la lista de EquipoAsignado al controlador y se hacía
     * .size() Ahora: 1 sola query COUNT en la BD → mucho más rápido.
     */
    public int contarEquiposAsignadosPorUsuario(Long idUsuario) {

        String jpql = """
                SELECT COUNT(ea)
                FROM   EquipoAsignado ea
                WHERE  ea.Usuario.idUsuario = :idUsuario
                AND    ea.fechaDevolucion IS NULL
                """;

        Long count = em.createQuery(jpql, Long.class)
                .setParameter("idUsuario", idUsuario)
                .getSingleResult();

        return count == null ? 0 : count.intValue();
    }

    private List<Predicate> buildPredicados(CriteriaBuilder cb,
            Root<Usuario> root,
            String cadena,
            String numero,
            String puesto) {

        List<Predicate> predicados = new ArrayList<>();

        // Solo usuarios activos
        predicados.add(cb.isTrue(root.get("activo")));

        if (cadena != null && !cadena.isBlank()) {
            predicados.add(
                    cb.like(cb.lower(root.get("nombre")),
                            "%" + cadena.toLowerCase() + "%")
            );
        }

        if (numero != null && !numero.isBlank()) {
            predicados.add(
                    cb.like(cb.lower(root.get("noNomina")),
                            "%" + numero.toLowerCase() + "%")
            );
        }

        if (puesto != null && !puesto.isBlank()) {
            Join<Usuario, Puesto> join = root.join("puesto", JoinType.LEFT);
            predicados.add(
                    cb.equal(cb.lower(join.get("nombre")), puesto.toLowerCase())
            );
        }

        return predicados;
    }
}
