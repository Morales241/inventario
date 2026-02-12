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
 * Implementación del DAO para la entidad {@link Sucursal}. Gestiona la
 * persistencia y consultas avanzadas de las sedes físicas de las empresas.
 *
 * * @author JMorales
 */
public class DaoSucursal extends DaoGenerico<Sucursal, Long> implements IDaoSucursal {

    public DaoSucursal() {
        super(Sucursal.class);
    }

    @Override
    public Sucursal busquedaEspecifica(String nombre, String ubicacion) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Sucursal> cq = cb.createQuery(Sucursal.class);
        Root<Sucursal> root = cq.from(Sucursal.class);

        List<Predicate> predicados = new ArrayList<>();

        if (nombre != null && !nombre.isBlank()) {
            predicados.add(
                    cb.equal(cb.lower(root.get("nombre")),
                            nombre.toLowerCase())
            );
        }

        if (ubicacion != null && !ubicacion.isBlank()) {
            predicados.add(
                    cb.equal(cb.lower(root.get("ubicacion")),
                            ubicacion.toLowerCase())
            );
        }

        cq.select(root)
                .where(predicados.toArray(new Predicate[0]));

        List<Sucursal> resultados = em.createQuery(cq).getResultList();
        return resultados.isEmpty() ? null : resultados.get(0);
    }

    @Override
    public List<Sucursal> busquedaConFiltros(String cadena,
            String cadenaUbicacion,
            Long idEmpresa) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Sucursal> cq = cb.createQuery(Sucursal.class);
        Root<Sucursal> root = cq.from(Sucursal.class);

        List<Predicate> predicados = new ArrayList<>();

        if (cadena != null && !cadena.isBlank()) {
            predicados.add(
                    cb.like(cb.lower(root.get("nombre")),
                            "%" + cadena.toLowerCase() + "%")
            );
        }

        if (cadenaUbicacion != null && !cadenaUbicacion.isBlank()) {
            predicados.add(
                    cb.like(cb.lower(root.get("ubicacion")),
                            "%" + cadenaUbicacion.toLowerCase() + "%")
            );
        }

        if (idEmpresa != null) {
            Join<Sucursal, Empresa> join = root.join("empresa");
            predicados.add(cb.equal(join.get("id"), idEmpresa));
        }

        cq.select(root)
                .where(predicados.toArray(new Predicate[0]));

        return em.createQuery(cq).getResultList();
    }
}