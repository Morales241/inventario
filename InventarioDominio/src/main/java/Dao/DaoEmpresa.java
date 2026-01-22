/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Entidades.Empresa;
import Interfaces.IDaoEmpresa;
import conexion.Conexion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;

/**
 *
 * @author JMorales
 */
public class DaoEmpresa extends DaoGenerico<Empresa, Long> implements IDaoEmpresa {

    private final EntityManagerFactory emf = Conexion.getInstancia().getEntityManagerFactory();

    public DaoEmpresa(Class<Empresa> claseEntidad) {
        super(claseEntidad);
    }

    @Override
    public Empresa buscarPorNombre(String nombre) {

        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Empresa> cq = cb.createQuery(Empresa.class);

            Root<Empresa> root = cq.from(Empresa.class);

            Predicate predicate = cb.equal(cb.lower(root.get("nombre")), nombre.toLowerCase());

            cq.select(root);

            cq.where(predicate);

            return em.createQuery(cq).getSingleResult();

        }
    }

    @Override
    public List<Empresa> buscarPorCoincidencias(String cadena) {
        
        try (EntityManager em = getEntityManager()) {
            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Empresa> cq = cb.createQuery(Empresa.class);

            Root<Empresa> root = cq.from(Empresa.class);

            Predicate predicate = cb.like(cb.lower(root.get("%nombre%")), cadena.toLowerCase());
            
            cq.select(root);

            cq.where(predicate);

            return em.createQuery(cq).getResultList();

        }
    }

}
