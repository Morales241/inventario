/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Entidades.Departamento;
import Entidades.Empresa;
import Entidades.Sucursal;
import Interfaces.IDaoDepartamento;
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
public class DaoDepartamento extends DaoGenerico<Departamento, Long> implements IDaoDepartamento {

    private final EntityManagerFactory emf = Conexion.getInstancia().getEntityManagerFactory();

    public DaoDepartamento(Class<Departamento> claseEntidad) {
        super(claseEntidad);
    }

    @Override
    public Departamento busquedaEspecifica(String nombre) {

        try (EntityManager em = getEntityManager()) {

            List<Predicate> predicados = new ArrayList<>();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Departamento> cq = cb.createQuery(Departamento.class);

            Root<Departamento> root = cq.from(Departamento.class);

            predicados.add(cb.equal(cb.lower(root.get("nombre")), nombre.toLowerCase()));

            cq.select(root);

            cq.where(predicados);

            return em.createQuery(cq).getSingleResult();

        }
    }

    @Override
    public List<Departamento> busquedaConFiltros(String nombre, String nombreSucursal) {
        
        try (EntityManager em = getEntityManager()) {

            List<Predicate> predicados = new ArrayList<>();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Departamento> cq = cb.createQuery(Departamento.class);

            Root<Departamento> root = cq.from(Departamento.class);
            
            Join<Departamento, Sucursal> join = root.join("sucursal");

            if (!nombre.isEmpty()) {
                predicados.add(cb.like(cb.lower(root.get("%nombre%")), nombre.toLowerCase()));
            }
            
            if (!nombreSucursal.isEmpty()) {
                predicados.add(cb.equal(cb.lower(join.get("nombre")), nombreSucursal.toLowerCase()));
            }
            

            cq.select(root);

            cq.where(predicados);

            return em.createQuery(cq).getResultList();

        }
    }
}
