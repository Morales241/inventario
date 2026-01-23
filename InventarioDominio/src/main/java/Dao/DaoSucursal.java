/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
 *
 * @author JMorales
 */
public class DaoSucursal extends DaoGenerico<Sucursal, Long> implements IDaoSucursal {

    private final EntityManagerFactory emf = Conexion.getInstancia().getEntityManagerFactory();

    public DaoSucursal() {
        super(Sucursal.class);
    }

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

            cq.where(predicados);

            return em.createQuery(cq).getSingleResult();

        }
    }

    @Override
    public List<Sucursal> busquedaConFiltros(String cadena, String cadenaUbicacion, Long idEmpresa) {

        try (EntityManager em = getEntityManager()) {

            List<Predicate> predicados = new ArrayList<>();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Sucursal> cq = cb.createQuery(Sucursal.class);

            Root<Sucursal> root = cq.from(Sucursal.class);
            
            Join<Sucursal, Empresa> join = root.join("empresa");

            if (!cadena.isEmpty()) {
                predicados.add(cb.like(cb.lower(root.get("Nombre")), cadena.toLowerCase()));
            }

            if (!cadenaUbicacion.isEmpty()) {
                predicados.add(cb.like(cb.lower(root.get("Ubicacion")), cadenaUbicacion.toLowerCase()));
            }
            
            if (idEmpresa != null) {
                predicados.add(cb.equal(join.get("idEmpresa"), idEmpresa));
            }

            cq.select(root);

            cq.where(predicados);

            return em.createQuery(cq).getResultList();

        }
    }

}
