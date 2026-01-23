/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Entidades.Puesto;
import Entidades.Sucursal;
import Interfaces.IDaoPuesto;
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
 *
 * @author JMorales
 */
public class DaoPuesto extends DaoGenerico<Puesto, Long> implements IDaoPuesto {

    private final EntityManagerFactory emf = Conexion.getInstancia().getEntityManagerFactory();

    public DaoPuesto() {
        super(Puesto.class);
    }

    @Override
    public Puesto busquedaEspecifica(String nombre) {

        try (EntityManager em = getEntityManager()) {

            List<Predicate> predicados = new ArrayList<>();

            CriteriaBuilder cb = emf.getCriteriaBuilder();

            CriteriaQuery<Puesto> cq = cb.createQuery(Puesto.class);

            Root<Puesto> root = cq.from(Puesto.class);

            predicados.add(cb.equal(cb.lower(root.get("nombre")), nombre.toLowerCase()));

            cq.select(root);

            cq.where(predicados);

            return em.createQuery(cq).getSingleResult();

        }
    }
}